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

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static fr.aeldit.cyansh.util.GsonUtils.*;
import static fr.aeldit.cyansh.util.HomeUtils.HOMES_PATH;
import static fr.aeldit.cyansh.util.HomeUtils.TRUST_PATH;
import static fr.aeldit.cyansh.util.Utils.LOGGER;
import static fr.aeldit.cyansh.util.Utils.MODID;

public class EventUtils
{
    /**
     * Called on {@code ServerPlayConnectionEvents.JOIN} event
     * Renames the trust and homes files if the players username corresponding to the UUID changed.
     * (ex: UUID_Username -> UUID_updatedUsername)
     *
     * @param handler The ServerPlayNetworkHandler
     */
    public static void renameFileIfUsernameChanged(@NotNull ServerPlayNetworkHandler handler)
    {
        String playerUUID = handler.getPlayer().getUuidAsString();
        String playerName = handler.getPlayer().getName().getString();
        String playerKey = playerUUID + "_" + playerName;

        if (Files.exists(HOMES_PATH))
        {
            File[] listOfFiles = new File(HOMES_PATH.toUri()).listFiles();

            if (listOfFiles != null)
            {
                for (File file : listOfFiles)
                {
                    if (file.isFile())
                    {
                        String[] splitedFileName = file.getName().split("_");

                        if (splitedFileName[0].equals(playerUUID) && !splitedFileName[1].equals(playerName + ".json"))
                        {
                            try
                            {
                                Files.move(file.toPath(), Path.of(HOMES_PATH + "\\" + playerKey + ".json").resolveSibling(playerKey + ".json"));
                                LOGGER.info("[CyanSetHome] Rename the file '{}' to '{}' because the player changed its pseudo", file.getName(), playerKey + ".json");
                            }
                            catch (IOException e)
                            {
                                throw new RuntimeException(e);
                            }
                            break;
                        }
                    }
                }
            }
        }

        if (Files.exists(TRUST_PATH))
        {
            Map<String, ArrayList<String>> gsonTrustingPlayers = readTrustFile();

            boolean changed = false;
            String prevName = "";

            if (!gsonTrustingPlayers.isEmpty())
            {
                for (String key : gsonTrustingPlayers.keySet())
                {
                    // Changes the player username when it is a key
                    if (key.split("_")[0].equals(playerUUID) && !key.split("_")[1].equals(playerName))
                    {
                        prevName = key.split("_")[1];
                        gsonTrustingPlayers.put(playerKey, gsonTrustingPlayers.get(key));
                        gsonTrustingPlayers.remove(key);
                        changed = true;
                    }

                    if (changed)
                    {
                        key = playerKey;
                    }

                    // Changes the player's username when it is in a list of trusted players
                    for (String listKey : gsonTrustingPlayers.get(key))
                    {
                        if (listKey.split("_")[0].equals(playerUUID) && !listKey.split("_")[1].equals(playerName))
                        {
                            prevName = listKey.split("_")[1];
                            gsonTrustingPlayers.get(key).add(playerKey);
                            gsonTrustingPlayers.get(key).remove(listKey);
                            changed = true;
                        }
                    }
                }
            }

            if (changed)
            {
                writeGson(TRUST_PATH, gsonTrustingPlayers);
                LOGGER.info("[CyanSetHome] Updated {}'s pseudo in the trust file, because the player changed its pseudo (previously {})", playerName, prevName);
            }
        }
    }

    public static void transferPropertiesToGson()
    {
        File[] listOfFiles = new File(HOMES_PATH.toUri()).listFiles();

        if (listOfFiles != null)
        {
            for (File file : listOfFiles)
            {
                if (file.isFile())
                {
                    String[] splitedFileName = file.getName().split("\\.");

                    try
                    {
                        if (splitedFileName[1].equals("properties") && Files.readAllLines(file.toPath()).size() > 1)
                        {
                            Properties properties = new Properties();
                            FileInputStream fis = new FileInputStream(file);
                            properties.load(fis);
                            fis.close();

                            if (splitedFileName[0].equals("trusted_players"))
                            {
                                Map<String, ArrayList<String>> trusts = new HashMap<>();

                                if (!Files.exists(TRUST_PATH))
                                {
                                    Files.createFile(TRUST_PATH);
                                    properties.stringPropertyNames().forEach(name ->
                                            {
                                                String trustedPlayers = (String) properties.get(name);
                                                ArrayList<String> trusted = new ArrayList<>(List.of(trustedPlayers.split(" ")));
                                                trusts.put(name, trusted);
                                            }
                                    );
                                    LOGGER.info("[CyanSetHome] Transfered the home file " + file.getName() + " to a json file.");
                                }
                                else
                                {
                                    Map<String, ArrayList<String>> homesFromGson = readTrustFile();

                                    if (homesFromGson != null)
                                    {
                                        trusts.putAll(homesFromGson);
                                    }

                                    properties.stringPropertyNames().forEach(name ->
                                            {
                                                if (!trusts.containsKey(name))
                                                {
                                                    String trustedPlayers = (String) properties.get(name);
                                                    ArrayList<String> trusted = new ArrayList<>(List.of(trustedPlayers.split(" ")));
                                                    trusts.put(name, trusted);
                                                }
                                            }
                                    );

                                    LOGGER.info("[CyanSetHome] Transfered the missing trusted/trusting players of " + file.getName() + " to the corresponding json file.");
                                }

                                writeGson(TRUST_PATH, trusts);
                            }
                            else
                            {
                                ArrayList<Home> homes = new ArrayList<>();

                                if (!properties.stringPropertyNames().isEmpty())
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

                                Path gsonFilePath = FabricLoader.getInstance().getConfigDir().resolve(MODID + "/" + file.getName().split("\\.")[0] + ".json");

                                if (!Files.exists(gsonFilePath))
                                {
                                    Files.createFile(gsonFilePath);
                                    writeGson(gsonFilePath, homes);
                                    LOGGER.info("[CyanSetHome] Transfered the home file " + file.getName() + " to a json file.");
                                }
                                else
                                {
                                    ArrayList<Home> homesFromGson = readHomeFile(gsonFilePath);

                                    ArrayList<String> existantNames = new ArrayList<>();

                                    homesFromGson.forEach(home -> existantNames.add(home.name()));

                                    for (String name : properties.stringPropertyNames())
                                    {
                                        if (!existantNames.contains(name))
                                        {
                                            String currentHome = (String) properties.get(name);
                                            homesFromGson.add(new Home(
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

                                    writeGson(gsonFilePath, homesFromGson);
                                    LOGGER.info("[CyanSetHome] Transfered the missing home of " + file.getName() + " to the corresponding json file.");
                                }
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
