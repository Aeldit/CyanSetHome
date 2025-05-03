package fr.aeldit.cyansethome.util;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class MissingLivingEntityEvent
{
    /**
     * An event that is called after an entity is damaged. This is fired from {@link LivingEntity#damage} after damage
     * is applied, or after that damage was blocked by a shield.
     *
     * <p>The base damage taken is the damage initially applied to the entity. Damage taken is the amount of damage the
     * entity actually took, after effects such as shields and extra freezing damage are applied. Damage taken does NOT
     * include damage reduction from armor and enchantments.
     *
     * <p>This event is not fired if the entity was killed by the damage.
     */
    public static final Event<AfterDamage> AFTER_DAMAGE = EventFactory.createArrayBacked(
            AfterDamage.class, callbacks -> (entity, source, amount) -> {
                for (AfterDamage callback : callbacks)
                {
                    callback.afterDamage(entity, source, amount);
                }
            }
    );

    @FunctionalInterface
    public interface AfterDamage
    {
        /**
         * Called after a living entity took damage, unless they were killed. The base damage taken is given as damage
         * taken before armor or enchantments are applied, but after other effects like shields are applied.
         *
         * @param entity the entity that was damaged
         * @param source the source of the damage
         */
        void afterDamage(LivingEntity entity, DamageSource source, float amount);
    }
}
