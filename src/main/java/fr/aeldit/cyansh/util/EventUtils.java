package fr.aeldit.cyansh.util;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;

import static fr.aeldit.cyansh.CyanSHCore.HomesObj;
import static fr.aeldit.cyansh.CyanSHCore.TrustsObj;

public class EventUtils
{
    /**
     * Called on {@code ServerPlayConnectionEvents.JOIN} event
     * Renames the trust and homes files if the players username corresponding to the UUID changed.
     * (ex: UUID_Username -> UUID_updatedUsername)
     *
     * @param handler The ServerPlayNetworkHandler
     */
    public static void renameFileIfUsernameChanged(@NotNull ServerPlayNetworkHandler handler)
    {
        String playerUUID = handler.getPlayer().getUuidAsString();
        String playerName = handler.getPlayer().getName().getString();

        HomesObj.renameChangedUsernames(playerUUID, playerName);
        TrustsObj.renameChangedUsernames(playerUUID, playerName);
    }
}
