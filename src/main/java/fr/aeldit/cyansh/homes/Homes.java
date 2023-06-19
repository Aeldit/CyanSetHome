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
    private final ArrayList<String> currentArrays = new ArrayList<>();
    private final ArrayList<String> editingFiles = new ArrayList<>();


    public Homes()
    {
        this.homes = new ConcurrentHashMap<>();
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
            this.currentArrays.add(playerKey);
            this.homes.put(playerKey, new ArrayList<>(Collections.singletonList(home)));
            this.currentArrays.remove(playerKey);
            writeHomes(playerKey);
        }
        else
        {
            if (!this.currentArrays.contains(playerKey))
            {
                this.currentArrays.add(playerKey);
                this.homes.get(playerKey).add(home);
                this.currentArrays.remove(playerKey);
                writeHomes(playerKey);
            }
        }
    }

    /**
     * Can be called if and only if the result of {@link Homes#homeExists} is true
     */
    public void removeHome(@NotNull String playerKey, String homeName)
    {
        if (!this.currentArrays.contains(playerKey))
        {
            this.currentArrays.add(playerKey);
            this.homes.get(playerKey).remove(getHomeIndex(playerKey, homeName));
            this.currentArrays.remove(playerKey);
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
    public Home getPlayerHome(String playerName, String homeName)
    {
        return this.homes.get(playerName).get(getHomeIndex(playerName, homeName));
    }

    /**
     * Can be called if and only if the result of {@link Homes#isEmpty} is false
     */
    public ArrayList<Home> getPlayerHomes(String playerName)
    {
        return this.homes.get(playerName);
    }

    public ArrayList<String> getPlayersWithHomes(String excludedPlayer)
    {
        ArrayList<String> names = new ArrayList<>();

        for (String key : this.homes.keySet())
        {
            if (!key.split(" ")[1].equals(excludedPlayer))
            {
                names.add(key.split(" ")[1]);
            }
        }
        return names;
    }

    public boolean isEmpty(String playerKey)
    {
        if (this.homes.containsKey(playerKey))
        {
            return this.homes.get(playerKey).isEmpty();
        }
        return true;
    }

    public boolean isEmptyFromName(String playerName)
    {
        for (String playerKey : this.homes.keySet())
        {
            if (playerKey.split(" ")[1].equals(playerName))
            {
                return this.homes.get(playerKey).isEmpty();
            }
        }
        return true;
    }

    public boolean maxHomesReached(String playerKey)
    {
        if (this.homes.containsKey(playerKey))
        {
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


    public boolean homeExistsFromName(String playerName, String homeName)
    {
        for (String key : this.homes.keySet())
        {
            if (key.split(" ")[1].equals(playerName))
            {
                for (Home home : this.homes.get(key))
                {
                    if (home.name().equals(homeName))
                    {
                        return true;
                    }
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

    /**
     * Can be called if an only if the player receiving the suggestion is trusted by the player {@code playerName}
     * (result of {@link Trusts#isPlayerTrustingFromName})
     */
    public ArrayList<String> getHomesNamesOf(String playerName)
    {
        ArrayList<String> names = new ArrayList<>();

        for (String key : this.homes.keySet())
        {
            if (key.split(" ")[1].equals(playerName))
            {
                this.homes.get(key).forEach(home -> names.add(home.name()));
            }
        }

        return names;
    }

    public String getKeyFromName(String playerName)
    {
        for (String key : this.homes.keySet())
        {
            if (key.split(" ")[1].equals(playerName))
            {
                return key;
            }
        }
        return null;
    }

    public void renameIfUsernameChanged(String playerKey, String playerUUID, String playerName)
    {
        if (Files.exists(HOMES_PATH))
        {
            File[] listOfFiles = new File(HOMES_PATH.toUri()).listFiles();

            if (listOfFiles != null)
            {
                for (File file : listOfFiles)
                {
                    if (file.isFile())
                    {
                        String[] splitedFileName = file.getName().split(" ");

                        if (splitedFileName[0].equals(playerUUID) && !splitedFileName[1].equals(playerName + ".json"))
                        {
                            try
                            {
                                Files.move(file.toPath(), Path.of(HOMES_PATH + "\\" + playerKey + ".json").resolveSibling(playerKey + ".json"));
                                LOGGER.info("[CyanSetHome] Rename the file '{}' to '{}' because the player changed its pseudo", file.getName(), playerKey + ".json");
                            }
                            catch (IOException e)
                            {
                                throw new RuntimeException(e);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Directory : minecraft/config/cyansh/homes
     */
    public void readServer()
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


    /**
     * Directory (client) : minecraft/config/cyansh/save_name
     */
    public void readClient(String saveName)
    {
        this.homes = new ConcurrentHashMap<>();
        HOMES_PATH = Path.of(MOD_PATH + "/" + saveName);
        checkOrCreateHomesDir();
        File[] listOfFiles = new File(HOMES_PATH.toUri()).listFiles();

        if (listOfFiles != null)
        {
            for (File file : listOfFiles)
            {
                if (file.isFile())
                {
                    if (!file.getName().equals("trusted_players.json"))
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
                if (!this.editingFiles.contains(path.getFileName().toString()))
                {
                    this.editingFiles.add(path.getFileName().toString());

                    Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                    Writer writer = Files.newBufferedWriter(path);
                    gsonWriter.toJson(this.homes.get(playerKey), writer);
                    writer.close();

                    this.editingFiles.remove(path.getFileName().toString());
                }
                else
                {
                    long end = System.currentTimeMillis() + 1000; // 1 s
                    boolean couldWrite = false;

                    while (System.currentTimeMillis() < end)
                    {
                        if (!this.editingFiles.contains(path.getFileName().toString()))
                        {
                            this.editingFiles.add(path.getFileName().toString());

                            Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                            Writer writer = Files.newBufferedWriter(path);
                            gsonWriter.toJson(this.homes.get(playerKey), writer);
                            writer.close();

                            couldWrite = true;
                            this.editingFiles.remove(path.getFileName().toString());
                        }
                    }

                    if (!couldWrite)
                    {
                        LOGGER.info("[CyanSetHome] Could not write the file %s because it is already beeing written".formatted(path.getFileName().toString()));
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
