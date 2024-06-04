package fr.aeldit.cyansh.homes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static fr.aeldit.cyansh.CyanSHCore.*;
import static fr.aeldit.cyansh.homes.Homes.HOMES_PATH;

public class Trusts
{
    private final ConcurrentHashMap<String, List<String>> trusts = new ConcurrentHashMap<>();
    public final TypeToken<ConcurrentHashMap<String, List<String>>> trustType = new TypeToken<>()
    {
    };
    private boolean isEditingFile = false;
    public static Path TRUST_PATH = Path.of(MOD_PATH + "/trusted_players.json");

    /**
     * Adds {@code trustedPlayerKey} in the list of trusted players of {@code trustingPlayerKey}
     * and calls the {@link #write()} function
     * <p>
     * Can only be called if the result of {@link Trusts#isPlayerTrustingFromName} is {@code false}
     *
     * @param trustingPlayerKey The player running the command
     * @param trustedPlayerKey  The player that will be added to the trusted players list
     */
    public void trustPlayer(String trustingPlayerKey, String trustedPlayerKey)
    {
        if (!trusts.containsKey(trustingPlayerKey))
        {
            trusts.put(
                    trustingPlayerKey,
                    Collections.synchronizedList(new ArrayList<>(Collections.singletonList(trustedPlayerKey)))
            );
        }
        else
        {
            trusts.get(trustingPlayerKey).add(trustedPlayerKey);
        }
        write();
    }

    /**
     * Removes {@code trustedPlayerName} from the list of trusted players of {@code trustingPlayerName}
     * and calls the {@link #write()} function
     *
     * @param trustingPlayerName The player running the command
     * @param trustedPlayerName  The player that will be removed to the trusted players list
     */
    public void untrustPlayer(String trustingPlayerName, String trustedPlayerName)
    {
        for (ConcurrentHashMap.Entry<String, List<String>> entry : trusts.entrySet())
        {
            if (entry.getKey().split(" ")[1].equals(trustingPlayerName))
            {
                for (String name : entry.getValue())
                {
                    if (name.split(" ")[1].equals(trustedPlayerName))
                    {
                        trusts.get(entry.getKey()).remove(name);

                        if (trusts.get(entry.getKey()).isEmpty())
                        {
                            trusts.remove(entry.getKey());
                        }

                        write();
                        break;
                    }
                }
                break;
            }
        }
    }

    public void renameChangedUsernames(String playerUUID, String playerName)
    {
        if (Files.exists(TRUST_PATH))
        {
            boolean changed = false;
            String prevName = "";

            if (isNotEmpty())
            {
                String playerKey = "%s %s".formatted(playerUUID, playerName);
                for (String key : trusts.keySet())
                {
                    // Changes the player username when it is a key
                    if (key.split(" ")[0].equals(playerUUID) && !key.split(" ")[1].equals(playerName))
                    {
                        prevName = key.split(" ")[1];
                        trusts.put(playerKey, Collections.synchronizedList(new ArrayList<>(trusts.get(key))));
                        trusts.remove(key);
                        changed = true;
                    }

                    if (changed)
                    {
                        key = playerKey;
                    }

                    // Changes the player's username when it is in a list of trusted players
                    for (String listKey : trusts.get(key))
                    {
                        if (listKey.split(" ")[0].equals(playerUUID) && !listKey.split(" ")[1].equals(playerName))
                        {
                            prevName = listKey.split(" ")[1];
                            changed = true;

                            trusts.get(key).add(playerKey);
                            trusts.get(key).remove(listKey);
                            write();
                        }
                    }
                }
            }

            if (changed)
            {
                write();
                CYANSH_LOGGER.info("[CyanSetHome] Updated {}'s pseudo in the trust file, because the player changed " +
                        "its pseudo (previously {})", playerName, prevName);
            }
        }
    }

    /**
     * Returns an {@code Arraylist<String>} containing the names of all the players trusting {@code playerKey}
     *
     * @param playerKey The player key (in the form {@code "playerUUID playerName"})
     */
    public ArrayList<String> getTrustingPlayers(String playerKey)
    {
        ArrayList<String> trustingPlayers = new ArrayList<>(trusts.entrySet().size());
        for (Map.Entry<String, List<String>> entry : trusts.entrySet())
        {
            if (entry.getValue().contains(playerKey))
            {
                trustingPlayers.add(entry.getKey().split(" ")[1]);
            }
        }
        return trustingPlayers;
    }

    /**
     * Returns a {@code List<String>} containing the names of all the players {@code TRUSTED} by {@code playerKey}
     *
     * @param playerKey The player key (in the form {@code "playerUUID playerName"})
     */
    public @Nullable List<String> getTrustedPlayers(String playerKey)
    {
        if (trusts.containsKey(playerKey))
        {
            return trusts.get(playerKey);
        }
        return null;
    }

    /**
     * Returns whether the player {@code trustingPlayerName} trusts the player {@code trustedPlayerName}
     *
     * @param trustingPlayerName The trusting player's username
     * @param trustedPlayerName  The trusted player's username
     */
    public boolean isPlayerTrustingFromName(String trustingPlayerName, String trustedPlayerName)
    {
        for (String playerKey : trusts.keySet())
        {
            if (playerKey.split(" ")[1].equals(trustingPlayerName))
            {
                for (String trustedKey : trusts.get(playerKey))
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
        return !trusts.isEmpty();
    }

    public void readServer()
    {
        if (Files.exists(TRUST_PATH))
        {
            try
            {
                Gson gsonReader = new Gson();
                Reader reader = Files.newBufferedReader(TRUST_PATH);
                trusts.putAll(gsonReader.fromJson(reader, trustType));
                reader.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
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
                trusts.putAll(gsonReader.fromJson(reader, trustType));
                reader.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public void write()
    {
        checkOrCreateHomesDir();

        try
        {
            if (trusts.isEmpty())
            {
                if (Files.exists(TRUST_PATH))
                {
                    Files.delete(TRUST_PATH);
                    removeEmptyModDir();
                }
            }
            else
            {
                if (!isEditingFile)
                {
                    isEditingFile = true;

                    Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                    Writer writer = Files.newBufferedWriter(TRUST_PATH);
                    gsonWriter.toJson(trusts, writer);
                    writer.close();

                    isEditingFile = false;
                }
                else
                {
                    long end = System.currentTimeMillis() + 1000; // 1 s
                    boolean couldWrite = false;

                    while (System.currentTimeMillis() < end)
                    {
                        if (!isEditingFile)
                        {
                            isEditingFile = true;

                            Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                            Writer writer = Files.newBufferedWriter(TRUST_PATH);
                            gsonWriter.toJson(trusts, writer);
                            writer.close();

                            couldWrite = true;
                            isEditingFile = false;
                            break;
                        }
                    }

                    if (!couldWrite)
                    {
                        CYANSH_LOGGER.info("[CyanSetHome] Could not write the trusting_players.json file because it " +
                                "is already being written");
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
