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
import fr.aeldit.cyansh.config.CyanSHMidnightConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import static fr.aeldit.cyansh.util.Utils.*;

public class Homes
{
    private ConcurrentHashMap<String, ArrayList<Home>> homes;
    private final TypeToken<ArrayList<Home>> HOMES_TYPE = new TypeToken<>() {};
    private String currentArray;
    private boolean isEditingFile = false;


    public Homes()
    {
        read();
    }

    public void addPlayer(String playerKey, ArrayList<Home> playerHomes)
    {
        this.homes.put(playerKey, playerHomes);
        writeHomes(playerKey);
    }

    public void addHome(String playerKey, Home home)
    {
        if (!this.homes.containsKey(playerKey))
        {
            this.currentArray = playerKey;
            this.homes.put(playerKey, new ArrayList<>(Collections.singletonList(home)));
            this.currentArray = null;
            writeHomes(playerKey);
        }
        else
        {
            if (!playerKey.equals(this.currentArray))
            {
                this.currentArray = playerKey;
                this.homes.get(playerKey).add(home);
                this.currentArray = null;
                writeHomes(playerKey);
            }
        }
    }

    /**
     * Can be called if and only if the result of {@link Homes#homeExists} is true
     */
    public void removeHome(@NotNull String playerKey, String homeName)
    {
        if (!playerKey.equals(this.currentArray))
        {
            this.currentArray = playerKey;
            this.homes.get(playerKey).remove(getHomeIndex(playerKey, homeName));
            this.currentArray = null;
            writeHomes(playerKey);
        }
    }

    public boolean removeAll(String playerKey)
    {
        if (this.homes.containsKey(playerKey))
        {
            if (!this.homes.get(playerKey).isEmpty())
            {
                this.homes.get(playerKey).clear();
                writeHomes(playerKey);

                return true;
            }
        }
        return false;
    }

    /**
     * Can be called if and only if the result of {@link Homes#homeExists} is true
     */
    public Home getPlayerHome(String playerKey, String homeName)
    {
        return this.homes.get(playerKey).get(getHomeIndex(playerKey, homeName));
    }

    /**
     * Can be called if and only if the result of {@link Homes#isEmpty} is false
     */
    public ArrayList<Home> getPlayerHomes(String playerKey)
    {
        return this.homes.get(playerKey);
    }

    public boolean isEmpty(String playerKey)
    {
        if (this.homes.containsKey(playerKey))
        {
            return this.homes.get(playerKey).isEmpty();
        }
        return false;
    }

    public boolean maxHomesReached(String playerKey)
    {
        if (this.homes.containsKey(playerKey))
        {
            LOGGER.info(String.valueOf(this.homes.get(playerKey)));
            return this.homes.get(playerKey).size() >= CyanSHMidnightConfig.maxHomes;
        }
        return false;
    }

    public boolean homeExists(String playerKey, String homeName)
    {
        if (this.homes.containsKey(playerKey))
        {
            for (Home homeIterator : this.homes.get(playerKey))
            {
                if (homeIterator.name().equals(homeName))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Can be called if and only if the result of {@link Homes#homeExists} is true
     */
    public int getHomeIndex(String playerKey, String homeName)
    {
        for (Home home : this.homes.get(playerKey))
        {
            if (home.name().equals(homeName))
            {
                return this.homes.get(playerKey).indexOf(home);
            }
        }
        return -1;
    }

    public ArrayList<String> getHomesNames(String playerKey)
    {
        ArrayList<String> names = new ArrayList<>();

        if (this.homes.containsKey(playerKey))
        {
            this.homes.get(playerKey).forEach(home -> names.add(home.name()));
        }

        return names;
    }

    public void read()
    {
        File[] listOfFiles = new File(HOMES_PATH.toUri()).listFiles();
        this.homes = new ConcurrentHashMap<>();

        if (listOfFiles != null)
        {
            for (File file : listOfFiles)
            {
                if (file.isFile())
                {
                    try
                    {
                        Gson gsonReader = new Gson();
                        Reader reader = Files.newBufferedReader(file.toPath());
                        addPlayer(file.getName().split("\\.")[0], gsonReader.fromJson(reader, HOMES_TYPE));
                        reader.close();
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void writeHomes(String playerKey)
    {
        checkOrCreateHomesDir();

        try
        {
            Path path = Path.of(HOMES_PATH + "/" + playerKey + ".json");

            if (this.homes.get(playerKey).isEmpty())
            {
                if (Files.exists(path))
                {
                    Files.delete(path);
                }
            }
            else
            {
                if (!this.isEditingFile)
                {
                    this.isEditingFile = true;

                    Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                    Writer writer = Files.newBufferedWriter(path);
                    gsonWriter.toJson(this.homes.get(playerKey), writer);
                    writer.close();

                    this.isEditingFile = false;
                }
                else
                {
                    long end = System.currentTimeMillis() + 1000 * 1000; // 1 s

                    while (System.currentTimeMillis() < end)
                    {
                        if (!this.isEditingFile)
                        {
                            this.isEditingFile = true;

                            Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                            Writer writer = Files.newBufferedWriter(path);
                            gsonWriter.toJson(this.homes.get(playerKey), writer);
                            writer.close();

                            this.isEditingFile = false;
                        }
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