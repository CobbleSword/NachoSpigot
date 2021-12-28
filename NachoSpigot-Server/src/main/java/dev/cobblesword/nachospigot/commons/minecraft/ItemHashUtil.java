package dev.cobblesword.nachospigot.commons.minecraft;

import io.netty.handler.codec.EncoderException;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTCompressedStreamTools;
import net.minecraft.server.NBTTagCompound;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestOutputStream;

public final class ItemHashUtil {
    private ItemHashUtil() {
    }

    public static byte[] calculateItemHash(InputStream stream){
        try {
            return DigestUtils.md5(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] calculateItemHash(ItemStack itemStack) {
        try (DigestOutputStream outputStream = new DigestOutputStream(NullOutputStream.NULL_OUTPUT_STREAM, DigestUtils.getMd5Digest());
             DataOutputStream outputStream1 = new DataOutputStream(outputStream)) {

            if (itemStack == null || itemStack.getItem() == null) {
                outputStream1.writeShort(-1);
            } else {
                outputStream1.writeShort(Item.getId(itemStack.getItem()));
                outputStream1.writeByte(itemStack.count);
                outputStream1.writeShort(itemStack.getData());
                NBTTagCompound nbttagcompound = null;

                if (itemStack.getItem().usesDurability() || itemStack.getItem().p()) {
                    itemStack = itemStack.cloneItemStack();
                    CraftItemStack.setItemMeta(itemStack, CraftItemStack.getItemMeta(itemStack));
                    nbttagcompound = itemStack.getTag();
                }

                if (nbttagcompound == null) {
                    outputStream1.writeByte(0);
                } else {
                    try {
                        NBTCompressedStreamTools.a(nbttagcompound, (DataOutput) outputStream1);
                    } catch (Exception ioexception) {
                        throw new EncoderException(ioexception);
                    }
                }
            }

            return outputStream.getMessageDigest().digest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
