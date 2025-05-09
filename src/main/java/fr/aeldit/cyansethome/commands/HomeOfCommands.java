package fr.aeldit.cyansethome.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.aeldit.cyansethome.CombatTracking;
import fr.aeldit.cyansethome.commands.arguments.ArgumentSuggestion;
import fr.aeldit.cyansethome.homes.Home;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static fr.aeldit.cyansethome.CooldownManager.addPlayerCooldown;
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
                                                                                          "home_name",
                                                                                          StringArgumentType.string()
                                                                                  )
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
                                                                                          "home_name",
                                                                                          StringArgumentType.string()
                                                                                  )
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
                                                                                          "home_name",
                                                                                          StringArgumentType.string()
                                                                                  )
                                                                                  .suggests(
                                                                                          (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                                                                  builder,
                                                                                                  context.getSource()
                                                                                                         .getPlayer(),
                                                                                                  context.getInput()
                                                                                                         .split(" ")[1]
                                                                                          ))
                                                                                  .executes(
                                                                                          HomeOfCommands::removeHomeOf)
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
                                                                                          "home_name",
                                                                                          StringArgumentType.string()
                                                                                  )
                                                                                  .suggests(
                                                                                          (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                                                                  builder,
                                                                                                  context.getSource()
                                                                                                         .getPlayer(),
                                                                                                  context.getInput()
                                                                                                         .split(" ")[1]
                                                                                          ))
                                                                                  .executes(
                                                                                          HomeOfCommands::removeHomeOf)
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
                                                                                          "home_name",
                                                                                          StringArgumentType.string()
                                                                                  )
                                                                                  .suggests(
                                                                                          (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                                                                  builder,
                                                                                                  context.getSource()
                                                                                                         .getPlayer(),
                                                                                                  context.getInput()
                                                                                                         .split(" ")[1]
                                                                                          ))
                                                                                  .then(CommandManager.argument(
                                                                                                              "new_home_name",
                                                                                                              StringArgumentType.string()
                                                                                                      )
                                                                                                      .executes(
                                                                                                              HomeOfCommands::renameHomeOf)
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
                                                                                          "home_name",
                                                                                          StringArgumentType.string()
                                                                                  )
                                                                                  .suggests(
                                                                                          (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                                                                  builder,
                                                                                                  context.getSource()
                                                                                                         .getPlayer(),
                                                                                                  context.getInput()
                                                                                                         .split(" ")[1]
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
                                                                                          "home_name",
                                                                                          StringArgumentType.string()
                                                                                  )
                                                                                  .suggests(
                                                                                          (context, builder) -> ArgumentSuggestion.getHomesOf(
                                                                                                  builder,
                                                                                                  context.getSource()
                                                                                                         .getPlayer(),
                                                                                                  context.getInput()
                                                                                                         .split(" ")[1]
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
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null
                || !player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue())
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled")
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_BYPASS.getValue(), "bypassDisabled")
        )
        {
            return 0;
        }

        String trustingPlayer = StringArgumentType.getString(context, "player_name");

        String playerKey = HOMES.getKeyFromName(trustingPlayer);
        if (playerKey == null)
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(
                    player,
                    "error.playerNotFound",
                    Formatting.AQUA + trustingPlayer
            );
            return 0;
        }

        if (HOMES.maxHomesReached(playerKey))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "error.maxHomesReached", MAX_HOMES.getValue());
            return 0;
        }

        String homeName = StringArgumentType.getString(context, "home_name");

        if (!HOMES.addHome(
                playerKey, new Home(
                        homeName,
                        player.getWorld()
                              //? if <1.20.6 {
                              /*.getDimensionKey().getValue().toString()
                               *///?} else {
                              .getDimensionEntry().getIdAsString()
                              //?}
                              .replace("minecraft:", "")
                              .replace("the_", ""),
                        player.getX(), player.getY(), player.getZ(), player.getYaw(),
                        player.getPitch(),
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime())
                )
        ))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "error.homeAlreadyExists");
            return 0;
        }

        CYANSH_LANG_UTILS.sendPlayerMessage(
                player,
                "msg.setHomeOf",
                Formatting.YELLOW + homeName,
                Formatting.AQUA + trustingPlayer
        );
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
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null
                || !player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue())
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled")
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_BYPASS.getValue(), "bypassDisabled")
        )
        {
            return 0;
        }

        String trustingPlayer = StringArgumentType.getString(context, "player_name");

        String playerKey = HOMES.getKeyFromName(trustingPlayer);
        if (playerKey == null)
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(
                    player, "error.playerNotFound",
                    Formatting.AQUA + trustingPlayer
            );
            return 0;
        }

        String homeName = StringArgumentType.getString(context, "home_name");

        if (!HOMES.removeHome(playerKey, homeName))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "error.homeNotFound", Formatting.YELLOW + homeName);
            return 0;
        }

        CYANSH_LANG_UTILS.sendPlayerMessage(
                player,
                "msg.removeHomeOf",
                Formatting.YELLOW + homeName,
                Formatting.AQUA + trustingPlayer
        );
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
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null
                || !player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue())
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled")
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_BYPASS.getValue(), "bypassDisabled")
        )
        {
            return 0;
        }

        String trustingPlayer = StringArgumentType.getString(context, "player_name");

        String playerKey = HOMES.getKeyFromName(trustingPlayer);
        if (playerKey == null)
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(
                    player, "error.playerNotFound",
                    Formatting.AQUA + trustingPlayer
            );
            return 0;
        }

        if (!HOMES.removeAll(playerKey))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "error.noHomesOf");
            return 0;
        }

        CYANSH_LANG_UTILS.sendPlayerMessage(
                player,
                "msg.removeAllHomesOf",
                Formatting.AQUA + trustingPlayer
        );
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
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null
                || !player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue())
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled")
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_BYPASS.getValue(), "bypassDisabled")
        )
        {
            return 0;
        }

        String trustingPlayer = StringArgumentType.getString(context, "player_name");

        String playerKey = HOMES.getKeyFromName(trustingPlayer);
        if (playerKey == null)
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(
                    player,
                    "error.playerNotFound",
                    Formatting.AQUA + trustingPlayer
            );
            return 0;
        }

        String homeName = StringArgumentType.getString(context, "home_name");
        String newHomeName = StringArgumentType.getString(context, "new_home_name");

        if (!HOMES.rename(playerKey, homeName, newHomeName))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "error.homeNotFoundOrExists", homeName);
            return 0;
        }

        CYANSH_LANG_UTILS.sendPlayerMessage(
                player,
                "msg.renameHomeOf",
                Formatting.YELLOW + homeName,
                Formatting.YELLOW + newHomeName,
                Formatting.AQUA + trustingPlayer
        );
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
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
        {
            return 0;
        }

        if (!TP_IN_COMBAT.getValue() && CombatTracking.isPlayerInCombat(player.getName().getString()))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "error.noHomeWhileInCombat");
            return 0;
        }

        String trustingPlayer = StringArgumentType.getString(context, "player_name");
        String playerKey = HOMES.getKeyFromName(trustingPlayer);
        if (playerKey == null)
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(
                    player,
                    "error.playerNotFound",
                    Formatting.AQUA + trustingPlayer
            );
            return 0;
        }

        if ((!ALLOW_BYPASS.getValue() || !player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue()))
                && !TRUSTS.isPlayerTrustingFromName(trustingPlayer, player.getName().getString())
        )
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "error.notOpOrTrusted");
            return 0;
        }


        String homeName = StringArgumentType.getString(context, "home_name");

        Home home = HOMES.getHome(playerKey, homeName);
        if (home == null)
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "error.homeNotFound", Formatting.YELLOW + homeName);
            return 0;
        }

        MinecraftServer server = player.getServer();
        if (server == null)
        {
            return 0;
        }

        int requiredXpLevelOrPoints = 0;

        if (USE_XP_TO_TP_HOME.getValue() && !player.isCreative())
        {
            requiredXpLevelOrPoints = XP_USE_FIXED_AMOUNT.getValue() ? XP_AMOUNT.getValue()
                    : home.getRequiredXpLevelsToTp(player);

            if ((XP_USE_POINTS.getValue() ? player.totalExperience : player.experienceLevel) < requiredXpLevelOrPoints)
            {
                CYANSH_LANG_UTILS.sendPlayerMessage(
                        player,
                        "error.notEnoughXp",
                        Formatting.GOLD + String.valueOf(requiredXpLevelOrPoints),
                        Formatting.RED + (XP_USE_POINTS.getValue() ? "points" : "levels")
                );
                return 0;
            }
        }

        if (TP_COOLDOWN.getValue())
        {
            String playerName = player.getName().getString();
            HOMES.requestTp(playerName);
            addPlayerCooldown(
                    player, TP_COOLDOWN_SECONDS.getValue() * 1000, System.currentTimeMillis(), home,
                    requiredXpLevelOrPoints, server
            );
            CYANSH_LANG_UTILS.sendPlayerMessage(
                    player, "msg.waitingXSeconds", Formatting.GOLD + String.valueOf(TP_COOLDOWN_SECONDS.getValue())
            );
            // Teleportation will be executed in the CyanSHClientCore and CyanSHServerCore classes
            return Command.SINGLE_SUCCESS;
        }

        home.teleport(server, player);

        if (XP_USE_POINTS.getValue())
        {
            player.addExperience(-1 * requiredXpLevelOrPoints);
        }
        else
        {
            player.addExperienceLevels(-1 * requiredXpLevelOrPoints);
        }

        CYANSH_LANG_UTILS.sendPlayerMessage(player, "msg.goToHome", Formatting.YELLOW + homeName);
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
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled"))
        {
            return 0;
        }

        // Player accessing its own homes
        String trustingPlayer = StringArgumentType.getString(context, "player_name");
        if (player.getName().getString().equals(trustingPlayer))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "error.useSelfHomes");
            return 0;
        }

        // The player does not have sufficient permissions or is not trusted
        if ((!ALLOW_BYPASS.getValue() || !player.hasPermissionLevel(MIN_OP_LVL_BYPASS.getValue()))
                && !TRUSTS.isPlayerTrustingFromName(trustingPlayer, player.getName().getString())
        )
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "error.notOpOrTrusted");
            return 0;
        }

        // The player couldn't be found
        String playerKey = HOMES.getKeyFromName(trustingPlayer);
        if (playerKey == null)
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(
                    player,
                    "error.playerNotFound",
                    Formatting.AQUA + trustingPlayer
            );
            return 0;
        }

        // The player doesn't have homes
        List<Home> homes = HOMES.getPlayerHomes(playerKey);
        if (homes == null)
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "error.noHomesOf");
            return 0;
        }

        player.sendMessage(Text.of("§6------------------------------------"), false);
        CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                player,
                "msg.listHomesOf",
                false,
                Formatting.AQUA + trustingPlayer
        );

        homes.forEach(home -> home.sendFormatedMessage(player));

        player.sendMessage(Text.of("§6------------------------------------"), false);
        return Command.SINGLE_SUCCESS;
    }
}
