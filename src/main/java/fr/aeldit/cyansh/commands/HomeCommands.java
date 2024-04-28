package fr.aeldit.cyansh.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.aeldit.cyansh.commands.arguments.ArgumentSuggestion;
import fr.aeldit.cyansh.homes.Homes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import static fr.aeldit.cyanlib.lib.utils.TPUtils.getRequiredXpLevelsToTp;
import static fr.aeldit.cyansh.CyanSHCore.*;
import static fr.aeldit.cyansh.config.CyanLibConfigImpl.*;

public class HomeCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("set-home")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .executes(HomeCommands::setHome)
                )
        );
        dispatcher.register(CommandManager.literal("sh")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .executes(HomeCommands::setHome)
                )
        );

        dispatcher.register(CommandManager.literal("remove-home")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getHomes(
                                builder,
                                Objects.requireNonNull(
                                        context.getSource()
                                                .getPlayer())
                        ))
                        .executes(HomeCommands::removeHome)
                )
        );
        dispatcher.register(CommandManager.literal("rh")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getHomes(
                                builder,
                                Objects.requireNonNull(
                                        context.getSource()
                                                .getPlayer())
                        ))
                        .executes(HomeCommands::removeHome)
                )
        );

        dispatcher.register(CommandManager.literal("rename-home")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getHomes(
                                builder,
                                Objects.requireNonNull(
                                        context.getSource()
                                                .getPlayer())
                        ))
                        .then(CommandManager.argument(
                                                "new_home_name",
                                                StringArgumentType.string()
                                        )
                                        .executes(HomeCommands::renameHome)
                        )
                )
        );

        dispatcher.register(CommandManager.literal("remove-all-homes")
                .executes(HomeCommands::removeAllHomes)
        );

        dispatcher.register(CommandManager.literal("home")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getHomes(
                                builder,
                                Objects.requireNonNull(
                                        context.getSource()
                                                .getPlayer())
                        ))
                        .executes(HomeCommands::goToHome)
                )
        );
        dispatcher.register(CommandManager.literal("h")
                .then(CommandManager.argument("home_name", StringArgumentType.string())
                        .suggests((context, builder) -> ArgumentSuggestion.getHomes(
                                builder,
                                Objects.requireNonNull(
                                        context.getSource()
                                                .getPlayer())
                        ))
                        .executes(HomeCommands::goToHome)
                )
        );

        dispatcher.register(CommandManager.literal("get-homes")
                .executes(HomeCommands::getHomesList)
        );
        dispatcher.register(CommandManager.literal("gh")
                .executes(HomeCommands::getHomesList)
        );
    }

    /**
     * Called by the command {@code /set-home <home_name>} or {@code /sh <home_name>}
     * <p>
     * Creates a home with the player current position (dimension, x, y, z, yaw, pitch, date)
     */
    public static int setHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                if (CYANSH_LIB_UTILS.hasPermission(player, MIN_OP_LVL_HOMES.getValue()))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String playerKey = player.getUuidAsString() + " " + player.getName().getString();

                    if (HomesObj.maxHomesNotReached(playerKey))
                    {
                        if (HomesObj.addHome(
                                playerKey,
                                new Homes.Home(homeName, player.getWorld().getDimensionEntry().getIdAsString()
                                        .replace("minecraft:", "").replace("the_", ""),
                                        player.getX(), player.getY(), player.getZ(),
                                        player.getYaw(), player.getPitch(),
                                        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(
                                                Calendar.getInstance().getTime())
                                )
                        ))
                        {
                            CYANSH_LANG_UTILS.sendPlayerMessage(
                                    player,
                                    "cyansh.msg.setHome",
                                    Formatting.YELLOW + homeName
                            );
                        }
                        else
                        {
                            CYANSH_LANG_UTILS.sendPlayerMessage(
                                    player,
                                    "cyansh.error.homeAlreadyExists"
                            );
                        }
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansh.error.maxHomesReached",
                                Formatting.GOLD + String.valueOf(MAX_HOMES.getValue())
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /remove-home <home_name>} or {@code /rh <home_name>}
     * <p>
     * Removes the given home
     */
    public static int removeHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                if (CYANSH_LIB_UTILS.hasPermission(player, MIN_OP_LVL_HOMES.getValue()))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String playerKey = player.getUuidAsString() + " " + player.getName().getString();

                    if (HomesObj.removeHome(playerKey, homeName))
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansh.msg.removeHome",
                                Formatting.YELLOW + homeName
                        );
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansh.error.homeNotFound",
                                Formatting.YELLOW + homeName
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    // TODO -> ask for confirmation

    /**
     * Called by the command {@code /remove-all-homes}
     * <p>
     * Removes all the homes
     */
    public static int removeAllHomes(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                if (CYANSH_LIB_UTILS.hasPermission(player, MIN_OP_LVL_HOMES.getValue()))
                {
                    if (HomesObj.removeAll(player.getUuidAsString() + " " + player.getName().getString()))
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansh.msg.removeAllHomes"
                        );
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansh.error.noHomes"
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /rename-home <home_name> <new_home_name>}
     * <p>
     * Renames the location
     */
    public static int renameHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                if (CYANSH_LIB_UTILS.hasPermission(player, MIN_OP_LVL_HOMES.getValue()))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String newHomeName = StringArgumentType.getString(context, "new_home_name");

                    String playerKey = player.getUuidAsString() + " " + player.getName().getString();

                    if (HomesObj.rename(playerKey, homeName, newHomeName))
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansh.msg.renameHome",
                                Formatting.YELLOW + homeName,
                                Formatting.YELLOW + newHomeName
                        );
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansh.error.homeNotFoundOrExists",
                                homeName
                        );
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
        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                if (CYANSH_LIB_UTILS.hasPermission(player, MIN_OP_LVL_HOMES.getValue()))
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String playerKey = player.getUuidAsString() + " " + player.getName().getString();

                    if (HomesObj.homeExists(playerKey, homeName))
                    {
                        Homes.Home home = HomesObj.getHome(playerKey, homeName);
                        MinecraftServer server = player.getServer();

                        if (home != null)
                        {
                            int requiredXpLevel = 0;

                            if (USE_XP_TO_TP_HOME.getValue() && !player.isCreative())
                            {
                                requiredXpLevel = getRequiredXpLevelsToTp(player, player.getBlockPos(),
                                        BLOCKS_PER_XP_LEVEL_HOME
                                );

                                if (player.experienceLevel < requiredXpLevel)
                                {
                                    CYANSH_LANG_UTILS.sendPlayerMessage(
                                            player,
                                            "cyansh.error.notEnoughXp",
                                            Formatting.GOLD + String.valueOf(requiredXpLevel)
                                    );
                                    return 0;
                                }
                            }

                            switch (home.getDimension())
                            {
                                case "overworld" -> player.teleport(server.getWorld(World.OVERWORLD), home.getX(),
                                        home.getY(), home.getZ(), home.getYaw(), home.getPitch()
                                );
                                case "nether" -> player.teleport(server.getWorld(World.NETHER), home.getX(),
                                        home.getY(), home.getZ(), home.getYaw(), home.getPitch()
                                );
                                case "end" -> player.teleport(server.getWorld(World.END), home.getX(),
                                        home.getY(), home.getZ(), home.getYaw(), home.getPitch()
                                );
                            }

                            player.addExperienceLevels(-1 * requiredXpLevel);

                            CYANSH_LANG_UTILS.sendPlayerMessage(
                                    player,
                                    "cyansh.msg.goToHome",
                                    Formatting.YELLOW + homeName
                            );
                        }
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansh.error.homeNotFound",
                                Formatting.YELLOW + homeName
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /get-homes} or {@code /gh}
     * <p>
     * Sends a message in the player's chat with all its homes
     */
    public static int getHomesList(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                if (CYANSH_LIB_UTILS.hasPermission(player, MIN_OP_LVL_HOMES.getValue()))
                {
                    String playerKey = player.getUuidAsString() + " " + player.getName().getString();

                    if (!HomesObj.isEmpty(playerKey))
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                                player,
                                "cyanlib.msg.dashSeparation",
                                false
                        );
                        CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                                player,
                                "cyansh.msg.listHomes",
                                false
                        );

                        HomesObj.getPlayerHomes(playerKey)
                                .forEach(home -> CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                                                player,
                                                "cyansh.msg.getHome",
                                                false,
                                                Formatting.YELLOW + home.getName(),
                                                Formatting.DARK_AQUA + home.getDimension(),
                                                Formatting.DARK_AQUA + home.getDate()
                                        )
                                );

                        CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                                player,
                                "cyanlib.msg.dashSeparation",
                                false
                        );
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansh.error.noHomes"
                        );
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
