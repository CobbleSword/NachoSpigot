package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;

public class IntCache {

    private static int a = 256;
    private static List<int[]> b = Lists.newArrayList();
    private static List<int[]> c = Lists.newArrayList();
    private static List<int[]> d = Lists.newArrayList();
    private static List<int[]> e = Lists.newArrayList();

    public static synchronized int[] a(int i) {
        int[] aint;

        if (i <= 256) {
            if (IntCache.b.isEmpty()) {
                aint = new int[256];
                if (c.size() < org.spigotmc.SpigotConfig.intCacheLimit) IntCache.c.add(aint);
                return aint;
            } else {
                aint = (int[]) IntCache.b.remove(IntCache.b.size() - 1);
                if (c.size() < org.spigotmc.SpigotConfig.intCacheLimit) IntCache.c.add(aint);
                return aint;
            }
        } else if (i > IntCache.a) {
            IntCache.a = i;
            IntCache.d.clear();
            IntCache.e.clear();
            aint = new int[IntCache.a];
            if (e.size() < org.spigotmc.SpigotConfig.intCacheLimit) IntCache.e.add(aint);
            return aint;
        } else if (IntCache.d.isEmpty()) {
            aint = new int[IntCache.a];
            if (e.size() < org.spigotmc.SpigotConfig.intCacheLimit) IntCache.e.add(aint);
            return aint;
        } else {
            aint = (int[]) IntCache.d.remove(IntCache.d.size() - 1);
            if (e.size() < org.spigotmc.SpigotConfig.intCacheLimit) IntCache.e.add(aint);
            return aint;
        }
    }

    public static synchronized void a() {
        if (!IntCache.d.isEmpty()) {
            IntCache.d.remove(IntCache.d.size() - 1);
        }

        if (!IntCache.b.isEmpty()) {
            IntCache.b.remove(IntCache.b.size() - 1);
        }

        IntCache.d.addAll(IntCache.e);
        IntCache.b.addAll(IntCache.c);
        IntCache.e.clear();
        IntCache.c.clear();
    }

    public static synchronized String b() {
        return "cache: " + IntCache.d.size() + ", tcache: " + IntCache.b.size() + ", allocated: " + IntCache.e.size() + ", tallocated: " + IntCache.c.size();
    }
}
