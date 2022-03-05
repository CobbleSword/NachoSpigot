package dev.cobblesword.nachospigot.commons;

import net.minecraft.server.EnumParticle;
import org.bukkit.Effect;

import java.util.EnumMap;

public class Dictionary {
    public static final EnumMap<Effect, EnumParticle> EFFECT_TO_PARTICLE = new EnumMap<>(Effect.class);

    static {
        String tmp;
        for (EnumParticle p : EnumParticle.values()) {
            tmp = p.b().replace("_", "");
            for (Effect e : Effect.values()) {
                // This is pretty much the original code, but we only call it once / effect & matching particle
                if (e.getName() != null && e.getName().startsWith(tmp)) {
                    EFFECT_TO_PARTICLE.put(e, p);
                    break;
                }
            }
        }
    }
}