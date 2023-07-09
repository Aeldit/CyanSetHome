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
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static fr.aeldit.cyansh.util.Utils.*;

public final class ArgumentSuggestion
{
    /**
     * Called for the command {@code /get-homes} or the suggestions of the {@code home} commands
     *
     * @return A suggestion with all the player's homes
     */
    public static CompletableFuture<Suggestions> getHomes(@NotNull SuggestionsBuilder builder, @NotNull ServerPlayerEntity player)
    {
        return CommandSource.suggestMatching(HomesObj.getHomesNames(player.getUuidAsString() + " " + player.getName().getString()), builder);
    }

    /**
     * Called for the command {@code /get-homes-of} or the suggestions of the {@code homeOf} commands
     *
     * @return A suggestion with all the trusting player's homes
     */
    public static CompletableFuture<Suggestions> getHomesOf(@NotNull SuggestionsBuilder builder, @NotNull ServerPlayerEntity player, @NotNull String trustingPlayer)
    {
        if ((LibConfig.getBoolOption("allowByPass") && player.hasPermissionLevel(4)) || TrustsObj.isPlayerTrustingFromName(trustingPlayer, player.getName().getString()))
        {
            return CommandSource.suggestMatching(HomesObj.getHomesNamesOf(trustingPlayer), builder);
        }
        return new CompletableFuture<>();
    }

    /**
     * Called for the command {@code /home-trust}
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
     * Called for the command {@code /home-untrust}
     *
     * @return A suggestion with all the trusted players
     */
    public static CompletableFuture<Suggestions> getTrustedPlayersName(@NotNull SuggestionsBuilder builder, @NotNull ServerCommandSource source)
    {
        ArrayList<String> names = new ArrayList<>();
        TrustsObj.getTrustedPlayers(source.getPlayer().getUuidAsString() + " " + source.getPlayer().getName().getString()).forEach(s -> names.add(s.split(" ")[1]));

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

        if (player != null)
        {
            if (player.hasPermissionLevel(4) && LibConfig.getBoolOption("allowByPass"))
            {
                return CommandSource.suggestMatching(HomesObj.getPlayersWithHomes(player.getName().getString()), builder);
            }
            else
            {
                return CommandSource.suggestMatching(TrustsObj.getTrustingPlayers(player.getUuidAsString() + " " + player.getName().getString()), builder);
            }
        }
        return CommandSource.suggestMatching(new ArrayList<>(), builder);
    }
}
