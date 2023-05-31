/*
 * Copyright (c) 2023  -  Made by Aeldit
 *
 *              GNU LESSER GENERAL PUBLIC LICENSE
 *                  Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 *
 *
 * This version of the GNU Lesser General Public License incorporates
 * the terms and conditions of version 3 of the GNU General Public
 * License, supplemented by the additional permissions listed in the LICENSE.txt file
 * in the repo of this mod (https://github.com/Aeldit/CyanSetHome)
 */

package fr.aeldit.cyansh.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static fr.aeldit.cyansh.util.Utils.*;

public class EventUtils
{
    /**
     * Called on {@code ServerPlayConnectionEvents.JOIN} event
     * Renames the trust and homes files if the players username corresponding to the UUID changed.
     * (ex: UUID_Username -> UUID_newUsername)
     *
     * @param handler The ServerPlayNetworkHandler
     */
    public static void renameFileIfUsernameChanged(@NotNull ServerPlayNetworkHandler handler)
    {
        ServerPlayerEntity player = handler.getPlayer();
        String playerKey = player.getUuidAsString() + "_" + player.getName().getString();

        File currentHomesDir = new File(homesPath.toUri());
        Path currentHomesPath = Path.of(homesPath + "\\" + playerKey + ".json");
        checkOrCreateHomesDir();
        File[] listOfFiles = currentHomesDir.listFiles();

        if (listOfFiles != null)
        {
            for (File file : listOfFiles)
            {
                if (file.isFile())
                {
                    String[] splitedFileName = file.getName().split("_");
                    if (Objects.equals(splitedFileName[0], player.getUuidAsString()) && !Objects.equals(splitedFileName[1], player.getName().getString() + ".json"))
                    {
                        try
                        {
                            Files.move(file.toPath(), currentHomesPath.resolveSibling(playerKey + ".json"));
                            LOGGER.info("[CyanSetHome] Rename the file '{}' to '{}' because the player changed its pseudo", file.getName(), playerKey + ".json");
                        } catch (IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        if (Files.exists(trustPath)) // TODO -> transfer to Gson
        {
            try
            {
                Properties properties = new Properties();
                properties.load(new FileInputStream(trustPath.toFile()));
                String prevName;
                if (properties.stringPropertyNames().size() != 0)
                {
                    if (!properties.containsKey(playerKey))
                    {
                        for (String key : properties.stringPropertyNames())
                        {
                            if (Objects.equals(key.split("_")[0], player.getUuidAsString()))
                            {
                                prevName = key.split("_")[1];
                                if (!Objects.equals(key.split("_")[1], player.getName().getString()))
                                {
                                    properties.put(playerKey, properties.get(key));
                                    properties.remove(key);
                                    properties.store(new FileOutputStream(trustPath.toFile()), null);
                                    LOGGER.info("[CyanSetHome] Updated {}'s pseudo in the trust file, because the player changed its pseudo (previously {})", player.getName().getString(), prevName);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void transferPropertiesToGson()
    {
        File currentHomesDir = new File(homesPath.toUri());
        File[] listOfFiles = currentHomesDir.listFiles();

        if (listOfFiles != null)
        {
            for (File file : listOfFiles)
            {
                if (file.isFile())
                {
                    String[] splitedFileName = file.getName().split("\\.");
                    if (Objects.equals(splitedFileName[1], "properties"))
                    {
                        List<Home> homes = new ArrayList<>();
                        try
                        {
                            Properties properties = new Properties();
                            properties.load(new FileInputStream(file));
                            if (properties.stringPropertyNames().size() != 0)
                            {
                                for (String name : properties.stringPropertyNames())
                                {
                                    String currentHome = (String) properties.get(name);
                                    homes.add(new Home(
                                            name,
                                            currentHome.split(" ")[0],
                                            Double.parseDouble(currentHome.split(" ")[1]),
                                            Double.parseDouble(currentHome.split(" ")[2]),
                                            Double.parseDouble(currentHome.split(" ")[3]),
                                            Float.parseFloat(currentHome.split(" ")[4]),
                                            Float.parseFloat(currentHome.split(" ")[5]),
                                            currentHome.split(" ")[6]
                                    ));
                                }
                            }

                            Path jsonFilePath = FabricLoader.getInstance().getConfigDir().resolve(MODID + "/" + file.getName().split("\\.")[0] + ".json");
                            if (!Files.exists(jsonFilePath))
                            {
                                Files.createFile(jsonFilePath);
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                Writer writer = Files.newBufferedWriter(jsonFilePath);
                                gson.toJson(homes, writer);
                                writer.close();
                                LOGGER.info("[CyanSetHome] Transfered the home file " + file.getName() + " to a json file.");
                            }
                            else
                            {
                                Gson gson = new Gson();
                                Reader reader = Files.newBufferedReader(jsonFilePath);
                                Home[] homesFromGson = gson.fromJson(reader, Home[].class);
                                reader.close();

                                ArrayList<String> existantNames = new ArrayList<>();
                                ArrayList<Home> mutableHomes = new ArrayList<>(List.of(homesFromGson));

                                for (Home home : homesFromGson)
                                {
                                    existantNames.add(home.name());
                                }
                                for (String name : properties.stringPropertyNames())
                                {
                                    if (!existantNames.contains(name))
                                    {
                                        String currentHome = (String) properties.get(name);
                                        mutableHomes.add(new Home(
                                                name,
                                                currentHome.split(" ")[0],
                                                Double.parseDouble(currentHome.split(" ")[1]),
                                                Double.parseDouble(currentHome.split(" ")[2]),
                                                Double.parseDouble(currentHome.split(" ")[3]),
                                                Float.parseFloat(currentHome.split(" ")[4]),
                                                Float.parseFloat(currentHome.split(" ")[5]),
                                                currentHome.split(" ")[6]
                                        ));
                                    }
                                }

                                Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                                Writer writer = Files.newBufferedWriter(jsonFilePath);
                                gsonWriter.toJson(mutableHomes, writer);
                                writer.close();
                                LOGGER.info("[CyanSetHome] Transfered the missing home of " + file.getName() + " to the corresponding json file.");
                            }
                        } catch (IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }
}
