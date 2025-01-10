package fr.aeldit.cyansethome.homes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static fr.aeldit.cyansethome.CyanSHCore.*;
import static fr.aeldit.cyansethome.config.CyanLibConfigImpl.MAX_HOMES;

public class Homes
{
    private final ConcurrentHashMap<String, List<Home>> homes = new ConcurrentHashMap<>();
    public static Path HOMES_PATH = Path.of("%s/homes".formatted(MOD_PATH));

    /**
     * Adds an entry to the map {@code homes} with all the player's homes
     */
    public void addPlayerHomes(String playerKey, List<Home> playerHomes)
    {
        homes.put(playerKey, playerHomes);
        writeHomes(playerKey);
    }

    /**
     * @return {@code true} on success | {@code false} on failure
     */
    public boolean addHome(String playerKey, @NotNull Home home)
    {
        if (getHome(playerKey, home.name()) == null)
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
        Home home = getHome(playerKey, homeName);
        if (home != null)
        {
            homes.get(playerKey).remove(home);

            if (homes.get(playerKey).isEmpty())
            {
                homes.remove(playerKey);
            }
            writeHomes(playerKey);

            return true;
        }
        return false;
    }

    /**
     * Removes the key:value entry of the player {@code playerName} if it exists
     *
     * @return {@code true} if homes were removed | {@code false} otherwise
     */
    public boolean removeAll(String playerKey)
    {
        if (homes.containsKey(playerKey))
        {
            if (!homes.get(playerKey).isEmpty())
            {
                homes.get(playerKey).clear();
                homes.remove(playerKey);
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
        Home home = getHome(playerKey, homeName);
        if (home != null)
        {
            homes.get(playerKey).remove(home);
            homes.get(playerKey).add(home.getRenamed(newHomeName));
            writeHomes(playerKey);

            return true;
        }
        return false;
    }

    /**
     * @return An ArrayList containing all the homes of the player {@code playerKey}, {@code null} if the player doesn't
     * have homes
     */
    public @Nullable List<Home> getPlayerHomes(String playerKey)
    {
        return (!homes.containsKey(playerKey) || homes.get(playerKey).isEmpty()) ? null : homes.get(playerKey);
    }

    /**
     * @return An ArrayList containing the names of all the players that have at least 1 home
     */
    public List<String> getPlayersWithHomes(String excludedPlayer)
    {
        return homes.keySet().stream().filter(key -> !key.split(" ")[1].equals(excludedPlayer))
                    .map(key -> key.split(" ")[1])
                    .collect(Collectors.toCollection(() -> new ArrayList<>(homes.size())));
    }

    /**
     * @return An ArrayList containing all the homes names of the player {@code playerName}
     */
    public @Nullable List<String> getHomesNames(String playerKey)
    {
        if (homes.containsKey(playerKey))
        {
            List<String> names = new ArrayList<>(homes.get(playerKey).size());
            homes.get(playerKey).forEach(home -> names.add(home.name()));
            return names;
        }
        return null;
    }

    /**
     * Can be called only if the player receiving the suggestion is trusted by the player {@code playerName}
     * (result of {@link Trusts#isPlayerTrustingFromName})
     *
     * @return An ArrayList containing all the homes names of the player {@code playerName}
     */
    public @Nullable List<String> getHomesNamesOf(String playerName)
    {
        for (String key : homes.keySet())
        {
            if (key.split(" ")[1].equals(playerName))
            {
                List<String> names = new ArrayList<>(homes.get(key).size());
                homes.get(key).forEach(home -> names.add(home.name()));
                return names;
            }
        }
        return null;
    }

    /**
     * Returns the key associated with the name {@code playerName}
     */
    public @Nullable String getKeyFromName(String playerName)
    {
        return homes.keySet().stream().filter(key -> key.split(" ")[1].equals(playerName)).findFirst().orElse(null);
    }

    /**
     * Returns whether the given player has reached its maximum amount of homes
     * <p>
     * If the player is not found in the list (has no homes), this returns {@code false}
     *
     * @param playerKey The player key (in the form {@code "playerUUID playerName"})
     */
    public boolean maxHomesReached(String playerKey)
    {
        return homes.containsKey(playerKey) && homes.get(playerKey).size() >= MAX_HOMES.getValue();
    }

    /**
     * Returns the index of the home named {@code homeName} belonging
     * to the player whose key is {@code playerKey}
     *
     * @return The index of the given home if it exists | {@code -1} otherwise
     */
    public @Nullable Home getHome(String playerKey, String homeName)
    {
        if (homes.containsKey(playerKey))
        {
            return homes.get(playerKey).stream().filter(home -> home.name().equals(homeName)).findFirst().orElse(null);
        }
        return null;
    }

    /**
     * Renames the username for each UUID if the username currently associated with the UUID is not the same in the file
     *
     * @param playerUUID The player's UUID
     * @param playerName The player's username
     */
    public void renameChangedUsernames(String playerUUID, String playerName)
    {
        if (!Files.exists(HOMES_PATH))
        {
            return;
        }

        File[] listOfFiles = new File(HOMES_PATH.toUri()).listFiles();
        if (listOfFiles == null)
        {
            return;
        }

        String playerKey = "%s %s".formatted(playerUUID, playerName);
        for (File file : listOfFiles)
        {
            if (file.isFile())
            {
                String[] splitFileName = file.getName().split(" ");
                String[] splitFileNameOld = file.getName().split("_");

                if (splitFileName[0].equals(playerUUID)
                    && !splitFileName[1].equals("%s.json".formatted(playerName))
                    || (
                            splitFileNameOld.length == 2 && splitFileNameOld[0].equals(
                                    playerUUID) && !splitFileNameOld[1].equals("%s.json".formatted(playerName))
                    )
                )
                {
                    try
                    {
                        Files.move(
                                file.toPath(), Path.of("%s/%s.json".formatted(HOMES_PATH, playerKey))
                                                   .resolveSibling("%s.json".formatted(playerKey))
                        );
                        CYANSH_LOGGER.info(
                                "[CyanSetHome] Rename the file '{}' to '{}' because the player changed its pseudo",
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

    /**
     * Iterates over each file in the folder of the single-player world folder give as argument
     * (minecraft/config/cyansethome/homes)
     * <p>
     * Used when running as a SERVER
     */
    public void readServer()
    {
        File[] listOfFiles = new File(HOMES_PATH.toUri()).listFiles();

        if (listOfFiles == null)
        {
            return;
        }

        Gson gson = new Gson();
        Arrays.stream(listOfFiles).filter(File::isFile).forEach(file -> {
            try (Reader reader = Files.newBufferedReader(file.toPath()))
            {
                addPlayerHomes(
                        file.getName().split("\\.")[0],
                        Collections.synchronizedList(List.of(gson.fromJson(reader, Home[].class)))
                );
            }
            catch (IOException e)
            {
                CYANSH_LOGGER.error("Could not open the file '{}' to read homes (readServer)", file.getPath());
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Iterates over each file in the folder of the single-player world folder give as argument
     * <p>
     * Used when running as a CLIENT
     *
     * @param saveName: The name of the folder containing the world the player is in
     *                  (minecraft/config/cyansethome/save_name)
     */
    public void readClient(String saveName)
    {
        HOMES_PATH = Path.of("%s/%s".formatted(MOD_PATH, saveName));
        checkOrCreateHomesDir();
        File[] listOfFiles = new File(HOMES_PATH.toUri()).listFiles();

        if (listOfFiles == null)
        {
            return;
        }

        Gson gson = new Gson();
        Arrays.stream(listOfFiles).filter(File::isFile).filter(file -> !file.getName().equals("trusted_players.json"))
              .forEach(file -> {
                  try (Reader reader = Files.newBufferedReader(file.toPath()))
                  {
                      addPlayerHomes(
                              file.getName().split("\\.")[0],
                              Collections.synchronizedList(List.of(gson.fromJson(reader, Home[].class)))
                      );
                  }
                  catch (IOException e)
                  {
                      CYANSH_LOGGER.error("Could not open the file '{}' to read homes (readClient)", file.getPath());
                      throw new RuntimeException(e);
                  }
              });
    }

    private void writeHomes(String playerKey)
    {
        checkOrCreateHomesDir();

        Path path = Path.of("%s/%s.json".formatted(HOMES_PATH, playerKey));

        // Remove the player from the map as it doesn't have any home
        if (!homes.containsKey(playerKey) || (homes.containsKey(playerKey) && homes.get(playerKey).isEmpty()))
        {
            if (Files.exists(path))
            {
                try
                {
                    Files.delete(path);
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
            try (Writer writer = Files.newBufferedWriter(path))
            {
                new GsonBuilder().setPrettyPrinting().create().toJson(homes.get(playerKey), writer);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
