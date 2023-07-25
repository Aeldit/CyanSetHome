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
import fr.aeldit.cyansh.homes.Homes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import static fr.aeldit.cyanlib.lib.utils.TranslationsPrefixes.ERROR;
import static fr.aeldit.cyansh.util.Utils.*;

public class HomeCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("set-home")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .executes(HomeCommands::setHome)
                )
        );
        dispatcher.register(CommandManager.literal("sh")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .executes(HomeCommands::setHome)
                )
        );

        dispatcher.register(CommandManager.literal("home")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getHomes(builder4, Objects.requireNonNull(context4.getSource().getPlayer())))
                        .executes(HomeCommands::goToHome)
                )
        );
        dispatcher.register(CommandManager.literal("h")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getHomes(builder4, Objects.requireNonNull(context4.getSource().getPlayer())))
                        .executes(HomeCommands::goToHome)
                )
        );

        dispatcher.register(CommandManager.literal("remove-home")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getHomes(builder4, Objects.requireNonNull(context4.getSource().getPlayer())))
                        .executes(HomeCommands::removeHome)
                )
        );
        dispatcher.register(CommandManager.literal("rh")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getHomes(builder4, Objects.requireNonNull(context4.getSource().getPlayer())))
                        .executes(HomeCommands::removeHome)
                )
        );


        dispatcher.register(CommandManager.literal("rename-home")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getHomes(builder4, Objects.requireNonNull(context4.getSource().getPlayer())))
                        .then(CommandManager.argument("new_home_name", StringArgumentType.string())
                                .executes(HomeCommands::renameHome)
                        )
                )
        );

        dispatcher.register(CommandManager.literal("remove-all-homes")
                .executes(HomeCommands::removeAllHomes)
        );

        dispatcher.register(CommandManager.literal("get-homes")
                .executes(HomeCommands::getHomesList)
        );
        dispatcher.register(CommandManager.literal("gh")
                .executes(HomeCommands::getHomesList)

        );
    }

    /**
     * Called by the command {@code /set-home <home_name>} or {@code /sh <home_name>}
     * <p>
     * Creates a home with the player current position (dimension, x, y, z, yaw, pitch, date)
     */
    public static int setHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (LibUtils.isPlayer(context.getSource()))
        {
            if (LibUtils.isOptionAllowed(player, LibConfig.getBoolOption("allowHomes"), "homesDisabled"))
            {
                if (LibUtils.hasPermission(player, LibConfig.getIntOption("minOpLevelExeHomes")))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String playerKey = player.getUuidAsString() + " " + player.getName().getString();

                    if (!HomesObj.maxHomesReached(playerKey))
                    {
                        if (!HomesObj.homeExists(playerKey, homeName))
                        {
                            if (player.getWorld() == player.getServer().getWorld(World.OVERWORLD))
                            {
                                HomesObj.addHome(playerKey,
                                        new Homes.Home(homeName, "overworld", player.getX(), player.getY(), player.getZ(),
                                                player.getYaw(), player.getPitch(),
                                                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime())
                                        ));
                            }
                            else if (player.getWorld() == player.getServer().getWorld(World.NETHER))
                            {
                                HomesObj.addHome(playerKey,
                                        new Homes.Home(homeName, "nether", player.getX(), player.getY(), player.getZ(),
                                                player.getYaw(), player.getPitch(),
                                                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime())
                                        ));
                            }
                            else
                            {
                                HomesObj.addHome(playerKey,
                                        new Homes.Home(homeName, "end", player.getX(), player.getY(), player.getZ(),
                                                player.getYaw(), player.getPitch(),
                                                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime())
                                        ));
                            }

                            LanguageUtils.sendPlayerMessage(player,
                                    LanguageUtils.getTranslation("setHome"),
                                    "cyansh.msg.setHome",
                                    Formatting.YELLOW + homeName
                            );
                        }
                        else
                        {
                            LanguageUtils.sendPlayerMessage(player,
                                    LanguageUtils.getTranslation(ERROR + "homeAlreadyExists"),
                                    "cyansh.msg.homeAlreadyExists"
                            );
                        }
                    }
                    else
                    {
                        LanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation(ERROR + "maxHomesReached"),
                                "cyansh.msg.maxHomesReached",
                                Formatting.GOLD + String.valueOf(LibConfig.getIntOption("maxHomes"))
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /remove-home <home_name>} or {@code /rh <home_name>}
     * <p>
     * Removes the given home
     */
    public static int removeHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (LibUtils.isPlayer(context.getSource()))
        {
            if (LibUtils.isOptionAllowed(player, LibConfig.getBoolOption("allowHomes"), "homesDisabled"))
            {
                if (LibUtils.hasPermission(player, LibConfig.getIntOption("minOpLevelExeHomes")))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String playerKey = player.getUuidAsString() + " " + player.getName().getString();

                    if (HomesObj.homeExists(playerKey, homeName))
                    {
                        HomesObj.removeHome(playerKey, homeName);

                        LanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation("removeHome"),
                                "cyansh.msg.removeHome",
                                Formatting.YELLOW + homeName
                        );
                    }
                    else
                    {
                        LanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation(ERROR + "homeNotFound"),
                                "cyansh.msg.homeNotFound",
                                Formatting.YELLOW + homeName
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /remove-all-homes}
     * <p>
     * Removes all the homes
     */
    public static int removeAllHomes(@NotNull CommandContext<ServerCommandSource> context) // TODO -> ask for confirmation
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (LibUtils.isPlayer(context.getSource()))
        {
            if (LibUtils.isOptionAllowed(player, LibConfig.getBoolOption("allowHomes"), "homesDisabled"))
            {
                if (LibUtils.hasPermission(player, LibConfig.getIntOption("minOpLevelExeHomes")))
                {
                    if (HomesObj.removeAll(player.getUuidAsString() + " " + player.getName().getString()))
                    {
                        LanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation("removeAllHomes"),
                                "cyansh.msg.removeAllHomes"
                        );
                    }
                    else
                    {
                        LanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation(ERROR + "noHomes"),
                                "cyansh.msg.noHomes"
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /rename-home <home_name> <new_home_name>}
     * <p>
     * Renames the location
     */
    public static int renameHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (LibUtils.isPlayer(context.getSource()))
        {
            if (LibUtils.isOptionAllowed(player, LibConfig.getBoolOption("allowHomes"), "locationsDisabled"))
            {
                if (LibUtils.hasPermission(player, LibConfig.getIntOption("minOpLevelExeEditLocation")))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String newHomeName = StringArgumentType.getString(context, "new_home_name");

                    String playerKey = player.getUuidAsString() + " " + player.getName().getString();

                    if (HomesObj.homeExists(playerKey, homeName))
                    {
                        HomesObj.rename(playerKey, homeName, newHomeName);

                        LanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation("renameHome"),
                                "cyansh.msg.renameHome",
                                Formatting.YELLOW + homeName,
                                Formatting.YELLOW + newHomeName
                        );
                    }
                    else
                    {
                        LanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation(ERROR + "homeNotFound"),
                                "cyansh.msg.homeNotFound",
                                homeName
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /home <home_name>} or {@code /h <home_name>}
     * <p>
     * Teleports the player to the given home
     */
    public static int goToHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (LibUtils.isPlayer(context.getSource()))
        {
            if (LibUtils.isOptionAllowed(player, LibConfig.getBoolOption("allowHomes"), "homesDisabled"))
            {
                if (LibUtils.hasPermission(player, LibConfig.getIntOption("minOpLevelExeHomes")))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String playerKey = player.getUuidAsString() + " " + player.getName().getString();

                    if (HomesObj.homeExists(playerKey, homeName))
                    {
                        Homes.Home home = HomesObj.getPlayerHome(playerKey, homeName);

                        switch (home.getDimension())
                        {
                            case "overworld" ->
                                    player.teleport(player.getServer().getWorld(World.OVERWORLD), home.getX(), home.getY(), home.getZ(), home.getYaw(), home.getPitch());
                            case "nether" ->
                                    player.teleport(player.getServer().getWorld(World.NETHER), home.getX(), home.getY(), home.getZ(), home.getYaw(), home.getPitch());
                            case "end" ->
                                    player.teleport(player.getServer().getWorld(World.END), home.getX(), home.getY(), home.getZ(), home.getYaw(), home.getPitch());
                        }

                        LanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation("goToHome"),
                                "cyansh.msg.goToHome",
                                Formatting.YELLOW + homeName
                        );
                    }
                    else
                    {
                        LanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation(ERROR + "homeNotFound"),
                                "cyansh.msg.homeNotFound",
                                Formatting.YELLOW + homeName
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /get-homes} or {@code /gh}
     * <p>
     * Sends a message in the player's chat with all its homes
     */
    public static int getHomesList(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (LibUtils.isPlayer(context.getSource()))
        {
            if (LibUtils.isOptionAllowed(player, LibConfig.getBoolOption("allowHomes"), "homesDisabled"))
            {
                if (LibUtils.hasPermission(player, LibConfig.getIntOption("minOpLevelExeHomes")))
                {
                    String playerKey = player.getUuidAsString() + " " + player.getName().getString();

                    if (!HomesObj.isEmpty(playerKey))
                    {
                        LanguageUtils.sendPlayerMessageActionBar(player,
                                LanguageUtils.getTranslation("dashSeparation"),
                                "cyansh.msg.dashSeparation",
                                false
                        );
                        LanguageUtils.sendPlayerMessageActionBar(player,
                                LanguageUtils.getTranslation("listHomes"),
                                "cyansh.msg.listHomes",
                                false
                        );

                        HomesObj.getPlayerHomes(playerKey).forEach(home -> LanguageUtils.sendPlayerMessageActionBar(player,
                                        LanguageUtils.getTranslation("getHome"),
                                        "cyansh.msg.getHome",
                                        false,
                                        Formatting.YELLOW + home.getName(),
                                        Formatting.DARK_AQUA + home.getDimension(),
                                        Formatting.DARK_AQUA + home.getDate()
                                )
                        );

                        LanguageUtils.sendPlayerMessageActionBar(player,
                                LanguageUtils.getTranslation("dashSeparation"),
                                "cyansh.msg.dashSeparation",
                                false
                        );
                    }
                    else
                    {
                        LanguageUtils.sendPlayerMessage(player,
                                LanguageUtils.getTranslation(ERROR + "noHomes"),
                                "cyansh.msg.noHomes"
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
