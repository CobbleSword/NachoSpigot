package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.aikar.timings.SpigotTimings; // Spigot
import co.aikar.timings.Timing; // Spigot
import org.bukkit.inventory.InventoryHolder; // CraftBukkit

public abstract class TileEntity {

    public Timing tickTimer = SpigotTimings.getTileEntityTimings(this); // Spigot
    private static final Logger a = LogManager.getLogger();
    private static Map<String, Class<? extends TileEntity>> f = Maps.newHashMap();
    private static Map<Class<? extends TileEntity>, String> g = Maps.newHashMap();
    protected World world;
    protected BlockPosition position;
    protected boolean d;
    private int h;
    protected Block e;

    public TileEntity() {
        this.position = BlockPosition.ZERO;
        this.h = -1;
    }

    private static void a(Class<? extends TileEntity> oclass, String s) {
        if (TileEntity.f.containsKey(s)) {
            throw new IllegalArgumentException("Duplicate id: " + s);
        } else {
            TileEntity.f.put(s, oclass);
            TileEntity.g.put(oclass, s);
        }
    }

    public World getWorld() {
        return this.world;
    }

    public void a(World world) {
        this.world = world;
    }

    public boolean t() {
        return this.world != null;
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.position = new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z"));
    }

    public void b(NBTTagCompound nbttagcompound) {
        String s = (String) TileEntity.g.get(this.getClass());

        if (s == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        } else {
            nbttagcompound.setString("id", s);
            nbttagcompound.setInt("x", this.position.getX());
            nbttagcompound.setInt("y", this.position.getY());
            nbttagcompound.setInt("z", this.position.getZ());
        }
    }

    public static TileEntity c(NBTTagCompound nbttagcompound) {
        TileEntity tileentity = null;

        try {
            Class oclass = (Class) TileEntity.f.get(nbttagcompound.getString("id"));

            if (oclass != null) {
                tileentity = (TileEntity) oclass.newInstance();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (tileentity != null) {
            tileentity.a(nbttagcompound);
        } else {
            TileEntity.a.warn("Skipping BlockEntity with id " + nbttagcompound.getString("id"));
        }

        return tileentity;
    }

    public int u() {
        if (this.h == -1) {
            IBlockData iblockdata = this.world.getType(this.position);

            this.h = iblockdata.getBlock().toLegacyData(iblockdata);
        }

        return this.h;
    }

    public void update() {
        if (this.world != null) {
            IBlockData iblockdata = this.world.getType(this.position);

            this.h = iblockdata.getBlock().toLegacyData(iblockdata);
            this.world.b(this.position, this);
            if (this.w() != Blocks.AIR) {
                this.world.updateAdjacentComparators(this.position, this.w());
            }
        }

    }

    public BlockPosition getPosition() {
        return this.position;
    }

    public Block w() {
        if (this.e == null) {
            this.e = this.world.getType(this.position).getBlock();
        }

        return this.e;
    }

    public Packet getUpdatePacket() {
        return null;
    }

    public boolean x() {
        return this.d;
    }

    public void y() {
        this.d = true;
    }

    public void D() {
        this.d = false;
    }

    public boolean c(int i, int j) {
        return false;
    }

    public void E() {
        this.e = null;
        this.h = -1;
    }

    public void a(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Name", new Callable() {
            public String a() throws Exception {
                return (String) TileEntity.g.get(TileEntity.this.getClass()) + " // " + TileEntity.this.getClass().getCanonicalName();
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        if (this.world != null) {
            // PaperSpigot start - Prevent tile entity and entity crashes
            Block block = this.w();
            if (block != null) {
                CrashReportSystemDetails.a(crashreportsystemdetails, this.position, this.w(), this.u());
            }
            // PaperSpigot end
            crashreportsystemdetails.a("Actual block type", new Callable() {
                public String a() throws Exception {
                    int i = Block.getId(TileEntity.this.world.getType(TileEntity.this.position).getBlock());

                    try {
                        return String.format("ID #%d (%s // %s)", new Object[] { Integer.valueOf(i), Block.getById(i).a(), Block.getById(i).getClass().getCanonicalName()});
                    } catch (Throwable throwable) {
                        return "ID #" + i;
                    }
                }

                public Object call() throws Exception {
                    return this.a();
                }
            });
            crashreportsystemdetails.a("Actual block data value", new Callable() {
                public String a() throws Exception {
                    IBlockData iblockdata = TileEntity.this.world.getType(TileEntity.this.position);
                    int i = iblockdata.getBlock().toLegacyData(iblockdata);

                    if (i < 0) {
                        return "Unknown? (Got " + i + ")";
                    } else {
                        String s = String.format("%4s", new Object[] { Integer.toBinaryString(i)}).replace(" ", "0");

                        return String.format("%1$d / 0x%1$X / 0b%2$s", new Object[] { Integer.valueOf(i), s});
                    }
                }

                public Object call() throws Exception {
                    return this.a();
                }
            });
        }
    }

    public void a(BlockPosition blockposition) {
        this.position = blockposition;
    }

    public boolean F() {
        return false;
    }

    static {
        a(TileEntityFurnace.class, "Furnace");
        a(TileEntityChest.class, "Chest");
        a(TileEntityEnderChest.class, "EnderChest");
        a(BlockJukeBox.TileEntityRecordPlayer.class, "RecordPlayer");
        a(TileEntityDispenser.class, "Trap");
        a(TileEntityDropper.class, "Dropper");
        a(TileEntitySign.class, "Sign");
        a(TileEntityMobSpawner.class, "MobSpawner");
        a(TileEntityNote.class, "Music");
        a(TileEntityPiston.class, "Piston");
        a(TileEntityBrewingStand.class, "Cauldron");
        a(TileEntityEnchantTable.class, "EnchantTable");
        a(TileEntityEnderPortal.class, "Airportal");
        a(TileEntityCommand.class, "Control");
        a(TileEntityBeacon.class, "Beacon");
        a(TileEntitySkull.class, "Skull");
        a(TileEntityLightDetector.class, "DLDetector");
        a(TileEntityHopper.class, "Hopper");
        a(TileEntityComparator.class, "Comparator");
        a(TileEntityFlowerPot.class, "FlowerPot");
        a(TileEntityBanner.class, "Banner");
    }

    // CraftBukkit start - add method
    public InventoryHolder getOwner() {
        if (world == null) return null;
        // Spigot start
        org.bukkit.block.Block block = world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());
        if (block == null) {
            org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.WARNING, "No block for owner at %s %d %d %d", new Object[]{world.getWorld(), position.getX(), position.getY(), position.getZ()});
            return null;
        }
        // Spigot end
        org.bukkit.block.BlockState state = block.getState();
        if (state instanceof InventoryHolder) return (InventoryHolder) state;
        return null;
    }
    // CraftBukkit end
}
