package dev.loaf.goal;

import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.feline.Cat;

/**
 * a low-priority goal that makes cats flop down on the spot now and then.
 * it only touches the visual pose flags, never the ordered-to-sit flag, so a
 * player can still call the cat over whenever they want.
 *
 * the cat is not stuck. the cat is simply committed to the floor today.
 *
 * (=^･ω･^=)
 */
public class CatLoungeGoal extends Goal {
    private static final int ACTIVATION_CHANCE = 50;
    private static final int MIN_DURATION = 200;
    private static final int MAX_DURATION = 600;

    private final Cat cat;
    private int remainingTicks;

    public CatLoungeGoal(Cat cat) {
        this.cat = cat;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // no lounging in water, on a leash, or while plotting violence
        if (this.cat.isInWater() || this.cat.isPassenger() || this.cat.isLeashed()
                || this.cat.isOrderedToSit() || this.cat.getTarget() != null) {
            return false;
        }

        // the cat rolls the dice. the cat always wins.  (=^･ω･^=)
        return this.cat.getRandom().nextInt(ACTIVATION_CHANCE) == 0;
    }

    @Override
    public void start() {
        this.remainingTicks = MIN_DURATION
                + this.cat.getRandom().nextInt(MAX_DURATION - MIN_DURATION + 1);

        // ordered sitting is left alone on purpose. it is a player command, not a pose.
        // pancake mode or sit-upright mode, the cat picks  /\_/\
        if (this.cat.getRandom().nextBoolean()) {
            this.cat.setLying(true);
        } else {
            this.cat.setInSittingPose(true);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.remainingTicks > 0 && !this.cat.isOrderedToSit() && this.cat.getTarget() == null;
    }

    @Override
    public void tick() {
        this.remainingTicks--;
    }

    @Override
    public void stop() {
        // clear both even though only one was set. another mod may have posed the cat.
        // the loaf era has ended, back to being a normal cat  (=\^.\./^=)
        this.cat.setLying(false);
        this.cat.setInSittingPose(false);
        this.remainingTicks = 0;
    }
}
