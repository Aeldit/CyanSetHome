package fr.aeldit.cyansh.commands.argumentTypes;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.aeldit.cyansh.config.CyanSHMidnightConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static fr.aeldit.cyansh.util.Utils.*;

public final class ArgumentSuggestion
{
    /**
     * Called for the commands {@code /cyansh config booleanOption} and {@code /cyansh description options booleanOption}
     *
     * @param builder the suggestion builder
     * @return a suggestion with all the boolean options
     */
    public static CompletableFuture<Suggestions> getBoolOptions(@NotNull SuggestionsBuilder builder)
    {
        return CommandSource.suggestMatching(CyanSHMidnightConfig.generateBoolOptionsMap().keySet(), builder);
    }

    /**
     * Called for the commands {@code /cyansh config integerOption} and {@code /cyansh description options integerOption}
     *
     * @param builder the suggestion builder
     * @return a suggestion with all the integer options
     */
    public static CompletableFuture<Suggestions> getIntegerOptions(@NotNull SuggestionsBuilder builder)
    {
        return CommandSource.suggestMatching(CyanSHMidnightConfig.generateIntegerOptionsMap().keySet(), builder);
    }

    /**
     * Called for the command {@code /cyansh getConfig}
     *
     * @param builder the suggestion builder
     * @return a suggestion with all the available commands
     */
    public static CompletableFuture<Suggestions> getCommands(@NotNull SuggestionsBuilder builder)
    {
        return CommandSource.suggestMatching(CyanSHMidnightConfig.generateCommandsList(), builder);
    }

    public static CompletableFuture<Suggestions> getHomes(@NotNull SuggestionsBuilder builder, @NotNull ServerPlayerEntity player)
    {
        List<String> homes;
        try
        {
            Properties properties = new Properties();
            properties.load(new FileInputStream(homesPath + "\\" + player.getUuidAsString() + ".properties"));
            homes = new ArrayList<>(properties.stringPropertyNames());
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return CommandSource.suggestMatching(homes, builder);
    }

    public static CompletableFuture<Suggestions> getOnlinePlayersName(@NotNull SuggestionsBuilder builder, @NotNull ServerCommandSource source)
    {
        List<String> players = new ArrayList<>();

        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList())
        {
            players.add(player.getName().getString());
        }

        players.remove(Objects.requireNonNull(source.getPlayer()).getName().getString());

        return CommandSource.suggestMatching(players, builder);
    }

    public static CompletableFuture<Suggestions> getTrustedPlayersName(@NotNull SuggestionsBuilder builder, @NotNull ServerCommandSource source)
    {
        List<String> players = new ArrayList<>();
        ServerPlayerEntity player = source.getPlayer();

        checkOrCreateTrustFile();
        Properties properties = new Properties();
        try
        {
            properties.load(new FileInputStream(trustPath.toFile()));

            if (player != null)
            {
                for (String str : List.of(properties.get(player.getUuidAsString() + "_" + player.getName().getString()).toString().split(" ")))
                {
                    players.add(str.split("_")[1]);
                }
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return CommandSource.suggestMatching(players, builder);
    }
}
