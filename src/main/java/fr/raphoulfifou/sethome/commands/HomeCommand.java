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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.Objects;

public class HomeCommand {

    private static final SetHomeJSONConfig jsonConfig = new SetHomeJSONConfig();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("home")
                .then(CommandManager.argument("homeName", StringArgumentType.string())
                        .executes(HomeCommand::teleportHome)
                )
        );
        dispatcher.register(CommandManager.literal("h")
                .then(CommandManager.argument("homeName", StringArgumentType.string())
                        .executes(HomeCommand::teleportHome)
                )
        );
    }


    public static int teleportHome(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        String name = StringArgumentType.getString(context, "homeName");
        ServerWorld overworld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
        ServerWorld nether = Objects.requireNonNull(player.getServer()).getWorld(World.NETHER);
        ServerWorld end = Objects.requireNonNull(player.getServer()).getWorld(World.END);
        //RegistryKey<World> dimension = SetHomeJSONConfig.getDimension(name);
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        float yaw = player.getYaw();
        float pitch = player.getPitch();

        if(jsonConfig.areHomesAllowed()) {
            if(jsonConfig.maxHomes == jsonConfig.getMaxHomes()) {
                if(!jsonConfig.multiDimensionalHomes) {
                    if(player.getServerWorld().getDimension() == Objects.requireNonNull(overworld).getDimension()) {
                        //player.teleport(dimension, x, y, z, yaw, pitch);
                        player.sendMessage(new TranslatableText("sh.msg.sethome"), true);
                    } else if(player.getServerWorld().getDimension() == Objects.requireNonNull(nether).getDimension()) {
                        player.sendMessage(new TranslatableText("sh.msg.sethome"), true);
                    } else if(player.getServerWorld().getDimension() == Objects.requireNonNull(end).getDimension()) {
                        player.sendMessage(new TranslatableText("sh.msg.sethome"), true);
                    }
                    else {
                        player.sendMessage(new TranslatableText("sh.msg.notInAnyDim"), false);
                    }
                }
                else {
                    player.teleport(overworld, x, y, z ,yaw, pitch);
                    player.sendMessage(new TranslatableText("sh.msg.teleportedToHome"), true);
                }
            }
            else {
                player.sendMessage(new TranslatableText("sh.msg.maxHomesReached"), false);
            }
        }
        else {
            player.sendMessage(new TranslatableText("sh.msg.homesNotAllowed"), false);
        }

        return Command.SINGLE_SUCCESS;
    }
}