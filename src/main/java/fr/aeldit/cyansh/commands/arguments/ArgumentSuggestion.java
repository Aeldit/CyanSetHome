package fr.aeldit.cyansh.commands.arguments;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static fr.aeldit.cyansh.CyanSHCore.HomesObj;
import static fr.aeldit.cyansh.CyanSHCore.TrustsObj;
import static fr.aeldit.cyansh.config.CyanLibConfigImpl.ALLOW_BYPASS;

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
        return CommandSource.suggestMatching(
                HomesObj.getHomesNames(player.getUuidAsString() + " " + player.getName().getString()), builder);
    }

    /**
     * Called for the command {@code /get-homes-of} or the suggestions of the {@code homeOf} commands
     *
     * @return A suggestion with all the trusting player's homes
     */
    @Contract("_, null, _ -> new")
    public static CompletableFuture<Suggestions> getHomesOf(
            @NotNull SuggestionsBuilder builder,
            @Nullable ServerPlayerEntity player,
            @NotNull String trustingPlayer
    )
    {
        if (player != null)
        {
            return (ALLOW_BYPASS.getValue() && player.hasPermissionLevel(4)) || TrustsObj.isPlayerTrustingFromName(
                    trustingPlayer, player.getName().getString())
                   ? CommandSource.suggestMatching(HomesObj.getHomesNamesOf(trustingPlayer), builder)
                   : new CompletableFuture<>();
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
        ArrayList<String> players = new ArrayList<>(source.getServer().getPlayerManager().getPlayerList().size() - 1);
        String playerName = Objects.requireNonNull(source.getPlayer()).getName().getString();

        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList())
        {
            if (!playerName.equals(player.getName().getString()))
            {
                players.add(player.getName().getString());
            }
        }
        return CommandSource.suggestMatching(players, builder);
    }

    /**
     * Called for the command {@code /home-untrust}
     *
     * @return A suggestion with all the trusted players
     */
    public static CompletableFuture<Suggestions> getTrustedPlayersName(
            @NotNull SuggestionsBuilder builder,
            @Nullable ServerPlayerEntity player
    )
    {
        if (player != null)
        {
            String playerNameUUID = player.getUuidAsString() + " " + player.getName().getString();
            ArrayList<String> names = new ArrayList<>(TrustsObj.getTrustedPlayers(playerNameUUID).size());

            for (String s : TrustsObj.getTrustedPlayers(playerNameUUID))
            {
                names.add(s.split(" ")[1]);
            }
            return CommandSource.suggestMatching(names, builder);
        }
        return CommandSource.suggestMatching(new ArrayList<>(0), builder);
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
            return player.hasPermissionLevel(4) && ALLOW_BYPASS.getValue()
                   ? CommandSource.suggestMatching(HomesObj.getPlayersWithHomes(player.getName().getString()), builder)
                   : CommandSource.suggestMatching(
                           TrustsObj.getTrustingPlayers(player.getUuidAsString() + " " + player.getName().getString()),
                           builder
                   );
        }
        return new CompletableFuture<>();
    }
}
