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
        dispatcher.register(CommandManager.literal("home-trust")
                .then(CommandManager.argument("player", StringArgumentType.string())
                        .suggests(
                                (context, builder) -> ArgumentSuggestion.getOnlinePlayersName(
                                        builder, context.getSource()))
                        .executes(PermissionCommands::trustPlayer)
                )
        );

        dispatcher.register(CommandManager.literal("home-untrust")
                .then(CommandManager.argument("player", StringArgumentType.string())
                        .suggests(
                                (context, builder) -> ArgumentSuggestion.getTrustedPlayersName(
                                        builder, context.getSource().getPlayer()))
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
        if (source.getPlayer() != null)
        {
            ServerPlayerEntity player = source.getPlayer();
            String playerName = StringArgumentType.getString(context, "player");

            if (source.getServer().getPlayerManager().getPlayer(playerName) == null)
            {
                CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.playerNotOnline");
            }
            else
            {
                String trustingPlayer = "%s %s".formatted(player.getUuidAsString(), player.getName().getString());
                String trustedPlayer =
                        "%s %s".formatted(source.getServer().getPlayerManager().getPlayer(playerName).getUuid(),
                                playerName
                        );

                if (!trustingPlayer.equals(trustedPlayer))
                {
                    if (!TrustsObj.isPlayerTrustingFromName(player.getName().getString(), playerName))
                    {
                        TrustsObj.trustPlayer(trustingPlayer, trustedPlayer);

                        CYANSH_LANG_UTILS.sendPlayerMessage(
                                player,
                                "cyansethome.msg.playerTrusted",
                                Formatting.AQUA + playerName
                        );
                    }
                    else
                    {
                        CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.playerAlreadyTrusted");
                    }
                }
                else
                {
                    CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.selfTrust");
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /home-untrust <player>}
     * <p>
     * Used to remove a player from the trust list
     */
    public static int untrustPlayer(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (context.getSource().getPlayer() != null)
        {
            ServerPlayerEntity player = context.getSource().getPlayer();
            String untrustedPlayerName = StringArgumentType.getString(context, "player");

            if (!player.getName().getString().equals(untrustedPlayerName))
            {
                if (TrustsObj.isPlayerTrustingFromName(player.getName().getString(), untrustedPlayerName))
                {
                    TrustsObj.untrustPlayer(player.getName().getString(), untrustedPlayerName);

                    CYANSH_LANG_UTILS.sendPlayerMessage(
                            player,
                            "cyansethome.msg.playerUnTrusted",
                            Formatting.AQUA + untrustedPlayerName
                    );
                }
                else
                {
                    CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.playerNotTrusted");
                }
            }
            else
            {
                CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.selfTrust");
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /get-trusting-players}
     * <p>
     * Send a message to the player with all the players that trust her/him
     */
    public static int getTrustingPlayers(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (context.getSource().getPlayer() != null)
        {
            ServerPlayerEntity player = context.getSource().getPlayer();
            ArrayList<String> trustingPlayers = TrustsObj.getTrustingPlayers(
                    "%s %s".formatted(player.getUuidAsString(), player.getName().getString()));

            if (!trustingPlayers.isEmpty())
            {
                String players = getPlayers(trustingPlayers);

                CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                        player,
                        "cyansethome.msg.getTrustingPlayers",
                        false,
                        Formatting.AQUA + players
                );
            }
            else
            {
                CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.noTrustingPlayer");
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /get-trusted-players}
     * <p>
     * Send a message to the player with all the players that she/he trusts
     */
    public static int getTrustedPlayers(@NotNull CommandContext<ServerCommandSource> context)
    {
        if (context.getSource().getPlayer() != null)
        {
            ServerPlayerEntity player = context.getSource().getPlayer();
            List<String> trustedPlayers = TrustsObj.getTrustedPlayers(
                    "%s %s".formatted(player.getUuidAsString(), player.getName().getString()));

            if (trustedPlayers != null && !trustedPlayers.isEmpty())
            {
                String players = getPlayers(trustedPlayers);

                CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                        player,
                        "cyansethome.msg.getTrustedPlayers",
                        false,
                        Formatting.AQUA + players
                );
            }
            else
            {
                CYANSH_LANG_UTILS.sendPlayerMessage(player, "cyansethome.error.noTrustedPlayer");
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static @NotNull String getPlayers(@NotNull List<String> trustedPlayers) // TODO -> Test if this still works
    {
        if (trustedPlayers.size() == 1)
        {
            return trustedPlayers.get(0);
        }

        String players = "";

        for (int i = 0; i < trustedPlayers.size(); ++i)
        {
            if (i == trustedPlayers.size() - 1)
            {
                players = players.concat("%s".formatted(trustedPlayers.get(i).split(" ")[1]));
            }
            else
            {
                players = players.concat("%s, ".formatted(trustedPlayers.get(i).split(" ")[1]));
            }
        }
        return players;
    }
}
