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

import static fr.aeldit.cyanlib.lib.utils.TPUtils.getRequiredXpLevelsToTp;
import static fr.aeldit.cyansh.CyanSHCore.*;
import static fr.aeldit.cyansh.config.CyanLibConfigImpl.*;

public class HomeOfCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("set-home-of")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests(
                                (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                        builder,
                                        context.getSource().getPlayer()
                                ))
                        .then(CommandManager.argument(
                                        "home_name", StringArgumentType.string())
                                .executes(HomeOfCommands::setHomeOf)
                        )
                )
        );
        dispatcher.register(CommandManager.literal("sho")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests(
                                (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                        builder,
                                        context.getSource().getPlayer()
                                ))
                        .then(CommandManager.argument(
                                        "home_name", StringArgumentType.string())
                                .executes(HomeOfCommands::setHomeOf)
                        )
                )
        );

        dispatcher.register(CommandManager.literal("remove-home-of")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests(
                                (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                        builder,
                                        context.getSource().getPlayer()
                                ))
                        .then(CommandManager.argument(
                                        "home_name", StringArgumentType.string())
                                .suggests(
                                        (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                builder,
                                                context.getSource().getPlayer(),
                                                context.getInput().split(" ")[1]
                                        ))
                                .executes(HomeOfCommands::removeHomeOf)
                        )
                )
        );
        dispatcher.register(CommandManager.literal("rho")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests(
                                (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                        builder,
                                        context.getSource().getPlayer()
                                ))
                        .then(CommandManager.argument(
                                        "home_name", StringArgumentType.string())
                                .suggests(
                                        (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                builder,
                                                context.getSource().getPlayer(),
                                                context.getInput().split(" ")[1]
                                        ))
                                .executes(HomeOfCommands::removeHomeOf)
                        )
                )
        );

        dispatcher.register(CommandManager.literal("remove-all-homes-of")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests(
                                (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                        builder,
                                        context.getSource().getPlayer()
                                ))
                        .executes(HomeOfCommands::removeAllHomesOf)
                )
        );

        dispatcher.register(CommandManager.literal("rename-home-of")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests(
                                (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                        builder,
                                        context.getSource().getPlayer()
                                ))
                        .then(CommandManager.argument(
                                        "home_name", StringArgumentType.string())
                                .suggests(
                                        (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                builder,
                                                context.getSource().getPlayer(),
                                                context.getInput().split(" ")[1]
                                        ))
                                .then(CommandManager.argument(
                                                        "new_home_name",
                                                        StringArgumentType.string()
                                                )
                                                .executes(HomeOfCommands::renameHomeOf)
                                )
                        )
                )
        );

        dispatcher.register(CommandManager.literal("home-of")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests(
                                (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                        builder,
                                        context.getSource().getPlayer()
                                ))
                        .then(CommandManager.argument(
                                        "home_name", StringArgumentType.string())
                                .suggests(
                                        (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                builder,
                                                context.getSource().getPlayer(),
                                                context.getInput().split(" ")[1]
                                        ))
                                .executes(HomeOfCommands::goToHomeOf)
                        )
                )
        );
        dispatcher.register(CommandManager.literal("ho")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests(
                                (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                        builder,
                                        context.getSource().getPlayer()
                                ))
                        .then(CommandManager.argument(
                                        "home_name", StringArgumentType.string())
                                .suggests(
                                        (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                builder,
                                                context.getSource().getPlayer(),
                                                context.getInput().split(" ")[1]
                                        ))
                                .executes(HomeOfCommands::goToHomeOf)
                        )
                )
        );

        dispatcher.register(CommandManager.literal("get-homes-of")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests(
                                (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                        builder,
                                        context.getSource().getPlayer()
                                ))
                        .executes(HomeOfCommands::getHomesOfList)
                )
        );
        dispatcher.register(CommandManager.literal("gho")
                .then(CommandManager.argument("player_name", StringArgumentType.string())
                        .suggests(
                                (context, builder) -> ArgumentSuggestion.getTrustingPlayersName(
                                        builder,
                                        context.getSource().getPlayer()
                                ))
                        .executes(HomeOfCommands::getHomesOfList)
                )
        );
    }

    /**
     * Called by the command {@code /set-home-of <player_name> <home_name>} or {@code /sho <player_name> <home_name>}
     * <p>
     * Adds the given home to the given player
     * <p>
     * Succeeds only if the player has OP level {@code MIN_OP_LVL_BYPASS} and the bypass option is set to {@code true}
     */
    public static int setHomeOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_BYPASS.getValue(), "cyansh.error.bypassDisabled")
                        && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue())
                )
                {
                    String trustingPlayer = StringArgumentType.getString(context, "player_name");
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String playerKey = HomesObj.getKeyFromName(trustingPlayer);

                    if (HomesObj.maxHomesNotReached(playerKey))
                    {
                        if (HomesObj.addHome(
                                playerKey,
                                new Homes.Home(homeName,
                                        player.getWorld().getDimensionKey().getValue()
                                                .toString().replace("minecraft:", "").replace("the_", ""),
                                        player.getX(), player.getY(), player.getZ(), player.getYaw(),
                                        player.getPitch(),
                                        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(
                                                Calendar.getInstance().getTime())
                                )
                        ))
                        {
                            CYANSH_LANG_UTILS.sendPlayerMessage(
                                    player,
                                    "cyansh.msg.setHomeOf",
                                    Formatting.YELLOW + homeName,
                                    Formatting.AQUA + trustingPlayer
                            );
                        }
                        else
                        {
                            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansh.error.homeAlreadyExists");
                        }
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /remove-home-of <player_name> <home_name>} or {@code /rho <player_name> <home_name>}
     * <p>
     * Removes the given home of the given player
     * <p>
     * Succeeds only if the player has OP level {@code MIN_OP_LVL_BYPASS} and the bypass option is set to {@code true}
     */
    public static int removeHomeOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_BYPASS.getValue(), "cyansh.error.bypassDisabled")
                        && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue())
                )
                {
                    String homeName = StringArgumentType.getString(context, "home_name");

                    if (HomesObj.removeHome(HomesObj.getKeyFromName(trustingPlayer), homeName))
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansh.msg.removeHomeOf",
                                Formatting.YELLOW + homeName,
                                Formatting.AQUA + trustingPlayer
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

    /**
     * Called by the command {@code /remove-all-homes-of <player_name>}
     * <p>
     * Removes all the homes of the given player
     * <p>
     * Succeeds only if the player has OP level {@code MIN_OP_LVL_BYPASS} and the byPass option is set to {@code true}
     */
    // TODO -> ask for confirmation
    public static int removeAllHomesOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "disabled.homes"))
            {
                if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_BYPASS.getValue(), "cyansh.error.bypassDisabled")
                        && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue())
                )
                {
                    String trustingPlayer = StringArgumentType.getString(context, "player_name");

                    if (HomesObj.removeAll(HomesObj.getKeyFromName(trustingPlayer)))
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansh.msg.removeAllHomesOf",
                                Formatting.AQUA + trustingPlayer
                        );
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansh.error.noHomesOf");
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /rename-home-of <player_name> <home_name> <new_name>}
     * <p>
     * Renames the given home of the given player
     * <p>
     * Succeeds only if the player has OP level {@code MIN_OP_LVL_BYPASS} and the bypass option is set to {@code true}
     */
    public static int renameHomeOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_BYPASS.getValue(), "cyansh.error.bypassDisabled")
                        && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue())
                )
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String newHomeName = StringArgumentType.getString(context, "new_home_name");
                    String playerKey = HomesObj.getKeyFromName(trustingPlayer);

                    if (HomesObj.rename(playerKey, homeName, newHomeName))
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansh.msg.renameHomeOf",
                                Formatting.YELLOW + homeName,
                                Formatting.YELLOW + newHomeName,
                                Formatting.AQUA + trustingPlayer
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
     * Called by the command {@code /home-of <player_name> <home_name>} or {@code /ho <player_name> <home_name>}
     * <p>
     * Teleports the player to the given home
     * <p>
     * Succeeds only if the player is trusted or has OP level {@code MIN_OP_LVL_BYPASS} and the bypass option is set
     * to {@code true}
     */
    public static int goToHomeOf(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if ((ALLOW_BYPASS.getValue() && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue()))
                        || TrustsObj.isPlayerTrustingFromName(trustingPlayer, player.getName().getString())
                )
                {
                    String homeName = StringArgumentType.getString(context, "home_name");

                    if (HomesObj.homeExistsFromName(trustingPlayer, homeName))
                    {
                        Homes.Home home = HomesObj.getHome(HomesObj.getKeyFromName(trustingPlayer), homeName);
                        MinecraftServer server = player.getServer();

                        if (home != null && server != null)
                        {
                            int requiredXpLevel = 0;

                            if (USE_XP_TO_TP_HOME.getValue())
                            {
                                requiredXpLevel = getRequiredXpLevelsToTp(player, player.getBlockPos(),
                                        BLOCKS_PER_XP_LEVEL_HOME
                                );

                                if (player.experienceLevel < requiredXpLevel)
                                {
                                    CYANSH_LANG_UTILS.sendPlayerMessage(
                                            player,
                                            "cyan.msg.notEnoughXp",
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
                else
                {
                    CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansh.msg.notOpOrTrusted");
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /get-homes-of} or {@code /gho}
     * <p>
     * Sends a message in the player's chat with all the given player's homes
     * <p>
     * Succeeds only if the player is trusted or OP level {@code MIN_OP_LVL_BYPASS} and the bypass option is set to
     * {@code true}
     */
    public static int getHomesOfList(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (CYANSH_LIB_UTILS.isPlayer(context.getSource()))
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if (player.getName().getString().equals(trustingPlayer))
                {
                    CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansh.msg.useSelfHomes");
                }
                else if ((ALLOW_BYPASS.getValue() && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue()))
                        || TrustsObj.isPlayerTrustingFromName(trustingPlayer, player.getName().getString())
                )
                {
                    if (!HomesObj.isEmptyFromName(trustingPlayer))
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                                player,
                                "cyanlib.msg.dashSeparation",
                                false
                        );
                        CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                                player,
                                "cyansh.msg.listHomesOf",
                                false,
                                Formatting.AQUA + trustingPlayer
                        );

                        HomesObj.getPlayerHomes(HomesObj.getKeyFromName(trustingPlayer))
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
                        CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansh.error.noHomesOf");
                    }
                }
                else
                {
                    CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansh.error.notOpOrTrusted");
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
