package net.minecraft.server;

import com.google.common.base.Predicate;

// CraftBukkit start
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
// CraftBukkit end

public class BlockPumpkin extends BlockDirectional {

    private ShapeDetector snowGolemPart;
    private ShapeDetector snowGolem;
    private ShapeDetector ironGolemPart;
    private ShapeDetector ironGolem;
    private static final Predicate<IBlockData> Q = new Predicate() {
        public boolean a(IBlockData iblockdata) {
            return iblockdata != null && (iblockdata.getBlock() == Blocks.PUMPKIN || iblockdata.getBlock() == Blocks.LIT_PUMPKIN);
        }

        public boolean apply(Object object) {
            return this.a((IBlockData) object);
        }
    };

    protected BlockPumpkin() {
        super(Material.PUMPKIN, MaterialMapColor.q);
        this.j(this.blockStateList.getBlockData().set(BlockPumpkin.FACING, EnumDirection.NORTH));
        this.a(true);
        this.a(CreativeModeTab.b);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.onPlace(world, blockposition, iblockdata);
        this.f(world, blockposition);
    }

    public boolean e(World world, BlockPosition blockposition) {
        return this.getDetectorSnowGolemPart().a(world, blockposition) != null || this.getDetectorIronGolemPart().a(world, blockposition) != null;
    }

    private void f(World world, BlockPosition blockposition) {
        ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection;
        int i;
        int j;

        if ((shapedetector_shapedetectorcollection = this.getDetectorSnowGolem().a(world, blockposition)) != null) {
            BlockStateListPopulator blockList = new BlockStateListPopulator(world.getWorld()); // CraftBukkit - Use BlockStateListPopulator
            for (i = 0; i < this.getDetectorSnowGolem().b(); ++i) {
                ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.a(0, i, 0);

                // CraftBukkit start
                // world.setTypeAndData(shapedetectorblock.d(), Blocks.AIR.getBlockData(), 2);
                BlockPosition pos = shapedetectorblock.getPosition();
                blockList.setTypeId(pos.getX(), pos.getY(), pos.getZ(), 0);
                // CraftBukkit end
            }

            EntitySnowman entitysnowman = new EntitySnowman(world);
            BlockPosition blockposition1 = shapedetector_shapedetectorcollection.a(0, 2, 0).getPosition();

            entitysnowman.setPositionRotation((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.05D, (double) blockposition1.getZ() + 0.5D, 0.0F, 0.0F);
            // CraftBukkit start
            if (world.addEntity(entitysnowman, SpawnReason.BUILD_SNOWMAN)) {
                blockList.updateList();

            for (j = 0; j < 120; ++j) {
                world.addParticle(EnumParticle.SNOW_SHOVEL, (double) blockposition1.getX() + world.random.nextDouble(), (double) blockposition1.getY() + world.random.nextDouble() * 2.5D, (double) blockposition1.getZ() + world.random.nextDouble(), 0.0D, 0.0D, 0.0D, new int[0]);
            }

            for (j = 0; j < this.getDetectorSnowGolem().b(); ++j) {
                ShapeDetectorBlock shapedetectorblock1 = shapedetector_shapedetectorcollection.a(0, j, 0);

                world.update(shapedetectorblock1.getPosition(), Blocks.AIR);
            }
            } // CraftBukkit end
        } else if ((shapedetector_shapedetectorcollection = this.getDetectorIronGolem().a(world, blockposition)) != null) {
            BlockStateListPopulator blockList = new BlockStateListPopulator(world.getWorld()); // CraftBukkit - Use BlockStateListPopulator
            for (i = 0; i < this.getDetectorIronGolem().c(); ++i) {
                for (int k = 0; k < this.getDetectorIronGolem().b(); ++k) {
                    // CraftBukkit start
                    // world.setTypeAndData(shapedetectorcollection.a(i, k, 0).d(), Blocks.AIR.getBlockData(), 2);
                    BlockPosition pos = shapedetector_shapedetectorcollection.a(i, k, 0).getPosition();
                    blockList.setTypeId(pos.getX(), pos.getY(), pos.getZ(), 0);
                    // CraftBukkit end
                }
            }

            BlockPosition blockposition2 = shapedetector_shapedetectorcollection.a(1, 2, 0).getPosition();
            EntityIronGolem entityirongolem = new EntityIronGolem(world);

            entityirongolem.setPlayerCreated(true);
            entityirongolem.setPositionRotation((double) blockposition2.getX() + 0.5D, (double) blockposition2.getY() + 0.05D, (double) blockposition2.getZ() + 0.5D, 0.0F, 0.0F);

            // CraftBukkit start
            if (world.addEntity(entityirongolem, SpawnReason.BUILD_IRONGOLEM)) {
                blockList.updateList();

            for (j = 0; j < 120; ++j) {
                world.addParticle(EnumParticle.SNOWBALL, (double) blockposition2.getX() + world.random.nextDouble(), (double) blockposition2.getY() + world.random.nextDouble() * 3.9D, (double) blockposition2.getZ() + world.random.nextDouble(), 0.0D, 0.0D, 0.0D, new int[0]);
            }

            for (j = 0; j < this.getDetectorIronGolem().c(); ++j) {
                for (int l = 0; l < this.getDetectorIronGolem().b(); ++l) {
                    ShapeDetectorBlock shapedetectorblock2 = shapedetector_shapedetectorcollection.a(j, l, 0);

                    world.update(shapedetectorblock2.getPosition(), Blocks.AIR);
                }
            }
            } // CraftBukkit end
        }

    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return world.getType(blockposition).getBlock().material.isReplaceable() && World.a((IBlockAccess) world, blockposition.down());
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockPumpkin.FACING, entityliving.getDirection().opposite());
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockPumpkin.FACING, EnumDirection.fromType2(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((EnumDirection) iblockdata.get(BlockPumpkin.FACING)).b();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockPumpkin.FACING});
    }

    protected ShapeDetector getDetectorSnowGolemPart() {
        if (this.snowGolemPart == null) {
            this.snowGolemPart = ShapeDetectorBuilder.a().a(new String[] { " ", "#", "#"}).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SNOW))).b();
        }

        return this.snowGolemPart;
    }

    protected ShapeDetector getDetectorSnowGolem() {
        if (this.snowGolem == null) {
            this.snowGolem = ShapeDetectorBuilder.a().a(new String[] { "^", "#", "#"}).a('^', ShapeDetectorBlock.a(BlockPumpkin.Q)).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SNOW))).b();
        }

        return this.snowGolem;
    }

    protected ShapeDetector getDetectorIronGolemPart() {
        if (this.ironGolemPart == null) {
            this.ironGolemPart = ShapeDetectorBuilder.a().a(new String[] { "~ ~", "###", "~#~"}).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.IRON_BLOCK))).a('~', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.AIR))).b();
        }

        return this.ironGolemPart;
    }

    protected ShapeDetector getDetectorIronGolem() {
        if (this.ironGolem == null) {
            this.ironGolem = ShapeDetectorBuilder.a().a(new String[] { "~^~", "###", "~#~"}).a('^', ShapeDetectorBlock.a(BlockPumpkin.Q)).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.IRON_BLOCK))).a('~', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.AIR))).b();
        }

        return this.ironGolem;
    }
}
