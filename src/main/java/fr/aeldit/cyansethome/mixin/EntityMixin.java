package fr.aeldit.cyansethome.mixin;

import fr.aeldit.cyansethome.event.PlayerMoveEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Shadow
    public abstract boolean isPlayer();

    @Inject(method = "move", at = @At("TAIL"))
    private void onPlayerMove(MovementType type, Vec3d movement, CallbackInfo ci)
    {
        if (this.isPlayer())
        {
            PlayerMoveEvent.AFTER_MOVE.invoker().afterMove((Entity) (Object) this, type, movement);
        }
    }
}
