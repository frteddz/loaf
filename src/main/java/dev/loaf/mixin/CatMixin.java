package dev.loaf.mixin;

import dev.loaf.goal.CatLoungeGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cat.class)
public abstract class CatMixin extends Mob {
    private static final int STROLL_INTERVAL = 1200;
    private static final int LOUNGE_PRIORITY = 7;

    protected CatMixin(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"), require = 1)
    private void loaf$registerLoungeGoal(CallbackInfo ci) {
        // slow the vanilla wander instead of ripping it out. cats still roam, just rarely.
        // consider this the cat's new slow-motion walking playlist
        for (WrappedGoal wrapped : this.goalSelector.getAvailableGoals()) {
            if (wrapped.getGoal() instanceof WaterAvoidingRandomStrollGoal stroll) {
                stroll.setInterval(STROLL_INTERVAL);
            }
        }

        this.goalSelector.addGoal(LOUNGE_PRIORITY, new CatLoungeGoal((Cat) (Object) this));
    }
}
