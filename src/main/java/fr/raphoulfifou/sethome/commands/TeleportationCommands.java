package fr.raphoulfifou.sethome.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Objects;

public class TeleportationCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("bed")
                .executes(TeleportationCommands::bed)
        );
        dispatcher.register(CommandManager.literal("b")
                .executes(TeleportationCommands::bed)
        );

        dispatcher.register(CommandManager.literal("surface")
                .executes(TeleportationCommands::surface)
        );
        dispatcher.register(CommandManager.literal("s")
                .executes(TeleportationCommands::surface)
        );
    }

    public static int bed(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerWorld overworld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
        ServerWorld nether = Objects.requireNonNull(player.getServer()).getWorld(World.NETHER);

        if(player.getSpawnPointDimension() == World.OVERWORLD && player.getSpawnPointPosition() != null) {
            int x = player.getSpawnPointPosition().getX();
            int y = player.getSpawnPointPosition().getY();
            int z = player.getSpawnPointPosition().getZ();
            player.teleport(overworld, x, y, z, 0, 0);
            player.sendMessage(new TranslatableText("cyan.message.bed"), true);
            return Command.SINGLE_SUCCESS;
        }

        if(player.getSpawnPointDimension() == World.NETHER && player.getSpawnPointPosition() != null) {
            int x = player.getSpawnPointPosition().getX();
            int y = player.getSpawnPointPosition().getY();
            int z = player.getSpawnPointPosition().getZ();
            player.teleport(nether, x, y, z, 0, 0);
            player.sendMessage(new TranslatableText("cyan.message.respawnanchor"), true);
            return Command.SINGLE_SUCCESS;
        }
        else {
            player.sendMessage(new TranslatableText("cyan.message.bed.notfound"), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int surface(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerWorld world = context.getSource().getWorld();

        int x = player.getBlockPos().getX();
        int z = player.getBlockPos().getZ();
        int y = player.world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
        player.teleport(world, x, y, z, 0, 0);
        player.sendMessage(new TranslatableText("cyan.message.surface"), true);
        return Command.SINGLE_SUCCESS;
    }
}