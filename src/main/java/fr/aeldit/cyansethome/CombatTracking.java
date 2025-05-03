package fr.aeldit.cyansethome;

import java.util.concurrent.ConcurrentHashMap;

import static fr.aeldit.cyansethome.config.CyanLibConfigImpl.COMBAT_TIMEOUT_SECONDS;

public class CombatTracking
{
    private static final ConcurrentHashMap<String, Long> lastHurtTime = new ConcurrentHashMap<>();

    public static void addEntry(String playerName, long hurtTime)
    {
        lastHurtTime.put(playerName, hurtTime);
    }

    public static boolean isPlayerInCombat(String playerName)
    {
        if (lastHurtTime.containsKey(playerName))
        {
            return System.currentTimeMillis() - lastHurtTime.get(playerName)
                   < COMBAT_TIMEOUT_SECONDS.getValue() * 1000;
        }
        return false;
    }

    public static void removePlayerOnPlayerQuit(String playerName)
    {
        lastHurtTime.remove(playerName);
    }
}
