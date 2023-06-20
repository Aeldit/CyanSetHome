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

import fr.aeldit.cyansh.homes.Home;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static fr.aeldit.cyansh.homes.Trusts.TRUST_PATH;
import static fr.aeldit.cyansh.util.Utils.*;

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
        String playerKey = playerUUID + " " + playerName;

        HomesObj.renameIfUsernameChanged(playerKey, playerUUID, playerName);
        TrustsObj.renameChangedUsernames(playerKey, playerUUID, playerName);
    }

    public static void transferPropertiesToGson()
    {
        File[] listOfFiles = new File(MOD_PATH.toUri()).listFiles();

        if (listOfFiles != null)
        {
            for (File file : listOfFiles)
            {
                if (file.isFile())
                {
                    String[] splitedFileName = file.getName().split("\\.");

                    try
                    {
                        if (splitedFileName[splitedFileName.length - 1].equals("properties") && Files.readAllLines(file.toPath()).size() >= 1)
                        {
                            Properties properties = new Properties();
                            FileInputStream fis = new FileInputStream(file);
                            properties.load(fis);
                            fis.close();

                            if (splitedFileName[0].equals("trusted_players"))
                            {
                                ConcurrentHashMap<String, List<String>> trusts = new ConcurrentHashMap<>();

                                if (!Files.exists(TRUST_PATH))
                                {
                                    Files.createFile(TRUST_PATH);

                                    properties.stringPropertyNames().forEach(name ->
                                            {
                                                String trustedPlayers = (String) properties.get(name);
                                                List<String> trusted = Collections.synchronizedList(new ArrayList<>(List.of(trustedPlayers.split(" "))));
                                                trusts.put(name, trusted);
                                            }
                                    );

                                    TrustsObj.setTrusts(trusts);
                                    LOGGER.info("[CyanSetHome] Transfered the home file " + file.getName() + " to a json file.");
                                }
                                else
                                {
                                    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
                                    {
                                        TrustsObj.readServer();
                                    }
                                    else
                                    {
                                        TrustsObj.readClient();
                                    }

                                    if (TrustsObj.isNotEmpty())
                                    {
                                        trusts.putAll(TrustsObj.getTrusts());
                                    }

                                    properties.stringPropertyNames().forEach(name ->
                                            {
                                                if (!trusts.containsKey(name))
                                                {
                                                    String trustedPlayers = (String) properties.get(name);
                                                    List<String> trusted = Collections.synchronizedList(new ArrayList<>(List.of(trustedPlayers.split(" "))));
                                                    trusts.put(name, trusted);
                                                }
                                            }
                                    );

                                    LOGGER.info("[CyanSetHome] Transfered the missing trusted/trusting players of " + file.getName() + " to the corresponding json file.");
                                }

                                TrustsObj.write();
                            }
                            else
                            {
                                boolean changed = false;

                                if (!properties.stringPropertyNames().isEmpty())
                                {
                                    if (HomesObj.isEmpty(file.getName().split("\\.")[0]))
                                    {
                                        List<Home> homes = Collections.synchronizedList(new ArrayList<>());

                                        properties.stringPropertyNames().forEach(s -> homes.add(new Home(
                                                        s,
                                                        properties.getProperty(s).split(" ")[0],
                                                        Double.parseDouble(properties.getProperty(s).split(" ")[1]),
                                                        Double.parseDouble(properties.getProperty(s).split(" ")[2]),
                                                        Double.parseDouble(properties.getProperty(s).split(" ")[3]),
                                                        Float.parseFloat(properties.getProperty(s).split(" ")[4]),
                                                        Float.parseFloat(properties.getProperty(s).split(" ")[5]),
                                                        properties.getProperty(s).split(" ")[6]
                                                ))
                                        );

                                        HomesObj.addPlayer(file.getName().split("\\.")[0], Collections.synchronizedList(new ArrayList<>(homes)));
                                    }
                                    else
                                    {
                                        for (String name : properties.stringPropertyNames())
                                        {
                                            String currentHome = (String) properties.get(name);

                                            if (currentHome.split(" ").length == 7)
                                            {
                                                HomesObj.addHome(file.getName().split("\\.")[0], new Home(
                                                        name,
                                                        currentHome.split(" ")[0],
                                                        Double.parseDouble(currentHome.split(" ")[1]),
                                                        Double.parseDouble(currentHome.split(" ")[2]),
                                                        Double.parseDouble(currentHome.split(" ")[3]),
                                                        Float.parseFloat(currentHome.split(" ")[4]),
                                                        Float.parseFloat(currentHome.split(" ")[5]),
                                                        currentHome.split(" ")[6]
                                                ));

                                                changed = true;
                                            }
                                        }
                                    }
                                }

                                if (changed)
                                {
                                    LOGGER.info("[CyanSetHome] Transfered the home file " + file.getName() + " to a json file.");
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
