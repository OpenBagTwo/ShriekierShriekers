package io.github.openbagtwo.shkrieker.mixin;

import io.github.openbagtwo.shkrieker.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkShriekerBlockEntity.class)
public abstract class ShriekerMixin extends BlockEntity {

  private @Nullable Config config;

  public ShriekerMixin(BlockEntityType<?> type,
      BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Accessor("warningLevel")
  abstract void setWarningLevel(int level);

  @Shadow
  protected abstract boolean canRespond(ServerLevel world);

  @Shadow
  protected abstract void playWardenReplySound(Level world);

  @Shadow
  protected abstract void shriek(ServerLevel world, @Nullable Entity entity);

  @Inject(
      method="tryShriek(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;)V",
      at=@At("HEAD"),
      cancellable = true
  )
  public void nonPlayerShriek(ServerLevel world, @Nullable ServerPlayer player, CallbackInfo callbackInfo) {
    if (player == null) {
      if (this.config == null) {
        this.config = Config.loadConfiguration();
      }
      BlockState blockState = ((SculkShriekerBlockEntity) (Object) this).getBlockState();
      if (blockState.getValue(SculkShriekerBlock.SHRIEKING).booleanValue()) {
        callbackInfo.cancel();
      }
      if (!this.canRespond(world) || this.config.getApplyToNaturalSetting()) {
        this.setWarningLevel(0);
        this.shriek(world, (Entity) null);
        if (this.config.getCauseDarknessSetting()) {
          this.playWardenReplySound(world);
          Warden.applyDarknessAround(world, Vec3.atCenterOf(this.getBlockPos()),
              (Entity) null,
              40);
        }
        callbackInfo.cancel();
      }
    }
  }
}
