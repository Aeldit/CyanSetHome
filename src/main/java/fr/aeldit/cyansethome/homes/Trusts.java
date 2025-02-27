package fr.aeldit.cyansethome.homes;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static fr.aeldit.cyansethome.CyanSHCore.*;
import static fr.aeldit.cyansethome.homes.Homes.HOMES_PATH;

public class Trusts
{
    private final ConcurrentHashMap<String, List<String>> trusts = new ConcurrentHashMap<>();
    public final TypeToken<ConcurrentHashMap<String, List<String>>> trustType = new TypeToken<>()
    {
    };
    public static Path TRUST_PATH = Path.of("%s/trusted_players.json".formatted(MOD_PATH));

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
        if (!Files.exists(TRUST_PATH) || trusts.isEmpty())
        {
            return;
        }

        boolean changed = false;
        String prevName = "";

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
                    changed  = true;

                    trusts.get(key).add(playerKey);
                    trusts.get(key).remove(listKey);
                    write();
                }
            }
        }

        if (changed)
        {
            write();
            CYANSH_LOGGER.info(
                    "[CyanSetHome] Updated {}'s pseudo in the trust file, because the player changed " +
                    "its pseudo (previously {})", playerName, prevName
            );
        }
    }

    /**
     * Returns an {@code Arraylist<String>} containing the names of all the players trusting {@code playerKey}
     *
     * @param playerKey The player key (in the form {@code "playerUUID playerName"})
     */
    public ArrayList<String> getTrustingPlayers(String playerKey)
    {
        return trusts.entrySet().stream()
                     .filter(entry -> entry.getValue().contains(playerKey))
                     .map(entry -> entry.getKey().split(" ")[1]).collect(
                        Collectors.toCollection(() -> new ArrayList<>(trusts.size())));
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
            List<String> trustedPlayersKeys = trusts.get(playerKey);
            return trustedPlayersKeys.stream().map(player -> player.split(" ")[1]).collect(
                    Collectors.toCollection(() -> new ArrayList<>(trustedPlayersKeys.size())));
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

    public void readServer()
    {
        if (!Files.exists(TRUST_PATH))
        {
            return;
        }

        try (Reader reader = Files.newBufferedReader(TRUST_PATH))
        {
            trusts.putAll(new Gson().fromJson(reader, trustType));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void readClient()
    {
        TRUST_PATH = Path.of("%s/trusted_players.json".formatted(HOMES_PATH));

        if (!Files.exists(TRUST_PATH))
        {
            return;
        }

        try (Reader reader = Files.newBufferedReader(TRUST_PATH))
        {
            trusts.putAll(new Gson().fromJson(reader, trustType));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void write()
    {
        checkOrCreateHomesDir();

        if (trusts.isEmpty())
        {
            if (Files.exists(TRUST_PATH))
            {
                try
                {
                    Files.delete(TRUST_PATH);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                removeEmptyModDir();
            }
        }
        else
        {
            try (Writer writer = Files.newBufferedWriter(TRUST_PATH))
            {
                new GsonBuilder().setPrettyPrinting().create().toJson(trusts, writer);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
