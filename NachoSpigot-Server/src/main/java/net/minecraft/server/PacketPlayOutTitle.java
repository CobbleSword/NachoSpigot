package net.minecraft.server;

import java.io.IOException;

@SuppressWarnings("SpellCheckingInspection")
public class PacketPlayOutTitle implements Packet<PacketListenerPlayOut> {

    private EnumTitleAction a;
    private IChatBaseComponent b;
    public net.kyori.adventure.text.Component adventure$text; // Paper
    private int c;
    private int d;
    private int e;

    public PacketPlayOutTitle() {}

    public PacketPlayOutTitle(EnumTitleAction packetplayouttitle_enumtitleaction, IChatBaseComponent ichatbasecomponent) {
        this(packetplayouttitle_enumtitleaction, ichatbasecomponent, -1, -1, -1);
    }

    public PacketPlayOutTitle(int i, int j, int k) {
        this(EnumTitleAction.TIMES, null, i, j, k);
    }

    public PacketPlayOutTitle(EnumTitleAction packetplayouttitle_enumtitleaction, IChatBaseComponent ichatbasecomponent, int i, int j, int k) {
        this.a = packetplayouttitle_enumtitleaction;
        this.b = ichatbasecomponent;
        this.c = i;
        this.d = j;
        this.e = k;
    }

    public void a(PacketDataSerializer serializer) throws IOException {
        this.a = serializer.a(EnumTitleAction.class);
        if (this.a == EnumTitleAction.TITLE || this.a == EnumTitleAction.SUBTITLE) {
            this.b = serializer.d();
        }

        if (this.a == EnumTitleAction.TIMES) {
            this.c = serializer.readInt();
            this.d = serializer.readInt();
            this.e = serializer.readInt();
        }

    }

    public void b(PacketDataSerializer serializer) throws IOException {
        serializer.a(this.a);
        if (this.a == EnumTitleAction.TITLE || this.a == EnumTitleAction.SUBTITLE) {
            // Paper start
            if (this.adventure$text != null) {
                serializer.writeComponent(this.adventure$text);
            } else
            // Paper end
            serializer.writeComponent(this.b);
        }

        if (this.a == EnumTitleAction.TIMES) {
            serializer.writeInt(this.c);
            serializer.writeInt(this.d);
            serializer.writeInt(this.e);
        }
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public enum EnumTitleAction {

        TITLE, SUBTITLE, TIMES, CLEAR, RESET;

        EnumTitleAction() {}

        public static EnumTitleAction a(String s) {
            EnumTitleAction[] apacketplayouttitle_enumtitleaction = values();
            // Nacho - unused
            //int i = apacketplayouttitle_enumtitleaction.length;

            for (EnumTitleAction packetplayouttitle_enumtitleaction : apacketplayouttitle_enumtitleaction) {
                if (packetplayouttitle_enumtitleaction.name().equalsIgnoreCase(s)) {
                    return packetplayouttitle_enumtitleaction;
                }
            }

            return EnumTitleAction.TITLE;
        }

        public static String[] a() {
            String[] astring = new String[values().length];
            int i = 0;
            EnumTitleAction[] apacketplayouttitle_enumtitleaction = values();
            // Nacho - unused
            //int j = apacketplayouttitle_enumtitleaction.length;

            for (EnumTitleAction packetplayouttitle_enumtitleaction : apacketplayouttitle_enumtitleaction) {
                astring[i++] = packetplayouttitle_enumtitleaction.name().toLowerCase();
            }

            return astring;
        }
    }
}
