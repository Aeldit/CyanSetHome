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
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

import static fr.aeldit.cyanlib.util.ChatUtil.sendPlayerMessage;
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

        String playerName = StringArgumentType.getString(context, "player_name");
        String homeName = StringArgumentType.getString(context, "home_name");

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else
        {
            if (CyanSHMidnightConfig.allowHomes)
            {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    File currentHomesDir = new File(homesPath.toUri());
                    checkOrCreateHomesDir();
                    File[] listOfFiles = currentHomesDir.listFiles();
                    if (listOfFiles != null)
                    {
                        for (File file : listOfFiles)
                        {
                            if (file.isFile())
                            {
                                if (file.getName().contains(playerName))
                                {
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
                                                    getCmdFeedbackTraduction("goToHome"),
                                                    yellow + homeName,
                                                    "cyansh.message.goToHome",
                                                    CyanSHMidnightConfig.msgToActionBar,
                                                    CyanSHMidnightConfig.useTranslations
                                            );
                                        } else
                                        {
                                            sendPlayerMessage(player,
                                                    getCmdFeedbackTraduction("homeNotFound"),
                                                    yellow + homeName,
                                                    "cyansh.error.homeNotFound",
                                                    CyanSHMidnightConfig.errorToActionBar,
                                                    CyanSHMidnightConfig.useTranslations
                                            );
                                        }
                                    } catch (IOException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                } else
                {
                    sendPlayerMessage(player,
                            getErrorTraduction("notOp"),
                            null,
                            "cyansh.error.notOp",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useTranslations
                    );
                }
            } else
            {
                sendPlayerMessage(player,
                        getErrorTraduction("disabled.homes"),
                        null,
                        "cyansh.error.disabled.homes",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /removehome <home_name>} or {@code /rh <home_name>}
     * <p>
     * Removes the given home
     */
    public static int removeHomeOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        String homeName = StringArgumentType.getString(context, "home_name");

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else
        {
            if (CyanSHMidnightConfig.allowHomes)
            {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String playerKey = player.getUuidAsString() + "_" + player.getName().getString();
                    Path currentHomesPath = Path.of(homesPath + "\\" + playerKey + ".properties");

                    checkOrCreateHomesFiles(currentHomesPath);
                    try
                    {
                        Properties properties = new Properties();
                        properties.load(new FileInputStream(currentHomesPath.toFile()));

                        if (properties.containsKey(homeName))
                        {
                            properties.remove(homeName);
                            properties.store(new FileOutputStream(currentHomesPath.toFile()), null);

                            sendPlayerMessage(player,
                                    getCmdFeedbackTraduction("removeHome"),
                                    yellow + homeName,
                                    "cyansh.message.removeHome",
                                    CyanSHMidnightConfig.msgToActionBar,
                                    CyanSHMidnightConfig.useTranslations
                            );
                        } else
                        {
                            sendPlayerMessage(player,
                                    getCmdFeedbackTraduction("homeNotFound"),
                                    yellow + homeName,
                                    "cyansh.error.homeNotFound",
                                    CyanSHMidnightConfig.errorToActionBar,
                                    CyanSHMidnightConfig.useTranslations
                            );
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                } else
                {
                    sendPlayerMessage(player,
                            getErrorTraduction("notOp"),
                            null,
                            "cyansh.error.notOp",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useTranslations
                    );
                }
            } else
            {
                sendPlayerMessage(player,
                        getErrorTraduction("disabled.homes"),
                        null,
                        "cyansh.error.disabled.homes",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /gethomes} or {@code /gh}
     * <p>
     * Sends a message in the player's chat with all her/his homes
     */
    public static int getHomesOfList(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        String trustingPlayer = StringArgumentType.getString(context, "player_name");

        File currentHomesDir = new File(homesPath.toUri());
        checkOrCreateHomesDir();
        File[] listOfFiles = currentHomesDir.listFiles();
        boolean isTrusted = false;

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else
        {
            if (CyanSHMidnightConfig.allowHomes)
            {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    if (listOfFiles != null)
                    {
                        for (File file : listOfFiles)
                        {
                            if (file.isFile())
                            {
                                if (file.getName().contains(trustingPlayer))
                                {
                                    isTrusted = true;
                                    try
                                    {
                                        Properties properties = new Properties();
                                        properties.load(new FileInputStream(file));
                                        if (!(properties.size() == 0))
                                        {
                                            sendPlayerMessage(player,
                                                    getMiscTraduction("dashSeparation"),
                                                    null,
                                                    "cyansh.message.getDescription.dashSeparation",
                                                    false,
                                                    CyanSHMidnightConfig.useTranslations
                                            );
                                            sendPlayerMessage(player,
                                                    getMiscTraduction("listHomesOf"),
                                                    Formatting.AQUA + trustingPlayer,
                                                    "cyansh.message.listHomesOf",
                                                    false,
                                                    CyanSHMidnightConfig.useTranslations
                                            );

                                            for (String key : properties.stringPropertyNames())
                                            {
                                                player.sendMessage(Text.of(yellow + key + gold + " (" + properties.get(key).toString().split(" ")[0] + ")"));
                                            }

                                            sendPlayerMessage(player,
                                                    getMiscTraduction("dashSeparation"),
                                                    null,
                                                    "cyansh.message.getDescription.dashSeparation",
                                                    false,
                                                    CyanSHMidnightConfig.useTranslations
                                            );
                                        } else
                                        {
                                            sendPlayerMessage(player,
                                                    getErrorTraduction("noHomes"),
                                                    null,
                                                    "cyansh.error.noHomes",
                                                    CyanSHMidnightConfig.errorToActionBar,
                                                    CyanSHMidnightConfig.useTranslations
                                            );
                                        }
                                    } catch (IOException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if (!isTrusted)
                        {
                            sendPlayerMessage(player,
                                    getErrorTraduction("noHomesOf"),
                                    null,
                                    "cyansh.error.noHomesOf",
                                    CyanSHMidnightConfig.errorToActionBar,
                                    CyanSHMidnightConfig.useTranslations
                            );
                        }
                    } else
                    {
                        sendPlayerMessage(player,
                                getErrorTraduction("noHomesOf"),
                                null,
                                "cyansh.error.noHomesOf",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useTranslations
                        );
                    }
                } else
                {
                    sendPlayerMessage(player,
                            getErrorTraduction("notOp"),
                            null,
                            "cyansh.error.notOp",
                            false,
                            CyanSHMidnightConfig.useTranslations
                    );
                }
            } else
            {
                sendPlayerMessage(player,
                        getErrorTraduction("disabled.homesOf"),
                        null,
                        "cyansh.error.disabled.homesOf",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
