package net.minecraft.server;

public class BiomeTheEndDecorator extends BiomeDecorator {

    protected WorldGenerator M;

    public BiomeTheEndDecorator() {
        this.M = new WorldGenEnder(Blocks.END_STONE);
    }

    protected void a(BiomeBase biomebase) {
        this.a();
        if (this.b.nextInt(5) == 0) {
            int i = this.b.nextInt(16) + 8;
            int j = this.b.nextInt(16) + 8;

            this.M.generate(this.a, this.b, this.a.r(this.c.a(i, 0, j)));
        }

        if (this.c.getX() == 0 && this.c.getZ() == 0) {
            EntityEnderDragon entityenderdragon = new EntityEnderDragon(this.a);

            entityenderdragon.setPositionRotation(0.0D, 128.0D, 0.0D, this.b.nextFloat() * 360.0F, 0.0F);
            this.a.addEntity(entityenderdragon, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CHUNK_GEN); // CraftBukkit - add SpawnReason
        }

    }
}
