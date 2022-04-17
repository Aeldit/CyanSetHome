package fr.raphoulfifou.cyansh.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.raphoulfifou.cyansh.config.SethomeMidnightConfig;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

public class GetCommand
{

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("getCyanSetHomeConfigOptions")
                .executes(GetCommand::getConfigOptions)
        );
    }

    /**
     * <p>Called when a player execute the command "/getCyanSetHomeConfigOptions"</p>
     * <p>Send a player in the player's chat with all options and their values</p>
     *
     * @throws CommandSyntaxException if the syntaxe of the command isn't correct
     */
    public static int getConfigOptions(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        player.sendMessage(new TranslatableText("sethome.message.getCfgOptions.header"), false);
        player.sendMessage(new TranslatableText("sethome.message.getCfgOptions.allowSethome",
                Boolean.toString(SethomeMidnightConfig.allowSethome)), false);
        player.sendMessage(new TranslatableText("sethome.message.getCfgOptions.allowHomeOf",
                Boolean.toString(SethomeMidnightConfig.allowHomeOf)), false);

        player.sendMessage(new TranslatableText("sethome.message.getCfgOptions.maxHomes",
                Integer.toString(SethomeMidnightConfig.maxHomes)), false);
        player.sendMessage(new TranslatableText("sethome.message.getCfgOptions.minOpLevelExeKgi",
                Integer.toString(SethomeMidnightConfig.minOpLevelExeKgi)), false);

        return Command.SINGLE_SUCCESS;
    }

}
