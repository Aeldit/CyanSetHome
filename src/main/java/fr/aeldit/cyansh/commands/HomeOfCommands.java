/*
 * Copyright (c) 2023-2024  -  Made by Aeldit
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
import fr.aeldit.cyansh.commands.arguments.ArgumentSuggestion;
import fr.aeldit.cyansh.homes.Homes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static fr.aeldit.cyanlib.lib.utils.TranslationsPrefixes.ERROR;
import static fr.aeldit.cyansh.config.CyanSHConfig.*;
import static fr.aeldit.cyansh.util.Utils.*;

public class HomeOfCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("set-home-of")
                                    .then(CommandManager.argument("player_name", StringArgumentType.string())
                                                  .suggests(
                                                          (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                                                  builder,
                                                                  context.getSource().getPlayer()
                                                          ))
                                                  .then(CommandManager.argument(
                                                                  "home_name", StringArgumentType.string())
                                                                .executes(HomeOfCommands::setHomeOf)
                                                  )
                                    )
        );
        dispatcher.register(CommandManager.literal("sho")
                                    .then(CommandManager.argument("player_name", StringArgumentType.string())
                                                  .suggests(
                                                          (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                                                  builder,
                                                                  context.getSource().getPlayer()
                                                          ))
                                                  .then(CommandManager.argument(
                                                                  "home_name", StringArgumentType.string())
                                                                .executes(HomeOfCommands::setHomeOf)
                                                  )
                                    )
        );

        dispatcher.register(CommandManager.literal("remove-home-of")
                                    .then(CommandManager.argument("player_name", StringArgumentType.string())
                                                  .suggests(
                                                          (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                                                  builder,
                                                                  context.getSource().getPlayer()
                                                          ))
                                                  .then(CommandManager.argument(
                                                                  "home_name", StringArgumentType.string())
                                                                .suggests(
                                                                        (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                                                builder,
                                                                                context.getSource().getPlayer(),
                                                                                context.getInput().split(" ")[1]
                                                                        ))
                                                                .executes(HomeOfCommands::removeHomeOf)
                                                  )
                                    )
        );
        dispatcher.register(CommandManager.literal("rho")
                                    .then(CommandManager.argument("player_name", StringArgumentType.string())
                                                  .suggests(
                                                          (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                                                  builder,
                                                                  context.getSource().getPlayer()
                                                          ))
                                                  .then(CommandManager.argument(
                                                                  "home_name", StringArgumentType.string())
                                                                .suggests(
                                                                        (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                                                builder,
                                                                                context.getSource().getPlayer(),
                                                                                context.getInput().split(" ")[1]
                                                                        ))
                                                                .executes(HomeOfCommands::removeHomeOf)
                                                  )
                                    )
        );

        dispatcher.register(CommandManager.literal("remove-all-homes-of")
                                    .then(CommandManager.argument("player_name", StringArgumentType.string())
                                                  .suggests(
                                                          (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                                                  builder,
                                                                  context.getSource().getPlayer()
                                                          ))
                                                  .executes(HomeOfCommands::removeAllHomesOf)
                                    )
        );

        dispatcher.register(CommandManager.literal("rename-home-of")
                                    .then(CommandManager.argument("player_name", StringArgumentType.string())
                                                  .suggests(
                                                          (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                                                  builder,
                                                                  context.getSource().getPlayer()
                                                          ))
                                                  .then(CommandManager.argument(
                                                                  "home_name", StringArgumentType.string())
                                                                .suggests(
                                                                        (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                                                builder,
                                                                                context.getSource().getPlayer(),
                                                                                context.getInput().split(" ")[1]
                                                                        ))
                                                                .then(CommandManager.argument(
                                                                                "new_home_name",
                                                                                StringArgumentType.string())
                                                                              .executes(HomeOfCommands::renameHomeOf)
                                                                )
                                                  )
                                    )
        );

        dispatcher.register(CommandManager.literal("home-of")
                                    .then(CommandManager.argument("player_name", StringArgumentType.string())
                                                  .suggests(
                                                          (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                                                  builder,
                                                                  context.getSource().getPlayer()
                                                          ))
                                                  .then(CommandManager.argument(
                                                                  "home_name", StringArgumentType.string())
                                                                .suggests(
                                                                        (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                                                builder,
                                                                                context.getSource().getPlayer(),
                                                                                context.getInput().split(" ")[1]
                                                                        ))
                                                                .executes(HomeOfCommands::goToHomeOf)
                                                  )
                                    )
        );
        dispatcher.register(CommandManager.literal("ho")
                                    .then(CommandManager.argument("player_name", StringArgumentType.string())
                                                  .suggests(
                                                          (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                                                  builder,
                                                                  context.getSource().getPlayer()
                                                          ))
                                                  .then(CommandManager.argument(
                                                                  "home_name", StringArgumentType.string())
                                                                .suggests(
                                                                        (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                                                builder,
                                                                                context.getSource().getPlayer(),
                                                                                context.getInput().split(" ")[1]
                                                                        ))
                                                                .executes(HomeOfCommands::goToHomeOf)
                                                  )
                                    )
        );

        dispatcher.register(CommandManager.literal("get-homes-of")
                                    .then(CommandManager.argument("player_name", StringArgumentType.string())
                                                  .suggests(
                                                          (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                                                  builder,
                                                                  context.getSource().getPlayer()
                                                          ))
                                                  .executes(HomeOfCommands::getHomesOfList)
                                    )
        );
        dispatcher.register(CommandManager.literal("gho")
                                    .then(CommandManager.argument("player_name", StringArgumentType.string())
                                                  .suggests(
                                                          (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                                                  builder,
                                                                  context.getSource().getPlayer()
                                                          ))
                                                  .executes(HomeOfCommands::getHomesOfList)
                                    )
        );
    }

    /**
     * Called by the command {@code /set-home-of <player_name> <home_name>} or {@code /sho <player_name> <home_name>}
     * <p>
     * Adds the given home to the given player
     * <p>
     * Succeeds only if the player has OP level {@code MIN_OP_LVL_BYPASS} and the bypass option is set to {@code true}
     */
    public static int setHomeOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            if (CYANSH_LIB_UTILS.isOptionAllowed(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                if (ALLOW_BYPASS.getValue() && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue()))
                {
                    String trustingPlayer = StringArgumentType.getString(context, "player_name");
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String playerKey = HomesObj.getKeyFromName(trustingPlayer);

                    if (HomesObj.maxHomesNotReached(playerKey))
                    {
                        if (HomesObj.addHome(
                                playerKey,
                                new Homes.Home(homeName,
                                               player.getWorld().getDimensionKey().getValue()
                                                       .toString().replace("minecraft:", "").replace("the_", ""),
                                               player.getX(), player.getY(), player.getZ(), player.getYaw(),
                                               player.getPitch(),
                                               new SimpleDateFormat("dd/MM/yyyy HH:mm").format(
                                                       Calendar.getInstance().getTime())
                                )
                        ))
                        {
                            CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                                    player,
                                    CYANSH_LANGUAGE_UTILS.getTranslation("setHomeOf"),
                                    "cyansh.msg.setHomeOf",
                                    Formatting.YELLOW + homeName,
                                    Formatting.AQUA + trustingPlayer
                            );
                        }
                        else
                        {
                            CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                                    player,
                                    CYANSH_LANGUAGE_UTILS.getTranslation(ERROR + "homeAlreadyExists"),
                                    "cyansh.msg.homeAlreadyExists"
                            );
                        }
                    }
                }
                else
                {
                    CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                            player,
                            CYANSH_LANGUAGE_UTILS.getTranslation(ERROR + "bypassDisabled"),
                            "cyansh.msg.bypassDisabled"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /remove-home-of <player_name> <home_name>} or {@code /rho <player_name> <home_name>}
     * <p>
     * Removes the given home of the given player
     * <p>
     * Succeeds only if the player has OP level {@code MIN_OP_LVL_BYPASS} and the bypass option is set to {@code true}
     */
    public static int removeHomeOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            if (CYANSH_LIB_UTILS.isOptionAllowed(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if (ALLOW_BYPASS.getValue() && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue()))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");

                    if (HomesObj.removeHome(HomesObj.getKeyFromName(trustingPlayer), homeName))
                    {
                        CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                                player,
                                CYANSH_LANGUAGE_UTILS.getTranslation("removeHomeOf"),
                                "cyansh.msg.removeHomeOf",
                                Formatting.YELLOW + homeName,
                                Formatting.AQUA + trustingPlayer
                        );
                    }
                    else
                    {
                        CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                                player,
                                CYANSH_LANGUAGE_UTILS.getTranslation(ERROR + "homeNotFound"),
                                "cyansh.msg.homeNotFound",
                                Formatting.YELLOW + homeName
                        );
                    }
                }
                else
                {
                    CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                            player,
                            CYANSH_LANGUAGE_UTILS.getTranslation(ERROR + "bypassDisabled"),
                            "cyansh.msg.bypassDisabled"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /remove-all-homes-of <player_name>}
     * <p>
     * Removes all the homes of the given player
     * <p>
     * Succeeds only if the player has OP level {@code MIN_OP_LVL_BYPASS} and the byPass option is set to {@code true}
     */
    public static int removeAllHomesOf(@NotNull CommandContext<ServerCommandSource> context) // TODO -> ask for
    // confirmation
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            if (CYANSH_LIB_UTILS.isOptionAllowed(player, ALLOW_HOMES.getValue(), "disabled.homes"))
            {
                if (ALLOW_BYPASS.getValue() && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue()))
                {
                    String trustingPlayer = StringArgumentType.getString(context, "player_name");

                    if (HomesObj.removeAll(HomesObj.getKeyFromName(trustingPlayer)))
                    {
                        CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                                player,
                                CYANSH_LANGUAGE_UTILS.getTranslation("removeAllHomesOf"),
                                "cyansh.msg.removeAllHomesOf",
                                Formatting.AQUA + trustingPlayer
                        );
                    }
                    else
                    {
                        CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                                player,
                                CYANSH_LANGUAGE_UTILS.getTranslation(ERROR + "noHomesOf"),
                                "cyansh.msg.noHomesOf"
                        );
                    }
                }
                else
                {
                    CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                            player,
                            CYANSH_LANGUAGE_UTILS.getTranslation(ERROR + "bypassDisabled"),
                            "cyansh.msg.bypassDisabled"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /rename-home-of <player_name> <home_name> <new_name>}
     * <p>
     * Renames the given home of the given player
     * <p>
     * Succeeds only if the player has OP level {@code MIN_OP_LVL_BYPASS} and the bypass option is set to {@code true}
     */
    public static int renameHomeOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            if (CYANSH_LIB_UTILS.isOptionAllowed(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if (ALLOW_BYPASS.getValue() && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue()))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String newHomeName = StringArgumentType.getString(context, "new_home_name");
                    String playerKey = HomesObj.getKeyFromName(trustingPlayer);

                    if (HomesObj.rename(playerKey, homeName, newHomeName))
                    {
                        CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                                player,
                                CYANSH_LANGUAGE_UTILS.getTranslation("renameHomeOf"),
                                "cyansh.msg.renameHomeOf",
                                Formatting.YELLOW + homeName,
                                Formatting.YELLOW + newHomeName,
                                Formatting.AQUA + trustingPlayer
                        );
                    }
                    else
                    {
                        CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                                player,
                                CYANSH_LANGUAGE_UTILS.getTranslation(ERROR + "homeNotFoundOrExists"),
                                "cyansh.msg.homeNotFoundOrExists",
                                homeName
                        );
                    }
                }
                else
                {
                    CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                            player,
                            CYANSH_LANGUAGE_UTILS.getTranslation(ERROR + "bypassDisabled"),
                            "cyansh.msg.bypassDisabled"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /home-of <player_name> <home_name>} or {@code /ho <player_name> <home_name>}
     * <p>
     * Teleports the player to the given home
     * <p>
     * Succeeds only if the player is trusted or has OP level {@code MIN_OP_LVL_BYPASS} and the bypass option is set
     * to {@code true}
     */
    public static int goToHomeOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            if (CYANSH_LIB_UTILS.isOptionAllowed(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if ((ALLOW_BYPASS.getValue() && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue()))
                        || TrustsObj.isPlayerTrustingFromName(trustingPlayer, player.getName().getString())
                )
                {
                    String homeName = StringArgumentType.getString(context, "home_name");

                    if (HomesObj.homeExistsFromName(trustingPlayer, homeName))
                    {
                        Homes.Home home = HomesObj.getPlayerHome(HomesObj.getKeyFromName(trustingPlayer), homeName);
                        MinecraftServer server = player.getServer();

                        if (home != null && server != null)
                        {
                            switch (home.dimension())
                            {
                                case "overworld" ->
                                        player.teleport(server.getWorld(World.OVERWORLD), home.x(), home.y(),
                                                        home.z(), home.yaw(), home.pitch()
                                        );
                                case "nether" -> player.teleport(server.getWorld(World.NETHER), home.x(), home.y(),
                                                                 home.z(), home.yaw(), home.pitch()
                                );
                                case "end" -> player.teleport(server.getWorld(World.END), home.x(), home.y(),
                                                              home.z(), home.yaw(), home.pitch()
                                );
                            }

                            CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                                    player,
                                    CYANSH_LANGUAGE_UTILS.getTranslation("goToHome"),
                                    "cyansh.msg.goToHome",
                                    Formatting.YELLOW + homeName
                            );
                        }
                    }
                    else
                    {
                        CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                                player,
                                CYANSH_LANGUAGE_UTILS.getTranslation(ERROR + "homeNotFound"),
                                "cyansh.msg.homeNotFound",
                                Formatting.YELLOW + homeName
                        );
                    }
                }
                else
                {
                    CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                            player,
                            CYANSH_LANGUAGE_UTILS.getTranslation(ERROR + "notOpOrTrusted"),
                            "cyansh.msg.notOpOrTrusted"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /get-homes-of} or {@code /gho}
     * <p>
     * Sends a message in the player's chat with all the given player's homes
     * <p>
     * Succeeds only if the player is trusted or OP level {@code MIN_OP_LVL_BYPASS} and the bypass option is set to
     * {@code true}
     */
    public static int getHomesOfList(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            if (CYANSH_LIB_UTILS.isOptionAllowed(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if (player.getName().getString().equals(trustingPlayer))
                {
                    CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                            player,
                            CYANSH_LANGUAGE_UTILS.getTranslation(ERROR + "useSelfHomes"),
                            "cyansh.msg.useSelfHomes"
                    );
                }
                else if ((ALLOW_BYPASS.getValue() && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue()))
                        || TrustsObj.isPlayerTrustingFromName(trustingPlayer, player.getName().getString())
                )
                {
                    if (!HomesObj.isEmptyFromName(trustingPlayer))
                    {
                        CYANSH_LANGUAGE_UTILS.sendPlayerMessageActionBar(
                                player,
                                CYANSH_LANGUAGE_UTILS.getTranslation("dashSeparation"),
                                "cyansh.msg.dashSeparation",
                                false
                        );
                        CYANSH_LANGUAGE_UTILS.sendPlayerMessageActionBar(
                                player,
                                CYANSH_LANGUAGE_UTILS.getTranslation("listHomesOf"),
                                "cyansh.msg.listHomesOf",
                                false,
                                Formatting.AQUA + trustingPlayer
                        );

                        HomesObj.getPlayerHomes(HomesObj.getKeyFromName(trustingPlayer))
                                .forEach(home -> CYANSH_LANGUAGE_UTILS.sendPlayerMessageActionBar(
                                                 player,
                                                 CYANSH_LANGUAGE_UTILS.getTranslation("getHome"),
                                                 "cyansh.msg.getHome",
                                                 false,
                                                 Formatting.YELLOW + home.name(),
                                                 Formatting.DARK_AQUA + home.dimension(),
                                                 Formatting.DARK_AQUA + home.date()
                                         )
                                );

                        CYANSH_LANGUAGE_UTILS.sendPlayerMessageActionBar(
                                player,
                                CYANSH_LANGUAGE_UTILS.getTranslation("dashSeparation"),
                                "cyansh.msg.dashSeparation",
                                false
                        );
                    }
                    else
                    {
                        CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                                player,
                                CYANSH_LANGUAGE_UTILS.getTranslation(ERROR + "noHomesOf"),
                                "cyansh.msg.noHomesOf"
                        );
                    }
                }
                else
                {
                    CYANSH_LANGUAGE_UTILS.sendPlayerMessage(
                            player,
                            CYANSH_LANGUAGE_UTILS.getTranslation(ERROR + "notOpOrTrusted"),
                            "cyansh.msg.notOpOrTrusted"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
