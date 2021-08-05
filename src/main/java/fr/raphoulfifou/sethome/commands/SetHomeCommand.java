package fr.raphoulfifou.sethome.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.raphoulfifou.sethome.util.SetHomeJSONConfig;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @since 0.0.1
 * @see SetCommands
 * @see HomeCommand
 * @author Raphoulfifou
 */
public class SetHomeCommand {

    public static SetHomeJSONConfig jsonConfig = new SetHomeJSONConfig();

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("sethome")
                .then(CommandManager.argument("homeName", StringArgumentType.string())
                        .executes(SetHomeCommand::setHome)
                )
        );
        dispatcher.register(CommandManager.literal("sh")
                .then(CommandManager.argument("homeName", StringArgumentType.string())
                        .executes(SetHomeCommand::setHome)
        ));
    }

    /**
     * Called when a player execute the command "/sethome home_name"
     * Gets: - the name of the home (name)
     *       - the dimension the player is in (dimension)
     *       - the UUID of the player (uuid)
     *       - the x, y, z coordinates (x, y, z)
     *       - the yaw and pitch of the player -> his eyes position (yaw, pitch)
     *
     * Call the "createHome" function located in 'SetHomeJSONConfig' and take as parameters the elements listed above
     *
     * @throws CommandSyntaxException if the syntaxe of the command isn't correct
     * (ex: too much arguments or incorreect ones)
     */
    public static int setHome(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        String name = StringArgumentType.getString(context, "homeName");
        UUID uuid = player.getUuid();
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        float yaw = player.getYaw();
        float pitch = player.getPitch();

        if (player.world.getRegistryKey() == World.OVERWORLD) {
            jsonConfig.createHome(uuid, name, "overworld", x, y, z, yaw, pitch);
        }
        else if (player.world.getRegistryKey() == World.NETHER) {
            jsonConfig.createHome(uuid, name, "nether", x, y, z, yaw, pitch);
        }
        else if (player.world.getRegistryKey() == World.END) {
            jsonConfig.createHome(uuid, name, "end", x, y, z, yaw, pitch);
        }
        else {
            player.sendMessage(new TranslatableText("sh.msg.notInAnyDim"), false);
        }

        /*
        if(JSON_CONFIG.areHomesAllowed()) {
            if(JSON_CONFIG.getHomesNumber(uuid) < JSON_CONFIG.getMaxHomes()) {

                if (player.world.getRegistryKey() == World.OVERWORLD) {
                    JSON_CONFIG.createHome(uuid, name, "overworld", x, y, z, yaw, pitch);
                }
                else if (player.world.getRegistryKey() == World.NETHER) {
                    JSON_CONFIG.createHome(uuid, name, "nether", x, y, z, yaw, pitch);
                }
                else if (player.world.getRegistryKey() == World.END) {
                    JSON_CONFIG.createHome(uuid, name, "end", x, y, z, yaw, pitch);
                }
                else {
                    player.sendMessage(new TranslatableText("sh.msg.notInAnyDim"), false);
                }

            }
            else {
                player.sendMessage(new TranslatableText("sh.msg.maxHomesReached"), false);
            }
        }

        else {
            player.sendMessage(new TranslatableText("sh.msg.homesNotAllowed"), false);
        }
        */

        return Command.SINGLE_SUCCESS;
    }
}