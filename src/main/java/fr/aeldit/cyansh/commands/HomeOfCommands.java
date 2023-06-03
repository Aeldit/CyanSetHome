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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static fr.aeldit.cyanlib.util.ChatUtils.sendPlayerMessage;
import static fr.aeldit.cyanlib.util.Constants.ERROR;
import static fr.aeldit.cyansh.util.HomeUtils.*;
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
     * Called by the command {@code /homeof <player_name> <home_name>} or {@code /ho <player_name> <home_name>}
     * <p>
     * Teleports the player to the given home
     */
    public static int goToHomeOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (CyanLibUtils.isPlayer(source))
        {
            if (CyanLibUtils.isOptionAllowed(player, CyanSHMidnightConfig.allowHomesOf, "homesOfDisabled"))
            {
                String playerName = StringArgumentType.getString(context, "player_name");

                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeMisc) || trustPlayer(playerName, player.getName().getString()))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");

                    boolean fileFound = false;
                    File currentHomesDir = new File(homesPath.toUri());
                    checkOrCreateHomesDir();
                    File[] listOfFiles = currentHomesDir.listFiles();

                    if (listOfFiles != null)
                    {
                        for (File file : listOfFiles)
                        {
                            if (file.isFile())
                            {
                                if (file.getName().split("_")[1].equals(playerName + ".json"))
                                {
                                    fileFound = true;

                                    try
                                    {
                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        Reader reader = Files.newBufferedReader(file.toPath());
                                        List<Home> homes = List.of(gson.fromJson(reader, Home[].class));
                                        reader.close();

                                        if (homeExists(homes, homeName))
                                        {
                                            Home home = homes.get(getHomeIndex(homes, homeName));

                                            switch (home.dimension())
                                            {
                                                case "overworld" ->
                                                        player.teleport(player.getServer().getWorld(World.OVERWORLD),
                                                                home.x(), home.y(), home.z(), home.yaw(), home.pitch());
                                                case "nether" ->
                                                        player.teleport(player.getServer().getWorld(World.NETHER),
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
                                    catch (IOException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        if (!fileFound)
                        {
                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation(ERROR + "noHomesOf"),
                                    "cyansh.message.noHomesOf",
                                    CyanSHMidnightConfig.errorToActionBar,
                                    CyanSHMidnightConfig.useCustomTranslations
                            );
                        }
                    }
                }
                else
                {
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "notOpOrTrusted"),
                            "cyansh.message.notOpOrTrusted",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useCustomTranslations
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /removehomeof <home_name>} or {@code /rho <home_name>}
     * <p>
     * Removes the given home of the given player
     */
    public static int removeHomeOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (CyanLibUtils.isPlayer(source))
        {
            if (CyanLibUtils.isOptionAllowed(player, CyanSHMidnightConfig.allowHomesOf, "homesOfDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeMisc) || trustPlayer(trustingPlayer, player.getName().getString()))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    File currentHomesDir = new File(homesPath.toUri());
                    checkOrCreateHomesDir();
                    File[] listOfFiles = currentHomesDir.listFiles();
                    boolean fileFound = false;

                    if (listOfFiles != null)
                    {
                        for (File file : listOfFiles)
                        {
                            if (file.isFile())
                            {
                                try
                                {
                                    if (file.getName().split("_")[1].equals(trustingPlayer + ".json"))
                                    {
                                        fileFound = true;
                                        Gson gson = new Gson();
                                        Reader reader = Files.newBufferedReader(file.toPath());
                                        List<Home> homes = List.of(gson.fromJson(reader, Home[].class));
                                        reader.close();
                                        ArrayList<Home> mutableHomes = new ArrayList<>(homes);

                                        if (homeExists(homes, homeName))
                                        {
                                            mutableHomes.remove(getHomeIndex(homes, homeName));

                                            Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                                            Writer writer = Files.newBufferedWriter(file.toPath());
                                            gsonWriter.toJson(mutableHomes, writer);
                                            writer.close();

                                            sendPlayerMessage(player,
                                                    CyanSHLanguageUtils.getTranslation("removeHomeOf"),
                                                    "cyansh.message.removeHomeOf",
                                                    CyanSHMidnightConfig.msgToActionBar,
                                                    CyanSHMidnightConfig.useCustomTranslations,
                                                    Formatting.YELLOW + homeName,
                                                    Formatting.AQUA + trustingPlayer
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

                                    if (!fileFound)
                                    {
                                        sendPlayerMessage(player,
                                                CyanSHLanguageUtils.getTranslation(ERROR + "noHomesOf"),
                                                "cyansh.message.noHomesOf",
                                                CyanSHMidnightConfig.errorToActionBar,
                                                CyanSHMidnightConfig.useCustomTranslations
                                        );
                                    }
                                }
                                catch (IOException e)
                                {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
                else
                {
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "notOpOrTrusted"),
                            "cyansh.message.notOpOrTrusted",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useCustomTranslations
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
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (CyanLibUtils.isPlayer(source))
        {
            if (CyanLibUtils.isOptionAllowed(player, CyanSHMidnightConfig.allowHomesOf, "homesOfDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");
                if (Objects.equals(player.getName().getString(), trustingPlayer))
                {
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "useSelfHomes"),
                            "cyansh.message.useSelfHomes",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useCustomTranslations
                    );
                }
                else if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeMisc) || trustPlayer(trustingPlayer, player.getName().getString()))
                {
                    File currentHomesDir = new File(homesPath.toUri());
                    checkOrCreateHomesDir();
                    File[] listOfFiles = currentHomesDir.listFiles();
                    boolean isTrusted = false;
                    if (listOfFiles != null)
                    {
                        for (File file : listOfFiles)
                        {
                            if (file.isFile())
                            {
                                if (file.getName().split("_")[1].equals(trustingPlayer + ".json"))
                                {
                                    isTrusted = true;
                                    try
                                    {
                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        Reader reader = Files.newBufferedReader(file.toPath());
                                        List<Home> homes = List.of(gson.fromJson(reader, Home[].class));
                                        reader.close();

                                        if (homes.size() != 0)
                                        {
                                            sendPlayerMessage(player,
                                                    CyanSHLanguageUtils.getTranslation("dashSeparation"),
                                                    "cyansh.message.getDescription.dashSeparation",
                                                    false,
                                                    CyanSHMidnightConfig.useCustomTranslations
                                            );

                                            sendPlayerMessage(player,
                                                    CyanSHLanguageUtils.getTranslation("listHomesOf"),
                                                    "cyansh.message.listHomesOf",
                                                    false,
                                                    CyanSHMidnightConfig.useCustomTranslations,
                                                    Formatting.AQUA + trustingPlayer
                                            );

                                            for (Home home : homes)
                                            {
                                                sendPlayerMessage(player,
                                                        CyanSHLanguageUtils.getTranslation("getHome"),
                                                        "cyansh.message.getHome",
                                                        false,
                                                        CyanSHMidnightConfig.useCustomTranslations,
                                                        Formatting.YELLOW + home.name(),
                                                        Formatting.DARK_AQUA + home.dimension(), Formatting.DARK_AQUA + home.date()
                                                );
                                            }

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
                                                    CyanSHLanguageUtils.getTranslation(ERROR + "noHomesOf"),
                                                    "cyansh.message.noHomesOf",
                                                    CyanSHMidnightConfig.errorToActionBar,
                                                    CyanSHMidnightConfig.useCustomTranslations
                                            );
                                        }
                                    }
                                    catch (IOException e)
                                    {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                        if (!isTrusted)
                        {
                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation(ERROR + "playerNotTrusting"),
                                    "cyansh.message.playerNotTrusting",
                                    CyanSHMidnightConfig.errorToActionBar,
                                    CyanSHMidnightConfig.useCustomTranslations
                            );
                        }
                    }
                    else
                    {
                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation(ERROR + "noHomesOf"),
                                "cyansh.message.noHomesOf",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useCustomTranslations
                        );
                    }
                }
                else
                {
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "notOpOrTrusted"),
                            "cyansh.message.notOpOrTrusted",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useCustomTranslations
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
