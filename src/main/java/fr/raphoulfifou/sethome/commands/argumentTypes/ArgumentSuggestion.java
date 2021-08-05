package fr.raphoulfifou.sethome.commands.argumentTypes;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.raphoulfifou.sethome.util.SetHomeJSONConfig;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ArgumentSuggestion {
    private static final SetHomeJSONConfig JSON_CONFIG = new SetHomeJSONConfig();

    public static @NotNull <S> CompletableFuture<Suggestions> suggestHomeName(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(JSON_CONFIG.getHomes(), builder);
    }
}
