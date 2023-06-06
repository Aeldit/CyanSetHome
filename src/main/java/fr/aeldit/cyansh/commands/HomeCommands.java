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
import fr.aeldit.cyansh.config.CyanSHMidnightConfig;
import fr.aeldit.cyansh.util.Home;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static fr.aeldit.cyanlib.util.ChatUtils.sendPlayerMessage;
import static fr.aeldit.cyanlib.util.Constants.ERROR;
import static fr.aeldit.cyansh.util.GsonUtils.*;
import static fr.aeldit.cyansh.util.HomeUtils.*;
import static fr.aeldit.cyansh.util.Utils.*;

public class HomeCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("sethome")
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

        dispatcher.register(CommandManager.literal("removehome")
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
        dispatcher.register(CommandManager.literal("removeallhomes")
                .executes(HomeCommands::removeAllHomes)
        );

        dispatcher.register(CommandManager.literal("gethomes")
                .executes(HomeCommands::getHomesList)
        );
        dispatcher.register(CommandManager.literal("gh")
                .executes(HomeCommands::getHomesList)

        );
    }

    /**
     * Called by the command {@code /sethome <home_name>} or {@code /sh <home_name>}
     * <p>
     * Creates a home with the player current position (dimension, x, y, z, yaw, pitch, date)
     */
    public static int setHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            if (CyanLibUtils.isOptionAllowed(player, CyanSHMidnightConfig.allowHomes, "homesDisabled"))
            {
                if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String dimension = "overworld";

                    Path currentHomesPath = Path.of(HOMES_PATH + "/" + player.getUuidAsString() + "_" + player.getName().getString() + ".json");

                    if (player.getWorld() == player.getServer().getWorld(World.OVERWORLD))
                    {
                        dimension = "overworld";
                    }
                    else if (player.getWorld() == player.getServer().getWorld(World.NETHER))
                    {
                        dimension = "nether";
                    }
                    else if (player.getWorld() == player.getServer().getWorld(World.END))
                    {
                        dimension = "end";
                    }

                    checkOrCreateFile(currentHomesPath);

                    try
                    {
                        Home home = new Home(
                                homeName,
                                dimension,
                                player.getX(), player.getY(), player.getZ(),
                                player.getYaw(), player.getPitch(),
                                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime())
                        );

                        if (Files.readAllLines(currentHomesPath).isEmpty())
                        {
                            writeGson(currentHomesPath, List.of(home));

                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation("setHome"),
                                    "cyansh.message.setHome",
                                    CyanSHMidnightConfig.msgToActionBar,
                                    CyanSHMidnightConfig.useCustomTranslations,
                                    Formatting.YELLOW + homeName
                            );
                        }
                        else
                        {
                            ArrayList<Home> homes = readHomeFile(currentHomesPath);

                            if (homes.size() < CyanSHMidnightConfig.maxHomes)
                            {
                                if (!homeExists(homes, homeName))
                                {
                                    homes.add(home);
                                    writeGson(currentHomesPath, homes);

                                    sendPlayerMessage(player,
                                            CyanSHLanguageUtils.getTranslation("setHome"),
                                            "cyansh.message.setHome",
                                            CyanSHMidnightConfig.msgToActionBar,
                                            CyanSHMidnightConfig.useCustomTranslations,
                                            Formatting.YELLOW + homeName
                                    );
                                }
                                else
                                {
                                    sendPlayerMessage(player,
                                            CyanSHLanguageUtils.getTranslation(ERROR + "homeAlreadyExists"),
                                            "cyansh.message.homeAlreadyExists",
                                            CyanSHMidnightConfig.errorToActionBar,
                                            CyanSHMidnightConfig.useCustomTranslations
                                    );
                                }
                            }
                            else
                            {
                                sendPlayerMessage(player,
                                        CyanSHLanguageUtils.getTranslation(ERROR + "maxHomesReached"),
                                        "cyansh.message.maxHomesReached",
                                        CyanSHMidnightConfig.errorToActionBar,
                                        CyanSHMidnightConfig.useCustomTranslations,
                                        Formatting.GOLD + String.valueOf(CyanSHMidnightConfig.maxHomes)
                                );
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
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

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            if (CyanLibUtils.isOptionAllowed(player, CyanSHMidnightConfig.allowHomes, "homesDisabled"))
            {
                if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    Path currentHomesPath = Path.of(HOMES_PATH + "/" + player.getUuidAsString() + "_" + player.getName().getString() + ".json");

                    checkOrCreateFile(currentHomesPath);

                    ArrayList<Home> homes = readHomeFile(currentHomesPath);

                    if (homeExists(homes, homeName))
                    {
                        Home home = homes.get(getHomeIndex(homes, homeName));

                        switch (home.dimension())
                        {
                            case "overworld" -> player.teleport(player.getServer().getWorld(World.OVERWORLD),
                                    home.x(), home.y(), home.z(), home.yaw(), home.pitch());
                            case "nether" -> player.teleport(player.getServer().getWorld(World.NETHER),
                                    home.x(), home.y(), home.z(), home.yaw(), home.pitch());
                            case "end" -> player.teleport(player.getServer().getWorld(World.END),
                                    home.x(), home.y(), home.z(), home.yaw(), home.pitch());
                        }

                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation("goToHome"),
                                "cyansh.message.goToHome",
                                CyanSHMidnightConfig.msgToActionBar,
                                CyanSHMidnightConfig.useCustomTranslations,
                                Formatting.YELLOW + homeName
                        );
                    }
                    else
                    {
                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation(ERROR + "homeNotFound"),
                                "cyansh.message.homeNotFound",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useCustomTranslations,
                                Formatting.YELLOW + homeName
                        );
                    }

                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /removehome <home_name>} or {@code /rh <home_name>}
     * <p>
     * Removes the given home
     */
    public static int removeHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            if (CyanLibUtils.isOptionAllowed(player, CyanSHMidnightConfig.allowHomes, "homesDisabled"))
            {
                if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    Path currentHomesPath = Path.of(HOMES_PATH + "/" + player.getUuidAsString() + "_" + player.getName().getString() + ".json");

                    checkOrCreateFile(currentHomesPath);

                    ArrayList<Home> homes = readHomeFile(currentHomesPath);

                    if (homeExists(homes, homeName))
                    {
                        homes.remove(getHomeIndex(homes, homeName));

                        writeGsonOrDeleteFile(currentHomesPath, homes);

                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation("removeHome"),
                                "cyansh.message.removeHome",
                                CyanSHMidnightConfig.msgToActionBar,
                                CyanSHMidnightConfig.useCustomTranslations,
                                Formatting.YELLOW + homeName
                        );
                    }
                    else
                    {
                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation(ERROR + "homeNotFound"),
                                "cyansh.message.homeNotFound",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useCustomTranslations,
                                Formatting.YELLOW + homeName
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /removeallhomes}
     * <p>
     * Removes all the homes
     */
    public static int removeAllHomes(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            if (CyanLibUtils.isOptionAllowed(player, CyanSHMidnightConfig.allowHomes, "homesDisabled"))
            {
                if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    Path currentHomesPath = Path.of(HOMES_PATH + "/" + player.getUuidAsString() + "_" + player.getName().getString() + ".json");

                    if (Files.exists(currentHomesPath))
                    {
                        try
                        {
                            Files.delete(currentHomesPath);

                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation("removeAllHomes"),
                                    "cyansh.message.removeAllHomes",
                                    CyanSHMidnightConfig.msgToActionBar,
                                    CyanSHMidnightConfig.useCustomTranslations
                            );
                        }
                        catch (IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                    else
                    {
                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation(ERROR + "noHomes"),
                                "cyansh.message.noHomes",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useCustomTranslations
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /gethomes} or {@code /gh}
     * <p>
     * Sends a message in the player's chat with all its homes
     */
    public static int getHomesList(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            if (CyanLibUtils.isOptionAllowed(player, CyanSHMidnightConfig.allowHomes, "homesDisabled"))
            {
                if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    Path currentHomesPath = Path.of(HOMES_PATH + "/" + player.getUuidAsString() + "_" + player.getName().getString() + ".json");

                    if (Files.exists(currentHomesPath))
                    {
                        ArrayList<Home> homes = readHomeFile(currentHomesPath);

                        if (!homes.isEmpty())
                        {
                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation("dashSeparation"),
                                    "cyansh.message.getDescription.dashSeparation",
                                    false,
                                    CyanSHMidnightConfig.useCustomTranslations
                            );
                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation("listHomes"),
                                    "cyansh.message.listHomes",
                                    false,
                                    CyanSHMidnightConfig.useCustomTranslations
                            );

                            homes.forEach(home -> sendPlayerMessage(player,
                                            CyanSHLanguageUtils.getTranslation("getHome"),
                                            "cyansh.message.getHome",
                                            false,
                                            CyanSHMidnightConfig.useCustomTranslations,
                                            Formatting.YELLOW + home.name(),
                                            Formatting.DARK_AQUA + home.dimension(),
                                            Formatting.DARK_AQUA + home.date()
                                    )
                            );

                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation("dashSeparation"),
                                    "cyansh.message.getDescription.dashSeparation",
                                    false,
                                    CyanSHMidnightConfig.useCustomTranslations
                            );
                        }
                        else
                        {
                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation(ERROR + "noHomes"),
                                    "cyansh.message.noHomes",
                                    CyanSHMidnightConfig.errorToActionBar,
                                    CyanSHMidnightConfig.useCustomTranslations
                            );
                        }
                    }
                    else
                    {
                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation(ERROR + "noHomes"),
                                "cyansh.message.noHomes",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useCustomTranslations
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
