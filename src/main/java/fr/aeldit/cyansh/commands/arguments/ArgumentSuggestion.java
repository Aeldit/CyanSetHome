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

package fr.aeldit.cyansh.commands.arguments;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static fr.aeldit.cyansh.config.CyanSHConfig.ALLOW_BYPASS;
import static fr.aeldit.cyansh.util.Utils.HomesObj;
import static fr.aeldit.cyansh.util.Utils.TrustsObj;

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
        return (ALLOW_BYPASS.getValue() && player.hasPermissionLevel(4)) || TrustsObj.isPlayerTrustingFromName(trustingPlayer, player.getName().getString())
                ? CommandSource.suggestMatching(HomesObj.getHomesNamesOf(trustingPlayer), builder) : new CompletableFuture<>();
    }

    /**
     * Called for the command {@code /home-trust}
     *
     * @return A suggestion with all the online players
     */
    public static CompletableFuture<Suggestions> getOnlinePlayersName(@NotNull SuggestionsBuilder builder, @NotNull ServerCommandSource source)
    {
        List<String> players = source.getServer().getPlayerManager().getPlayerList().stream().map(player -> player.getName().getString()).collect(Collectors.toList());
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
        ArrayList<String> names = TrustsObj.getTrustedPlayers(source.getPlayer().getUuidAsString() + " " + source.getPlayer().getName().getString())
                .stream().map(s -> s.split(" ")[1]).collect(Collectors.toCollection(ArrayList::new));

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

        if (player != null) // TODO -> Remove check
        {
            return player.hasPermissionLevel(4) && ALLOW_BYPASS.getValue()
                    ? CommandSource.suggestMatching(HomesObj.getPlayersWithHomes(player.getName().getString()), builder)
                    : CommandSource.suggestMatching(TrustsObj.getTrustingPlayers(player.getUuidAsString() + " " + player.getName().getString()), builder);
        }
        return CommandSource.suggestMatching(new ArrayList<>(), builder);
    }
}
