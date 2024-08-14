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
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static fr.aeldit.cyanlib.lib.utils.TPUtils.getRequiredXpLevelsToTp;
import static fr.aeldit.cyansethome.CyanSHCore.*;
import static fr.aeldit.cyansethome.config.CyanLibConfigImpl.*;

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
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null
                || !CYANSH_LIB_UTILS.hasPermission(player, MIN_OP_LVL_HOMES.getValue())
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled")
        )
        {
            return 0;
        }

        String homeName = StringArgumentType.getString(context, "home_name");
        String playerKey = "%s %s".formatted(player.getUuidAsString(), player.getName().getString());

        if (!HomesObj.maxHomesNotReached(playerKey))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(
                    player,
                    "cyansethome.error.maxHomesReached",
                    Formatting.GOLD + MAX_HOMES.getValue().toString()
            );
            return 0;
        }

        if (!HomesObj.addHome(playerKey, new Homes.Home(
                homeName,
                player.getWorld()
                        //? if <1.20.6 {
                        /*.getDimensionKey().getValue().toString()
                         *///?} else {
                        .getDimensionEntry().getIdAsString()
                        //?}
                        .replace("minecraft:", "")
                        .replace("the_", ""),
                player.getX(), player.getY(), player.getZ(),
                player.getYaw(), player.getPitch(),
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime())
        )))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.homeAlreadyExists");
            return 0;
        }

        CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.msg.setHome", Formatting.YELLOW + homeName);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /remove-home <home_name>} or {@code /rh <home_name>}
     * <p>
     * Removes the given home
     */
    public static int removeHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null
                || !CYANSH_LIB_UTILS.hasPermission(player, MIN_OP_LVL_HOMES.getValue())
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled")
        )
        {
            return 0;
        }

        String homeName = StringArgumentType.getString(context, "home_name");
        String playerKey = "%s %s".formatted(player.getUuidAsString(), player.getName().getString());

        if (!HomesObj.removeHome(playerKey, homeName))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.homeNotFound", Formatting.YELLOW + homeName);
            return 0;
        }

        CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.msg.removeHome", Formatting.YELLOW + homeName);
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
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null
                || !CYANSH_LIB_UTILS.hasPermission(player, MIN_OP_LVL_HOMES.getValue())
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled")
        )
        {
            return 0;
        }

        if (!HomesObj.removeAll("%s %s".formatted(player.getUuidAsString(), player.getName().getString())))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.noHomes");
            return 0;
        }

        CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.msg.removeAllHomes");
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /rename-home <home_name> <new_home_name>}
     * <p>
     * Renames the location
     */
    public static int renameHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null
                || !CYANSH_LIB_UTILS.hasPermission(player, MIN_OP_LVL_HOMES.getValue())
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled")
        )
        {
            return 0;
        }

        String homeName = StringArgumentType.getString(context, "home_name");
        String newHomeName = StringArgumentType.getString(context, "new_home_name");

        String playerKey = "%s %s".formatted(player.getUuidAsString(), player.getName().getString());

        if (!HomesObj.rename(playerKey, homeName, newHomeName))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.homeNotFoundOrExists", homeName);
            return 0;
        }

        CYANSH_LANG_UTILS.sendPlayerMessage(
                player,
                "cyansethome.msg.renameHome",
                Formatting.YELLOW + homeName,
                Formatting.YELLOW + newHomeName
        );
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /home <home_name>} or {@code /h <home_name>}
     * <p>
     * Teleports the player to the given home
     */
    public static int goToHome(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null
                || !CYANSH_LIB_UTILS.hasPermission(player, MIN_OP_LVL_HOMES.getValue())
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled")
        )
        {
            return 0;
        }

        String homeName = StringArgumentType.getString(context, "home_name");
        String playerKey = "%s %s".formatted(player.getUuidAsString(), player.getName().getString());

        Homes.Home home = HomesObj.getHome(playerKey, homeName);
        if (home == null)
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.homeNotFound", Formatting.YELLOW + homeName);
            return 0;
        }

        MinecraftServer server = player.getServer();
        if (server == null)
        {
            return 0;
        }

        int requiredXpLevel = 0;

        if (USE_XP_TO_TP_HOME.getValue() && !player.isCreative())
        {
            requiredXpLevel = getRequiredXpLevelsToTp(
                    player, player.getBlockPos(),
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

        home.teleport(player, server);

        player.addExperienceLevels(-1 * requiredXpLevel);

        CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.msg.goToHome", Formatting.YELLOW + homeName);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /get-homes} or {@code /gh}
     * <p>
     * Sends a message in the player's chat with all its homes
     */
    public static int getHomesList(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null
                || !CYANSH_LIB_UTILS.hasPermission(player, MIN_OP_LVL_HOMES.getValue())
                || !CYANSH_LIB_UTILS.isOptionEnabled(player, ALLOW_HOMES.getValue(), "homesDisabled")
        )
        {
            return 0;
        }

        List<Homes.Home> homes = HomesObj.getPlayerHomes(
                "%s %s".formatted(player.getUuidAsString(), player.getName().getString())
        );
        if (homes == null)
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.noHomes");
            return 0;
        }

        player.sendMessage(Text.of("§6------------------------------------"), false);
        CYANSH_LANG_UTILS.sendPlayerMessageActionBar(player, "cyansethome.msg.listHomes", false);

        for (Homes.Home home : homes)
        {
            home.sendFormatedMessage(player);
        }

        player.sendMessage(Text.of("§6------------------------------------"), false);
        return Command.SINGLE_SUCCESS;
    }
}
