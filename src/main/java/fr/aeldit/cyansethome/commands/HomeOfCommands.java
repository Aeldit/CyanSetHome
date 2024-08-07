package fr.aeldit.cyansethome.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.aeldit.cyansethome.commands.arguments.ArgumentSuggestion;
import fr.aeldit.cyansethome.homes.Homes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static fr.aeldit.cyanlib.lib.utils.TPUtils.getRequiredXpLevelsToTp;
import static fr.aeldit.cyansethome.CyanSHCore.*;
import static fr.aeldit.cyansethome.config.CyanLibConfigImpl.*;

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
        if (context.getSource().getPlayer() != null)
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_BYPASS.getValue(), "bypassDisabled")
                        && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue())
                )
                {
                    String trustingPlayer = StringArgumentType.getString(context, "player_name");
                    String homeName = StringArgumentType.getString(context, "home_name");

                    String playerKey = HomesObj.getKeyFromName(trustingPlayer);
                    if (playerKey != null)
                    {
                        if (HomesObj.maxHomesNotReached(playerKey))
                        {
                            if (HomesObj.addHome(
                                    playerKey,
                                    new Homes.Home(homeName, player.getWorld()
                                            //? if <1.20.6 {
                                            /*.getDimensionKey().getValue().toString()
                                             *///?} else {
                                            .getDimensionEntry().getIdAsString()
                                            //?}
                                            .replace("minecraft:", "").replace("the_", ""),
                                            player.getX(), player.getY(), player.getZ(), player.getYaw(),
                                            player.getPitch(),
                                            new SimpleDateFormat("dd/MM/yyyy HH:mm").format(
                                                    Calendar.getInstance().getTime())
                                    )
                            ))
                            {
                                CYANSH_LANG_UTILS.sendPlayerMessage(
                                        player,
                                        "cyansethome.msg.setHomeOf",
                                        Formatting.YELLOW + homeName,
                                        Formatting.AQUA + trustingPlayer
                                );
                            }
                            else
                            {
                                CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.homeAlreadyExists");
                            }
                        }
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.playerNotFound",
                                Formatting.AQUA + trustingPlayer
                        );
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
        if (context.getSource().getPlayer() != null)
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_BYPASS.getValue(), "bypassDisabled")
                        && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue())
                )
                {
                    String homeName = StringArgumentType.getString(context, "home_name");

                    String playerKey = HomesObj.getKeyFromName(trustingPlayer);
                    if (playerKey != null)
                    {
                        if (HomesObj.removeHome(playerKey, homeName))
                        {
                            CYANSH_LANG_UTILS.sendPlayerMessage(
                                    player,
                                    "cyansethome.msg.removeHomeOf",
                                    Formatting.YELLOW + homeName,
                                    Formatting.AQUA + trustingPlayer
                            );
                        }
                        else
                        {
                            CYANSH_LANG_UTILS.sendPlayerMessage(
                                    player,
                                    "cyansethome.error.homeNotFound",
                                    Formatting.YELLOW + homeName
                            );
                        }
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.playerNotFound",
                                Formatting.AQUA + trustingPlayer
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
        if (context.getSource().getPlayer() != null)
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_BYPASS.getValue(), "bypassDisabled")
                        && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue())
                )
                {
                    String trustingPlayer = StringArgumentType.getString(context, "player_name");

                    String playerKey = HomesObj.getKeyFromName(trustingPlayer);
                    if (playerKey != null)
                    {
                        if (HomesObj.removeAll(playerKey))
                        {
                            CYANSH_LANG_UTILS.sendPlayerMessage(
                                    player,
                                    "cyansethome.msg.removeAllHomesOf",
                                    Formatting.AQUA + trustingPlayer
                            );
                        }
                        else
                        {
                            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.noHomesOf");
                        }
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.playerNotFound",
                                Formatting.AQUA + trustingPlayer
                        );
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
        if (context.getSource().getPlayer() != null)
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_BYPASS.getValue(), "bypassDisabled")
                        && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue())
                )
                {
                    String homeName = StringArgumentType.getString(context, "home_name");
                    String newHomeName = StringArgumentType.getString(context, "new_home_name");

                    String playerKey = HomesObj.getKeyFromName(trustingPlayer);
                    if (playerKey != null)
                    {
                        if (HomesObj.rename(playerKey, homeName, newHomeName))
                        {
                            CYANSH_LANG_UTILS.sendPlayerMessage(
                                    player,
                                    "cyansethome.msg.renameHomeOf",
                                    Formatting.YELLOW + homeName,
                                    Formatting.YELLOW + newHomeName,
                                    Formatting.AQUA + trustingPlayer
                            );
                        }
                        else
                        {
                            CYANSH_LANG_UTILS.sendPlayerMessage(
                                    player,
                                    "cyansethome.error.homeNotFoundOrExists",
                                    homeName
                            );
                        }
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.playerNotFound",
                                Formatting.AQUA + trustingPlayer
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
        if (context.getSource().getPlayer() != null)
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
                        String playerKey = HomesObj.getKeyFromName(trustingPlayer);
                        if (playerKey != null)
                        {
                            Homes.Home home = HomesObj.getHome(playerKey, homeName);
                            MinecraftServer server = player.getServer();

                            if (home != null && server != null)
                            {
                                int requiredXpLevel = 0;

                                if (USE_XP_TO_TP_HOME.getValue() && !player.isCreative())
                                {
                                    requiredXpLevel = getRequiredXpLevelsToTp(player, player.getBlockPos(),
                                            BLOCKS_PER_XP_LEVEL_HOME.getValue()
                                    );

                                    if (player.experienceLevel < requiredXpLevel)
                                    {
                                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                                player,
                                                "cyansethome.error.notEnoughXp",
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
                                        "cyansethome.msg.goToHome",
                                        Formatting.YELLOW + homeName
                                );
                            }
                        }
                        else
                        {
                            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.playerNotFound",
                                    Formatting.AQUA + trustingPlayer
                            );
                        }
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansethome.error.homeNotFound",
                                Formatting.YELLOW + homeName
                        );
                    }
                }
                else
                {
                    CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.notOpOrTrusted");
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
        if (context.getSource().getPlayer() != null)
        {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if (CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
            {
                String trustingPlayer = StringArgumentType.getString(context, "player_name");

                if (player.getName().getString().equals(trustingPlayer))
                {
                    CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.useSelfHomes");
                }
                else if ((ALLOW_BYPASS.getValue() && player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue()))
                        || TrustsObj.isPlayerTrustingFromName(trustingPlayer, player.getName().getString())
                )
                {
                    if (!HomesObj.isEmptyFromName(trustingPlayer))
                    {
                        player.sendMessage(Text.of("§6------------------------------------"), false);
                        CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                                player,
                                "cyansethome.msg.listHomesOf",
                                false,
                                Formatting.AQUA + trustingPlayer
                        );

                        String playerKey = HomesObj.getKeyFromName(trustingPlayer);
                        if (playerKey != null)
                        {
                            List<Homes.Home> homes = HomesObj.getPlayerHomes(playerKey);
                            if (homes != null)
                            {
                                for (Homes.Home home : homes)
                                {
                                    CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                                            player,
                                            "cyansethome.msg.getHome",
                                            false,
                                            Formatting.YELLOW + home.getName(),
                                            Formatting.DARK_AQUA + home.getDimension(),
                                            Formatting.DARK_AQUA + home.getDate()
                                    );
                                }
                            }
                            player.sendMessage(Text.of("§6------------------------------------"), false);
                        }
                        else
                        {
                            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.playerNotFound",
                                    Formatting.AQUA + trustingPlayer
                            );
                        }
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.noHomesOf");
                    }
                }
                else
                {
                    CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.notOpOrTrusted");
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
