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
import fr.aeldit.cyansh.commands.argumentTypes.ArgumentSuggestion;
import fr.aeldit.cyansh.config.CyanSHMidnightConfig;
import fr.aeldit.cyansh.util.Utils;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static fr.aeldit.cyanlib.util.Constants.*;
import static fr.aeldit.cyansh.util.EventUtils.transferPropertiesToGson;
import static fr.aeldit.cyansh.util.Utils.CyanLibUtils;
import static fr.aeldit.cyansh.util.Utils.CyanSHLanguageUtils;

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
                .then(CommandManager.literal("getConfig")
                        .executes(ConfigCommands::getConfigOptions)
                )
                .then(CommandManager.literal("reloadTranslations")
                        .executes(ConfigCommands::reloadTranslations)
                )
                .then(CommandManager.literal("removepropertiesfiles")
                        .executes(ConfigCommands::removePropertiesFiles)
                )
        );
    }

    /**
     * Called by the command {@code /cyansh reloadTranslations}
     * <p>
     * Reloads the custom translations
     */
    public static int reloadTranslations(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            CyanSHLanguageUtils.loadLanguage(Utils.getDefaultTranslations());
            CyanLibUtils.sendPlayerMessage(player,
                    CyanSHLanguageUtils.getTranslation("translationsReloaded"),
                    "cyansh.message.translationsReloaded"
            );
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyan removepropertiesfiles}
     * <p>
     * Removes all the properties files
     */
    public static int removePropertiesFiles(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        transferPropertiesToGson();

        if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeEditConfig))
        {
            Utils.removePropertiesFiles(player);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh <optionName> set [booleanValue] [mode]}
     * <p>
     * Changes the option in the {@link CyanSHMidnightConfig} class to the value [booleanValue] and executes the
     * {@code /cyansh getConfig} command if {@code [mode]} is true, and the command {@code /cyansh config <optionName>} otherwise.
     * This allows to see the changed option in the chat
     */
    public static int setBoolOption(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();

        if (CyanLibUtils.isPlayer(source))
        {
            if (CyanLibUtils.hasPermission(source.getPlayer(), CyanSHMidnightConfig.minOpLevelExeEditConfig))
            {
                String option = StringArgumentType.getString(context, "optionName");

                if (Utils.getOptionsList().get("booleans").contains(option))
                {
                    CyanSHMidnightConfig.setBoolOption(option, BoolArgumentType.getBool(context, "booleanValue"));

                    if (BoolArgumentType.getBool(context, "mode"))
                    {
                        source.getServer().getCommandManager().executeWithPrefix(source, "/cyansh getConfig");
                    }
                    else
                    {
                        source.getServer().getCommandManager().executeWithPrefix(source, "/cyansh config %s".formatted(option));
                    }
                }
                else
                {
                    CyanLibUtils.sendPlayerMessage(source.getPlayer(),
                            CyanSHLanguageUtils.getTranslation(ERROR + "optionNotFound"),
                            "cyansh.message.error.optionNotFound"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh <optionName> set [boolValue]}
     * <p>
     * Changes the option in the {@link CyanSHMidnightConfig} class to the value [boolValue]
     */
    public static int setBoolOptionFromCommand(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeEditConfig))
            {
                String option = StringArgumentType.getString(context, "optionName");
                boolean value = BoolArgumentType.getBool(context, "booleanValue");

                if (Utils.getOptionsList().get("booleans").contains(option))
                {
                    CyanSHMidnightConfig.setBoolOption(option, value);

                    CyanLibUtils.sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(SET + option),
                            "cyansh.message.set.%s".formatted(option),
                            value ? Formatting.GREEN + "ON" : Formatting.RED + "OFF"
                    );
                }
                else
                {
                    CyanLibUtils.sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "optionNotFound"),
                            "cyansh.message.error.optionNotFound"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh <optionName> set [intValue] [mode]}
     * <p>
     * Changes the option in the {@link CyanSHMidnightConfig} class to the value [intValue] and executes the
     * {@code /cyansh getConfig} command if {@code [mode]} is true, and the command {@code /cyansh config <optionName>} otherwise.
     * This allows to see the changed option in the chat
     */
    public static int setIntOption(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();

        if (CyanLibUtils.isPlayer(source))
        {
            if (CyanLibUtils.hasPermission(source.getPlayer(), CyanSHMidnightConfig.minOpLevelExeEditConfig))
            {
                String option = StringArgumentType.getString(context, "optionName");

                if (Utils.getOptionsList().get("integers").contains(option))
                {
                    CyanSHMidnightConfig.setIntOption(option, IntegerArgumentType.getInteger(context, "integerValue"));

                    if (BoolArgumentType.getBool(context, "mode"))
                    {
                        source.getServer().getCommandManager().executeWithPrefix(source, "/cyansh getConfig");
                    }
                    else
                    {
                        source.getServer().getCommandManager().executeWithPrefix(source, "/cyansh config %s".formatted(option));
                    }
                }
                else
                {
                    CyanLibUtils.sendPlayerMessage(source.getPlayer(),
                            CyanSHLanguageUtils.getTranslation(ERROR + "optionNotFound"),
                            "cyansh.message.error.optionNotFound"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh <optionName> set [intValue]}
     * <p>
     * Changes the option in the {@link CyanSHMidnightConfig} class to the value [intValue]
     */
    public static int setIntOptionFromCommand(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeEditConfig))
            {
                String option = StringArgumentType.getString(context, "optionName");
                int value = IntegerArgumentType.getInteger(context, "integerValue");

                if (Utils.getOptionsList().get("integers").contains(option))
                {
                    CyanSHMidnightConfig.setIntOption(option, value);

                    CyanLibUtils.sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(SET + option),
                            "cyansh.message.set.%s".formatted(option),
                            Formatting.GOLD + String.valueOf(value)
                    );
                }
                else
                {
                    CyanLibUtils.sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "optionNotFound"),
                            "cyansh.message.error.optionNotFound"
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh config <optionName>}
     * <p>
     * Send a message in the player's chat with a description of the option {@code optionName} and its value
     */
    public static int getOptionChatConfig(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeEditConfig))
            {
                String optionName = StringArgumentType.getString(context, "optionName");

                if (Utils.getOptionsList().get("booleans").contains(optionName) || Utils.getOptionsList().get("integers").contains(optionName))
                {
                    Object value = CyanSHMidnightConfig.getAllOptionsMap().get(optionName);

                    CyanLibUtils.sendPlayerMessageActionBar(player,
                            CyanSHLanguageUtils.getTranslation("dashSeparation"),
                            "cyansh.message.getDescription.dashSeparation",
                            false
                    );
                    CyanLibUtils.sendPlayerMessageActionBar(player,
                            CyanSHLanguageUtils.getTranslation(DESC + optionName),
                            "cyansh.message.getDescription.%s".formatted(optionName),
                            false
                    );

                    if (value instanceof Boolean currentValue)
                    {
                        CyanLibUtils.sendPlayerMessageActionBar(player,
                                CyanSHLanguageUtils.getTranslation("currentValue"),
                                "cyansh.message.currentValue",
                                false,
                                currentValue ? Text.literal(Formatting.GREEN + "ON (click to change)").
                                        setStyle(Style.EMPTY.withClickEvent(
                                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set false false".formatted(optionName)))
                                        ) : Text.literal(Formatting.RED + "OFF (click to change)").
                                        setStyle(Style.EMPTY.withClickEvent(
                                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set true false".formatted(optionName)))
                                        )
                        );
                    }
                    else if (value instanceof Integer currentValue)
                    {
                        CyanLibUtils.sendPlayerMessageActionBar(player,
                                CyanSHLanguageUtils.getTranslation("currentValue"),
                                "cyansh.message.currentValue",
                                false,
                                Formatting.GOLD + String.valueOf(currentValue)
                        );

                        if (optionName.startsWith("minOpLevelExe"))
                        {
                            CyanLibUtils.sendPlayerMessageActionBar(player,
                                    CyanSHLanguageUtils.getTranslation("setValue"),
                                    "cyansh.message.setValue",
                                    false,
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "0")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 0 false".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "1")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 1 false".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "2")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 2 false".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "3")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 3 false".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "4")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 4 false".formatted(optionName)))
                                            )
                            );
                        }
                        else
                        {
                            CyanLibUtils.sendPlayerMessageActionBar(player,
                                    CyanSHLanguageUtils.getTranslation("setValue"),
                                    "cyansh.message.setValue",
                                    false,
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "8")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 8 false".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "16")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 16 false".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "32")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 32 false".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "64")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 64 false".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "128")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 128 false".formatted(optionName)))
                                            )
                            );
                        }
                    }
                    CyanLibUtils.sendPlayerMessageActionBar(player,
                            CyanSHLanguageUtils.getTranslation("dashSeparation"),
                            "cyansh.message.getDescription.dashSeparation",
                            false
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh getConfig}
     * <p>
     * Send a messsage in the player's chat with all the mod's options and their values
     */
    public static int getConfigOptions(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeEditConfig))
            {
                CyanLibUtils.sendPlayerMessageActionBar(player,
                        CyanSHLanguageUtils.getTranslation("dashSeparation"),
                        "cyansh.message.getDescription.dashSeparation",
                        false
                );
                CyanLibUtils.sendPlayerMessageActionBar(player,
                        CyanSHLanguageUtils.getTranslation(GETCFG + "header"),
                        "cyansh.message.getCfg.header",
                        false
                );

                for (Map.Entry<String, Object> entry : CyanSHMidnightConfig.getAllOptionsMap().entrySet())
                {
                    String key = entry.getKey();
                    String currentTrad = CyanSHLanguageUtils.getTranslation(GETCFG + key);

                    if (entry.getValue() instanceof Boolean value)
                    {
                        CyanLibUtils.sendPlayerMessageActionBar(player,
                                currentTrad,
                                "cyansh.message.getCfg.%s".formatted(key),
                                false,
                                value ? Text.literal(Formatting.GREEN + "ON").
                                        setStyle(Style.EMPTY.withClickEvent(
                                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set false true".formatted(key)))
                                        ) : Text.literal(Formatting.RED + "OFF").
                                        setStyle(Style.EMPTY.withClickEvent(
                                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set true true".formatted(key)))
                                        )
                        );
                    }
                    else if (entry.getValue() instanceof Integer value)
                    {
                        CyanLibUtils.sendPlayerMessageActionBar(player,
                                currentTrad,
                                "cyansh.message.getCfg.%s".formatted(key),
                                false,
                                Formatting.GOLD + Integer.toString(value)
                        );
                    }
                }

                CyanLibUtils.sendPlayerMessageActionBar(player,
                        CyanSHLanguageUtils.getTranslation("dashSeparation"),
                        "cyansh.message.getDescription.dashSeparation",
                        false
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
