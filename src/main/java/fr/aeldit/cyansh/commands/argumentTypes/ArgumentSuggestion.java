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

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.aeldit.cyansh.config.CyanSHMidnightConfig;
import fr.aeldit.cyansh.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static fr.aeldit.cyansh.util.Utils.*;

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
        if (Files.exists(Path.of(homesPath + "\\" + player.getUuidAsString() + "_" + player.getName().getString() + ".properties")))
        {
            try
            {
                Properties properties = new Properties();
                properties.load(new FileInputStream(homesPath + "\\" + player.getUuidAsString() + "_" + player.getName().getString() + ".properties"));
                homes = new ArrayList<>(properties.stringPropertyNames());
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
        List<String> players = new ArrayList<>();
        ServerPlayerEntity player = source.getPlayer();

        if (Files.exists(trustPath))
        {
            try
            {
                Properties properties = new Properties();
                properties.load(new FileInputStream(trustPath.toFile()));

                if (player != null)
                {
                    for (String str : List.of(properties.get(player.getUuidAsString() + "_" + player.getName().getString()).toString().split(" ")))
                    {
                        players.add(str.split("_")[1]);
                    }
                }
            } catch (IOException e)
            {
                throw new RuntimeException(e);
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
        List<String> players = new ArrayList<>();
        ServerPlayerEntity player = source.getPlayer();

        checkOrCreateHomesDir();

        if (player != null)
        {
            try
            {
                Properties properties = new Properties();
                properties.load(new FileInputStream(trustPath.toFile()));
                for (String key : properties.stringPropertyNames())
                {
                    if (properties.get(key).toString().contains(player.getUuidAsString()) || CyanSHMidnightConfig.allowOPHomesOf)
                    {
                        players.add(key.split("_")[1]);
                    }
                }
                players.remove(player.getName().getString());
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return CommandSource.suggestMatching(players, builder);
    }
}
