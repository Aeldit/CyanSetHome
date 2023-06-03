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
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static fr.aeldit.cyansh.util.Utils.MODID;

public class HomeUtils
{
    public static final Path homesPath = FabricLoader.getInstance().getConfigDir().resolve(MODID);
    public static final Path trustPath = Path.of(homesPath + "/trusted_players.json");


    public static boolean homeExists(@NotNull List<Home> homes, String homeName)
    {
        for (Home homeKey : homes)
        {
            if (Objects.equals(homeKey.name(), homeName))
            {
                return true;
            }
        }
        return false;
    }

    public static int getHomeIndex(@NotNull List<Home> homes, String homeName)
    {
        for (Home homeKey : homes)
        {
            if (Objects.equals(homeKey.name(), homeName))
            {
                return homes.indexOf(homeKey);
            }
        }
        return -1;
    }

    public static boolean trustPlayer(String trustingPlayerUsername, String trustedPlayerUsername)
    {
        if (Files.exists(trustPath))
        {
            try
            {
                Gson gsonReader = new Gson();
                Reader reader = Files.newBufferedReader(trustPath);
                Type mapType = new TypeToken<Map<String, ArrayList<String>>>() {}.getType();
                Map<String, ArrayList<String>> gsonTrustingPlayers = gsonReader.fromJson(reader, mapType);
                reader.close();

                for (String playerKey : gsonTrustingPlayers.keySet())
                {
                    if (playerKey.split("_")[1].equals(trustingPlayerUsername))
                    {
                        if (gsonTrustingPlayers.get(playerKey).contains(trustedPlayerUsername))
                        {
                            return true;
                        }
                    }
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public static ArrayList<String> getTrustedPlayers(String trustingPlayer)
    {
        try
        {
            Gson gsonReader = new Gson();
            Reader reader = Files.newBufferedReader(trustPath);
            Type mapType = new TypeToken<Map<String, ArrayList<String>>>() {}.getType();
            Map<String, ArrayList<String>> gsonTrustingPlayers = gsonReader.fromJson(reader, mapType);
            reader.close();

            if (gsonTrustingPlayers.containsKey(trustingPlayer))
            {
                return gsonTrustingPlayers.get(trustingPlayer);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return new ArrayList<>();
    }
}
