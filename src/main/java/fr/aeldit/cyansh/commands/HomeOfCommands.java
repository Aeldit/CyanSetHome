package fr.aeldit.cyansh.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.aeldit.cyansh.commands.argumentTypes.ArgumentSuggestion;
import fr.aeldit.cyansh.config.CyanSHMidnightConfig;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import static fr.aeldit.cyanlib.util.ChatUtils.sendPlayerMessage;
import static fr.aeldit.cyanlib.util.Constants.ERROR;
import static fr.aeldit.cyansh.util.Utils.*;

public class HomeOfCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("homeof")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getTrustingPlayersName(builder4, context4.getSource()))
                        .then(CommandManager.argument("home_name", StringArgumentType.string())
                                .executes(HomeOfCommands::goToHomeOf)
                        )
                )
        );
        dispatcher.register(CommandManager.literal("ho")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getTrustingPlayersName(builder4, context4.getSource()))
                        .then(CommandManager.argument("home_name", StringArgumentType.string())
                                .executes(HomeOfCommands::goToHomeOf)
                        )
                )
        );

        dispatcher.register(CommandManager.literal("removehomeof")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getTrustingPlayersName(builder4, context4.getSource()))
                        .then(CommandManager.argument("home_name", StringArgumentType.string())
                                .executes(HomeOfCommands::removeHomeOf)
                        )
                )
        );
        dispatcher.register(CommandManager.literal("rho")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getTrustingPlayersName(builder4, context4.getSource()))
                        .then(CommandManager.argument("home_name", StringArgumentType.string())
                                .executes(HomeOfCommands::removeHomeOf)
                        )
                )
        );

        dispatcher.register(CommandManager.literal("gethomesof")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getTrustingPlayersName(builder4, context4.getSource()))
                        .executes(HomeOfCommands::getHomesOfList)
                )
        );
        dispatcher.register(CommandManager.literal("gho")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getTrustingPlayersName(builder4, context4.getSource()))
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

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(CyanSHLanguageUtils.getTranslation(ERROR + "playerOnlyCmd")));
        } else
        {
            if (CyanSHMidnightConfig.allowHomesOf)
            {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeHomesOf) || player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeOPHomesOf))
                {
                    String playerName = StringArgumentType.getString(context, "player_name");
                    String homeName = StringArgumentType.getString(context, "home_name");

                    if (playerTrust(playerName, player.getName().getString()))
                    {
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
                                    if (file.getName().split("_")[1].equals(playerName + ".properties"))
                                    {
                                        fileFound = true;
                                        try
                                        {
                                            Properties properties = new Properties();
                                            properties.load(new FileInputStream(file));

                                            if (properties.containsKey(homeName))
                                            {
                                                ServerWorld overworld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
                                                ServerWorld nether = Objects.requireNonNull(player.getServer()).getWorld(World.NETHER);
                                                ServerWorld end = Objects.requireNonNull(player.getServer()).getWorld(World.END);

                                                String home = (String) properties.get(homeName);
                                                String world = home.split(" ")[0];

                                                if (Objects.equals(world, "overworld"))
                                                {
                                                    player.teleport(
                                                            overworld,
                                                            Double.parseDouble(home.split(" ")[1]),
                                                            Double.parseDouble(home.split(" ")[2]),
                                                            Double.parseDouble(home.split(" ")[3]),
                                                            Float.parseFloat(home.split(" ")[4]),
                                                            Float.parseFloat(home.split(" ")[5])
                                                    );
                                                } else if (Objects.equals(world, "nether"))
                                                {
                                                    player.teleport(
                                                            nether,
                                                            Double.parseDouble(home.split(" ")[1]),
                                                            Double.parseDouble(home.split(" ")[2]),
                                                            Double.parseDouble(home.split(" ")[3]),
                                                            Float.parseFloat(home.split(" ")[4]),
                                                            Float.parseFloat(home.split(" ")[5])
                                                    );
                                                } else if (Objects.equals(world, "end"))
                                                {
                                                    player.teleport(
                                                            end,
                                                            Double.parseDouble(home.split(" ")[1]),
                                                            Double.parseDouble(home.split(" ")[2]),
                                                            Double.parseDouble(home.split(" ")[3]),
                                                            Float.parseFloat(home.split(" ")[4]),
                                                            Float.parseFloat(home.split(" ")[5])
                                                    );
                                                }

                                                sendPlayerMessage(player,
                                                        CyanSHLanguageUtils.getTranslation("goToHome"),
                                                        "cyansh.message.goToHome",
                                                        CyanSHMidnightConfig.msgToActionBar,
                                                        CyanSHMidnightConfig.useTranslations,
                                                        yellow + homeName
                                                );
                                            } else
                                            {
                                                sendPlayerMessage(player,
                                                        CyanSHLanguageUtils.getTranslation(ERROR + "homeNotFound"),
                                                        "cyansh.error.homeNotFound",
                                                        CyanSHMidnightConfig.errorToActionBar,
                                                        CyanSHMidnightConfig.useTranslations,
                                                        yellow + homeName
                                                );
                                            }
                                        } catch (IOException e)
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
                                        "cyansh.error.noHomesOf",
                                        CyanSHMidnightConfig.errorToActionBar,
                                        CyanSHMidnightConfig.useTranslations
                                );
                            }
                        }
                    } else
                    {
                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation(ERROR + "playerNotTrusting"),
                                "cyansh.error.playerNotTrusting",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useTranslations
                        );
                    }
                } else
                {
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "notOp"),
                            "cyansh.error.notOp",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useTranslations
                    );
                }
            } else
            {
                sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation(ERROR + "disabled.homes"),
                        "cyansh.error.disabled.homes",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
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

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(CyanSHLanguageUtils.getTranslation(ERROR + "playerOnlyCmd")));
        } else
        {
            if (CyanSHMidnightConfig.allowHomesOf)
            {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeOPHomesOf))
                {
                    String trustingPlayer = StringArgumentType.getString(context, "player_name");

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
                                    Properties properties = new Properties();
                                    properties.load(new FileInputStream(file));

                                    if (file.getName().split("_")[1].equals(trustingPlayer + ".properties"))
                                    {
                                        fileFound = true;
                                        if (properties.remove(homeName) != null)
                                        {
                                            properties.store(new FileOutputStream(file), null);
                                            sendPlayerMessage(player,
                                                    CyanSHLanguageUtils.getTranslation("removeHomeOf"),
                                                    "cyansh.message.removeHomeOf",
                                                    CyanSHMidnightConfig.msgToActionBar,
                                                    CyanSHMidnightConfig.useTranslations,
                                                    yellow + homeName,
                                                    Formatting.AQUA + trustingPlayer
                                            );
                                        } else
                                        {
                                            sendPlayerMessage(player,
                                                    CyanSHLanguageUtils.getTranslation(ERROR + "homeNotFound"),
                                                    "cyansh.error.homeNotFound",
                                                    CyanSHMidnightConfig.errorToActionBar,
                                                    CyanSHMidnightConfig.useTranslations,
                                                    yellow + homeName
                                            );
                                        }
                                    }
                                    if (!fileFound)
                                    {
                                        sendPlayerMessage(player,
                                                CyanSHLanguageUtils.getTranslation(ERROR + "noHomesOf"),
                                                "cyansh.error.noHomesOf",
                                                CyanSHMidnightConfig.errorToActionBar,
                                                CyanSHMidnightConfig.useTranslations
                                        );
                                    }
                                } catch (IOException e)
                                {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                } else
                {
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "notOp"),
                            "cyansh.error.notOp",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useTranslations
                    );
                }
            } else
            {
                sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation(ERROR + "disabled.homes"),
                        "cyansh.error.disabled.homes",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
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

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(CyanSHLanguageUtils.getTranslation(ERROR + "playerOnlyCmd")));
        } else
        {
            if (CyanSHMidnightConfig.allowHomesOf)
            {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeOPHomesOf))
                {
                    String trustingPlayer = StringArgumentType.getString(context, "player_name");

                    if (playerTrust(trustingPlayer, player.getName().getString()))
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
                                    if (file.getName().split("_")[1].equals(trustingPlayer + ".properties"))
                                    {
                                        isTrusted = true;
                                        try
                                        {
                                            Properties properties = new Properties();
                                            properties.load(new FileInputStream(file));
                                            if (!(properties.size() == 0))
                                            {
                                                sendPlayerMessage(player,
                                                        CyanSHLanguageUtils.getTranslation("dashSeparation"),
                                                        "cyansh.message.getDescription.dashSeparation",
                                                        false,
                                                        CyanSHMidnightConfig.useTranslations
                                                );

                                                sendPlayerMessage(player,
                                                        CyanSHLanguageUtils.getTranslation("listHomesOf"),
                                                        "cyansh.message.listHomesOf",
                                                        false,
                                                        CyanSHMidnightConfig.useTranslations,
                                                        Formatting.AQUA + trustingPlayer
                                                );

                                                for (String key : properties.stringPropertyNames())
                                                {
                                                    player.sendMessage(Text.of(yellow + key + gold + " (" + properties.get(key).toString().split(" ")[0] + ")"));
                                                }

                                                sendPlayerMessage(player,
                                                        CyanSHLanguageUtils.getTranslation("dashSeparation"),
                                                        "cyansh.message.getDescription.dashSeparation",
                                                        false,
                                                        CyanSHMidnightConfig.useTranslations
                                                );
                                            } else
                                            {
                                                sendPlayerMessage(player,
                                                        CyanSHLanguageUtils.getTranslation(ERROR + "noHomes"),
                                                        "cyansh.error.noHomes",
                                                        CyanSHMidnightConfig.errorToActionBar,
                                                        CyanSHMidnightConfig.useTranslations
                                                );
                                            }
                                        } catch (IOException e)
                                        {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            }
                            if (!isTrusted)
                            {
                                sendPlayerMessage(player,
                                        CyanSHLanguageUtils.getTranslation(ERROR + "noHomesOf"),
                                        "cyansh.error.noHomesOf",
                                        CyanSHMidnightConfig.errorToActionBar,
                                        CyanSHMidnightConfig.useTranslations
                                );
                            }
                        } else
                        {
                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation(ERROR + "noHomesOf"),
                                    "cyansh.error.noHomesOf",
                                    CyanSHMidnightConfig.errorToActionBar,
                                    CyanSHMidnightConfig.useTranslations
                            );
                        }
                    } else
                    {
                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation(ERROR + "playerNotTrusting"),
                                "cyansh.error.playerNotTrusting",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useTranslations
                        );
                    }
                } else
                {
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "notOp"),
                            "cyansh.error.notOp",
                            false,
                            CyanSHMidnightConfig.useTranslations
                    );
                }
            } else
            {
                sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation(ERROR + "disabled.homesOf"),
                        "cyansh.error.disabled.homesOf",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
