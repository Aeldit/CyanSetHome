package fr.aeldit.cyansethome.mixin;

import fr.aeldit.cyansethome.event.PlayerMoveEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin
{
    @Inject(method = "applyMovementEffects", at = @At("TAIL"))
    //? if <=1.20.6 {
    /*private void onPlayerMove(BlockPos pos, CallbackInfo ci)
     *///?} else {
    private void onPlayerMove(ServerWorld world, BlockPos pos, CallbackInfo ci)
    //?}
    {
        PlayerMoveEvent.AFTER_MOVE.invoker().afterMove((ServerPlayerEntity) (Object) this);
    }
}
