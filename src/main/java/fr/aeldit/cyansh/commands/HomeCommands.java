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
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

import static fr.aeldit.cyanlib.util.ChatUtil.sendPlayerMessage;
import static fr.aeldit.cyansh.util.Constants.*;

public class HomeCommands {

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("sethome")
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .executes(HomeCommands::setHome)
                )
        );
        dispatcher.register(CommandManager.literal("sh")
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .executes(HomeCommands::setHome)
                )
        );

        dispatcher.register(CommandManager.literal("home")
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getHomes(builder4, Objects.requireNonNull(context4.getSource().getPlayer())))
                        .executes(HomeCommands::goToHome)
                )
        );
        dispatcher.register(CommandManager.literal("h")
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getHomes(builder4, Objects.requireNonNull(context4.getSource().getPlayer())))
                        .executes(HomeCommands::goToHome)
                )
        );

        dispatcher.register(CommandManager.literal("removehome")
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getHomes(builder4, Objects.requireNonNull(context4.getSource().getPlayer())))
                        .executes(HomeCommands::removeHome)
                )
        );
        dispatcher.register(CommandManager.literal("rh")
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getHomes(builder4, Objects.requireNonNull(context4.getSource().getPlayer())))
                        .executes(HomeCommands::removeHome)
                )
        );

        dispatcher.register(CommandManager.literal("gethomes")
                .executes(HomeCommands::getHomesList)
        );
        dispatcher.register(CommandManager.literal("gh")
                .executes(HomeCommands::getHomesList)

        );
    }

    public static int setHome(@NotNull CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        String homeName = StringArgumentType.getString(context, "name");

        if (player == null) {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
            return 0;
        } else {
            Path homesPath = Path.of(locationsPath + "\\" + player.getUuidAsString() + ".properties");
            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();
            float yaw = player.getYaw();
            float pitch = player.getPitch();

            if (CyanSHMidnightConfig.allowHomes) {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeHome)) {
                    ServerWorld overworld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
                    ServerWorld nether = Objects.requireNonNull(player.getServer()).getWorld(World.NETHER);
                    ServerWorld end = Objects.requireNonNull(player.getServer()).getWorld(World.END);

                    Properties properties = new Properties();
                    try {
                        if (!Files.exists(locationsPath)) {
                            try {
                                Files.createDirectory(locationsPath);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (!Files.exists(homesPath)) {
                            try {
                                Files.createFile(homesPath);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        properties.load(new FileInputStream(homesPath.toFile()));

                        if (properties.stringPropertyNames().size() < CyanSHMidnightConfig.maxHomes) {
                            if (!properties.containsKey(homeName)) {
                                if (player.getWorld() == overworld) {
                                    properties.put(homeName, "%s %f %f %f %f %f".formatted("overworld", x, y, z, yaw, pitch));
                                } else if (player.getWorld() == nether) {
                                    properties.put(homeName, "%s %f %f %f %f %f".formatted("nether", x, y, z, yaw, pitch));
                                } else if (player.getWorld() == end) {
                                    properties.put(homeName, "%s %f %f %f %f %f".formatted("end", x, y, z, yaw, pitch));
                                }

                                properties.store(new FileOutputStream(homesPath.toFile()), null);

                                sendPlayerMessage(player,
                                        getCmdFeedbackTraduction("setHome"),
                                        yellow + homeName,
                                        "cyansh.message.setHome",
                                        CyanSHMidnightConfig.msgToActionBar,
                                        CyanSHMidnightConfig.useTranslations
                                );
                            } else {
                                sendPlayerMessage(player,
                                        getCmdFeedbackTraduction("homeAlreadyExists"),
                                        null,
                                        "cyansh.error.homeAlreadyExists",
                                        CyanSHMidnightConfig.msgToActionBar,
                                        CyanSHMidnightConfig.useTranslations
                                );
                                return 0;
                            }
                        } else {
                            sendPlayerMessage(player,
                                    getErrorTraduction("maxHomesReached"),
                                    gold + String.valueOf(CyanSHMidnightConfig.maxHomes),
                                    "cyansh.error.maxHomesReached",
                                    CyanSHMidnightConfig.msgToActionBar,
                                    CyanSHMidnightConfig.useTranslations
                            );
                            return 0;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                    return Command.SINGLE_SUCCESS;
                } else {
                    sendPlayerMessage(player,
                            getErrorTraduction("notOp"),
                            null,
                            "cyansh.error.notOp",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useTranslations
                    );
                    return 0;
                }
            } else {
                sendPlayerMessage(player,
                        getErrorTraduction("disabled.homes"),
                        null,
                        "cyansh.error.disabled.homes",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
                return 0;
            }
        }
    }

    public static int goToHome(@NotNull CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        String homeName = StringArgumentType.getString(context, "name");

        if (player == null) {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
            return 0;
        } else {
            if (CyanSHMidnightConfig.allowHomes) {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeHome)) {
                    Path homesPath = Path.of(locationsPath + "\\" + player.getUuidAsString() + ".properties");
                    ServerWorld overworld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
                    ServerWorld nether = Objects.requireNonNull(player.getServer()).getWorld(World.NETHER);
                    ServerWorld end = Objects.requireNonNull(player.getServer()).getWorld(World.END);

                    try {
                        Properties properties = new Properties();
                        properties.load(new FileInputStream(homesPath.toFile()));

                        if (properties.containsKey(homeName)) {
                            String location = (String) properties.get(homeName);
                            String world = location.split(" ")[0];

                            if (Objects.equals(world, "overworld")) {
                                player.teleport(
                                        overworld,
                                        Double.parseDouble(location.split(" ")[1]),
                                        Double.parseDouble(location.split(" ")[2]),
                                        Double.parseDouble(location.split(" ")[3]),
                                        Float.parseFloat(location.split(" ")[4]),
                                        Float.parseFloat(location.split(" ")[5])
                                );
                            } else if (Objects.equals(world, "nether")) {
                                player.teleport(
                                        nether,
                                        Double.parseDouble(location.split(" ")[1]),
                                        Double.parseDouble(location.split(" ")[2]),
                                        Double.parseDouble(location.split(" ")[3]),
                                        Float.parseFloat(location.split(" ")[4]),
                                        Float.parseFloat(location.split(" ")[5])
                                );
                            } else if (Objects.equals(world, "end")) {
                                player.teleport(
                                        end,
                                        Double.parseDouble(location.split(" ")[1]),
                                        Double.parseDouble(location.split(" ")[2]),
                                        Double.parseDouble(location.split(" ")[3]),
                                        Float.parseFloat(location.split(" ")[4]),
                                        Float.parseFloat(location.split(" ")[5])
                                );
                            }

                            sendPlayerMessage(player,
                                    getCmdFeedbackTraduction("goToHome"),
                                    yellow + homeName,
                                    "cyansh.message.goToHome",
                                    CyanSHMidnightConfig.msgToActionBar,
                                    CyanSHMidnightConfig.useTranslations
                            );
                        } else {
                            sendPlayerMessage(player,
                                    getCmdFeedbackTraduction("homeNotFound"),
                                    yellow + homeName,
                                    "cyansh.error.homeNotFound",
                                    CyanSHMidnightConfig.msgToActionBar,
                                    CyanSHMidnightConfig.useTranslations
                            );
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return Command.SINGLE_SUCCESS;
                } else {
                    sendPlayerMessage(player,
                            getErrorTraduction("notOp"),
                            null,
                            "cyansh.error.notOp",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useTranslations
                    );
                    return 0;
                }
            } else {
                sendPlayerMessage(player,
                        getErrorTraduction("disabled.homes"),
                        null,
                        "cyansh.error.disabled.homes",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
                return 0;
            }
        }
    }

    public static int removeHome(@NotNull CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        String homeName = StringArgumentType.getString(context, "name");

        if (player == null) {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
            return 0;
        } else {
            if (CyanSHMidnightConfig.allowHomes) {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeHome)) {
                    Path homesPath = Path.of(locationsPath + "\\" + player.getUuidAsString() + ".properties");
                    Properties properties = new Properties();

                    try {
                        properties.load(new FileInputStream(homesPath.toFile()));

                        if (properties.containsKey(homeName)) {
                            properties.remove(homeName);
                            properties.store(new FileOutputStream(homesPath.toFile()), null);

                            sendPlayerMessage(player,
                                    getCmdFeedbackTraduction("removeHome"),
                                    yellow + homeName,
                                    "cyansh.message.removeHome",
                                    CyanSHMidnightConfig.msgToActionBar,
                                    CyanSHMidnightConfig.useTranslations
                            );
                        } else {
                            sendPlayerMessage(player,
                                    getCmdFeedbackTraduction("homeNotFound"),
                                    yellow + homeName,
                                    "cyansh.error.homeNotFound",
                                    CyanSHMidnightConfig.msgToActionBar,
                                    CyanSHMidnightConfig.useTranslations
                            );
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return Command.SINGLE_SUCCESS;
                } else {
                    sendPlayerMessage(player,
                            getErrorTraduction("notOp"),
                            null,
                            "cyansh.error.notOp",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useTranslations
                    );
                    return 0;
                }
            } else {
                sendPlayerMessage(player,
                        getErrorTraduction("disabled.homes"),
                        null,
                        "cyansh.error.disabled.homes",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
                return 0;
            }
        }
    }

    public static int getHomesList(@NotNull CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
            return 0;
        } else {
            if (CyanSHMidnightConfig.allowHomes) {
                if (player.hasPermissionLevel(CyanSHMidnightConfig.minOpLevelExeHome)) {
                    Path homesPath = Path.of(locationsPath + "\\" + player.getUuidAsString() + ".properties");

                    try {
                        Properties properties = new Properties();
                        properties.load(new FileInputStream(homesPath.toFile()));
                        sendPlayerMessage(player,
                                getMiscTraduction("listHomes"),
                                null,
                                "cyansh.message.listHomes",
                                false,
                                CyanSHMidnightConfig.useTranslations
                        );
                        for (String key : properties.stringPropertyNames()) {
                            player.sendMessage(Text.of(yellow + key + gold + " (" + properties.get(key).toString().split(" ")[0] + ")"));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return Command.SINGLE_SUCCESS;
                } else {
                    sendPlayerMessage(player,
                            getErrorTraduction("notOp"),
                            null,
                            "cyansh.error.notOp",
                            false,
                            CyanSHMidnightConfig.useTranslations
                    );
                    return 0;
                }
            } else {
                sendPlayerMessage(player,
                        getErrorTraduction("disabled.homes"),
                        null,
                        "cyansh.error.disabled.homes",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useTranslations
                );
                return 0;
            }
        }
    }
}
