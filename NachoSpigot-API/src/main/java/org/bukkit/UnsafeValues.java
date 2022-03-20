package org.bukkit;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * This interface provides value conversions that may be specific to a
 * runtime, or have arbitrary meaning (read: magic values).
 * <p>
 * Their existence and behavior is not guaranteed across future versions. They
 * may be poorly named, throw exceptions, have misleading parameters, or any
 * other bad programming practice.
 * <p>
 * This interface is unsupported and only for internal use.
 *
 * @deprecated Unsupported {@literal &} internal use only
 */
@Deprecated
public interface UnsafeValues {
    // Paper start
    net.kyori.adventure.text.flattener.ComponentFlattener componentFlattener();
    @SuppressWarnings("UnstableApiUsage")
    @ApiStatus.ScheduledForRemoval
    @Deprecated net.kyori.adventure.text.serializer.plain.PlainComponentSerializer plainComponentSerializer();
    net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer plainTextSerializer();
    net.kyori.adventure.text.serializer.gson.GsonComponentSerializer gsonComponentSerializer();
    net.kyori.adventure.text.serializer.gson.GsonComponentSerializer colorDownsamplingGsonComponentSerializer();
    net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer legacyComponentSerializer();
    // Paper end

    Material getMaterialFromInternalName(String name);

    List<String> tabCompleteInternalMaterialName(String token, List<String> completions);

    ItemStack modifyItemStack(ItemStack stack, String arguments);

    Statistic getStatisticFromInternalName(String name);

    Achievement getAchievementFromInternalName(String name);

    List<String> tabCompleteInternalStatisticOrAchievementName(String token, List<String> completions);

    // Paper start
    /**
      * Called once by the version command on first use, then cached.
     */
    default com.destroystokyo.paper.util.VersionFetcher getVersionFetcher() {
        return new com.destroystokyo.paper.util.VersionFetcher.DummyVersionFetcher();
    }
    // Paper end
}
