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
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static fr.aeldit.cyanlib.util.ChatUtils.sendPlayerMessage;
import static fr.aeldit.cyanlib.util.Constants.*;
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
                                                .executes(ConfigCommands::setBoolOption)
                                        )
                                        .then(CommandManager.argument("integerValue", IntegerArgumentType.integer())
                                                .suggests((context, builder) -> ArgumentSuggestion.getInts(builder))
                                                .executes(ConfigCommands::setIntOption)
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
            CyanSHLanguageUtils.loadLanguage(Utils.getDefaultTranslations(true));
            sendPlayerMessage(player,
                    CyanSHLanguageUtils.getTranslation("translationsReloaded"),
                    "cyansh.message.translationsReloaded",
                    CyanSHMidnightConfig.msgToActionBar,
                    CyanSHMidnightConfig.useCustomTranslations
            );
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh config <optionName> set <booleanValue>}
     * <p>
     * Sets the option {@code <optionName>} to the value {@code <booleanValue>} if the option exists
     */
    public static int setBoolOption(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (CyanLibUtils.isPlayer(source))
        {
            if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeEditConfig))
            {
                String option = StringArgumentType.getString(context, "optionName");
                boolean value = BoolArgumentType.getBool(context, "booleanValue");

                if (Utils.getOptionsList().get("booleans").contains(option))
                {
                    CyanSHMidnightConfig.setBoolOption(option, value);

                    source.getServer().getCommandManager().executeWithPrefix(source, "/cyansh config %s".formatted(option));
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(SET + option),
                            "cyansh.message.set.%s".formatted(option),
                            CyanSHMidnightConfig.msgToActionBar,
                            CyanSHMidnightConfig.useCustomTranslations,
                            value ? Formatting.GREEN + "ON" : Formatting.RED + "OFF"
                    );
                }
                else
                {
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "optionNotFound"),
                            "cyansh.message.error.optionNotFound",
                            CyanSHMidnightConfig.msgToActionBar,
                            CyanSHMidnightConfig.useCustomTranslations
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh config <optionName> set <integerValue>}
     * <p>
     * Sets the option {@code <optionName>} to the value {@code <integerValue>} if the option exists
     * and the value is correct
     */
    public static int setIntOption(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (CyanLibUtils.isPlayer(source))
        {
            if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeEditConfig))
            {
                String option = StringArgumentType.getString(context, "optionName");
                int value = IntegerArgumentType.getInteger(context, "integerValue");

                if (Utils.getOptionsList().get("integers").contains(option))
                {
                    CyanSHMidnightConfig.setIntOption(option, value);

                    source.getServer().getCommandManager().executeWithPrefix(source, "/cyansh config %s".formatted(option));
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(SET + option),
                            "cyansh.message.set.%s".formatted(option),
                            CyanSHMidnightConfig.msgToActionBar,
                            CyanSHMidnightConfig.useCustomTranslations,
                            Formatting.GOLD + String.valueOf(value)
                    );
                }
                else
                {
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "optionNotFound"),
                            "cyansh.message.error.optionNotFound",
                            CyanSHMidnightConfig.msgToActionBar,
                            CyanSHMidnightConfig.useCustomTranslations
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

                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation("dashSeparation"),
                            "cyansh.message.getDescription.dashSeparation",
                            false,
                            CyanSHMidnightConfig.useCustomTranslations
                    );

                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(DESC + optionName),
                            "cyansh.message.getDescription.%s".formatted(optionName),
                            false,
                            CyanSHMidnightConfig.useCustomTranslations
                    );

                    if (value instanceof Boolean currentValue)
                    {
                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation("currentValue"),
                                "cyansh.message.currentValue",
                                false,
                                CyanSHMidnightConfig.useCustomTranslations,
                                currentValue ? Text.literal(Formatting.GREEN + "ON (click to change)").
                                        setStyle(Style.EMPTY.withClickEvent(
                                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set false".formatted(optionName)))
                                        ) : Text.literal(Formatting.RED + "OFF (click to change)").
                                        setStyle(Style.EMPTY.withClickEvent(
                                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set true".formatted(optionName)))
                                        )
                        );
                    }
                    else if (value instanceof Integer currentValue)
                    {
                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation("currentValue"),
                                "cyansh.message.currentValue",
                                false,
                                CyanSHMidnightConfig.useCustomTranslations,
                                Formatting.GOLD + String.valueOf(currentValue)
                        );

                        if (optionName.startsWith("minOpLevelExe"))
                        {
                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation("setValue"),
                                    "cyansh.message.setValue",
                                    false,
                                    CyanSHMidnightConfig.useCustomTranslations,
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "0")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 0".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "1")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 1".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "2")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 2".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "3")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 3".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "4")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 4".formatted(optionName)))
                                            )
                            );
                        }
                        else
                        {
                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation("setValue"),
                                    "cyansh.message.setValue",
                                    false,
                                    CyanSHMidnightConfig.useCustomTranslations,
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "8")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 8".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "16")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 16".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "32")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 32".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "64")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 64".formatted(optionName)))
                                            ),
                                    Text.literal(Formatting.DARK_GREEN + (Formatting.BOLD + "128")).
                                            setStyle(Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set 128".formatted(optionName)))
                                            )
                            );
                        }
                    }
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation("dashSeparation"),
                            "cyansh.message.getDescription.dashSeparation",
                            false,
                            CyanSHMidnightConfig.useCustomTranslations
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /cyansh config}
     * <p>
     * Send a messsage in the player's chat with all the mod's options and their values
     */
    public static int getConfigOptions(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();
        String currentTrad = null;

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            if (CyanLibUtils.hasPermission(player, CyanSHMidnightConfig.minOpLevelExeEditConfig))
            {
                sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation("dashSeparation"),
                        "cyansh.message.getDescription.dashSeparation",
                        false,
                        CyanSHMidnightConfig.useCustomTranslations
                );
                sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation(GETCFG + "header"),
                        "cyansh.message.getCfg.header",
                        false,
                        CyanSHMidnightConfig.useCustomTranslations
                );

                for (Map.Entry<String, Object> entry : CyanSHMidnightConfig.getAllOptionsMap().entrySet())
                {
                    String key = entry.getKey();

                    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
                    {
                        currentTrad = CyanSHLanguageUtils.getTranslation(GETCFG + key);
                    }

                    if (entry.getValue() instanceof Boolean value)
                    {
                        sendPlayerMessage(player,
                                currentTrad,
                                "cyansh.message.getCfg.%s".formatted(key),
                                false,
                                CyanSHMidnightConfig.useCustomTranslations,
                                value ? Text.literal(Formatting.GREEN + "ON").
                                        setStyle(Style.EMPTY.withClickEvent(
                                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set false".formatted(key)))
                                        ) : Text.literal(Formatting.RED + "OFF").
                                        setStyle(Style.EMPTY.withClickEvent(
                                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cyansh config %s set true".formatted(key)))
                                        )
                        );
                    }
                    else if (entry.getValue() instanceof Integer value)
                    {
                        sendPlayerMessage(player,
                                currentTrad,
                                "cyansh.message.getCfg.%s".formatted(key),
                                false,
                                CyanSHMidnightConfig.useCustomTranslations,
                                Formatting.GOLD + Integer.toString(value)
                        );
                    }
                }
                sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation("dashSeparation"),
                        "cyansh.message.getDescription.dashSeparation",
                        false,
                        CyanSHMidnightConfig.useCustomTranslations
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
