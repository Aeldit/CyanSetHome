package fr.raphoulfifou.cyansh.commands.argumentTypes;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ArgumentSuggestion
{
    /*private static final SetHomeJSONConfig JSON_CONFIG = new SetHomeJSONConfig();

    public static @NotNull <S> CompletableFuture<Suggestions> suggestHomeName(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(JSON_CONFIG.getHomes(), builder);
    }*/

    public static CompletableFuture<Suggestions> getAllPlayerNames(@NotNull CommandContext<ServerCommandSource> context, @NotNull SuggestionsBuilder builder)
    {
        MinecraftServer server = context.getSource().getServer();

        Set<String> userNames = new HashSet<>(ArgumentSuggestion.getOnlinePlayerNames(server));
        userNames.addAll(ArgumentSuggestion.getWhitelistedNames(server));
        /*if (!builder.getRemaining().isEmpty())
        {

        }*/

        // Return the suggestion handler
        return CommandSource.suggestMatching(userNames, builder);
    }

    public static @NotNull List<String> getOnlinePlayerNames(final @NotNull MinecraftServer server)
    {
        PlayerManager playerManager = server.getPlayerManager();
        return Arrays.asList(playerManager.getPlayerNames());
    }

    public static @NotNull List<String> getWhitelistedNames(final @NotNull MinecraftServer server)
    {
        PlayerManager playerManager = server.getPlayerManager();
        return Arrays.asList(playerManager.getWhitelistedNames());
    }

}
