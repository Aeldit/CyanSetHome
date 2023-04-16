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

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        Path currentHomesPath = Path.of(homesPath + "\\" + playerKey + ".properties");
        checkOrCreateHomesDir();
        File[] listOfFiles = currentHomesDir.listFiles();

        if (listOfFiles != null)
        {
            for (File file : listOfFiles)
            {
                if (file.isFile())
                {
                    String[] splitedFileName = file.getName().split("_");
                    if (Objects.equals(splitedFileName[0], player.getUuidAsString()) && !Objects.equals(splitedFileName[1], player.getName().getString() + ".properties"))
                    {
                        try
                        {
                            Files.move(file.toPath(), currentHomesPath.resolveSibling(playerKey + ".properties"));
                            LOGGER.info("[CyanSetHome] Rename the file '{}' to '{}' because the player changed its pseudo", file.getName(), playerKey + ".properties");
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
}
