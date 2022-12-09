package fr.aeldit.cyansh.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.aeldit.cyansh.config.HomesSaving;
import fr.aeldit.cyansh.config.SethomeMidnightConfig;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class HomeCommand
{

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
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

    public static int teleportHome(@NotNull CommandContext<ServerCommandSource> context)
    {
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

        if (SethomeMidnightConfig.allowSethome)
        {
            /*if(SethomeMidnightConfig.maxHomes < player.getMaxHomes())
            {
                player.teleport(overworld, x, y, z ,yaw, pitch);
                player.sendMessage(new TranslatableText("sh.msg.teleportedToHome"), true);
            }
            else
            {
                player.sendMessage(new TranslatableText("sh.msg.maxHomesReached"), false);
            }*/
            return 0;
        } else
        {
            player.sendMessage(Text.translatable("sh.msg.homesNotAllowed"), false);
        }

        HomesSaving.load();

        return Command.SINGLE_SUCCESS;
    }

}
