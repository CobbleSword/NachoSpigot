package dev.cobblesword.nachospigot.anticrash;

import dev.cobblesword.nachospigot.Nacho;
import dev.cobblesword.nachospigot.protocol.PacketListener;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AntiCrash {

    private final List<PacketListener> checkers = new ArrayList<>();

    public AntiCrash() {
        try {
            int i = 0;
            Set<Class<? extends PacketListener>> classes = new Reflections(this.getClass().getPackage().getName() + ".base").getSubTypesOf(PacketListener.class);
            for (Class<? extends PacketListener> clazz : classes) {
                if(clazz != PacketListener.class) {
                    checkers.add(clazz.getDeclaredConstructor().newInstance());
                    i++;
                }
            }
            System.out.println("[NS-AntiCrash] Loaded " + i + " anti-crash methods.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerAll() {
        int i = 0;
        for (PacketListener checker : checkers) {
            Nacho.get().registerPacketListener(checker);
            i++;
        }
        System.out.println("[NS-AntiCrash] Registered " + i + " anti-crash methods.");
    }

    public void unregisterAll() {
        int i = 0;
        for (PacketListener checker : checkers) {
            Nacho.get().unregisterPacketListener(checker);
            i++;
        }
        System.out.println("[NS-AntiCrash] Unregistered " + i + " anti-crash methods.");
    }

}
