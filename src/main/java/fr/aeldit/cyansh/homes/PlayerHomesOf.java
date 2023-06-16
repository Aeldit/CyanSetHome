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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static fr.aeldit.cyansh.util.Utils.MOD_PATH;
import static fr.aeldit.cyansh.util.Utils.checkOrCreateHomesDir;

public class PlayerHomesOf
{
    private String playerName;
    private Path currentPath;
    private ArrayList<Home> playerHomes;
    private TypeToken<ArrayList<Home>> HOMES_TYPE = new TypeToken<>() {};


    public PlayerHomesOf(String playerName)
    {
        this.playerName = playerName;
        read();
    }

    public void destroy()
    {
        try
        {
            this.playerName = null;
            this.currentPath = null;
            this.playerHomes = null;
            this.HOMES_TYPE = null;
            System.gc();
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

    public void add(Home home)
    {
        this.playerHomes.add(home);
        write();
    }

    public void remove(String homeName)
    {
        this.playerHomes.remove(getHomeIndex(homeName));
        write();
    }

    public boolean removeAll()
    {
        if (!this.playerHomes.isEmpty())
        {
            this.playerHomes.clear();
            write();

            return true;
        }
        return false;
    }

    public boolean maxHomesReached()
    {
        return this.playerHomes.size() >= CyanSHMidnightConfig.maxHomes;
    }

    public boolean isEmpty()
    {
        return this.playerHomes.isEmpty();
    }

    public Home getPlayerHome(String homeName)
    {
        return this.playerHomes.get(getHomeIndex(homeName));
    }

    public ArrayList<Home> getPlayerHomes()
    {
        return this.playerHomes;
    }

    public ArrayList<String> getHomesNames()
    {
        ArrayList<String> homesNames = new ArrayList<>();
        this.playerHomes.forEach(location -> homesNames.add(location.name()));

        return homesNames;
    }

    public int getHomeIndex(String homeName)
    {
        for (Home home : this.playerHomes)
        {
            if (home.name().equals(homeName))
            {
                return this.playerHomes.indexOf(home);
            }
        }
        return -1;
    }

    public boolean homeExists(String homeName)
    {
        for (Home home : this.playerHomes)
        {
            if (home.name().equals(homeName))
            {
                return true;
            }
        }
        return false;
    }

    public void read()
    {
        File[] listOfFiles = new File(MOD_PATH.toUri()).listFiles();
        this.playerHomes = new ArrayList<>();

        if (listOfFiles != null)
        {
            for (File file : listOfFiles)
            {
                if (file.isFile())
                {
                    if (file.getName().split("_")[1].split("\\.")[0].equals(this.playerName))
                    {
                        this.currentPath = file.toPath();

                        try
                        {
                            Gson gsonReader = new Gson();
                            Reader reader = Files.newBufferedReader(file.toPath());
                            this.playerHomes = gsonReader.fromJson(reader, HOMES_TYPE);
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
    }

    private void write()
    {
        checkOrCreateHomesDir();

        try
        {
            if (this.playerHomes.isEmpty())
            {
                if (Files.exists(this.currentPath))
                {
                    Files.delete(this.currentPath);
                }
            }
            else
            {
                Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                Writer writer = Files.newBufferedWriter(this.currentPath);
                gsonWriter.toJson(this.playerHomes, writer);
                writer.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
