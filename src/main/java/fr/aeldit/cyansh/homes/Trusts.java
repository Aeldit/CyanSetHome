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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static fr.aeldit.cyansh.util.Utils.HOMES_PATH;
import static fr.aeldit.cyansh.util.Utils.checkOrCreateHomesDir;

public class Trusts
{
    private ConcurrentHashMap<String, ArrayList<String>> trusts;
    private boolean isEditingFile = false;

    public void read()
    {
        File[] listOfFiles = new File(HOMES_PATH.toUri()).listFiles();
        this.trusts = new ConcurrentHashMap<>();

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
                        //addPlayer(file.getName().split("\\.")[0], gsonReader.fromJson(reader, HOMES_TYPE));
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

    private void write(String playerKey)
    {
        checkOrCreateHomesDir();

        try
        {
            Path path = Path.of(HOMES_PATH + "/" + playerKey + ".json");

            if (this.trusts.get(playerKey).isEmpty())
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
                    gsonWriter.toJson(this.trusts.get(playerKey), writer);
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
                            gsonWriter.toJson(this.trusts.get(playerKey), writer);
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
