package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.Random;

// CraftBukkit start
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
// CraftBukkit end

public class BlockSkull extends BlockContainer {

    public static final BlockStateDirection FACING = BlockStateDirection.of("facing");
    public static final BlockStateBoolean NODROP = BlockStateBoolean.of("nodrop");
    private static final Predicate<ShapeDetectorBlock> N = new Predicate() {
        public boolean a(ShapeDetectorBlock shapedetectorblock) {
            return shapedetectorblock.a() != null && shapedetectorblock.a().getBlock() == Blocks.SKULL && shapedetectorblock.b() instanceof TileEntitySkull && ((TileEntitySkull) shapedetectorblock.b()).getSkullType() == 1;
        }

        public boolean apply(Object object) {
            return this.a((ShapeDetectorBlock) object);
        }
    };
    private ShapeDetector O;
    private ShapeDetector P;

    protected BlockSkull() {
        super(Material.ORIENTABLE);
        this.j(this.blockStateList.getBlockData().set(BlockSkull.FACING, EnumDirection.NORTH).set(BlockSkull.NODROP, Boolean.valueOf(false)));
        this.a(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
    }

    public String getName() {
        return LocaleI18n.get("tile.skull.skeleton.name");
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch (BlockSkull.SyntheticClass_1.a[((EnumDirection) iblockaccess.getType(blockposition).get(BlockSkull.FACING)).ordinal()]) {
        case 1:
        default:
            this.a(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
            break;

        case 2:
            this.a(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
            break;

        case 3:
            this.a(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
            break;

        case 4:
            this.a(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
            break;

        case 5:
            this.a(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
        }

    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.updateShape(world, blockposition);
        return super.a(world, blockposition, iblockdata);
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockSkull.FACING, entityliving.getDirection()).set(BlockSkull.NODROP, Boolean.valueOf(false));
    }

    public TileEntity a(World world, int i) {
        return new TileEntitySkull();
    }

    public int getDropData(World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity instanceof TileEntitySkull ? ((TileEntitySkull) tileentity).getSkullType() : super.getDropData(world, blockposition);
    }
    
    // CraftBukkit start - Special case dropping so we can get info from the tile entity
    @Override
    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        if (world.random.nextFloat() < f) {
            ItemStack itemstack = new ItemStack(Items.SKULL, 1, this.getDropData(world, blockposition));
            TileEntitySkull tileentityskull = (TileEntitySkull) world.getTileEntity(blockposition);

            if (tileentityskull.getSkullType() == 3 && tileentityskull.getGameProfile() != null) {
                itemstack.setTag(new NBTTagCompound());
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                GameProfileSerializer.serialize(nbttagcompound, tileentityskull.getGameProfile());
                itemstack.getTag().set("SkullOwner", nbttagcompound);
            }

            a(world, blockposition, itemstack);
        }
    }
    // CraftBukkit end

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (entityhuman.abilities.canInstantlyBuild) {
            iblockdata = iblockdata.set(BlockSkull.NODROP, Boolean.valueOf(true));
            world.setTypeAndData(blockposition, iblockdata, 4);
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.isClientSide) {
            // CraftBukkit start - Drop item in code above, not here
            // if (!((Boolean) iblockdata.get(BlockSkull.NODROP)).booleanValue()) {
            if (false) {
                // CraftBukkit end
                TileEntity tileentity = world.getTileEntity(blockposition);

                if (tileentity instanceof TileEntitySkull) {
                    TileEntitySkull tileentityskull = (TileEntitySkull) tileentity;
                    ItemStack itemstack = new ItemStack(Items.SKULL, 1, this.getDropData(world, blockposition));

                    if (tileentityskull.getSkullType() == 3 && tileentityskull.getGameProfile() != null) {
                        itemstack.setTag(new NBTTagCompound());
                        NBTTagCompound nbttagcompound = new NBTTagCompound();

                        GameProfileSerializer.serialize(nbttagcompound, tileentityskull.getGameProfile());
                        itemstack.getTag().set("SkullOwner", nbttagcompound);
                    }

                    a(world, blockposition, itemstack);
                }
            }

            super.remove(world, blockposition, iblockdata);
        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.SKULL;
    }

    public boolean b(World world, BlockPosition blockposition, ItemStack itemstack) {
        return itemstack.getData() == 1 && blockposition.getY() >= 2 && world.getDifficulty() != EnumDifficulty.PEACEFUL && !world.isClientSide ? this.l().a(world, blockposition) != null : false;
    }

    public void a(World world, BlockPosition blockposition, TileEntitySkull tileentityskull) {
        if (world.captureBlockStates) return; // CraftBukkit
        if (tileentityskull.getSkullType() == 1 && blockposition.getY() >= 2 && world.getDifficulty() != EnumDifficulty.PEACEFUL && !world.isClientSide) {
            ShapeDetector shapedetector = this.n();
            ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = shapedetector.a(world, blockposition);

            if (shapedetector_shapedetectorcollection != null) {
                // CraftBukkit start - Use BlockStateListPopulator
                BlockStateListPopulator blockList = new BlockStateListPopulator(world.getWorld());
                int i;

                for (i = 0; i < 3; ++i) {
                    ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.a(i, 0, 0);

                    // CraftBukkit start
                    // world.setTypeAndData(shapedetectorblock.getPosition(), shapedetectorblock.a().set(BlockSkull.NODROP, Boolean.valueOf(true)), 2);
                    BlockPosition pos = shapedetectorblock.getPosition();
                    IBlockData data = shapedetectorblock.a().set(BlockSkull.NODROP, Boolean.valueOf(true));
                    blockList.setTypeAndData(pos.getX(), pos.getY(), pos.getZ(), data.getBlock(), data.getBlock().toLegacyData(data), 2);
                    // CraftBukkit end
                }

                for (i = 0; i < shapedetector.c(); ++i) {
                    for (int j = 0; j < shapedetector.b(); ++j) {
                        ShapeDetectorBlock shapedetectorblock1 = shapedetector_shapedetectorcollection.a(i, j, 0);

                        // CraftBukkit start
                        // world.setTypeAndData(shapedetectorblock1.getPosition(), Blocks.AIR.getBlockData(), 2);
                        BlockPosition pos = shapedetectorblock1.getPosition();
                        blockList.setTypeAndData(pos.getX(), pos.getY(), pos.getZ(), Blocks.AIR, 0, 2);
                        // CraftBukkit end
                    }
                }

                BlockPosition blockposition1 = shapedetector_shapedetectorcollection.a(1, 0, 0).getPosition();
                EntityWither entitywither = new EntityWither(world);
                BlockPosition blockposition2 = shapedetector_shapedetectorcollection.a(1, 2, 0).getPosition();

                entitywither.setPositionRotation((double) blockposition2.getX() + 0.5D, (double) blockposition2.getY() + 0.55D, (double) blockposition2.getZ() + 0.5D, shapedetector_shapedetectorcollection.b().k() == EnumDirection.EnumAxis.X ? 0.0F : 90.0F, 0.0F);
                entitywither.aI = shapedetector_shapedetectorcollection.b().k() == EnumDirection.EnumAxis.X ? 0.0F : 90.0F;
                entitywither.n();
                Iterator iterator = world.a(EntityHuman.class, entitywither.getBoundingBox().grow(50.0D, 50.0D, 50.0D)).iterator();

                // CraftBukkit start
                if (world.addEntity(entitywither, SpawnReason.BUILD_WITHER)) {
                    blockList.updateList();
                    
                while (iterator.hasNext()) {
                    EntityHuman entityhuman = (EntityHuman) iterator.next();

                    entityhuman.b((Statistic) AchievementList.I);
                }

                int k;

                for (k = 0; k < 120; ++k) {
                    world.addParticle(EnumParticle.SNOWBALL, (double) blockposition1.getX() + world.random.nextDouble(), (double) (blockposition1.getY() - 2) + world.random.nextDouble() * 3.9D, (double) blockposition1.getZ() + world.random.nextDouble(), 0.0D, 0.0D, 0.0D, new int[0]);
                }

                for (k = 0; k < shapedetector.c(); ++k) {
                    for (int l = 0; l < shapedetector.b(); ++l) {
                        ShapeDetectorBlock shapedetectorblock2 = shapedetector_shapedetectorcollection.a(k, l, 0);

                        world.update(shapedetectorblock2.getPosition(), Blocks.AIR);
                    }
                }
                } // CraftBukkit end

            }
        }
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockSkull.FACING, EnumDirection.fromType1(i & 7)).set(BlockSkull.NODROP, Boolean.valueOf((i & 8) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockSkull.FACING)).a();

        if (((Boolean) iblockdata.get(BlockSkull.NODROP)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockSkull.FACING, BlockSkull.NODROP});
    }

    protected ShapeDetector l() {
        if (this.O == null) {
            this.O = ShapeDetectorBuilder.a().a(new String[] { "   ", "###", "~#~"}).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SOUL_SAND))).a('~', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.AIR))).b();
        }

        return this.O;
    }

    protected ShapeDetector n() {
        if (this.P == null) {
            this.P = ShapeDetectorBuilder.a().a(new String[] { "^^^", "###", "~#~"}).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SOUL_SAND))).a('^', BlockSkull.N).a('~', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.AIR))).b();
        }

        return this.P;
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[EnumDirection.values().length];

        static {
            try {
                BlockSkull.SyntheticClass_1.a[EnumDirection.UP.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                BlockSkull.SyntheticClass_1.a[EnumDirection.NORTH.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

            try {
                BlockSkull.SyntheticClass_1.a[EnumDirection.SOUTH.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror2) {
                ;
            }

            try {
                BlockSkull.SyntheticClass_1.a[EnumDirection.WEST.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror3) {
                ;
            }

            try {
                BlockSkull.SyntheticClass_1.a[EnumDirection.EAST.ordinal()] = 5;
            } catch (NoSuchFieldError nosuchfielderror4) {
                ;
            }

        }
    }
}
