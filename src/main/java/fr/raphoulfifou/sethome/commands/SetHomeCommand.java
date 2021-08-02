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
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.UUID;

public class SetHomeCommand {

    private static final SetHomeJSONConfig JSON_CONFIG = new SetHomeJSONConfig();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
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
     * @throws CommandSyntaxException if the syntaxe of the command isn't correct (ex: "/sethome ba se" will throw
     *                                an exception because there is two arguments instead of one)
     */
    public static int setHome(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        String name = StringArgumentType.getString(context, "homeName");
        RegistryKey<World> dimension = player.getServerWorld().getRegistryKey();
        UUID uuid = player.getUuid();
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        float yaw = player.getYaw();
        float pitch = player.getPitch();

        JSON_CONFIG.createHome(uuid, name, dimension, x, y, z, yaw, pitch);

        /*
        if(options.areHomesAllowed()) {
            if(options.maxHomes == options.getMaxHomes() && !options.multiDimensionalHomes) {
                if(player.getServerWorld().getDimension() == Objects.requireNonNull(overworld).getDimension()) {
                    homes.createHome(uuid, name, overworld,  x, y, z, yaw, pitch);
                    player.sendMessage(new TranslatableText("sh.msg.sethome"), true);
                } else if(player.getServerWorld().getDimension() == Objects.requireNonNull(nether).getDimension()) {
                    homes.createHome(uuid, name, nether,  x, y, z, yaw, pitch);
                    player.sendMessage(new TranslatableText("sh.msg.sethome"), true);
                } else if(player.getServerWorld().getDimension() == Objects.requireNonNull(end).getDimension()) {
                    homes.createHome(uuid, name, end,  x, y, z, yaw, pitch);
                    player.sendMessage(new TranslatableText("sh.msg.sethome"), true);
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