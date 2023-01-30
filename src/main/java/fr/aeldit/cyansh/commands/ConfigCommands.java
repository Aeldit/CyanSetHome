package fr.aeldit.cyansh.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

public class ConfigCommands {
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("cyansh")
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .executes(HomeCommands::setHome)
                )
        );
    }

}
