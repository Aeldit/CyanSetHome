package fr.aeldit.cyansethome.util;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;

import static fr.aeldit.cyansethome.CyanSHCore.HOMES;
import static fr.aeldit.cyansethome.CyanSHCore.TRUSTS;

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

        HOMES.renameChangedUsernames(playerUUID, playerName);
        TRUSTS.renameChangedUsernames(playerUUID, playerName);
    }
}
