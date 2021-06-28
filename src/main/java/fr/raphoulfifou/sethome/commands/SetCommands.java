package fr.raphoulfifou.sethome.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.raphoulfifou.sethome.util.SetHomeJSONConfig;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class SetCommands {

    private static SetHomeJSONConfig.Options options;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
                dispatcher.register(CommandManager.literal("allowhomes")
                .then(CommandManager.argument("true | false", BoolArgumentType.bool())
                        .executes(SetCommands::setAreHomesAllowed)
                )
        );
        dispatcher.register(CommandManager.literal("ah")
                .then(CommandManager.argument("true | false", BoolArgumentType.bool())
                        .executes(SetCommands::setAreHomesAllowed)
                )
        );

        dispatcher.register(CommandManager.literal("setmaxhomes")
                .then(CommandManager.argument("maxHomes", IntegerArgumentType.integer())
                        .executes(SetCommands::setMaxHomes)
                )
        );
        dispatcher.register(CommandManager.literal("smh")
                .then(CommandManager.argument("maxHomes", IntegerArgumentType.integer())
                        .executes(SetCommands::setMaxHomes)
                )
        );
    }


    public static int setAreHomesAllowed(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        boolean bool = BoolArgumentType.getBool(context, "true | false");
        options.setAreHomesAllowed(bool);

        player.sendMessage(new TranslatableText("sh.msg.allowHomes"), false);
        return Command.SINGLE_SUCCESS;
    }

    public static int setMaxHomes(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        int integer = IntegerArgumentType.getInteger(context, "homeName");
        options.setMaxHomes(integer);

        player.sendMessage(new TranslatableText("sh.msg.maxHomesSet"), false);
        return Command.SINGLE_SUCCESS;
    }

}
