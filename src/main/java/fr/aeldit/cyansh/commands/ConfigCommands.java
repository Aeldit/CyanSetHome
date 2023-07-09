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
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.aeldit.cyanlib.lib.CyanLibCommands;
import fr.aeldit.cyansh.commands.argumentTypes.ArgumentSuggestion;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static fr.aeldit.cyansh.util.EventUtils.transferPropertiesToGson;
import static fr.aeldit.cyansh.util.Utils.*;

public class ConfigCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("cyansh")
                .then(CommandManager.literal("config")
                        .then(CommandManager.argument("optionName", StringArgumentType.string())
                                .suggests((context, builder) -> ArgumentSuggestion.getOptions(builder))
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("booleanValue", BoolArgumentType.bool())
                                                .then(CommandManager.argument("mode", BoolArgumentType.bool())
                                                        .executes(ConfigCommands::setBoolOption)
                                                )
                                                .executes(ConfigCommands::setBoolOptionFromCommand)
                                        )
                                        .then(CommandManager.argument("integerValue", IntegerArgumentType.integer())
                                                .suggests((context, builder) -> ArgumentSuggestion.getInts(builder))
                                                .then(CommandManager.argument("mode", BoolArgumentType.bool())
                                                        .executes(ConfigCommands::setIntOption)
                                                )
                                                .executes(ConfigCommands::setIntOptionFromCommand)
                                        )
                                )
                                .executes(ConfigCommands::getOptionChatConfig)
                        )
                )
                .then(CommandManager.literal("get-config")
                        .executes(ConfigCommands::getConfigOptions)
                )
                .then(CommandManager.literal("reload-translations")
                        .executes(ConfigCommands::reloadTranslations)
                )
                .then(CommandManager.literal("remove-properties-files")
                        .executes(ConfigCommands::removePropertiesFiles)
                )
        );
    }

    /**
     * Called by the command {@code /cyansh reload-translations}
     * <p>
     * Reloads the custom translations
     */
    public static int reloadTranslations(@NotNull CommandContext<ServerCommandSource> context)
    {
        CyanLibCommands.reloadTranslations(context, getDefaultTranslations(), LibUtils);

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyan remove-properties-files}
     * <p>
     * Removes all the properties files
     */
    public static int removePropertiesFiles(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        transferPropertiesToGson();

        if (LibUtils.hasPermission(player, LibConfig.getIntOption("minOpLevelExeEditConfig")))
        {
            if (Files.exists(MOD_PATH))
            {
                File[] listOfFiles = new File(MOD_PATH.toUri()).listFiles();

                if (listOfFiles != null)
                {
                    for (File file : listOfFiles)
                    {
                        if (file.isFile())
                        {
                            if (file.getName().split("\\.")[-1].equals(".properties"))
                            {
                                try
                                {
                                    Files.delete(file.toPath());
                                    LOGGER.info("[CyanSetHome] Delete the file %s".formatted(file.getName()));
                                }
                                catch (IOException e)
                                {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh <optionName> set [booleanValue] [mode]}
     * <p>
     * Changes the option in the {@link fr.aeldit.cyanlib.lib.CyanLibConfig} class to the value [booleanValue] and executes the
     * {@code /cyansh getConfig} command if {@code [mode]} is true, and the command {@code /cyansh config <optionName>} otherwise.
     * This allows to see the changed option in the chat
     */
    public static int setBoolOption(@NotNull CommandContext<ServerCommandSource> context)
    {
        CyanLibCommands.setBoolOption(context, getDefaultTranslations(), LibUtils,
                StringArgumentType.getString(context, "optionName"), BoolArgumentType.getBool(context, "booleanValue"),
                false, BoolArgumentType.getBool(context, "mode")
        );
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh <optionName> set [boolValue]}
     * <p>
     * Changes the option in the {@link fr.aeldit.cyanlib.lib.CyanLibConfig} class to the value [boolValue]
     */
    public static int setBoolOptionFromCommand(@NotNull CommandContext<ServerCommandSource> context)
    {
        CyanLibCommands.setBoolOption(context, getDefaultTranslations(), LibUtils,
                StringArgumentType.getString(context, "optionName"), BoolArgumentType.getBool(context, "booleanValue"),
                true, false
        );
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh <optionName> set [intValue] [mode]}
     * <p>
     * Changes the option in the {@link fr.aeldit.cyanlib.lib.CyanLibConfig} class to the value [intValue] and executes the
     * {@code /cyansh getConfig} command if {@code [mode]} is true, and the command {@code /cyansh config <optionName>} otherwise.
     * This allows to see the changed option in the chat
     */
    public static int setIntOption(@NotNull CommandContext<ServerCommandSource> context)
    {
        CyanLibCommands.setIntOption(context, LibUtils,
                StringArgumentType.getString(context, "optionName"), IntegerArgumentType.getInteger(context, "integerValue"),
                false, BoolArgumentType.getBool(context, "mode")
        );
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh <optionName> set [intValue]}
     * <p>
     * Changes the option in the {@link fr.aeldit.cyanlib.lib.CyanLibConfig} class to the value [intValue]
     */
    public static int setIntOptionFromCommand(@NotNull CommandContext<ServerCommandSource> context)
    {
        CyanLibCommands.setIntOption(context, LibUtils,
                StringArgumentType.getString(context, "optionName"), IntegerArgumentType.getInteger(context, "integerValue"),
                true, false
        );
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh config <optionName>}
     * <p>
     * Send a message in the player's chat with a description of the option {@code optionName} and its value
     */
    public static int getOptionChatConfig(@NotNull CommandContext<ServerCommandSource> context)
    {
        CyanLibCommands.getOptionChatConfig(context, LibUtils, StringArgumentType.getString(context, "optionName"));

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh get-config}
     * <p>
     * Send a messsage in the player's chat with all the mod's options and their values
     */
    public static int getConfigOptions(@NotNull CommandContext<ServerCommandSource> context)
    {
        CyanLibCommands.getConfigOptions(context, LibUtils);

        return Command.SINGLE_SUCCESS;
    }
}
