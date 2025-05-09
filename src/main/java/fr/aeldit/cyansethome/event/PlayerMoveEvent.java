package fr.aeldit.cyansethome.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerMoveEvent
{
    /**
     * An event that is called after a player moved. This is fired from {@link ServerPlayerEntity#applyMovementEffects}
     * after moving
     */
    public static final Event<AfterMove> AFTER_MOVE = EventFactory.createArrayBacked(
            AfterMove.class, callbacks -> (player) -> {
                for (AfterMove callback : callbacks)
                {
                    callback.afterMove(player);
                }
            }
    );

    @FunctionalInterface
    public interface AfterMove
    {
        void afterMove(ServerPlayerEntity player);
    }
}
