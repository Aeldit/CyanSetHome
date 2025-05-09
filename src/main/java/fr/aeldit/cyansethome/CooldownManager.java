package fr.aeldit.cyansethome;

import fr.aeldit.cyansethome.homes.Home;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager
{
    // Holds the cooldown start time for each player
    private static final ConcurrentHashMap<ServerPlayerEntity, Tuple> playersCooldowns = new ConcurrentHashMap<>();
    private static final List<ServerPlayerEntity> canceledCooldowns = Collections.synchronizedList(new ArrayList<>());

    public static void cancelCooldown(ServerPlayerEntity player)
    {
        canceledCooldowns.add(player);
        playersCooldowns.remove(player);
    }

    public static List<ServerPlayerEntity> getCanceledCooldowns()
    {
        return canceledCooldowns;
    }

    public static void clearCanceledCooldowns()
    {
        canceledCooldowns.clear();
    }

    public static void addPlayerCooldown(
            ServerPlayerEntity player, long cooldown, long startTime, Home home, int requiredXpLevel,
            MinecraftServer server
    )
    {
        playersCooldowns.put(player, new Tuple(cooldown, startTime, home, requiredXpLevel, server));
    }

    public static @NotNull HashMap<ServerPlayerEntity, Tuple> getPlayersCompletedCooldowns()
    {
        long currentTime = System.currentTimeMillis();
        HashMap<ServerPlayerEntity, Tuple> completedCooldowns = new HashMap<>();
        for (Map.Entry<ServerPlayerEntity, Tuple> entry : playersCooldowns.entrySet())
        {
            if (currentTime - entry.getValue().startTime() > entry.getValue().cooldown())
            {
                completedCooldowns.put(entry.getKey(), entry.getValue());
            }
        }
        completedCooldowns.forEach(playersCooldowns::remove);
        return completedCooldowns;
    }

    public record Tuple(long cooldown, long startTime, Home home, int requiredXpLevel, MinecraftServer server)
    {
    }
}
