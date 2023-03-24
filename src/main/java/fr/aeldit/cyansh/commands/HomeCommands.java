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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Properties;

import static fr.aeldit.cyanlib.util.ChatUtil.sendPlayerMessage;
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

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(CyanSHLanguageUtils.getTranslation(ERROR + "playerOnlyCmd")));
        } else
        {
            if (CyanSHMidnightConfig.allowHomes)
            {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String playerKey = player.getUuidAsString() + "_" + player.getName().getString();
                    Path currentHomesPath = Path.of(homesPath + "/" + playerKey + ".properties");
                    double x = player.getX();
                    double y = player.getY();
                    double z = player.getZ();
                    float yaw = player.getYaw();
                    float pitch = player.getPitch();
                    ServerWorld overworld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
                    ServerWorld nether = Objects.requireNonNull(player.getServer()).getWorld(World.NETHER);
                    ServerWorld end = Objects.requireNonNull(player.getServer()).getWorld(World.END);
                    String date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

                    checkOrCreateHomesFiles(currentHomesPath);
                    try
                    {
                        Properties properties = new Properties();
                        properties.load(new FileInputStream(currentHomesPath.toFile()));

                        if (properties.stringPropertyNames().size() < CyanSHMidnightConfig.maxHomes)
                        {
                            if (!properties.containsKey(homeName))
                            {
                                if (player.getWorld() == overworld)
                                {
                                    properties.put(homeName, "%s %f %f %f %f %f %s".formatted("overworld", x, y, z, yaw, pitch, date));
                                } else if (player.getWorld() == nether)
                                {
                                    properties.put(homeName, "%s %f %f %f %f %f %s".formatted("nether", x, y, z, yaw, pitch, date));
                                } else if (player.getWorld() == end)
                                {
                                    properties.put(homeName, "%s %f %f %f %f %f %s".formatted("end", x, y, z, yaw, pitch, date));
                                }

                                properties.store(new FileOutputStream(currentHomesPath.toFile()), null);

                                sendPlayerMessage(player,
                                        CyanSHLanguageUtils.getTranslation("setHome"),
                                        "cyansh.message.setHome",
                                        CyanSHMidnightConfig.msgToActionBar,
                                        CyanSHMidnightConfig.useTranslations,
                                        yellow + homeName
                                );
                            } else
                            {
                                sendPlayerMessage(player,
                                        CyanSHLanguageUtils.getTranslation(ERROR + "homeAlreadyExists"),
                                        "cyansh.error.homeAlreadyExists",
                                        CyanSHMidnightConfig.errorToActionBar,
                                        CyanSHMidnightConfig.useTranslations
                                );
                            }
                        } else
                        {
                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation(ERROR + "maxHomesReached"),
                                    "cyansh.error.maxHomesReached",
                                    CyanSHMidnightConfig.errorToActionBar,
                                    CyanSHMidnightConfig.useTranslations,
                                    gold + String.valueOf(CyanSHMidnightConfig.maxHomes)
                            );
                        }
                    } catch (IOException e)
                    {
                        throw new RuntimeException(e);
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
     * Called by the command {@code /home <home_name>} or {@code /h <home_name>}
     * <p>
     * Teleports the player to the given home
     */
    public static int goToHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(CyanSHLanguageUtils.getTranslation(ERROR + "playerOnlyCmd")));
        } else
        {
            if (CyanSHMidnightConfig.allowHomes)
            {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String playerKey = player.getUuidAsString() + "_" + player.getName().getString();
                    Path currentHomesPath = Path.of(homesPath + "/" + playerKey + ".properties");
                    ServerWorld overworld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
                    ServerWorld nether = Objects.requireNonNull(player.getServer()).getWorld(World.NETHER);
                    ServerWorld end = Objects.requireNonNull(player.getServer()).getWorld(World.END);

                    checkOrCreateHomesFiles(currentHomesPath);
                    try
                    {
                        Properties properties = new Properties();
                        properties.load(new FileInputStream(currentHomesPath.toFile()));

                        if (properties.containsKey(homeName))
                        {
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
                        throw new RuntimeException(e);
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
     * Called by the command {@code /removehome <home_name>} or {@code /rh <home_name>}
     * <p>
     * Removes the given home
     */
    public static int removeHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(CyanSHLanguageUtils.getTranslation(ERROR + "playerOnlyCmd")));
        } else
        {
            if (CyanSHMidnightConfig.allowHomes)
            {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String playerKey = player.getUuidAsString() + "_" + player.getName().getString();
                    Path currentHomesPath = Path.of(homesPath + "/" + playerKey + ".properties");

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
                                    CyanSHLanguageUtils.getTranslation("removeHome"),
                                    "cyansh.message.removeHome",
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
                        throw new RuntimeException(e);
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
     * Called by the command {@code /removeallhomes}
     * <p>
     * Removes all the homes
     */
    public static int removeAllHomes(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(CyanSHLanguageUtils.getTranslation(ERROR + "playerOnlyCmd")));
        } else
        {
            if (CyanSHMidnightConfig.allowHomes)
            {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String playerKey = player.getUuidAsString() + "_" + player.getName().getString();
                    Path currentHomesPath = Path.of(homesPath + "/" + playerKey + ".properties");

                    if (Files.exists(currentHomesPath))
                    {
                        try
                        {
                            Properties properties = new Properties();
                            properties.load(new FileInputStream(currentHomesPath.toFile()));
                            properties.clear();
                            properties.store(new FileOutputStream(currentHomesPath.toFile()), null);

                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation("removeAllHomes"),
                                    "cyansh.message.removeAllHomes",
                                    CyanSHMidnightConfig.msgToActionBar,
                                    CyanSHMidnightConfig.useTranslations
                            );
                        } catch (IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                    } else
                    {
                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation(ERROR + "noHomes"),
                                "cyansh.error.noHomes",
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
     * Called by the command {@code /gethomes} or {@code /gh}
     * <p>
     * Sends a message in the player's chat with all its homes
     */
    public static int getHomesList(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(CyanSHLanguageUtils.getTranslation(ERROR + "playerOnlyCmd")));
        } else
        {
            if (CyanSHMidnightConfig.allowHomes)
            {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeHomes))
                {
                    String playerKey = player.getUuidAsString() + "_" + player.getName().getString();
                    Path currentHomesPath = Path.of(homesPath + "/" + playerKey + ".properties");

                    if (Files.exists(currentHomesPath))
                    {
                        try
                        {
                            Properties properties = new Properties();
                            properties.load(new FileInputStream(currentHomesPath.toFile()));
                            if (!(properties.size() == 0))
                            {
                                sendPlayerMessage(player,
                                        CyanSHLanguageUtils.getTranslation("dashSeparation"),
                                        "cyansh.message.getDescription.dashSeparation",
                                        false,
                                        CyanSHMidnightConfig.useTranslations
                                );
                                sendPlayerMessage(player,
                                        CyanSHLanguageUtils.getTranslation("listHomes"),
                                        "cyansh.message.listHomes",
                                        false,
                                        CyanSHMidnightConfig.useTranslations
                                );

                                for (String key : properties.stringPropertyNames())
                                {
                                    String[] items = properties.get(key).toString().split(" ");
                                    player.sendMessage(Text.of(yellow + key
                                            + Formatting.DARK_AQUA + " (" + items[0] + ", "
                                            + CyanSHLanguageUtils.getTranslation("dateCreated") + items[6]
                                            + ")"
                                    ));
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
                    } else
                    {
                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation(ERROR + "noHomes"),
                                "cyansh.error.noHomes",
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
                        CyanSHLanguageUtils.getTranslation(ERROR + "disabled.homes"),
                        "cyansh.error.disabled.homes",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
