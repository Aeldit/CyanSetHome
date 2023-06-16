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
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import static fr.aeldit.cyansh.util.HomeUtils.TRUST_PATH;
import static fr.aeldit.cyansh.util.HomeUtils.TRUST_TYPE;
import static fr.aeldit.cyansh.util.Utils.MOD_PATH;

public class GsonUtils
{
    public static Map<String, ArrayList<String>> readTrustFile()
    {
        try
        {
            Gson gsonReader = new Gson();
            Reader reader = Files.newBufferedReader(TRUST_PATH);
            Map<String, ArrayList<String>> trustedPlayers = gsonReader.fromJson(reader, TRUST_TYPE);
            reader.close();

            return trustedPlayers;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void writeGson(Path filePath, Object content)
    {
        if (Files.exists(filePath))
        {
            try
            {
                Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                Writer writer = Files.newBufferedWriter(filePath);
                gsonWriter.toJson(content, writer);
                writer.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void writeGsonOrDeleteFile(Path filePath, Object content)
    {
        if (Files.exists(filePath))
        {
            try
            {
                if ((content instanceof ArrayList<?> && ((ArrayList<?>) content).isEmpty()) || (content instanceof Map<?, ?> && ((Map<?, ?>) content).isEmpty()))
                {
                    Files.delete(filePath);
                }
                else
                {
                    Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                    Writer writer = Files.newBufferedWriter(filePath);
                    gsonWriter.toJson(content, writer);
                    writer.close();
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void removePropertiesFiles()
    {
        if (Files.exists(MOD_PATH))
        {
            File[] listOfFiles = new File(MOD_PATH.toUri()).listFiles();

            if (listOfFiles != null)
            {
                for (File file : listOfFiles)
                {
                    if (file.isFile())
                    {

                    }
                }
            }
        }
    }
}
