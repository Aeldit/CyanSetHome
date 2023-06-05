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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fr.aeldit.cyansh.util.Utils.MODID;

public class HomeUtils
{
    public static final Path HOMES_PATH = FabricLoader.getInstance().getConfigDir().resolve(MODID);
    public static final Path TRUST_PATH = Path.of(HOMES_PATH + "/trusted_players.json");

    public static final TypeToken<Map<String, ArrayList<String>>> TRUST_TYPE = new TypeToken<>() {};


    public static boolean homeExists(@NotNull List<Home> homes, String homeName)
    {
        for (Home homeKey : homes)
        {
            if (homeKey.name().equals(homeName))
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
            if (homeKey.name().equals(homeName))
            {
                return homes.indexOf(homeKey);
            }
        }
        return -1;
    }

    public static boolean isPlayerTrusting(String trustingPlayerUsername, String trustedPlayerUsername)
    {
        if (Files.exists(TRUST_PATH))
        {
            try
            {
                Gson gsonReader = new Gson();
                Reader reader = Files.newBufferedReader(TRUST_PATH);
                Map<String, ArrayList<String>> gsonTrustingPlayers = gsonReader.fromJson(reader, TRUST_TYPE);
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
            Reader reader = Files.newBufferedReader(TRUST_PATH);
            Map<String, ArrayList<String>> gsonTrustingPlayers = gsonReader.fromJson(reader, TRUST_TYPE);
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
