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

import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import static fr.aeldit.cyansh.util.GsonUtils.readTrustFile;
import static fr.aeldit.cyansh.util.Utils.MOD_PATH;

public class HomeUtils
{
    public static final Path TRUST_PATH = Path.of(MOD_PATH + "/trusted_players.json");

    public static final TypeToken<Map<String, ArrayList<String>>> TRUST_TYPE = new TypeToken<>() {};

    public static boolean isPlayerTrusting(String trustingPlayerUsername, String trustedPlayerUsername)
    {
        if (Files.exists(TRUST_PATH))
        {
            Map<String, ArrayList<String>> gsonTrustingPlayers = readTrustFile();

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
        return false;
    }

    public static ArrayList<String> getTrustedPlayers(String trustingPlayer)
    {
        Map<String, ArrayList<String>> gsonTrustingPlayers = readTrustFile();

        if (gsonTrustingPlayers.containsKey(trustingPlayer))
        {
            return gsonTrustingPlayers.get(trustingPlayer);
        }
        return new ArrayList<>();
    }
}
