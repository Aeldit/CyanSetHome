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

import static fr.aeldit.cyanlib.util.Constants.ERROR;
import static fr.aeldit.cyansh.util.Utils.*;

public class PermissionCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("hometrust")
                .then(CommandManager.argument("player", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getOnlinePlayersName(builder, context.getSource()))
                        .executes(PermissionCommands::trustPlayer)
                )
        );

        dispatcher.register(CommandManager.literal("homeuntrust")
                .then(CommandManager.argument("player", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getTrustedPlayersName(builder, context.getSource()))
                        .executes(PermissionCommands::untrustPlayer)
                )
        );

        dispatcher.register(CommandManager.literal("gettrustingplayers")
                .executes(PermissionCommands::getTrustingPlayers)
        );
        dispatcher.register(CommandManager.literal("gettrustedplayers")
                .executes(PermissionCommands::getTrustedPlayers)
        );
    }

    /**
     * Called by the command {@code /hometrust <player>}
     * <p>
     * Used to define which players can use the homes of the trusting player
     */
    public static int trustPlayer(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        String playerName = StringArgumentType.getString(context, "player");

        if (CyanLibUtils.isPlayer(source))
        {
            if (source.getServer().getPlayerManager().getPlayer(playerName) == null)
            {
                CyanLibUtils.sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation(ERROR + "playerNotOnline"),
                        "cyansh.error.playerNotOnline",
                        playerName
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

                        CyanLibUtils.sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation("playerTrusted"),
                                "cyansh.message.playerTrusted",
                                Formatting.AQUA + playerName
                        );
                    }
                    else
                    {
                        CyanLibUtils.sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation(ERROR + "playerAlreadyTrusted"),
                                "cyansh.error.playerAlreadyTrusted"
                        );
                    }
                }
                else
                {
                    CyanLibUtils.sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "selfTrust"),
                            "cyansh.error.selfTrust"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /homeuntrust <player>}
     * <p>
     * Used to remove a player from the trust list
     */
    public static int untrustPlayer(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            String untrustedPlayerName = StringArgumentType.getString(context, "player");

            if (!player.getName().getString().equals(untrustedPlayerName))
            {
                if (TrustsObj.isPlayerTrustingFromName(player.getName().getString(), untrustedPlayerName))
                {
                    TrustsObj.untrustPlayer(player.getName().getString(), untrustedPlayerName);

                    CyanLibUtils.sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation("playerUnTrusted"),
                            "cyansh.message.playerUnTrusted",
                            Formatting.AQUA + untrustedPlayerName
                    );
                }
                else
                {
                    CyanLibUtils.sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "playerNotTrusted"),
                            "cyansh.error.playerNotTrusted"
                    );
                }
            }
            else
            {
                CyanLibUtils.sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation(ERROR + "selfTrust"),
                        "cyansh.error.selfTrust"
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /gettrustingplayers}
     * <p>
     * Send a message to the player with all the players that trust her/him
     */
    public static int getTrustingPlayers(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
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

                CyanLibUtils.sendPlayerMessageActionBar(player,
                        CyanSHLanguageUtils.getTranslation("getTrustingPlayers"),
                        "cyansh.message.getTrustingPlayers",
                        false,
                        Formatting.AQUA + players
                );
            }
            else
            {
                CyanLibUtils.sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation("noTrustingPlayer"),
                        "cyansh.message.noTrustingPlayer"
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /gettrustedplayers}
     * <p>
     * Send a message to the player with all the players that she/he trusts
     */
    public static int getTrustedPlayers(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
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

                CyanLibUtils.sendPlayerMessageActionBar(player,
                        CyanSHLanguageUtils.getTranslation("getTrustedPlayers"),
                        "cyansh.message.getTrustedPlayers",
                        false,
                        Formatting.AQUA + players
                );
            }
            else
            {
                CyanLibUtils.sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation("noTrustedPlayer"),
                        "cyansh.message.noTrustedPlayer"
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
