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

package fr.aeldit.cyansh.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.aeldit.cyansh.commands.argumentTypes.ArgumentSuggestion;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static fr.aeldit.cyanlib.lib.utils.TranslationsPrefixes.ERROR;
import static fr.aeldit.cyansh.util.Utils.*;

public class PermissionCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("home-trust")
                .then(CommandManager.argument("player", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getOnlinePlayersName(builder, context.getSource()))
                        .executes(PermissionCommands::trustPlayer)
                )
        );

        dispatcher.register(CommandManager.literal("home-untrust")
                .then(CommandManager.argument("player", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getTrustedPlayersName(builder, context.getSource()))
                        .executes(PermissionCommands::untrustPlayer)
                )
        );

        dispatcher.register(CommandManager.literal("get-trusting-players")
                .executes(PermissionCommands::getTrustingPlayers)
        );
        dispatcher.register(CommandManager.literal("get-trusted-players")
                .executes(PermissionCommands::getTrustedPlayers)
        );
    }

    /**
     * Called by the command {@code /home-trust <player>}
     * <p>
     * Used to define which players can use the homes of the trusting player
     */
    public static int trustPlayer(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (LibUtils.isPlayer(source))
        {
            String playerName = StringArgumentType.getString(context, "player");

            if (source.getServer().getPlayerManager().getPlayer(playerName) == null)
            {
                LanguageUtils.sendPlayerMessage(player,
                        LanguageUtils.getTranslation(ERROR + "playerNotOnline"),
                        "cyansh.msg.playerNotOnline"
                );
            }
            else
            {
                String trustingPlayer = player.getUuidAsString() + " " + player.getName().getString();
                String trustedPlayer = source.getServer().getPlayerManager().getPlayer(playerName).getUuid() + " " + playerName;

                if (!trustingPlayer.equals(trustedPlayer))
                {
                    if (!TrustsObj.isPlayerTrustingFromName(player.getName().getString(), playerName))
                    {
                        TrustsObj.trustPlayer(trustingPlayer, trustedPlayer);

                        LanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation("playerTrusted"),
                                "cyansh.msg.playerTrusted",
                                Formatting.AQUA + playerName
                        );
                    }
                    else
                    {
                        LanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation(ERROR + "playerAlreadyTrusted"),
                                "cyansh.msg.playerAlreadyTrusted"
                        );
                    }
                }
                else
                {
                    LanguageUtils.sendPlayerMessage(player,
                            LanguageUtils.getTranslation(ERROR + "selfTrust"),
                            "cyansh.msg.selfTrust"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /home-untrust <player>}
     * <p>
     * Used to remove a player from the trust list
     */
    public static int untrustPlayer(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (LibUtils.isPlayer(context.getSource()))
        {
            String untrustedPlayerName = StringArgumentType.getString(context, "player");

            if (!player.getName().getString().equals(untrustedPlayerName))
            {
                if (TrustsObj.isPlayerTrustingFromName(player.getName().getString(), untrustedPlayerName))
                {
                    TrustsObj.untrustPlayer(player.getName().getString(), untrustedPlayerName);

                    LanguageUtils.sendPlayerMessage(player,
                            LanguageUtils.getTranslation("playerUnTrusted"),
                            "cyansh.msg.playerUnTrusted",
                            Formatting.AQUA + untrustedPlayerName
                    );
                }
                else
                {
                    LanguageUtils.sendPlayerMessage(player,
                            LanguageUtils.getTranslation(ERROR + "playerNotTrusted"),
                            "cyansh.msg.playerNotTrusted"
                    );
                }
            }
            else
            {
                LanguageUtils.sendPlayerMessage(player,
                        LanguageUtils.getTranslation(ERROR + "selfTrust"),
                        "cyansh.msg.selfTrust"
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /get-trusting-players}
     * <p>
     * Send a message to the player with all the players that trust her/him
     */
    public static int getTrustingPlayers(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (LibUtils.isPlayer(context.getSource()))
        {
            ArrayList<String> trustingPlayers = TrustsObj.getTrustingPlayers(player.getUuidAsString() + " " + player.getName().getString());

            if (!trustingPlayers.isEmpty())
            {
                String players = "";

                for (int i = 0; i < trustingPlayers.size(); i++)
                {
                    if (trustingPlayers.size() == 1)
                    {
                        players = players.concat("%s".formatted(trustingPlayers.get(i)));
                    }
                    else if (i == trustingPlayers.size() - 1)
                    {
                        players = players.concat(", %s".formatted(trustingPlayers.get(i)));
                    }
                    else
                    {
                        players = players.concat(", %s,".formatted(trustingPlayers.get(i)));
                    }
                }

                LanguageUtils.sendPlayerMessageActionBar(player,
                        LanguageUtils.getTranslation("getTrustingPlayers"),
                        "cyansh.msg.getTrustingPlayers",
                        false,
                        Formatting.AQUA + players
                );
            }
            else
            {
                LanguageUtils.sendPlayerMessage(player,
                        LanguageUtils.getTranslation("noTrustingPlayer"),
                        "cyansh.msg.noTrustingPlayer"
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /get-trusted-players}
     * <p>
     * Send a message to the player with all the players that she/he trusts
     */
    public static int getTrustedPlayers(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (LibUtils.isPlayer(context.getSource()))
        {
            List<String> trustedPlayers = TrustsObj.getTrustedPlayers(player.getUuidAsString() + " " + player.getName().getString());

            if (!trustedPlayers.isEmpty())
            {
                String players = "";

                for (int i = 0; i < trustedPlayers.size(); i++)
                {
                    if (trustedPlayers.size() == 1)
                    {
                        players = players.concat("%s".formatted(trustedPlayers.get(i).split(" ")[1]));
                    }
                    else if (i == trustedPlayers.size() - 1)
                    {
                        players = players.concat(", %s".formatted(trustedPlayers.get(i).split(" ")[1]));
                    }
                    else
                    {
                        players = players.concat(", %s,".formatted(trustedPlayers.get(i).split(" ")[1]));
                    }
                }

                LanguageUtils.sendPlayerMessageActionBar(player,
                        LanguageUtils.getTranslation("getTrustedPlayers"),
                        "cyansh.msg.getTrustedPlayers",
                        false,
                        Formatting.AQUA + players
                );
            }
            else
            {
                LanguageUtils.sendPlayerMessage(player,
                        LanguageUtils.getTranslation("noTrustedPlayer"),
                        "cyansh.msg.noTrustedPlayer"
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
