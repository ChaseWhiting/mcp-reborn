
package net.minecraft.entity.frog;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.task.LongJumpToRandomPos;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.UniformInt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;


public class LongJumpToPreferredBlock<E extends Mob>
extends LongJumpToRandomPos<E> {
    private final List<Block> preferredBlockTag;
    private final float preferredBlocksChance;
    private final List<LongJumpToRandomPos.PossibleJump> notPrefferedJumpCandidates = new ArrayList<LongJumpToRandomPos.PossibleJump>();
    private boolean currentlyWantingPreferredOnes;

    public LongJumpToPreferredBlock(UniformInt uniformInt, int n, int n2, float f, Function<E, SoundEvent> function, List<Block> tagKey, float f2, BiPredicate<E, BlockPos> biPredicate) {
        super(uniformInt, n, n2, f, function, biPredicate);
        this.preferredBlockTag = tagKey;
        this.preferredBlocksChance = f2;
    }

    @Override
    protected void start(ServerWorld serverLevel, E e, long l) {
        super.start(serverLevel, e, l);
        this.notPrefferedJumpCandidates.clear();
        this.currentlyWantingPreferredOnes = ((LivingEntity)e).getRandom().nextFloat() < this.preferredBlocksChance;
    }

    @Override
    protected Optional<LongJumpToRandomPos.PossibleJump> getJumpCandidate(ServerWorld serverLevel) {
        if (!this.currentlyWantingPreferredOnes) {
            return super.getJumpCandidate(serverLevel);
        }
        BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
        while (!this.jumpCandidates.isEmpty()) {
            Optional<LongJumpToRandomPos.PossibleJump> optional = super.getJumpCandidate(serverLevel);
            if (!optional.isPresent()) continue;
            LongJumpToRandomPos.PossibleJump possibleJump = optional.get();
            if (serverLevel.getBlockState(mutableBlockPos.setWithOffset((Vector3i) possibleJump.getJumpTarget(), Direction.DOWN)).is(this.preferredBlockTag)) {
                return optional;
            }
            this.notPrefferedJumpCandidates.add(possibleJump);
        }
        if (!this.notPrefferedJumpCandidates.isEmpty()) {
            return Optional.of(this.notPrefferedJumpCandidates.remove(0));
        }
        return Optional.empty();
    }
}

