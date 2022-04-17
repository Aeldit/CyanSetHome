package fr.raphoulfifou.cyansh.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.raphoulfifou.cyansh.config.SethomeMidnightConfig;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

/**
 * @author Raphoulfifou
 * @since 0.2.6
 */
public class SetCommands
{

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("setAllowSethome")
                .then(CommandManager.argument("bool", BoolArgumentType.bool())
                        .executes(SetCommands::setAllowSethome)
                )
        );
        dispatcher.register(CommandManager.literal("setAllowHomeOf")
                .then(CommandManager.argument("bool", BoolArgumentType.bool())
                        .executes(SetCommands::setAllowHomeOf)
                )
        );

        dispatcher.register(CommandManager.literal("setMaxHomes")
                .then(CommandManager.argument("int", IntegerArgumentType.integer())
                        .executes(SetCommands::setMaxHomes)
                )
        );
        dispatcher.register(CommandManager.literal("setRequiredOpLevelKgi")
                .then(CommandManager.argument("int", IntegerArgumentType.integer())
                        .executes(SetCommands::setRequiredOpLevelKgi)
                )
        );
    }

    /**
     * <p>Called when a player execute the command "/setAllowSethome (true|false)"</p>
     *
     * <ul>If the player has a permission level equal to 4
     *      <li>-> Enables/disables the use of the /sethome command</li>
     * </ul>
     * <ul>Else:
     *      <li>-> The player receive a message saying that it doesn't have the required permission</li>
     * </ul>
     *
     * @throws CommandSyntaxException if the syntaxe of the command isn't correct
     */
    public static int setAllowSethome(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        boolean arg = BoolArgumentType.getBool(context, "bool");

        // If OP with minimum defined level
        if (player.hasPermissionLevel(SethomeMidnightConfig.minOpLevelExeKgi))
        {
            SethomeMidnightConfig.setAllowSethome(arg);
            player.sendMessage(new TranslatableText("sethome.message.setAllowSethome", Boolean.toString(arg)), true);
        }
        // If not OP or not OP with max level
        else
        {
            source.sendFeedback(new TranslatableText("sethome.message.notOp"), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * <p>Called when a player execute the command "/setAllowHomeOf (true|false)"</p>
     *
     * <ul>If the player has a permission level equal to 4
     *      <li>-> Enables/disables the use of the /homeOf command</li>
     * </ul>
     * <ul>Else:
     *      <li>-> The player receive a message saying that it doesn't have the required permission</li>
     * </ul>
     *
     * @throws CommandSyntaxException if the syntaxe of the command isn't correct
     */
    public static int setAllowHomeOf(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        boolean arg = BoolArgumentType.getBool(context, "bool");

        // If OP with minimum defined level
        if (player.hasPermissionLevel(SethomeMidnightConfig.minOpLevelExeKgi))
        {
            SethomeMidnightConfig.setAllowHomeOf(arg);
            player.sendMessage(new TranslatableText("sethome.message.setAllowHomeOf", Boolean.toString(arg)), true);
        }
        // If not OP or not OP with max level
        else
        {
            source.sendFeedback(new TranslatableText("sethome.message.notOp"), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * <p>Called when a player execute the command "/setMaxHomes (int)"</p>
     *
     * <ul>If the player has a permission level equal to 4
     *      <li>-> Set the maximum number of homes a player can have</li>
     * </ul>
     * <ul>Else:
     *      <li>-> The player receive a message saying that it doesn't have the required permission</li>
     * </ul>
     *
     * @throws CommandSyntaxException if the syntaxe of the command isn't correct
     */
    public static int setMaxHomes(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        int arg = IntegerArgumentType.getInteger(context, "int");

        if (arg < 1 || arg > 64)
        {
            player.sendMessage(new TranslatableText("sethome.message.incorrectIntMaxHomes"), false);
            return 0;
        }
        // If OP with at least minimum defined level
        if (player.hasPermissionLevel(SethomeMidnightConfig.minOpLevelExeKgi))
        {
            SethomeMidnightConfig.setMaxHomes(arg);
            player.sendMessage(new TranslatableText("sethome.message.setMaxHomes", arg), true);
        }
        // If not OP or not OP with max level
        else
        {
            source.sendFeedback(new TranslatableText("sethome.message.notOp"), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * <p>Called when a player execute the command "/setRequiredOpLevelKgi (int)"</p>
     *
     * <ul>If the player has a permission level equal to 4
     *      <li>-> Set the minimum OP level required to execute the /kgi command</li>
     * </ul>
     * <ul>Else:
     *      <li>-> The player receive a message saying that it doesn't have the required permission</li>
     * </ul>
     *
     * @throws CommandSyntaxException if the syntaxe of the command isn't correct
     */
    public static int setRequiredOpLevelKgi(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        int arg = IntegerArgumentType.getInteger(context, "int");

        // If the argument passed to the command isn't in [0;4], the config file will not be modified and the function
        // stops here
        if (arg < 0 || arg > 4)
        {
            player.sendMessage(new TranslatableText("sethome.message.incorrectIntOp"), false);
            return 0;
        }

        // If OP with minimum defined level
        if (player.hasPermissionLevel(SethomeMidnightConfig.minOpLevelExeKgi))
        {
            SethomeMidnightConfig.setMinOpLevelExeKgi(arg);
            player.sendMessage(new TranslatableText("sethome.message.setRequiredOpLevelKgi", arg), true);
        }
        // If not OP or not OP with defined level
        else
        {
            source.sendFeedback(new TranslatableText("sethome.message.notOp"), true);
        }
        return Command.SINGLE_SUCCESS;
    }

}
