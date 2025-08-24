package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface RuleBlockEntityModifierType<P extends RuleBlockEntityModifier> {
    public static final RuleBlockEntityModifierType<Clear> CLEAR = RuleBlockEntityModifierType.register("clear", Clear.CODEC);
    public static final RuleBlockEntityModifierType<Passthrough> PASSTHROUGH = RuleBlockEntityModifierType.register("passthrough", Passthrough.CODEC);
    public static final RuleBlockEntityModifierType<AppendStatic> APPEND_STATIC = RuleBlockEntityModifierType.register("append_static", AppendStatic.CODEC);
    public static final RuleBlockEntityModifierType<AppendLoot> APPEND_LOOT = RuleBlockEntityModifierType.register("append_loot", AppendLoot.CODEC);

    public Codec<P> codec();

    private static <P extends RuleBlockEntityModifier> RuleBlockEntityModifierType<P> register(String string, Codec<P> codec) {
        return Registry.register(Registry.RULE_BLOCK_ENTITY_MODIFIER, string, () -> codec);
    }
}
