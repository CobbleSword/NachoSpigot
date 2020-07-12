package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutTitle implements Packet<PacketListenerPlayOut> {

    private EnumTitleAction a;
    private IChatBaseComponent b;
    private int c;
    private int d;
    private int e;

    // Paper start
    public net.md_5.bungee.api.chat.BaseComponent[] components;

    public PacketPlayOutTitle(EnumTitleAction action, net.md_5.bungee.api.chat.BaseComponent[] components, int fadeIn, int stay, int fadeOut) {
        this.a = action;
        this.components = components;
        this.c = fadeIn;
        this.d = stay;
        this.e = fadeOut;
    }
    // Paper end

    public PacketPlayOutTitle() {}

    public PacketPlayOutTitle(EnumTitleAction packetplayouttitle_enumtitleaction, IChatBaseComponent ichatbasecomponent) {
        this(packetplayouttitle_enumtitleaction, ichatbasecomponent, -1, -1, -1);
    }

    public PacketPlayOutTitle(int i, int j, int k) {
        this(EnumTitleAction.TIMES, (IChatBaseComponent) null, i, j, k);
    }

    public PacketPlayOutTitle(EnumTitleAction packetplayouttitle_enumtitleaction, IChatBaseComponent ichatbasecomponent, int i, int j, int k) {
        this.a = packetplayouttitle_enumtitleaction;
        this.b = ichatbasecomponent;
        this.c = i;
        this.d = j;
        this.e = k;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = (EnumTitleAction) packetdataserializer.a(EnumTitleAction.class);
        if (this.a == EnumTitleAction.TITLE || this.a == EnumTitleAction.SUBTITLE) {
            this.b = packetdataserializer.d();
        }

        if (this.a == EnumTitleAction.TIMES) {
            this.c = packetdataserializer.readInt();
            this.d = packetdataserializer.readInt();
            this.e = packetdataserializer.readInt();
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a((Enum) this.a);
        if (this.a == EnumTitleAction.TITLE || this.a == EnumTitleAction.SUBTITLE) {
            // Paper start
            if (this.components != null) {
                packetdataserializer.a(net.md_5.bungee.chat.ComponentSerializer.toString(components));
            } else {
                packetdataserializer.a(this.b);
            }
            // Paper end
        }

        if (this.a == EnumTitleAction.TIMES) {
            packetdataserializer.writeInt(this.c);
            packetdataserializer.writeInt(this.d);
            packetdataserializer.writeInt(this.e);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    // PaperSpigot start - fix compile error
    /*
    public void a(PacketListener packetlistener) {
        this.a((PacketListenerPlayOut) packetlistener);
    }
    */
    // PaperSpigot end

    public static enum EnumTitleAction {

        TITLE, SUBTITLE, TIMES, CLEAR, RESET;

        private EnumTitleAction() {}

        public static EnumTitleAction a(String s) {
            EnumTitleAction[] apacketplayouttitle_enumtitleaction = values();
            int i = apacketplayouttitle_enumtitleaction.length;

            for (int j = 0; j < i; ++j) {
                EnumTitleAction packetplayouttitle_enumtitleaction = apacketplayouttitle_enumtitleaction[j];

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
            int j = apacketplayouttitle_enumtitleaction.length;

            for (int k = 0; k < j; ++k) {
                EnumTitleAction packetplayouttitle_enumtitleaction = apacketplayouttitle_enumtitleaction[k];

                astring[i++] = packetplayouttitle_enumtitleaction.name().toLowerCase();
            }

            return astring;
        }
    }
}
