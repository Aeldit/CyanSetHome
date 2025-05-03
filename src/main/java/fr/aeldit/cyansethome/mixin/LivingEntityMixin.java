package fr.aeldit.cyansethome.mixin;

import fr.aeldit.cyansethome.util.MissingLivingEntityEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    //? if <=1.20.6 {
    /*@Shadow
    public abstract boolean isDead();

    @Inject(method = "damage", at = @At("TAIL"))
    private void afterDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        if (!isDead())
        {
            MissingLivingEntityEvent.AFTER_DAMAGE.invoker()
                                                 .afterDamage((LivingEntity) (Object) this, source, amount);
        }
    }
    *///?}
}