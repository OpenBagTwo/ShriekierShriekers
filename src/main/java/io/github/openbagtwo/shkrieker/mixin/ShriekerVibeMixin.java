package io.github.openbagtwo.shkrieker.mixin;

import java.util.Optional;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets="net.minecraft.block.entity.SculkShriekerBlockEntity$VibrationCallback")
abstract class ShriekerVibeMixin {

  @Accessor
  abstract PositionSource getPositionSource();

  /**
   * @author OpenBagTwo
   * @reason Bypass the check for whether the event was caused by a player
   */
  @Overwrite
  public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter){
    Optional<Vec3d> position = this.getPositionSource().getPos(world);
    if (position.isEmpty()){
      return false;
    }
    BlockEntity shkrieker = world.getBlockEntity(
        new BlockPos(
            (int) Math.floor(position.get().getX()),
            (int) Math.floor(position.get().getY()),
            (int) Math.floor(position.get().getZ())
        )
    );
    if (shkrieker == null) {
      return false;
    }
    if (!(shkrieker instanceof SculkShriekerBlockEntity)){
      return false;
    }

    return !shkrieker.getCachedState().getOrEmpty(SculkShriekerBlock.SHRIEKING).orElse(true);
  }




}
