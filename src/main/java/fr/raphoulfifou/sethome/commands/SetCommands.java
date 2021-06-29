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

    private static final SetHomeJSONConfig jsonConfig = new SetHomeJSONConfig();

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

        dispatcher.register(CommandManager.literal("multidimensionalhomes")
                .then(CommandManager.argument("true | false", BoolArgumentType.bool())
                        .executes(SetCommands::setAreMultiDimensionalHomesAllowed)
                )
        );
        dispatcher.register(CommandManager.literal("mdh")
                .then(CommandManager.argument("true | false", BoolArgumentType.bool())
                        .executes(SetCommands::setAreMultiDimensionalHomesAllowed)
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
        boolean arg = BoolArgumentType.getBool(context, "true | false");
        jsonConfig.setAreHomesAllowed(arg);

        player.sendMessage(new TranslatableText("sh.msg.allowHomesSet", arg), false);
        return Command.SINGLE_SUCCESS;
    }

    public static int setAreMultiDimensionalHomesAllowed(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        boolean arg = BoolArgumentType.getBool(context, "true | false");
        jsonConfig.setAreHomesMultiDimensional(arg);

        player.sendMessage(new TranslatableText("sh.msg.multiDimHomesSet", arg), false);
        return Command.SINGLE_SUCCESS;
    }

    public static int setMaxHomes(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        int arg = IntegerArgumentType.getInteger(context, "maxHomes");
        jsonConfig.setMaxHomes(arg);

        player.sendMessage(new TranslatableText("sh.msg.maxHomesSet", arg), false);
        return Command.SINGLE_SUCCESS;
    }
    
}