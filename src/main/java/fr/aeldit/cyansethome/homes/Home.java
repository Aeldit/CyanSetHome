package fr.aeldit.cyansethome.homes;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static fr.aeldit.cyansethome.CyanSHCore.CYANSH_LANG_UTILS;
import static fr.aeldit.cyansethome.config.CyanLibConfigImpl.BLOCKS_PER_XP_LEVEL_HOME;

public record Home(String name, String dimension, double x, double y, double z, float yaw, float pitch, String date)
{
    @Contract("_ -> new")
    public @NotNull Home getRenamed(String newName)
    {
        return new Home(newName, dimension, x, y, z, yaw, pitch, date);
    }

    public void sendFormatedMessage(@NotNull ServerPlayerEntity player)
    {
        CYANSH_LANG_UTILS.sendPlayerMessageActionBar(
                player,
                "msg.getHome",
                false,
                Formatting.YELLOW + name,
                Formatting.DARK_AQUA + dimension,
                Formatting.DARK_AQUA + date
        );
    }

    public int getRequiredXpLevelsToTp(@NotNull ServerPlayerEntity player)
    {
        double distanceX = player.getX() - x;
        double distanceZ = player.getZ() - z;

        // Converts to a positive distance
        if (distanceX < 0)
        {
            distanceX *= -1;
        }
        if (distanceZ < 0)
        {
            distanceZ *= -1;
        }
        // Minecraft doesn't center the position to the middle of the block but in 1 corner,
        // so this allows for a better centering
        ++distanceX;
        ++distanceZ;

        int coordinatesDistance = (int) (distanceX + distanceZ) / 2;
        int option = BLOCKS_PER_XP_LEVEL_HOME.getValue();
        return coordinatesDistance < option ? 1 : 1 + coordinatesDistance / option;
    }

    public void teleport(@NotNull MinecraftServer server, ServerPlayerEntity player)
    {
        if (player != null)
        {
            //? if >=1.21.2-1.21.3 {
            /*switch (dimension)
            {
                case "overworld" ->
                        player.teleport(server.getWorld(World.OVERWORLD), x, y, z, new HashSet<>(), yaw, pitch, false);
                case "nether" ->
                        player.teleport(server.getWorld(World.NETHER), x, y, z, new HashSet<>(), yaw, pitch, false);
                case "end" -> player.teleport(server.getWorld(World.END), x, y, z, new HashSet<>(), yaw, pitch, false);
            }
            *///?} else {
            switch (dimension)
            {
            case "overworld" -> player.teleport(server.getWorld(World.OVERWORLD), x, y, z, yaw, pitch);
            case "nether" -> player.teleport(server.getWorld(World.NETHER), x, y, z, yaw, pitch);
            case "end" -> player.teleport(server.getWorld(World.END), x, y, z, yaw, pitch);
            }
            //?}
        }
    }
}
