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
import fr.aeldit.cyansh.homes.PlayerHomes;
import fr.aeldit.cyansh.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static fr.aeldit.cyansh.util.GsonUtils.readTrustFile;
import static fr.aeldit.cyansh.util.HomeUtils.TRUST_PATH;
import static fr.aeldit.cyansh.util.HomeUtils.getTrustedPlayers;
import static fr.aeldit.cyansh.util.Utils.HomesObj;

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
     * Called for the command {@code /gethomes} or the suggestions of the {@code home} commands
     *
     * @return A suggestion with all the player's homes
     */
    public static CompletableFuture<Suggestions> getHomes(@NotNull SuggestionsBuilder builder, @NotNull ServerPlayerEntity player)
    {
        return CommandSource.suggestMatching(HomesObj.getHomesNames(player.getUuidAsString() + "_" + player.getName().getString()), builder);
    }

    /**
     * Called for the command {@code /gethomesof} or the suggestions of the {@code homeOf} commands
     *
     * @return A suggestion with all the trusting player's homes
     */
    public static CompletableFuture<Suggestions> getHomesOf(@NotNull SuggestionsBuilder builder, @NotNull ServerPlayerEntity player, @NotNull String trustingPlayer)
    {
        PlayerHomes PlayerHomesObj = new PlayerHomes(trustingPlayer);
        return CommandSource.suggestMatching(PlayerHomesObj.getHomesNames(), builder);
    }

    /**
     * Called for the command {@code /hometrust}
     *
     * @return A suggestion with all the online players
     */
    public static CompletableFuture<Suggestions> getOnlinePlayersName(@NotNull SuggestionsBuilder builder, @NotNull ServerCommandSource source)
    {
        List<String> players = new ArrayList<>();
        source.getServer().getPlayerManager().getPlayerList().forEach(player -> players.add(player.getName().getString()));
        players.remove(source.getPlayer().getName().getString());

        return CommandSource.suggestMatching(players, builder);
    }

    /**
     * Called for the command {@code /homeuntrust}
     *
     * @return A suggestion with all the trusted players
     */
    public static CompletableFuture<Suggestions> getTrustedPlayersName(@NotNull SuggestionsBuilder builder, @NotNull ServerCommandSource source)
    {
        ArrayList<String> names = new ArrayList<>();
        getTrustedPlayers(source.getPlayer().getUuidAsString() + "_" + source.getPlayer().getName().getString()).forEach(s -> names.add(s.split("_")[1]));

        return CommandSource.suggestMatching(names, builder);
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

        if (Files.exists(TRUST_PATH))
        {
            if (player != null)
            {
                Map<String, ArrayList<String>> gsonTrustingPlayers = readTrustFile();

                for (Map.Entry<String, ArrayList<String>> entry : gsonTrustingPlayers.entrySet())
                {
                    for (String value : entry.getValue())
                    {
                        if (value.split("_")[0].equals(player.getUuidAsString()))
                        {
                            players.add(entry.getKey().split("_")[1]);
                        }
                    }
                }
            }
        }
        return CommandSource.suggestMatching(players, builder);
    }
}
