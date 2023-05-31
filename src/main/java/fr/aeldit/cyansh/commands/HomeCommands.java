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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

import static fr.aeldit.cyanlib.util.ChatUtils.sendPlayerMessage;
import static fr.aeldit.cyanlib.util.Constants.ERROR;
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
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (CyanLibUtils.isPlayer(source))
        {
            if (CyanLibUtils.isOptionAllowed(player, CyanSHMidnightConfig.allowHomes, "homesDisabled"))
            {
                if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String dimension = "overworld";
                    double x = player.getX();
                    double y = player.getY();
                    double z = player.getZ();
                    float yaw = player.getYaw();
                    float pitch = player.getPitch();
                    String date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());

                    ServerWorld overworld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
                    ServerWorld nether = Objects.requireNonNull(player.getServer()).getWorld(World.NETHER);
                    ServerWorld end = Objects.requireNonNull(player.getServer()).getWorld(World.END);
                    String playerKey = player.getUuidAsString() + "_" + player.getName().getString();
                    Path currentHomesPath = Path.of(homesPath + "/" + playerKey + ".json");

                    if (player.getWorld() == overworld)
                    {
                        dimension = "overworld";
                    }
                    else if (player.getWorld() == nether)
                    {
                        dimension = "nether";
                    }
                    else if (player.getWorld() == end)
                    {
                        dimension = "end";
                    }

                    checkOrCreateFile(currentHomesPath);
                    try
                    {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        Home home = new Home(homeName, dimension, x, y, z, yaw, pitch, date);

                        if (Files.readAllLines(currentHomesPath).size() == 0)
                        {
                            Writer writer = Files.newBufferedWriter(currentHomesPath);
                            List<Home> homes = List.of(home);
                            gson.toJson(homes, writer);
                            writer.close();

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
                            Reader reader = Files.newBufferedReader(currentHomesPath);
                            List<Home> homes = Arrays.asList(gson.fromJson(reader, Home[].class));
                            reader.close();

                            if (homes.size() < CyanSHMidnightConfig.maxHomes)
                            {
                                if (!homeExists(homes, homeName))
                                {
                                    Writer writer = Files.newBufferedWriter(currentHomesPath);
                                    ArrayList<Home> mutableHomes = new ArrayList<>(homes);
                                    mutableHomes.add(home);
                                    gson.toJson(mutableHomes, writer);
                                    writer.close();

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
                    } catch (IOException e)
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
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (CyanLibUtils.isPlayer(source))
        {
            if (CyanLibUtils.isOptionAllowed(player, CyanSHMidnightConfig.allowHomes, "homesDisabled"))
            {
                if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String playerKey = player.getUuidAsString() + "_" + player.getName().getString();
                    Path currentHomesPath = Path.of(homesPath + "/" + playerKey + ".json");

                    checkOrCreateFile(currentHomesPath);
                    try
                    {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        Reader reader = Files.newBufferedReader(currentHomesPath);
                        List<Home> homes = List.of(gson.fromJson(reader, Home[].class));
                        reader.close();

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

                    } catch (IOException e)
                    {
                        throw new RuntimeException(e);
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
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (CyanLibUtils.isPlayer(source))
        {
            if (CyanLibUtils.isOptionAllowed(player, CyanSHMidnightConfig.allowHomes, "homesDisabled"))
            {
                if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String playerKey = player.getUuidAsString() + "_" + player.getName().getString();
                    Path currentHomesPath = Path.of(homesPath + "/" + playerKey + ".json");

                    checkOrCreateFile(currentHomesPath);
                    try
                    {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        Reader reader = Files.newBufferedReader(currentHomesPath);
                        List<Home> homes = List.of(gson.fromJson(reader, Home[].class));
                        reader.close();

                        if (homeExists(homes, homeName))
                        {
                            ArrayList<Home> mutableHomes = new ArrayList<>(homes);
                            mutableHomes.remove(getHomeIndex(homes, homeName));
                            Writer writer = Files.newBufferedWriter(currentHomesPath);
                            gson.toJson(mutableHomes, writer);
                            writer.close();

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
                    } catch (IOException e)
                    {
                        throw new RuntimeException(e);
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
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (CyanLibUtils.isPlayer(source))
        {
            if (CyanLibUtils.isOptionAllowed(player, CyanSHMidnightConfig.allowHomes, "homesDisabled"))
            {
                if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String playerKey = player.getUuidAsString() + "_" + player.getName().getString();
                    Path currentHomesPath = Path.of(homesPath + "/" + playerKey + ".json");

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
                        } catch (IOException e)
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
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (CyanLibUtils.isPlayer(source))
        {
            if (CyanLibUtils.isOptionAllowed(player, CyanSHMidnightConfig.allowHomes, "homesDisabled"))
            {
                if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String playerKey = player.getUuidAsString() + "_" + player.getName().getString();
                    Path currentHomesPath = Path.of(homesPath + "/" + playerKey + ".json");

                    if (Files.exists(currentHomesPath))
                    {
                        try
                        {
                            Gson gson = new Gson();
                            Reader reader = Files.newBufferedReader(currentHomesPath);
                            Home[] homes = gson.fromJson(reader, Home[].class);
                            reader.close();

                            if (homes.length != 0)
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
                                        CyanSHLanguageUtils.getTranslation(ERROR + "noHomes"),
                                        "cyansh.message.noHomes",
                                        CyanSHMidnightConfig.errorToActionBar,
                                        CyanSHMidnightConfig.useCustomTranslations
                                );
                            }
                        } catch (IOException e)
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
}
