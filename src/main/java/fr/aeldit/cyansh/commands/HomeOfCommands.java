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
import fr.aeldit.cyanlib.lib.CyanLibLanguageUtils;
import fr.aeldit.cyansh.commands.argumentTypes.ArgumentSuggestion;
import fr.aeldit.cyansh.homes.Home;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static fr.aeldit.cyanlib.lib.TranslationsPrefixes.ERROR;
import static fr.aeldit.cyansh.util.Utils.*;

public class HomeOfCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("homeof")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getTrustingPlayersName(builder, context.getSource()))
                        .then(CommandManager.argument("home_name", StringArgumentType.string())
                                .suggests((context, builder) -> ArgumentSuggestion.getHomesOf(builder, context.getSource().getPlayer(), context.getInput().split(" ")[1]))
                                .executes(HomeOfCommands::goToHomeOf)
                        )
                )
        );
        dispatcher.register(CommandManager.literal("ho")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getTrustingPlayersName(builder, context.getSource()))
                        .then(CommandManager.argument("home_name", StringArgumentType.string())
                                .suggests((context, builder) -> ArgumentSuggestion.getHomesOf(builder, context.getSource().getPlayer(), context.getInput().split(" ")[1]))
                                .executes(HomeOfCommands::goToHomeOf)
                        )
                )
        );

        dispatcher.register(CommandManager.literal("removehomeof")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getTrustingPlayersName(builder, context.getSource()))
                        .then(CommandManager.argument("home_name", StringArgumentType.string())
                                .suggests((context, builder) -> ArgumentSuggestion.getHomesOf(builder, context.getSource().getPlayer(), context.getInput().split(" ")[1]))
                                .executes(HomeOfCommands::removeHomeOf)
                        )
                )
        );
        dispatcher.register(CommandManager.literal("rho")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getTrustingPlayersName(builder, context.getSource()))
                        .then(CommandManager.argument("home_name", StringArgumentType.string())
                                .suggests((context, builder) -> ArgumentSuggestion.getHomesOf(builder, context.getSource().getPlayer(), context.getInput().split(" ")[1]))
                                .executes(HomeOfCommands::removeHomeOf)
                        )
                )
        );

        dispatcher.register(CommandManager.literal("gethomesof")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getTrustingPlayersName(builder, context.getSource()))
                        .executes(HomeOfCommands::getHomesOfList)
                )
        );
        dispatcher.register(CommandManager.literal("gho")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getTrustingPlayersName(builder, context.getSource()))
                        .executes(HomeOfCommands::getHomesOfList)
                )
        );
    }

    /**
     * Called by the command {@code /removehomeof <home_name>} or {@code /rho <home_name>}
     * <p>
     * Removes the given home of the given player
     */
    public static int removeHomeOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (LibUtils.isPlayer(context.getSource()))
        {
            if (LibUtils.isOptionAllowed(player, LibConfig.getBoolOption("allowHomesOf"), "homesOfDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if (LibConfig.getBoolOption("allowByPass") && player.hasPermissionLevel(4))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");

                    if (HomesObj.homeExistsFromName(trustingPlayer, homeName))
                    {
                        HomesObj.removeHome(HomesObj.getKeyFromName(trustingPlayer), homeName);

                        CyanLibLanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation("removeHomeOf"),
                                "cyansh.msg.removeHomeOf",
                                Formatting.YELLOW + homeName,
                                Formatting.AQUA + trustingPlayer
                        );
                    }
                    else
                    {
                        CyanLibLanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation(ERROR + "homeNotFound"),
                                "cyansh.msg.homeNotFound",
                                Formatting.YELLOW + homeName
                        );
                    }
                }
                else
                {
                    CyanLibLanguageUtils.sendPlayerMessage(player,
                            LanguageUtils.getTranslation(ERROR + "notOpOrTrusted"),
                            "cyansh.msg.notOpOrTrusted"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /homeof <player_name> <home_name>} or {@code /ho <player_name> <home_name>}
     * <p>
     * Teleports the player to the given home
     */
    public static int goToHomeOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (LibUtils.isPlayer(context.getSource()))
        {
            if (LibUtils.isOptionAllowed(player, LibConfig.getBoolOption("allowHomesOf"), "homesOfDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if ((LibConfig.getBoolOption("allowByPass") && player.hasPermissionLevel(4)) || TrustsObj.isPlayerTrustingFromName(trustingPlayer, player.getName().getString()))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");

                    if (HomesObj.homeExistsFromName(trustingPlayer, homeName))
                    {
                        Home home = HomesObj.getPlayerHome(HomesObj.getKeyFromName(trustingPlayer), homeName);

                        switch (home.dimension())
                        {
                            case "overworld" -> player.teleport(player.getServer().getWorld(World.OVERWORLD),
                                    home.x(), home.y(), home.z(), home.yaw(), home.pitch());
                            case "nether" -> player.teleport(player.getServer().getWorld(World.NETHER),
                                    home.x(), home.y(), home.z(), home.yaw(), home.pitch());
                            case "end" -> player.teleport(player.getServer().getWorld(World.END),
                                    home.x(), home.y(), home.z(), home.yaw(), home.pitch());
                        }

                        CyanLibLanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation("goToHome"),
                                "cyansh.msg.goToHome",
                                Formatting.YELLOW + homeName
                        );
                    }
                    else
                    {
                        CyanLibLanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation(ERROR + "homeNotFound"),
                                "cyansh.msg.homeNotFound",
                                Formatting.YELLOW + homeName
                        );
                    }
                }
                else
                {
                    CyanLibLanguageUtils.sendPlayerMessage(player,
                            LanguageUtils.getTranslation(ERROR + "notOpOrTrusted"),
                            "cyansh.msg.notOpOrTrusted"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /gethomesof} or {@code /gho}
     * <p>
     * Sends a message in the player's chat with all the given player's homes
     */
    public static int getHomesOfList(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (LibUtils.isPlayer(context.getSource()))
        {
            if (LibUtils.isOptionAllowed(player, LibConfig.getBoolOption("allowHomesOf"), "homesOfDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if (player.getName().getString().equals(trustingPlayer))
                {
                    CyanLibLanguageUtils.sendPlayerMessage(player,
                            LanguageUtils.getTranslation(ERROR + "useSelfHomes"),
                            "cyansh.msg.useSelfHomes"
                    );
                }
                else if ((LibConfig.getBoolOption("allowByPass") && player.hasPermissionLevel(4)) || TrustsObj.isPlayerTrustingFromName(trustingPlayer, player.getName().getString()))
                {
                    if (!HomesObj.isEmptyFromName(trustingPlayer))
                    {
                        CyanLibLanguageUtils.sendPlayerMessageActionBar(player,
                                LanguageUtils.getTranslation("dashSeparation"),
                                "cyansh.msg.dashSeparation",
                                false
                        );
                        CyanLibLanguageUtils.sendPlayerMessageActionBar(player,
                                LanguageUtils.getTranslation("listHomesOf"),
                                "cyansh.msg.listHomesOf",
                                false,
                                Formatting.AQUA + trustingPlayer
                        );

                        HomesObj.getPlayerHomes(HomesObj.getKeyFromName(trustingPlayer)).forEach(home -> CyanLibLanguageUtils.sendPlayerMessageActionBar(player,
                                        LanguageUtils.getTranslation("getHome"),
                                        "cyansh.msg.getHome",
                                        false,
                                        Formatting.YELLOW + home.name(),
                                        Formatting.DARK_AQUA + home.dimension(),
                                        Formatting.DARK_AQUA + home.date()
                                )
                        );

                        CyanLibLanguageUtils.sendPlayerMessageActionBar(player,
                                LanguageUtils.getTranslation("dashSeparation"),
                                "cyansh.msg.dashSeparation",
                                false
                        );
                    }
                    else
                    {
                        CyanLibLanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation(ERROR + "noHomesOf"),
                                "cyansh.msg.noHomesOf"
                        );
                    }
                }
                else
                {
                    CyanLibLanguageUtils.sendPlayerMessage(player,
                            LanguageUtils.getTranslation(ERROR + "notOpOrTrusted"),
                            "cyansh.msg.notOpOrTrusted"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
