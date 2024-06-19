package fr.aeldit.cyansethome.commands.arguments;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static fr.aeldit.cyansethome.CyanSHCore.HomesObj;
import static fr.aeldit.cyansethome.CyanSHCore.TrustsObj;
import static fr.aeldit.cyansethome.config.CyanLibConfigImpl.ALLOW_BYPASS;

public final class ArgumentSuggestion
{
    /**
     * Called for the command {@code /get-homes} or the suggestions of the {@code home} commands
     *
     * @return A suggestion with all the player's homes
     */
    public static CompletableFuture<Suggestions> getHomes(
            @NotNull SuggestionsBuilder builder,
            @NotNull ServerPlayerEntity player
    )
    {
        List<String> names = HomesObj.getHomesNames("%s %s".formatted(player.getUuidAsString(),
                player.getName().getString()
        ));
        if (names != null)
        {
            return CommandSource.suggestMatching(names, builder);
        }
        return new CompletableFuture<>();
    }

    /**
     * Called for the command {@code /get-homes-of} or the suggestions of the {@code homeOf} commands
     *
     * @return A suggestion with all the trusting player's homes
     */
    @Contract("_, null, _ -> new")
    public static @NotNull CompletableFuture<Suggestions> getHomesOf(
            @NotNull SuggestionsBuilder builder,
            @Nullable ServerPlayerEntity player,
            @NotNull String trustingPlayer
    )
    {
        if (player != null)
        {
            if ((ALLOW_BYPASS.getValue() && player.hasPermissionLevel(4))
                    || TrustsObj.isPlayerTrustingFromName(trustingPlayer, player.getName().getString())
            )
            {
                List<String> homesNames = HomesObj.getHomesNamesOf(trustingPlayer);
                if (homesNames != null)
                {
                    CommandSource.suggestMatching(homesNames, builder);
                }
            }
        }
        return new CompletableFuture<>();
    }

    /**
     * Called for the command {@code /home-trust}
     *
     * @return A suggestion with all the online players
     */
    public static CompletableFuture<Suggestions> getOnlinePlayersName(
            @NotNull SuggestionsBuilder builder,
            @NotNull ServerCommandSource source
    )
    {
        if (source.getPlayer() != null)
        {
            ArrayList<String> players =
                    new ArrayList<>(source.getServer().getPlayerManager().getPlayerList().size() - 1);
            String playerName = source.getPlayer().getName().getString();

            for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList())
            {
                if (!playerName.equals(player.getName().getString()))
                {
                    players.add(player.getName().getString());
                }
            }
            return CommandSource.suggestMatching(players, builder);
        }
        return new CompletableFuture<>();
    }

    /**
     * Called for the command {@code /home-untrust}
     *
     * @return A suggestion with all the trusted players
     */
    @Contract("_, null -> new")
    public static CompletableFuture<Suggestions> getTrustedPlayersName(
            @NotNull SuggestionsBuilder builder,
            @Nullable ServerPlayerEntity player
    )
    {
        if (player != null)
        {
            String playerNameUUID = "%s %s".formatted(player.getUuidAsString(), player.getName().getString());
            List<String> trustedPlayers = TrustsObj.getTrustedPlayers(playerNameUUID);

            if (trustedPlayers != null)
            {
                ArrayList<String> names = new ArrayList<>(trustedPlayers.size());
                for (String s : trustedPlayers)
                {
                    names.add(s.split(" ")[1]);
                }
                return CommandSource.suggestMatching(names, builder);
            }
        }
        return new CompletableFuture<>();
    }

    /**
     * Called for the homeOf commands
     *
     * @return A suggestion with all the trusting players
     */
    @Contract("_, null -> new")
    public static CompletableFuture<Suggestions> getTrustingPlayersName(
            @NotNull SuggestionsBuilder builder,
            @Nullable ServerPlayerEntity player
    )
    {
        if (player != null)
        {
            if (ALLOW_BYPASS.getValue() && player.hasPermissionLevel(4))
            {
                return CommandSource.suggestMatching(HomesObj.getPlayersWithHomes(player.getName().getString()),
                        builder
                );
            }
            else
            {
                CommandSource.suggestMatching(
                        TrustsObj.getTrustingPlayers("%s %s".formatted(player.getUuidAsString(),
                                player.getName().getString()
                        )),
                        builder
                );
            }
        }
        return new CompletableFuture<>();
    }
}
