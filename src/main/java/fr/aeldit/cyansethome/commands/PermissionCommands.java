package fr.aeldit.cyansethome.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.aeldit.cyansethome.commands.arguments.ArgumentSuggestion;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static fr.aeldit.cyansethome.CyanSHCore.CYANSH_LANG_UTILS;
import static fr.aeldit.cyansethome.CyanSHCore.TrustsObj;

public class PermissionCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(
                CommandManager.literal("home-trust")
                        .then(CommandManager.argument("player", StringArgumentType.string())
                                      .suggests((context, builder) -> ArgumentSuggestion.getOnlinePlayersName(
                                              builder, context.getSource())
                                      )
                                      .executes(PermissionCommands::trustPlayer)
                        )
        );

        dispatcher.register(
                CommandManager.literal("home-untrust")
                        .then(CommandManager.argument("player", StringArgumentType.string())
                                      .suggests((context, builder) -> ArgumentSuggestion.getTrustedPlayersName(
                                              builder, context.getSource().getPlayer())
                                      )
                                      .executes(PermissionCommands::untrustPlayer)
                        )
        );

        dispatcher.register(CommandManager.literal("get-trusting-players")
                                    .executes(PermissionCommands::getTrustingPlayers)
        );
        dispatcher.register(CommandManager.literal("get-trusted-players")
                                    .executes(PermissionCommands::getTrustedPlayers)
        );
    }

    /**
     * Called by the command {@code /home-trust <player>}
     * <p>
     * Used to define which players can use the homes of the trusting player
     */
    public static int trustPlayer(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null)
        {
            return 0;
        }

        ServerPlayerEntity player = source.getPlayer();
        String playerName = StringArgumentType.getString(context, "player");

        ServerPlayerEntity trustedPlayer = source.getServer().getPlayerManager().getPlayer(playerName);
        // The player to trust is not online
        if (trustedPlayer == null)
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.playerNotOnline");
            return 0;
        }

        String trustingPlayerKey = "%s %s".formatted(player.getUuidAsString(), player.getName().getString());
        String trustedPlayerKey = "%s %s".formatted(trustedPlayer.getUuid(), playerName);

        // The player tried to trust themselves
        if (trustingPlayerKey.equals(trustedPlayerKey))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.selfTrust");
            return 0;
        }

        // The player is already trusted
        if (TrustsObj.isPlayerTrustingFromName(player.getName().getString(), playerName))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.playerAlreadyTrusted");
            return 0;
        }

        TrustsObj.trustPlayer(trustingPlayerKey, trustedPlayerKey);

        CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.msg.playerTrusted", Formatting.AQUA + playerName);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /home-untrust <player>}
     * <p>
     * Used to remove a player from the trust list
     */
    public static int untrustPlayer(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null)
        {
            return 0;
        }

        String untrustedPlayerName = StringArgumentType.getString(context, "player");
        // The player tried to untrust themselves
        if (player.getName().getString().equals(untrustedPlayerName))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.selfTrust");
            return 0;
        }

        // The given player is already not trusted
        if (!TrustsObj.isPlayerTrustingFromName(player.getName().getString(), untrustedPlayerName))
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.playerNotTrusted");
            return 0;
        }

        TrustsObj.untrustPlayer(player.getName().getString(), untrustedPlayerName);

        CYANSH_LANG_UTILS.sendPlayerMessage(
                player,
                "cyansethome.msg.playerUnTrusted",
                Formatting.AQUA + untrustedPlayerName
        );
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /get-trusting-players}
     * <p>
     * Send a message to the player with all the players that trust her/him
     */
    public static int getTrustingPlayers(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null)
        {
            return 0;
        }

        ArrayList<String> trustingPlayers = TrustsObj.getTrustingPlayers(
                "%s %s".formatted(player.getUuidAsString(), player.getName().getString())
        );
        if (trustingPlayers == null || trustingPlayers.isEmpty())
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.msg.noTrustingPlayer");
            return 0;
        }

        String players = getPlayers(trustingPlayers);

        CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                player,
                "cyansethome.msg.getTrustingPlayers",
                false,
                Formatting.AQUA + players
        );
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /get-trusted-players}
     * <p>
     * Send a message to the player with all the players that she/he trusts
     */
    public static int getTrustedPlayers(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null)
        {
            return 0;
        }

        List<String> trustedPlayers = TrustsObj.getTrustedPlayers(
                "%s %s".formatted(player.getUuidAsString(), player.getName().getString())
        );
        if (trustedPlayers == null || trustedPlayers.isEmpty())
        {
            CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.msg.noTrustedPlayer");
            return 0;
        }

        CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                player,
                "cyansethome.msg.getTrustedPlayers",
                false,
                Formatting.AQUA + getPlayers(trustedPlayers)
        );
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Takes a list of player names and creates a String that contains them all, in a form that is understandable by any
     * player
     *
     * @param trustedPlayers The list of the trusted player's names
     * @return The String containing the list of all player names in the proper form (ex: {@code "Player1, Player2,
     * Player3"})
     */
    private static @NotNull String getPlayers(@NotNull List<String> trustedPlayers)
    {
        if (trustedPlayers.isEmpty())
        {
            return "";
        }

        if (trustedPlayers.size() == 1)
        {
            return trustedPlayers.get(0);
        }

        StringBuilder players = new StringBuilder();
        int size = trustedPlayers.size();
        for (int i = 0; i < size - 1; ++i)
        {
            players.append("%s, ".formatted(trustedPlayers.get(i)));
        }

        players.append("%s".formatted(trustedPlayers.get(size - 1)));
        return players.toString();
    }
}
