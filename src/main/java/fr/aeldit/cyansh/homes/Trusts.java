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

package fr.aeldit.cyansh.homes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static fr.aeldit.cyansh.homes.Homes.HOMES_PATH;
import static fr.aeldit.cyansh.util.Utils.*;

public class Trusts
{
    private ConcurrentHashMap<String, List<String>> trusts;
    public final TypeToken<ConcurrentHashMap<String, List<String>>> TRUST_TYPE = new TypeToken<>() {};
    public static Path TRUST_PATH = Path.of(MOD_PATH + "/trusted_players.json");
    private boolean isEditingFile = false;


    public Trusts()
    {
        this.trusts = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, List<String>> getTrusts()
    {
        return this.trusts;
    }

    public void setTrusts(ConcurrentHashMap<String, List<String>> trusts)
    {
        this.trusts = trusts;
    }

    public void trustPlayer(String trustingPlayerKey, String trustedPlayerKey)
    {
        if (!this.trusts.containsKey(trustingPlayerKey))
        {
            this.trusts.put(trustingPlayerKey, Collections.synchronizedList(new ArrayList<>(Collections.singletonList(trustedPlayerKey))));
            write();
        }
        else
        {
            this.trusts.get(trustingPlayerKey).add(trustedPlayerKey);
            write();
        }
    }

    public void untrustPlayer(String trustingPlayerName, String trustedPlayerName)
    {
        for (ConcurrentHashMap.Entry<String, List<String>> entry : this.trusts.entrySet())
        {
            if (entry.getKey().split(" ")[1].equals(trustingPlayerName))
            {
                for (String name : entry.getValue())
                {
                    if (name.split(" ")[1].equals(trustedPlayerName))
                    {
                        this.trusts.get(entry.getKey()).remove(name);

                        if (this.trusts.get(entry.getKey()).isEmpty())
                        {
                            this.trusts.remove(entry.getKey());
                        }

                        write();
                        break;
                    }
                }
                break;
            }
        }
    }

    public void renameChangedUsernames(String playerKey, String playerUUID, String playerName)
    {

        if (Files.exists(TRUST_PATH))
        {
            boolean changed = false;
            String prevName = "";

            if (isNotEmpty())
            {
                for (String key : this.trusts.keySet())
                {
                    // Changes the player username when it is a key
                    if (key.split(" ")[0].equals(playerUUID) && !key.split(" ")[1].equals(playerName))
                    {
                        prevName = key.split(" ")[1];
                        this.trusts.put(playerKey, Collections.synchronizedList(new ArrayList<>(this.trusts.get(key))));
                        this.trusts.remove(key);
                        changed = true;
                    }

                    if (changed)
                    {
                        key = playerKey;
                    }

                    // Changes the player's username when it is in a list of trusted players
                    for (String listKey : this.trusts.get(key))
                    {
                        if (listKey.split(" ")[0].equals(playerUUID) && !listKey.split(" ")[1].equals(playerName))
                        {
                            prevName = listKey.split(" ")[1];
                            changed = true;

                            this.trusts.get(key).add(playerKey);
                            this.trusts.get(key).remove(listKey);
                            write();
                        }
                    }
                }
            }

            if (changed)
            {
                write();
                LOGGER.info("[CyanSetHome] Updated {}'s pseudo in the trust file, because the player changed its pseudo (previously {})", playerName, prevName);
            }
        }
    }

    public ArrayList<String> getTrustingPlayers(String playerKey)
    {
        ArrayList<String> names = new ArrayList<>();

        for (ConcurrentHashMap.Entry<String, List<String>> entry : this.trusts.entrySet())
        {
            if (entry.getValue().contains(playerKey))
            {
                names.add(entry.getKey().split(" ")[1]);
            }
        }
        return names;
    }

    public List<String> getTrustedPlayers(String playerKey)
    {
        if (this.trusts.containsKey(playerKey))
        {
            return this.trusts.get(playerKey);
        }
        return new ArrayList<>();
    }

    public boolean isPlayerTrustingFromName(String trustingPlayerName, String trustedPlayerName)
    {
        for (String playerKey : this.trusts.keySet())
        {
            if (playerKey.split(" ")[1].equals(trustingPlayerName))
            {
                for (String trustedKey : this.trusts.get(playerKey))
                {
                    if (trustedKey.split(" ")[1].equals(trustedPlayerName))
                    {
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    public boolean isNotEmpty()
    {
        return !this.trusts.isEmpty();
    }

    public void readServer()
    {
        if (Files.exists(TRUST_PATH))
        {
            try
            {
                Gson gsonReader = new Gson();
                Reader reader = Files.newBufferedReader(TRUST_PATH);
                this.trusts = gsonReader.fromJson(reader, TRUST_TYPE);
                reader.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            this.trusts = new ConcurrentHashMap<>();
        }
    }

    public void readClient()
    {
        TRUST_PATH = Path.of(HOMES_PATH + "/trusted_players.json");

        if (Files.exists(TRUST_PATH))
        {
            try
            {
                Gson gsonReader = new Gson();
                Reader reader = Files.newBufferedReader(TRUST_PATH);
                this.trusts = gsonReader.fromJson(reader, TRUST_TYPE);
                reader.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            this.trusts = new ConcurrentHashMap<>();
        }
    }

    public void write()
    {
        checkOrCreateHomesDir();

        try
        {
            if (this.trusts.isEmpty())
            {
                if (Files.exists(TRUST_PATH))
                {
                    Files.delete(TRUST_PATH);
                }
            }
            else
            {
                if (!this.isEditingFile)
                {
                    this.isEditingFile = true;

                    Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                    Writer writer = Files.newBufferedWriter(TRUST_PATH);
                    gsonWriter.toJson(this.trusts, writer);
                    writer.close();

                    this.isEditingFile = false;
                }
                else
                {
                    long end = System.currentTimeMillis() + 1000; // 1 s
                    boolean couldWrite = false;

                    while (System.currentTimeMillis() < end)
                    {
                        if (!this.isEditingFile)
                        {
                            this.isEditingFile = true;

                            Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                            Writer writer = Files.newBufferedWriter(TRUST_PATH);
                            gsonWriter.toJson(this.trusts, writer);
                            writer.close();

                            couldWrite = true;
                            this.isEditingFile = false;
                            break;
                        }
                    }

                    if (!couldWrite)
                    {
                        LOGGER.info("[CyanSetHome] Could not write the trusting_players.json file because it is already being written");
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
