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
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static fr.aeldit.cyansh.util.HomeUtils.homesPath;
import static fr.aeldit.cyansh.util.HomeUtils.trustPath;
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
        String playerUUID = player.getUuidAsString();
        String playerName = player.getName().getString();
        String playerKey = playerUUID + "_" + playerName;

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

        if (Files.exists(trustPath))
        {
            try
            {
                Gson gsonReader = new Gson();
                Reader reader = Files.newBufferedReader(trustPath);
                Type mapType = new TypeToken<Map<String, ArrayList<String>>>() {}.getType();
                Map<String, ArrayList<String>> gsonTrustingPlayers = gsonReader.fromJson(reader, mapType);
                reader.close();

                Map<String, ArrayList<String>> trustingPlayers = new HashMap<>(gsonTrustingPlayers);
                boolean changed = false;
                String prevName = "";

                if (gsonTrustingPlayers.size() != 0)
                {
                    for (String key : gsonTrustingPlayers.keySet())
                    {
                        //Changes the key of the actual player
                        if (Objects.equals(key.split("_")[0], playerUUID) && !Objects.equals(key.split("_")[1], playerName))
                        {
                            prevName = key.split("_")[1];
                            trustingPlayers.put(playerKey, trustingPlayers.get(key));
                            trustingPlayers.remove(key);
                            changed = true;
                        }

                        if (changed)
                        {
                            key = playerKey;
                        }

                        //Changes the player when it is not a key but an element of the list
                        for (String listKey : trustingPlayers.get(key))
                        {
                            if (listKey.contains(playerUUID))
                            {
                                prevName = listKey.split("_")[1];
                                trustingPlayers.get(key).add(playerKey);
                                trustingPlayers.get(key).remove(listKey);
                                changed = true;
                            }
                        }
                    }
                }

                if (changed)
                {
                    Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                    Writer writer = Files.newBufferedWriter(trustPath);
                    gsonWriter.toJson(trustingPlayers, writer);
                    writer.close();
                    LOGGER.info("[CyanSetHome] Updated {}'s pseudo in the trust file, because the player changed its pseudo (previously {})", player.getName().getString(), prevName);
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
                    try
                    {
                        if (Objects.equals(splitedFileName[1], "properties") && Files.readAllLines(file.toPath()).size() > 1)
                        {
                            Properties properties = new Properties();
                            properties.load(new FileInputStream(file));
                            if (splitedFileName[0].equals("trusted_players"))
                            {
                                Map<String, ArrayList<?>> trusts = new HashMap<>();
                                for (String name : properties.stringPropertyNames())
                                {
                                    String trustedPlayers = (String) properties.get(name);
                                    ArrayList<Object> trusted = new ArrayList<>(List.of(trustedPlayers.split(" ")));
                                    trusts.put(name, trusted);
                                }

                                Path jsonFilePath = FabricLoader.getInstance().getConfigDir().resolve(MODID + "/" + file.getName().split("\\.")[0] + ".json");
                                if (!Files.exists(jsonFilePath))
                                {
                                    Files.createFile(jsonFilePath);
                                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                    Writer writer = Files.newBufferedWriter(jsonFilePath);
                                    gson.toJson(trusts, writer);
                                    writer.close();
                                    LOGGER.info("[CyanSetHome] Transfered the home file " + file.getName() + " to a json file.");
                                }
                                else
                                {
                                    Gson gson = new Gson();
                                    Reader reader = Files.newBufferedReader(jsonFilePath);
                                    Map<?, ?> homesFromGson = gson.fromJson(reader, Map.class);
                                    reader.close();

                                    if (homesFromGson != null)
                                    {
                                        for (Map.Entry<?, ?> entry : homesFromGson.entrySet())
                                        {
                                            trusts.put((String) entry.getKey(), (ArrayList<?>) entry.getValue());
                                        }
                                    }

                                    Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                                    Writer writer = Files.newBufferedWriter(jsonFilePath);
                                    gsonWriter.toJson(trusts, writer);
                                    writer.close();
                                    LOGGER.info("[CyanSetHome] Transfered the missing trusted/trusting players of " + file.getName() + " to the corresponding json file.");
                                }
                            }
                            else
                            {
                                List<Home> homes = new ArrayList<>();

                                if (properties.stringPropertyNames().size() != 0)
                                {
                                    for (String name : properties.stringPropertyNames())
                                    {
                                        String currentHome = (String) properties.get(name);
                                        if (currentHome.split(" ").length == 7)
                                        {
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
                            }
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
