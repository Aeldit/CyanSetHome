package fr.aeldit.cyansethome.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlayerMoveEvent
{
    /**
     * An event that is called after a player moved. This is fired from {@link ServerPlayerEntity#applyMovementEffects}
     * after moving
     * <p>
     * The base damage taken is the damage initially applied to the entity. Damage taken is the amount of damage the
     * entity actually took, after effects such as shields and extra freezing damage are applied. Damage taken does NOT
     * include damage reduction from armor and enchantments.
     * <p>
     * This event is not fired if the entity is not a player.
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
