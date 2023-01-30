package fr.aeldit.cyansh.commands.argumentTypes;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static fr.aeldit.cyansh.util.Constants.locationsPath;

public final class ArgumentSuggestion {
    public static CompletableFuture<Suggestions> getHomes(@NotNull SuggestionsBuilder builder, @NotNull ServerPlayerEntity player) {
        List<String> locations = new ArrayList<>();
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(locationsPath + "\\" + player.getUuidAsString() + ".properties"));

            locations.addAll(properties.stringPropertyNames());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommandSource.suggestMatching(locations, builder);
    }
}
