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

package fr.aeldit.cyansh.commands.argumentTypes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.aeldit.cyansh.util.Home;
import fr.aeldit.cyansh.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static fr.aeldit.cyansh.util.Utils.homesPath;
import static fr.aeldit.cyansh.util.Utils.trustPath;

public final class ArgumentSuggestion
{
    /**
     * Called for the command {@code /cyansh config <optionName>}
     *
     * @return a suggestion with the available options
     */
    public static CompletableFuture<Suggestions> getOptions(@NotNull SuggestionsBuilder builder)
    {
        List<String> options = new ArrayList<>();
        options.addAll(Utils.getOptionsList().get("booleans"));
        options.addAll(Utils.getOptionsList().get("integers"));
        return CommandSource.suggestMatching(options, builder);
    }

    /**
     * Called for the command {@code /cyansh config optionName [integer]}
     *
     * @return a suggestion with all the available integers for the configurations
     */
    public static CompletableFuture<Suggestions> getInts(@NotNull SuggestionsBuilder builder)
    {
        List<String> ints = new ArrayList<>();
        ints.add("0");
        ints.add("1");
        ints.add("2");
        ints.add("3");
        ints.add("4");
        return CommandSource.suggestMatching(ints, builder);
    }

    /**
     * Called for the command {@code /gethomes}
     *
     * @return A suggestion with all the player's homes
     */
    public static CompletableFuture<Suggestions> getHomes(@NotNull SuggestionsBuilder builder, @NotNull ServerPlayerEntity player)
    {
        List<String> homes = new ArrayList<>();
        if (Files.exists(Path.of(homesPath + "\\" + player.getUuidAsString() + "_" + player.getName().getString() + ".json")))
        {
            try
            {
                Gson gson = new Gson();
                Reader reader = Files.newBufferedReader(Path.of(homesPath + "\\" + player.getUuidAsString() + "_" + player.getName().getString() + ".json"));
                Home[] gsonHomes = gson.fromJson(reader, Home[].class);
                for (Home home : gsonHomes)
                {
                    homes.add(home.name());
                }

                reader.close();
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return CommandSource.suggestMatching(homes, builder);
    }

    /**
     * Called for the command {@code /hometrust}
     *
     * @return A suggestion with all the online players
     */
    public static CompletableFuture<Suggestions> getOnlinePlayersName(@NotNull SuggestionsBuilder builder, @NotNull ServerCommandSource source)
    {
        List<String> players = new ArrayList<>();

        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList())
        {
            players.add(player.getName().getString());
        }

        players.remove(Objects.requireNonNull(source.getPlayer()).getName().getString());

        return CommandSource.suggestMatching(players, builder);
    }

    /**
     * Called for the command {@code /homeuntrust}
     *
     * @return A suggestion with all the trusted players
     */
    public static CompletableFuture<Suggestions> getTrustedPlayersName(@NotNull SuggestionsBuilder builder, @NotNull ServerCommandSource source)
    {
        ServerPlayerEntity player = source.getPlayer();
        List<String> players = new ArrayList<>();

        if (Files.exists(trustPath))
        {
            if (player != null)
            {
                try
                {
                    Gson gsonReader = new Gson();
                    Reader reader = Files.newBufferedReader(trustPath);
                    Type mapType = new TypeToken<Map<String, ArrayList<String>>>() {}.getType();
                    Map<String, ArrayList<String>> gsonTrustingPlayers = gsonReader.fromJson(reader, mapType);
                    reader.close();

                    // TODO->Fix "Cannot invoke "java.util.ArrayList.iterator()" because "trustedPlayer" is null"
                    ArrayList<String> trustedPlayer = gsonTrustingPlayers.get(player.getUuidAsString() + "_" + player.getName().getString());
                    for (String str : trustedPlayer)
                    {
                        players.add(str.split("_")[1]);
                    }
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return CommandSource.suggestMatching(players, builder);
    }

    /**
     * Called for the homeOf commands
     *
     * @return A suggestion with all the trusting players
     */
    public static CompletableFuture<Suggestions> getTrustingPlayersName(@NotNull SuggestionsBuilder builder, @NotNull ServerCommandSource source)
    {
        ServerPlayerEntity player = source.getPlayer();
        List<String> players = new ArrayList<>();

        if (Files.exists(trustPath))
        {
            if (player != null)
            {
                try
                {
                    Gson gsonReader = new Gson();
                    Reader reader = Files.newBufferedReader(trustPath);
                    Type mapType = new TypeToken<Map<String, ArrayList<String>>>() {}.getType();
                    Map<String, ArrayList<String>> gsonTrustingPlayers = gsonReader.fromJson(reader, mapType);
                    reader.close();

                    for (Map.Entry<String, ArrayList<String>> entry : gsonTrustingPlayers.entrySet())
                    {
                        for (String value : entry.getValue())
                        {
                            if (value.contains(player.getUuidAsString()))
                            {
                                players.add(entry.getKey().split("_")[1]);
                            }
                        }
                    }
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return CommandSource.suggestMatching(players, builder);
    }
}
