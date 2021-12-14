package net.minecraft.server;

import com.destroystokyo.paper.PaperConfig;

public class MobEffectAttackDamage extends MobEffectList {

    protected MobEffectAttackDamage(int i, MinecraftKey minecraftkey, boolean flag, int j) {
        super(i, minecraftkey, flag, j);
    }

    public double a(int i, AttributeModifier attributemodifier) {
        // PaperSpigot - Configurable modifiers for strength and weakness effects
        return this.id == MobEffectList.WEAKNESS.id ? (double) (PaperConfig.weaknessEffectModifier * (float) (i + 1)) : PaperConfig.strengthEffectModifier * (double) (i + 1);
    }
}
