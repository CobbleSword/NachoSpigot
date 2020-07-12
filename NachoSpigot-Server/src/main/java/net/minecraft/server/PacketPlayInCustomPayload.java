package net.minecraft.server;

import org.bukkit.Bukkit;

import java.io.IOException;

public class PacketPlayInCustomPayload implements Packet<PacketListenerPlayIn> {
  private String a;
  
  private PacketDataSerializer b;
  
  public void a(PacketDataSerializer paramPacketDataSerializer) throws IOException
  {
    this.a = paramPacketDataSerializer.c(20);
    int i = paramPacketDataSerializer.readableBytes();
    if (i < 0 || i > 32767)
      throw new IOException("Payload may not be larger than 32767 bytes"); 
    this.b = new PacketDataSerializer(paramPacketDataSerializer.readBytes(i));
  }
  
  public void b(PacketDataSerializer paramPacketDataSerializer) throws IOException {
    paramPacketDataSerializer.a(this.a);
    paramPacketDataSerializer.writeBytes(this.b);
  }
  
  public void a(PacketListenerPlayIn paramPacketListenerPlayIn) {
    paramPacketListenerPlayIn.a(this);
  }
  
  public String a() {
    return this.a;
  }
  
  public PacketDataSerializer b() {
    return this.b;
  }
}