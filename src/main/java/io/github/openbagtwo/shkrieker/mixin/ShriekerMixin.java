package io.github.openbagtwo.shkrieker.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkShriekerBlockEntity.class)
public abstract class ShriekerMixin extends BlockEntity {

  public ShriekerMixin(BlockEntityType<?> type,
      BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Accessor("warningLevel")
  abstract void setWarningLevel(int level);

  @Shadow
  abstract void shriek(ServerWorld world, @Nullable Entity entity);

  @Inject(
      method="shriek(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;)V",
      at=@At("HEAD"),
      cancellable = true
  )
  public void nonPlayerShriek(ServerWorld world, @Nullable ServerPlayerEntity player, CallbackInfo callbackInfo) {
    BlockState blockState = ((SculkShriekerBlockEntity)(Object)this).getCachedState();
    if (blockState.get(SculkShriekerBlock.SHRIEKING).booleanValue()) {
      return;
    }
    this.setWarningLevel(0);
    if (player == null){
      this.shriek(world, (Entity) null);
      callbackInfo.cancel();
    }
  }
}
