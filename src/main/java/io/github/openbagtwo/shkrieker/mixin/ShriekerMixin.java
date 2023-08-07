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
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

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

  @Shadow
  abstract boolean canWarn(ServerWorld world);

  @Shadow
  abstract boolean trySyncWarningLevel(ServerWorld world, ServerPlayerEntity player);

  @Overwrite
  public void shriek(ServerWorld world, @Nullable ServerPlayerEntity player) {
    BlockState blockState = ((SculkShriekerBlockEntity)(Object)this).getCachedState();
    if (blockState.get(SculkShriekerBlock.SHRIEKING).booleanValue()) {
      return;
    }
    this.setWarningLevel(0);
    if (player == null){
      this.shriek(world, (Entity) null);
      return;
    }
    if (this.canWarn(world) && !this.trySyncWarningLevel(world, player)) {
      return;
    }
    this.shriek(world, (Entity)player);
  }
}
