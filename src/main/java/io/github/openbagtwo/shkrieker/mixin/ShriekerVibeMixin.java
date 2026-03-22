package io.github.openbagtwo.shkrieker.mixin;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets="net.minecraft.world.level.block.entity.SculkShriekerBlockEntity$VibrationUser")
abstract class ShriekerVibeMixin {

  @Accessor
  public abstract PositionSource getPositionSource();

  /**
   * @author OpenBagTwo
   * @reason Bypass the check for whether the event was caused by a player
   */
  @Overwrite
  public boolean canReceiveVibration(ServerLevel world, BlockPos pos, Holder<GameEvent> event, GameEvent.Context emitter){
    Optional<Vec3> position = this.getPositionSource().getPosition(world);
    if (position.isEmpty()){
      return false;
    }
    BlockEntity shkrieker = world.getBlockEntity(
        new BlockPos(
            (int) Math.floor(position.get().x()),
            (int) Math.floor(position.get().y()),
            (int) Math.floor(position.get().z())
        )
    );
    if (shkrieker == null) {
      return false;
    }
    if (!(shkrieker instanceof SculkShriekerBlockEntity)){
      return false;
    }

    return !shkrieker.getBlockState().getOptionalValue(SculkShriekerBlock.SHRIEKING).orElse(true);
  }




}
