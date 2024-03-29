/*
 * Copyright (c) 2023-2024  -  Made by Aeldit
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static fr.aeldit.cyansh.CyanSHCore.*;
import static fr.aeldit.cyansh.config.CyanSHConfig.MAX_HOMES;

public class Homes
{
    public record Home(String name, String dimension, double x, double y, double z, float yaw, float pitch, String date)
    {
    }

    private final ConcurrentHashMap<String, List<Home>> homes = new ConcurrentHashMap<>();
    private final TypeToken<List<Home>> homesType = new TypeToken<>()
    {
    };
    private final List<String> editingFiles = Collections.synchronizedList(new ArrayList<>());
    public static Path HOMES_PATH = Path.of(MOD_PATH + "/homes");

    /**
     * Adds an entry to the map {@code homes} with all the player's homes
     */
    public void addPlayer(String playerKey, List<Home> playerHomes)
    {
        homes.put(playerKey, playerHomes);
        writeHomes(playerKey);
    }

    /**
     * @return {@code true} on success | {@code false} on failure
     */
    public boolean addHome(String playerKey, @NotNull Home home)
    {
        if (!homeExists(playerKey, home.name()))
        {
            if (!homes.containsKey(playerKey))
            {
                homes.put(playerKey, Collections.synchronizedList(new ArrayList<>(Collections.singleton(home))));
            }
            else
            {
                homes.get(playerKey).add(home);
            }
            writeHomes(playerKey);

            return true;
        }
        return false;
    }

    /**
     * @return {@code true} on success | {@code false} on failure
     */
    public boolean removeHome(@NotNull String playerKey, String homeName)
    {
        if (homeExists(playerKey, homeName))
        {
            homes.get(playerKey).remove(getHomeIndex(playerKey, homeName));
            writeHomes(playerKey);

            return true;
        }
        return false;
    }

    /**
     * Removes the key:value entry of the player {@code playerName} if it exists
     *
     * @return {@code true} on success | {@code false} on failure
     */
    public boolean removeAll(String playerKey)
    {
        if (homes.containsKey(playerKey))
        {
            if (!homes.get(playerKey).isEmpty())
            {
                homes.get(playerKey).clear();
                writeHomes(playerKey);

                return true;
            }
        }
        return false;
    }

    /**
     * Renames the home of the player
     *
     * @return {@code true} on success | {@code false} on failure
     */
    public boolean rename(String playerKey, String homeName, String newHomeName)
    {
        if (homeExists(playerKey, homeName))
        {
            Home tmpHome = homes.get(playerKey).get(getHomeIndex(playerKey, homeName));
            // If we try to rename the home with the name of a home that already exists
            if (!addHome(playerKey, new Home(newHomeName,
                    tmpHome.dimension, tmpHome.x, tmpHome.y, tmpHome.z, tmpHome.yaw,
                    tmpHome.pitch, tmpHome.date
            )))
            {
                return false;
            }
            homes.get(playerKey).remove(getHomeIndex(playerKey, homeName));
            writeHomes(playerKey);

            return true;
        }
        return false;
    }

    /**
     * Can be called if and only if the result of {@link Homes#homeExists} is true
     *
     * @return The home with the name {@code homeName}
     */
    public @Nullable Home getPlayerHome(String playerName, String homeName)
    {
        int idx = getHomeIndex(playerName, homeName);
        if (idx != -1)
        {
            return homes.get(playerName).get(idx);
        }
        return null;
    }

    /**
     * Can be called if and only if the result of {@link Homes#isEmpty} is false
     *
     * @return An ArrayList containing all the homes of the player {@code playerName}
     */
    public List<Home> getPlayerHomes(String playerName)
    {
        return homes.get(playerName);
    }

    /**
     * @return An ArrayList containing the names of all the players that have at least 1 home
     */
    public List<String> getPlayersWithHomes(String excludedPlayer)
    {
        List<String> list = new ArrayList<>();
        for (String key : homes.keySet())
        {
            if (!key.split(" ")[1].equals(excludedPlayer))
            {
                list.add(key.split(" ")[1]);
            }
        }
        return list;
    }

    /**
     * @return An ArrayList containing all the homes names of the player {@code playerName}
     */
    public List<String> getHomesNames(String playerKey)
    {
        List<String> names = new ArrayList<>();

        if (homes.containsKey(playerKey))
        {
            homes.get(playerKey).forEach(home -> names.add(home.name));
        }
        return names;
    }

    /**
     * Can be called only if the player receiving the suggestion is trusted by the player {@code playerName}
     * (result of {@link Trusts#isPlayerTrustingFromName})
     *
     * @return An ArrayList containing all the homes names of the player {@code playerName}
     */
    public List<String> getHomesNamesOf(String playerName)
    {
        List<String> names = new ArrayList<>();

        for (String key : homes.keySet())
        {
            if (key.split(" ")[1].equals(playerName))
            {
                homes.get(key).forEach(home -> names.add(home.name));
                break;
            }
        }
        return names;
    }

    /**
     * Returns the index of the home named {@code homeName} belonging
     * to the player whose key is {@code playerKey}
     *
     * @return The index of the given home if it exists | {@code -1} otherwise
     */
    private int getHomeIndex(String playerKey, String homeName)
    {
        for (Home home : homes.get(playerKey))
        {
            if (home.name.equals(homeName))
            {
                return homes.get(playerKey).indexOf(home);
            }
        }
        return -1;
    }

    /**
     * Returns the key associated with the name {@code playerName}
     */
    public String getKeyFromName(String playerName)
    {
        for (String key : homes.keySet())
        {
            if (key.split(" ")[1].equals(playerName))
            {
                return key;
            }
        }
        return "";
    }

    public boolean isEmpty(String playerKey)
    {
        return !homes.containsKey(playerKey) || homes.get(playerKey).isEmpty();
    }

    public boolean isEmptyFromName(String playerName)
    {
        for (String s : homes.keySet())
        {
            if (s.split(" ")[1].equals(playerName))
            {
                return homes.get(s).isEmpty();
            }
        }
        return true;
    }

    /**
     * Returns whether the given player has reached its maximum amount of homes
     * <p>
     * If the player is not found in the list (has no homes), this returns {@code false}
     *
     * @param playerKey The player key (in the form {@code "playerUUID playerName"})
     */
    public boolean maxHomesNotReached(String playerKey)
    {
        return !homes.containsKey(playerKey) || homes.get(playerKey).size() < MAX_HOMES.getValue();
    }

    /**
     * Returns whether the home with the name {@code homeName} exists in the list of the player's homes
     * <p>
     * If the player is not found in the list, this returns {@code false}
     *
     * @param playerKey The player key (in the form {@code "playerUUID playerName"})
     * @param homeName  The name of the home
     */
    public boolean homeExists(String playerKey, String homeName)
    {
        if (homes.containsKey(playerKey))
        {
            for (Home home : homes.get(playerKey))
            {
                if (home.name.equals(homeName))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a home exists for the player named {@code playerName}
     */
    public boolean homeExistsFromName(String playerName, String homeName)
    {
        for (String key : homes.keySet())
        {
            if (key.split(" ")[1].equals(playerName))
            {
                for (Home home : homes.get(key))
                {
                    if (home.name.equals(homeName))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void renameChangedUsernames(String playerKey, String playerUUID, String playerName)
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
                        String[] splitFileName = file.getName().split(" ");
                        String[] splitFileNameOld = file.getName().split("_");

                        if (splitFileName[0].equals(playerUUID)
                                && !splitFileName[1].equals(playerName + ".json")
                                || (splitFileNameOld.length == 2 && splitFileNameOld[0].equals(
                                playerUUID) && !splitFileNameOld[1].equals(playerName + ".json"))
                        )
                        {
                            try
                            {
                                Files.move(
                                        file.toPath(), Path.of(HOMES_PATH + "\\" + playerKey + ".json")
                                                .resolveSibling(playerKey + ".json"));
                                CYANSH_LOGGER.info(
                                        "[CyanSetHome] Rename the file '{}' to '{}' because the player changed its " +
                                                "pseudo",
                                        file.getName(), playerKey + ".json"
                                );
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
                        addPlayer(
                                file.getName().split("\\.")[0],
                                Collections.synchronizedList(new ArrayList<>(gsonReader.fromJson(reader, homesType)))
                        );
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
                            addPlayer(
                                    file.getName().split("\\.")[0], Collections.synchronizedList(
                                            new ArrayList<>(gsonReader.fromJson(reader, homesType))));
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

            if (homes.get(playerKey).isEmpty())
            {
                if (Files.exists(path))
                {
                    Files.delete(path);
                    removeEmptyModDir();
                }
            }
            else
            {
                if (!editingFiles.contains(path.getFileName().toString()))
                {
                    editingFiles.add(path.getFileName().toString());

                    Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                    Writer writer = Files.newBufferedWriter(path);
                    gsonWriter.toJson(homes.get(playerKey), writer);
                    writer.close();

                    editingFiles.remove(path.getFileName().toString());
                }
                else
                {
                    long end = System.currentTimeMillis() + 1000; // 1 s
                    boolean couldWrite = false;

                    while (System.currentTimeMillis() < end)
                    {
                        if (!editingFiles.contains(path.getFileName().toString()))
                        {
                            editingFiles.add(path.getFileName().toString());

                            Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                            Writer writer = Files.newBufferedWriter(path);
                            gsonWriter.toJson(homes.get(playerKey), writer);
                            writer.close();

                            couldWrite = true;
                            editingFiles.remove(path.getFileName().toString());
                            break;
                        }
                    }

                    if (!couldWrite)
                    {
                        CYANSH_LOGGER.info(
                                ("[CyanSetHome] Could not write the file %s because it is already being written (for " +
                                        "more than 1 sec)").formatted(
                                        path.getFileName().toString()));
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
