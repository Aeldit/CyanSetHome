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
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static fr.aeldit.cyanlib.util.ChatUtil.sendPlayerMessage;
import static fr.aeldit.cyansh.util.Utils.*;

public class ConfigCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("cyansh")
                .then(CommandManager.literal("getConfig")
                        .executes(ConfigCommands::getConfigOptions)
                )
                .then(CommandManager.literal("config")
                        .then(CommandManager.literal("booleanOption")
                                .then(CommandManager.argument("option", StringArgumentType.string())
                                        .suggests((context4, builder4) -> ArgumentSuggestion.getBoolOptions(builder4))
                                        .then(CommandManager.argument("value", BoolArgumentType.bool())
                                                .executes(ConfigCommands::setBoolOption)
                                        )
                                )
                        )
                        .then(CommandManager.literal("integerOption")
                                .then(CommandManager.argument("option", StringArgumentType.string())
                                        .suggests((context4, builder4) -> ArgumentSuggestion.getIntegerOptions(builder4))
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                                .executes(ConfigCommands::setIntegerOption)
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("description")
                        .then(CommandManager.literal("commands")
                                .then(CommandManager.argument("commandName", StringArgumentType.string())
                                        .suggests((context2, builder2) -> ArgumentSuggestion.getCommands(builder2))
                                        .executes(ConfigCommands::getCommandDescription)
                                )
                                .executes(ConfigCommands::getAllCommandsDescription)
                        )
                        .then(CommandManager.literal("options")
                                .then(CommandManager.literal("booleanOption")
                                        .then(CommandManager.argument("option", StringArgumentType.string())
                                                .suggests((context4, builder4) -> ArgumentSuggestion.getBoolOptions(builder4))
                                                .executes(ConfigCommands::getOptionDescription)
                                        )
                                )
                                .then(CommandManager.literal("integerOption")
                                        .then(CommandManager.argument("option", StringArgumentType.string())
                                                .suggests((context4, builder4) -> ArgumentSuggestion.getIntegerOptions(builder4))
                                                .executes(ConfigCommands::getOptionDescription)
                                        )
                                )
                                .executes(ConfigCommands::getAllOptionsDescription)
                        )
                )
        );
    }

    // Set functions

    /**
     * <p>Called when a player execute the command {@code /cyansh config booleanOptions [optionName] [true|false]}</p>
     *
     * <ul>If the player has a permission level equal to the option MinOpLevelExeModifConfig (see {@link CyanSHMidnightConfig})
     *      <li>-> Set the options to the given value</li>
     * </ul>
     * <ul>Else:
     *      <li>-> The player receive a message saying that it doesn't have the required permission</li>
     * </ul>
     */
    public static int setBoolOption(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else
        {
            if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeEditConfig))
            {
                String option = StringArgumentType.getString(context, "option");
                boolean value = BoolArgumentType.getBool(context, "value");

                CyanSHMidnightConfig.setBoolOption(option, value);
                sendPlayerMessage(player,
                        getConfigSetTraduction(option),
                        "cyansh.message.set.%s".formatted(option),
                        CyanSHMidnightConfig.msgToActionBar,
                        CyanSHMidnightConfig.useTranslations,
                        value ? on : off
                );
            } else
            {
                sendPlayerMessage(player,
                        getErrorTraduction("notOp"),
                        "cyansh.error.notOp",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * <p>Called when a player execute the command {@code /cyansh config integerOptions [optionName] [int]}</p>
     *
     * <ul>If the player has a permission level equal to the option MinOpLevelExeModifConfig (see {@link CyanSHMidnightConfig})
     *      <li>-> Set the options to the given value</li>
     * </ul>
     * <ul>Else:
     *      <li>-> The player receive a message saying that it doesn't have the required permission</li>
     * </ul>
     */
    public static int setIntegerOption(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else
        {
            String option = StringArgumentType.getString(context, "option");
            int value = IntegerArgumentType.getInteger(context, "value");
            if (option.startsWith("minOpLevelExe") && (value < 0 || value > 4))
            {
                sendPlayerMessage(player,
                        getErrorTraduction("incorrectIntOp"),
                        "cyansh.error.incorrectIntOp",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
            } else if (option.startsWith("maxHomes") && (value < 1 || value > 128))
            {
                sendPlayerMessage(player,
                        getErrorTraduction("incorrectIntMaxHomes"),
                        "cyansh.error.incorrectIntMaxHomes",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
            } else
            {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeEditConfig))
                {
                    CyanSHMidnightConfig.setIntOption(option, value);
                    sendPlayerMessage(player,
                            getConfigSetTraduction(option),
                            "cyansh.message.set.%s".formatted(option),
                            CyanSHMidnightConfig.msgToActionBar,
                            CyanSHMidnightConfig.useTranslations,
                            gold + String.valueOf(value)
                    );
                } else
                {
                    sendPlayerMessage(player,
                            getErrorTraduction("notOp"),
                            "cyansh.error.notOp",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useTranslations
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    // Get functions

    /**
     * <p>Called when a player execute the command {@code /cyansh config}</p>
     * <p>Send a player in the player's chat with all the mod's options and their values</p>
     */
    public static int getConfigOptions(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else
        {
            String currentTrad;
            Map<String, Object> options = CyanSHMidnightConfig.getAllOptionsMap();

            sendPlayerMessage(player,
                    getMiscTraduction("dashSeparation"),
                    "cyansh.message.getDescription.dashSeparation",
                    false,
                    CyanSHMidnightConfig.useTranslations
            );
            sendPlayerMessage(player,
                    getConfigTraduction("header"),
                    "cyansh.message.getCfgOptions.header",
                    false,
                    CyanSHMidnightConfig.useTranslations
            );

            for (Map.Entry<String, Object> entry : options.entrySet())
            {
                Object key2 = entry.getKey();
                currentTrad = Utils.getConfigTraduction(entry.getKey());

                if (entry.getValue() instanceof Boolean value)
                {
                    sendPlayerMessage(player,
                            currentTrad,
                            "cyansh.message.getCfgOptions.%s".formatted(key2),
                            false,
                            CyanSHMidnightConfig.useTranslations,
                            value ? on : off
                    );
                } else if (entry.getValue() instanceof Integer value)
                {
                    sendPlayerMessage(player,
                            currentTrad,
                            "cyansh.message.getCfgOptions.%s".formatted(key2),
                            false,
                            CyanSHMidnightConfig.useTranslations,
                            gold + Integer.toString(value)
                    );
                }
            }
            sendPlayerMessage(player,
                    getMiscTraduction("dashSeparation"),
                    "cyansh.message.getDescription.dashSeparation",
                    false,
                    CyanSHMidnightConfig.useTranslations
            );
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * <p>Called when a player execute the command {@code /cyansh description commands [commandName]}</p>
     * <p>Send a message in the player's chat with the description of the command given as argument</p>
     */
    public static int getCommandDescription(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else
        {
            String option = StringArgumentType.getString(context, "commandName");
            sendPlayerMessage(player,
                    getCommandTraduction(option),
                    "cyansh.message.getDescription.command.%s".formatted(option),
                    false,
                    CyanSHMidnightConfig.useTranslations
            );
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * <p>Called when a player execute the command {@code /cyansh description [booleanOption|integerOption] [option]}</p>
     * <p>Send a message in the player's chat with the description of the option given as argument</p>
     */
    public static int getOptionDescription(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else
        {
            String option = StringArgumentType.getString(context, "option");
            sendPlayerMessage(player,
                    getOptionTraduction(option),
                    "cyansh.message.getDescription.options.%s".formatted(option),
                    false,
                    CyanSHMidnightConfig.useTranslations
            );
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * <p>Called when a player execute the command {@code /cyansh description commands}</p>
     * <p>Send a player in the player's chat with all the mod's commands and their description</p>
     */
    public static int getAllCommandsDescription(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else
        {
            List<String> commands = CyanSHMidnightConfig.getCommandsList();
            sendPlayerMessage(player,
                    getMiscTraduction("dashSeparation"),
                    "cyansh.message.getDescription.dashSeparation",
                    false,
                    CyanSHMidnightConfig.useTranslations
            );
            sendPlayerMessage(player,
                    getMiscTraduction("headerDescCmd"),
                    "cyansh.message.getDescription.headerDescCmd",
                    false,
                    CyanSHMidnightConfig.useTranslations
            );

            for (String command : commands)
            {
                sendPlayerMessage(player,
                        getCommandTraduction(command),
                        "cyansh.message.getDescription.command.%s".formatted(command),
                        false,
                        CyanSHMidnightConfig.useTranslations
                );
            }

            sendPlayerMessage(player,
                    getMiscTraduction("dashSeparation"),
                    "cyansh.message.getDescription.dashSeparation",
                    false,
                    CyanSHMidnightConfig.useTranslations
            );
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * <p>Called when a player execute the command {@code /cyansh description options}</p>
     * <p>Send a player in the player's chat with all the mod's options description</p>
     */
    public static int getAllOptionsDescription(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else
        {
            sendPlayerMessage(player,
                    getMiscTraduction("dashSeparation"),
                    "cyansh.message.getDescription.dashSeparation",
                    false,
                    CyanSHMidnightConfig.useTranslations
            );
            sendPlayerMessage(player,
                    getMiscTraduction("headerDescOptions"),
                    "cyansh.message.getDescription.headerDescOptions",
                    false,
                    CyanSHMidnightConfig.useTranslations
            );

            for (String option : getOptionsTraductionsMap().keySet())
            {
                sendPlayerMessage(player,
                        getOptionTraduction(option),
                        "cyansh.message.getDescription.options.%s".formatted(option),
                        false,
                        CyanSHMidnightConfig.useTranslations
                );
            }

            sendPlayerMessage(player,
                    getMiscTraduction("dashSeparation"),
                    "cyansh.message.getDescription.dashSeparation",
                    false,
                    CyanSHMidnightConfig.useTranslations
            );
        }
        return Command.SINGLE_SUCCESS;
    }
}
