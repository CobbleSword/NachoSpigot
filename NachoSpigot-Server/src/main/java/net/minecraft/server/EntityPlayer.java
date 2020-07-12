package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
// CraftBukkit end

public class EntityPlayer extends EntityHuman implements ICrafting {

    private static final Logger bH = LogManager.getLogger();
    public String locale = "en_US"; // Spigot
    public PlayerConnection playerConnection;
    public final MinecraftServer server;
    public final PlayerInteractManager playerInteractManager;
    public double d;
    public double e;
    public final List<ChunkCoordIntPair> chunkCoordIntPairQueue = Lists.newLinkedList();
    public final List<Integer> removeQueue = Lists.newLinkedList();
    private final ServerStatisticManager bK;
    private float bL = Float.MIN_VALUE;
    private float bM = -1.0E8F;
    private int bN = -99999999;
    private boolean bO = true;
    public int lastSentExp = -99999999;
    public int invulnerableTicks = 60;
    private EntityHuman.EnumChatVisibility bR;
    private boolean bS = true;
    private long bT = System.currentTimeMillis();
    private Entity bU = null;
    private int containerCounter;
    public boolean g;
    public int ping;
    public boolean viewingCredits;

    // CraftBukkit start
    public String displayName;
    public IChatBaseComponent listName;
    public org.bukkit.Location compassTarget;
    public int newExp = 0;
    public int newLevel = 0;
    public int newTotalExp = 0;
    public boolean keepLevel = false;
    public double maxHealthCache;
    public boolean joining = true;
    // CraftBukkit end
    // Spigot start
    public boolean collidesWithEntities = true;
    public int viewDistance; // PaperSpigot - Player view distance API
    private int containerUpdateDelay; // PaperSpigot

    @Override
    public boolean ad()
    {
        return this.collidesWithEntities && super.ad(); // (first !this.isDead near bottom of EntityLiving)
    }

    @Override
    public boolean ae()
    {
        return this.collidesWithEntities && super.ae(); // (second !this.isDead near bottom of EntityLiving)
    }
    // Spigot end

    public EntityPlayer(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager) {
        super(worldserver, gameprofile);
        this.viewDistance = world.spigotConfig.viewDistance; // PaperSpigot - Player view distance API
        playerinteractmanager.player = this;
        this.playerInteractManager = playerinteractmanager;
        BlockPosition blockposition = worldserver.getSpawn();

        if (!worldserver.worldProvider.o() && worldserver.getWorldData().getGameType() != WorldSettings.EnumGamemode.ADVENTURE) {
            int i = Math.max(5, minecraftserver.getSpawnProtection() - 6);
            int j = MathHelper.floor(worldserver.getWorldBorder().b((double) blockposition.getX(), (double) blockposition.getZ()));

            if (j < i) {
                i = j;
            }

            if (j <= 1) {
                i = 1;
            }

            blockposition = worldserver.r(blockposition.a(this.random.nextInt(i * 2) - i, 0, this.random.nextInt(i * 2) - i));
        }

        this.server = minecraftserver;
        this.bK = minecraftserver.getPlayerList().a((EntityHuman) this);
        this.S = 0.0F;
        this.setPositionRotation(blockposition, 0.0F, 0.0F);

        while (!worldserver.getCubes(this, this.getBoundingBox()).isEmpty() && this.locY < 255.0D) {
            this.setPosition(this.locX, this.locY + 1.0D, this.locZ);
        }

        // CraftBukkit start
        this.displayName = this.getName();
        // this.canPickUpLoot = true; TODO
        this.maxHealthCache = this.getMaxHealth();
        // CraftBukkit end
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("playerGameType", 99)) {
            if (MinecraftServer.getServer().getForceGamemode()) {
                this.playerInteractManager.setGameMode(MinecraftServer.getServer().getGamemode());
            } else {
                this.playerInteractManager.setGameMode(WorldSettings.EnumGamemode.getById(nbttagcompound.getInt("playerGameType")));
            }
        }

        this.getBukkitEntity().readExtraData(nbttagcompound); // CraftBukkit
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("playerGameType", this.playerInteractManager.getGameMode().getId());

        this.getBukkitEntity().setExtraData(nbttagcompound); // CraftBukkit
    }

    // CraftBukkit start - World fallback code, either respawn location or global spawn
    public void spawnIn(World world) {
        super.spawnIn(world);
        if (world == null) {
            this.dead = false;
            BlockPosition position = null;
            if (this.spawnWorld != null && !this.spawnWorld.equals("")) {
                CraftWorld cworld = (CraftWorld) Bukkit.getServer().getWorld(this.spawnWorld);
                if (cworld != null && this.getBed() != null) {
                    world = cworld.getHandle();
                    position = EntityHuman.getBed(cworld.getHandle(), this.getBed(), false);
                }
            }
            if (world == null || position == null) {
                world = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();
                position = world.getSpawn();
            }
            this.world = world;
            this.setPosition(position.getX() + 0.5, position.getY(), position.getZ() + 0.5);
        }
        this.dimension = ((WorldServer) this.world).dimension;
        this.playerInteractManager.a((WorldServer) world);
    }
    // CraftBukkit end

    public void levelDown(int i) {
        super.levelDown(i);
        this.lastSentExp = -1;
    }

    public void enchantDone(int i) {
        super.enchantDone(i);
        this.lastSentExp = -1;
    }

    public void syncInventory() {
        this.activeContainer.addSlotListener(this);
    }

    public void enterCombat() {
        super.enterCombat();
        this.playerConnection.sendPacket(new PacketPlayOutCombatEvent(this.bs(), PacketPlayOutCombatEvent.EnumCombatEventType.ENTER_COMBAT));
    }

    public void exitCombat() {
        super.exitCombat();
        this.playerConnection.sendPacket(new PacketPlayOutCombatEvent(this.bs(), PacketPlayOutCombatEvent.EnumCombatEventType.END_COMBAT));
    }

    public void t_() {
        // CraftBukkit start
        if (this.joining) {
            this.joining = false;
        }
        // CraftBukkit end
        this.playerInteractManager.a();
        --this.invulnerableTicks;
        if (this.noDamageTicks > 0) {
            --this.noDamageTicks;
        }
        
        // PaperSpigot start - Configurable container update tick rate
        if (--containerUpdateDelay <= 0) {
            this.activeContainer.b();
            containerUpdateDelay = world.paperSpigotConfig.containerUpdateTickRate;
        }
        // PaperSpigot end
        if (!this.world.isClientSide && !this.activeContainer.a((EntityHuman) this)) {
            this.closeInventory();
            this.activeContainer = this.defaultContainer;
        }

        while (!this.removeQueue.isEmpty()) {
            int i = Math.min(this.removeQueue.size(), Integer.MAX_VALUE);
            int[] aint = new int[i];
            Iterator iterator = this.removeQueue.iterator();
            int j = 0;

            while (iterator.hasNext() && j < i) {
                aint[j++] = ((Integer) iterator.next()).intValue();
                iterator.remove();
            }

            this.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(aint));
        }

        if (!this.chunkCoordIntPairQueue.isEmpty()) {
            ArrayList arraylist = Lists.newArrayList();
            Iterator iterator1 = this.chunkCoordIntPairQueue.iterator();
            ArrayList arraylist1 = Lists.newArrayList();

            Chunk chunk;

            while (iterator1.hasNext() && arraylist.size() < this.world.spigotConfig.maxBulkChunk) { // Spigot
                ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator1.next();

                if (chunkcoordintpair != null) {
                    if (this.world.isLoaded(new BlockPosition(chunkcoordintpair.x << 4, 0, chunkcoordintpair.z << 4))) {
                        chunk = this.world.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z);
                        if (chunk.isReady()) {
                            arraylist.add(chunk);
                            arraylist1.addAll(chunk.tileEntities.values()); // CraftBukkit - Get tile entities directly from the chunk instead of the world
                            iterator1.remove();
                        }
                    }
                } else {
                    iterator1.remove();
                }
            }

            if (!arraylist.isEmpty()) {
                if (arraylist.size() == 1) {
                    this.playerConnection.sendPacket(new PacketPlayOutMapChunk((Chunk) arraylist.get(0), true, '\uffff'));
                } else {
                    this.playerConnection.sendPacket(new PacketPlayOutMapChunkBulk(arraylist));
                }

                Iterator iterator2 = arraylist1.iterator();

                while (iterator2.hasNext()) {
                    TileEntity tileentity = (TileEntity) iterator2.next();

                    this.a(tileentity);
                }

                iterator2 = arraylist.iterator();

                while (iterator2.hasNext()) {
                    chunk = (Chunk) iterator2.next();
                    this.u().getTracker().a(this, chunk);
                }
            }
        }

        Entity entity = this.C();

        if (entity != this) {
            if (!entity.isAlive()) {
                this.setSpectatorTarget(this);
            } else {
                this.setLocation(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
                this.server.getPlayerList().d(this);
                if (this.isSneaking()) {
                    this.setSpectatorTarget(this);
                }
            }
        }

    }

    public void l() {
        try {
            super.t_();

            for (int i = 0; i < this.inventory.getSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);

                if (itemstack != null && itemstack.getItem().f()) {
                    Packet packet = ((ItemWorldMapBase) itemstack.getItem()).c(itemstack, this.world, this);

                    if (packet != null) {
                        this.playerConnection.sendPacket(packet);
                    }
                }
            }

            // CraftBukkit - Optionally scale health
            if (this.getHealth() != this.bM || this.bN != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0F != this.bO) {
                this.playerConnection.sendPacket(new PacketPlayOutUpdateHealth(this.getBukkitEntity().getScaledHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel()));
                this.bM = this.getHealth();
                this.bN = this.foodData.getFoodLevel();
                this.bO = this.foodData.getSaturationLevel() == 0.0F;
            }

            if (this.getHealth() + this.getAbsorptionHearts() != this.bL) {
                this.bL = this.getHealth() + this.getAbsorptionHearts();
                Collection collection = this.getScoreboard().getObjectivesForCriteria(IScoreboardCriteria.g);
                Iterator iterator = collection.iterator();

                while (iterator.hasNext()) {
                    ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

                    this.getScoreboard().getPlayerScoreForObjective(this.getName(), scoreboardobjective).updateForList(Arrays.asList(new EntityHuman[] { this}));
                }
                // CraftBukkit - Update ALL the scores!
                this.world.getServer().getScoreboardManager().updateAllScoresForList(IScoreboardCriteria.g, this.getName(), com.google.common.collect.ImmutableList.of(this));
            }
            // CraftBukkit start - Force max health updates
            if (this.maxHealthCache != this.getMaxHealth()) {
                this.getBukkitEntity().updateScaledHealth();
            }
            // CraftBukkit end

            if (this.expTotal != this.lastSentExp) {
                this.lastSentExp = this.expTotal;
                this.playerConnection.sendPacket(new PacketPlayOutExperience(this.exp, this.expTotal, this.expLevel));
            }

            if (this.ticksLived % 20 * 5 == 0 && !this.getStatisticManager().hasAchievement(AchievementList.L)) {
                this.i_();
            }

            // CraftBukkit start - initialize oldLevel and fire PlayerLevelChangeEvent
            if (this.oldLevel == -1) {
                this.oldLevel = this.expLevel;
            }

            if (this.oldLevel != this.expLevel) {
                CraftEventFactory.callPlayerLevelChangeEvent(this.world.getServer().getPlayer((EntityPlayer) this), this.oldLevel, this.expLevel);
                this.oldLevel = this.expLevel;
            }
            // CraftBukkit end
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Ticking player");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Player being ticked");

            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    protected void i_() {
        BiomeBase biomebase = this.world.getBiome(new BlockPosition(MathHelper.floor(this.locX), 0, MathHelper.floor(this.locZ)));
        String s = biomebase.ah;
        AchievementSet achievementset = (AchievementSet) this.getStatisticManager().b((Statistic) AchievementList.L);

        if (achievementset == null) {
            achievementset = (AchievementSet) this.getStatisticManager().a(AchievementList.L, new AchievementSet());
        }

        achievementset.add(s);
        if (this.getStatisticManager().b(AchievementList.L) && achievementset.size() >= BiomeBase.n.size()) {
            HashSet hashset = Sets.newHashSet(BiomeBase.n);
            Iterator iterator = achievementset.iterator();

            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();
                Iterator iterator1 = hashset.iterator();

                while (iterator1.hasNext()) {
                    BiomeBase biomebase1 = (BiomeBase) iterator1.next();

                    if (biomebase1.ah.equals(s1)) {
                        iterator1.remove();
                    }
                }

                if (hashset.isEmpty()) {
                    break;
                }
            }

            if (hashset.isEmpty()) {
                this.b((Statistic) AchievementList.L);
            }
        }

    }

    public void die(DamageSource damagesource) {
        // CraftBukkit start - fire PlayerDeathEvent
        if (this.dead) {
            return;
        }
        java.util.List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<org.bukkit.inventory.ItemStack>();
        boolean keepInventory = this.world.getGameRules().getBoolean("keepInventory");

        if (!keepInventory) {
            for (int i = 0; i < this.inventory.items.length; ++i) {
                if (this.inventory.items[i] != null) {
                    loot.add(CraftItemStack.asCraftMirror(this.inventory.items[i]));
                }
            }

            for (int i = 0; i < this.inventory.armor.length; ++i) {
                if (this.inventory.armor[i] != null) {
                    loot.add(CraftItemStack.asCraftMirror(this.inventory.armor[i]));
                }
            }
        }

        IChatBaseComponent chatmessage = this.bs().b();

        String deathmessage = chatmessage.c();
        org.bukkit.event.entity.PlayerDeathEvent event = CraftEventFactory.callPlayerDeathEvent(this, loot, deathmessage, keepInventory);

        String deathMessage = event.getDeathMessage();

        if (deathMessage != null && deathMessage.length() > 0 && this.world.getGameRules().getBoolean("showDeathMessages")) { // TODO: allow plugins to override?
            if (deathMessage.equals(deathmessage)) {
                this.server.getPlayerList().sendMessage(chatmessage);
            } else {
                this.server.getPlayerList().sendMessage(org.bukkit.craftbukkit.util.CraftChatMessage.fromString(deathMessage));
            }
        }

        // we clean the player's inventory after the EntityDeathEvent is called so plugins can get the exact state of the inventory.
        if (!event.getKeepInventory()) {
            for (int i = 0; i < this.inventory.items.length; ++i) {
                this.inventory.items[i] = null;
            }

            for (int i = 0; i < this.inventory.armor.length; ++i) {
                this.inventory.armor[i] = null;
            }
        }

        this.closeInventory();
        this.setSpectatorTarget(this); // Remove spectated target
        // CraftBukkit end

        // CraftBukkit - Get our scores instead
        Collection collection = this.world.getServer().getScoreboardManager().getScoreboardScores(IScoreboardCriteria.d, this.getName(), new java.util.ArrayList<ScoreboardScore>());
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreboardScore scoreboardscore = (ScoreboardScore) iterator.next(); // CraftBukkit - Use our scores instead

            scoreboardscore.incrementScore();
        }

        EntityLiving entityliving = this.bt();

        if (entityliving != null) {
            EntityTypes.MonsterEggInfo entitytypes_monsteregginfo = (EntityTypes.MonsterEggInfo) EntityTypes.eggInfo.get(Integer.valueOf(EntityTypes.a(entityliving)));

            if (entitytypes_monsteregginfo != null) {
                this.b(entitytypes_monsteregginfo.e);
            }

            entityliving.b(this, this.aW);
        }

        this.b(StatisticList.y);
        this.a(StatisticList.h);
        this.bs().g();
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            boolean flag = this.server.ae() && this.cr() && "fall".equals(damagesource.translationIndex);

            if (!flag && this.invulnerableTicks > 0 && damagesource != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                if (damagesource instanceof EntityDamageSource) {
                    Entity entity = damagesource.getEntity();

                    if (entity instanceof EntityHuman && !this.a((EntityHuman) entity)) {
                        return false;
                    }

                    if (entity instanceof EntityArrow) {
                        EntityArrow entityarrow = (EntityArrow) entity;

                        if (entityarrow.shooter instanceof EntityHuman && !this.a((EntityHuman) entityarrow.shooter)) {
                            return false;
                        }
                    }
                }

                return super.damageEntity(damagesource, f);
            }
        }
    }

    public boolean a(EntityHuman entityhuman) {
        return !this.cr() ? false : super.a(entityhuman);
    }

    private boolean cr() {
        // CraftBukkit - this.server.getPvP() -> this.world.pvpMode
        return this.world.pvpMode;
    }

    public void c(int i) {
        // PaperSpigot start - Allow configurable end portal credits
        boolean endPortal = this.dimension == 1 && i == 1;
        if (endPortal) {
            this.b((Statistic) AchievementList.D);
            if (!world.paperSpigotConfig.disableEndCredits) {
                this.world.kill(this);
                this.viewingCredits = true;
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(4, 0.0F));
            }
        // PaperSpigot end
        } else {
            if (this.dimension == 0 && i == 1) {
                this.b((Statistic) AchievementList.C);
                // CraftBukkit start - Rely on custom portal management
                /*
                BlockPosition blockposition = this.server.getWorldServer(i).getDimensionSpawn();

                if (blockposition != null) {
                    this.playerConnection.a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 0.0F, 0.0F);
                }

                i = 1;
                */
                // CraftBukkit end
            } else {
                this.b((Statistic) AchievementList.y);
            }
        }

        // PaperSpigot start - Allow configurable end portal credits
        if (!endPortal || world.paperSpigotConfig.disableEndCredits) {
            // CraftBukkit start
            TeleportCause cause = (endPortal || (this.dimension == 1 || i == 1)) ? TeleportCause.END_PORTAL : TeleportCause.NETHER_PORTAL;
            this.server.getPlayerList().changeDimension(this, i, cause);
            // CraftBukkit end
            this.lastSentExp = -1;
            this.bM = -1.0F;
            this.bN = -1;
        }
        // PaperSpigot end

    }

    public boolean a(EntityPlayer entityplayer) {
        return entityplayer.isSpectator() ? this.C() == this : (this.isSpectator() ? false : super.a(entityplayer));
    }

    private void a(TileEntity tileentity) {
        if (tileentity != null) {
            Packet packet = tileentity.getUpdatePacket();

            if (packet != null) {
                this.playerConnection.sendPacket(packet);
            }
        }

    }

    public void receive(Entity entity, int i) {
        super.receive(entity, i);
        this.activeContainer.b();
    }

    public EntityHuman.EnumBedResult a(BlockPosition blockposition) {
        EntityHuman.EnumBedResult entityhuman_enumbedresult = super.a(blockposition);

        if (entityhuman_enumbedresult == EntityHuman.EnumBedResult.OK) {
            PacketPlayOutBed packetplayoutbed = new PacketPlayOutBed(this, blockposition);

            this.u().getTracker().a((Entity) this, (Packet) packetplayoutbed);
            this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            this.playerConnection.sendPacket(packetplayoutbed);
        }

        return entityhuman_enumbedresult;
    }

    public void a(boolean flag, boolean flag1, boolean flag2) {
        if (!this.sleeping) return; // CraftBukkit - Can't leave bed if not in one!
        if (this.isSleeping()) {
            this.u().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(this, 2));
        }

        super.a(flag, flag1, flag2);
        if (this.playerConnection != null) {
            this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        }

    }

    public void mount(Entity entity) {
        Entity entity1 = this.vehicle;

        super.mount(entity);
        if (this.vehicle != entity1) { // CraftBukkit
            this.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, this, this.vehicle));
            this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        }

    }

    protected void a(double d0, boolean flag, Block block, BlockPosition blockposition) {}

    public void a(double d0, boolean flag) {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.locY - 0.20000000298023224D);
        int k = MathHelper.floor(this.locZ);
        BlockPosition blockposition = new BlockPosition(i, j, k);
        Block block = this.world.getType(blockposition).getBlock();

        if (block.getMaterial() == Material.AIR) {
            Block block1 = this.world.getType(blockposition.down()).getBlock();

            if (block1 instanceof BlockFence || block1 instanceof BlockCobbleWall || block1 instanceof BlockFenceGate) {
                blockposition = blockposition.down();
                block = this.world.getType(blockposition).getBlock();
            }
        }

        super.a(d0, flag, block, blockposition);
    }

    public void openSign(TileEntitySign tileentitysign) {
        tileentitysign.a((EntityHuman) this);
        this.playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(tileentitysign.getPosition()));
    }

    public int nextContainerCounter() { // CraftBukkit - private void -> public int
        this.containerCounter = this.containerCounter % 100 + 1;
        return containerCounter; // CraftBukkit
    }

    public void openTileEntity(ITileEntityContainer itileentitycontainer) {
        // CraftBukkit start - Inventory open hook
        Container container = CraftEventFactory.callInventoryOpenEvent(this, itileentitycontainer.createContainer(this.inventory, this));
        if (container == null) {
            return;
        }
        // CraftBukkit end
        this.nextContainerCounter();
        this.playerConnection.sendPacket(new PacketPlayOutOpenWindow(this.containerCounter, itileentitycontainer.getContainerName(), itileentitycontainer.getScoreboardDisplayName()));
        this.activeContainer = container; // CraftBukkit
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openContainer(IInventory iinventory) {
        // CraftBukkit start - Inventory open hook
        // Copied from below
        boolean cancelled = false;
        if (iinventory instanceof ITileInventory) {
            ITileInventory itileinventory = (ITileInventory) iinventory;
            cancelled = itileinventory.r_() && !this.a(itileinventory.i()) && !this.isSpectator();
        }

        Container container;
        if (iinventory instanceof ITileEntityContainer) {
            container = ((ITileEntityContainer)iinventory).createContainer(this.inventory, this);
        } else {
            container = new ContainerChest(this.inventory, iinventory, this);
        }
        container = CraftEventFactory.callInventoryOpenEvent(this, container, cancelled);
        if (container == null && !cancelled) { // Let pre-cancelled events fall through
            iinventory.closeContainer(this);
            return;
        }
        // CraftBukkit end
        if (this.activeContainer != this.defaultContainer) {
            this.closeInventory();
        }

        if (iinventory instanceof ITileInventory) {
            ITileInventory itileinventory = (ITileInventory) iinventory;

            if (itileinventory.r_() && !this.a(itileinventory.i()) && !this.isSpectator() && container == null) { // CraftBukkit - allow plugins to uncancel the lock
                this.playerConnection.sendPacket(new PacketPlayOutChat(new ChatMessage("container.isLocked", new Object[] { iinventory.getScoreboardDisplayName()}), (byte) 2));
                this.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect("random.door_close", this.locX, this.locY, this.locZ, 1.0F, 1.0F));

                iinventory.closeContainer(this); // CraftBukkit
                return;
            }
        }

        this.nextContainerCounter();
        if (iinventory instanceof ITileEntityContainer) {
            this.playerConnection.sendPacket(new PacketPlayOutOpenWindow(this.containerCounter, ((ITileEntityContainer) iinventory).getContainerName(), iinventory.getScoreboardDisplayName(), iinventory.getSize()));
            this.activeContainer = container; // CraftBukkit
        } else {
            this.playerConnection.sendPacket(new PacketPlayOutOpenWindow(this.containerCounter, "minecraft:container", iinventory.getScoreboardDisplayName(), iinventory.getSize()));
            this.activeContainer = container; // CraftBukkit
        }

        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openTrade(IMerchant imerchant) {
        // CraftBukkit start - Inventory open hook
        Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerMerchant(this.inventory, imerchant, this.world));
        if (container == null) {
            return;
        }
        // CraftBukkit end
        this.nextContainerCounter();
        this.activeContainer = container; // CraftBukkit
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
        InventoryMerchant inventorymerchant = ((ContainerMerchant) this.activeContainer).e();
        IChatBaseComponent ichatbasecomponent = imerchant.getScoreboardDisplayName();

        this.playerConnection.sendPacket(new PacketPlayOutOpenWindow(this.containerCounter, "minecraft:villager", ichatbasecomponent, inventorymerchant.getSize()));
        MerchantRecipeList merchantrecipelist = imerchant.getOffers(this);

        if (merchantrecipelist != null) {
            PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

            packetdataserializer.writeInt(this.containerCounter);
            merchantrecipelist.a(packetdataserializer);
            this.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|TrList", packetdataserializer));
        }

    }

    public void openHorseInventory(EntityHorse entityhorse, IInventory iinventory) {
        // CraftBukkit start - Inventory open hook
        Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerHorse(this.inventory, iinventory, entityhorse, this));
        if (container == null) {
            iinventory.closeContainer(this);
            return;
        }
        // CraftBukkit end
        if (this.activeContainer != this.defaultContainer) {
            this.closeInventory();
        }

        this.nextContainerCounter();
        this.playerConnection.sendPacket(new PacketPlayOutOpenWindow(this.containerCounter, "EntityHorse", iinventory.getScoreboardDisplayName(), iinventory.getSize(), entityhorse.getId()));
        this.activeContainer = container;
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openBook(ItemStack itemstack) {
        Item item = itemstack.getItem();

        if (item == Items.WRITTEN_BOOK) {
            this.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(Unpooled.buffer())));
        }

    }

    public void a(Container container, int i, ItemStack itemstack) {
        if (!(container.getSlot(i) instanceof SlotResult)) {
            if (!this.g) {
                this.playerConnection.sendPacket(new PacketPlayOutSetSlot(container.windowId, i, itemstack));
            }
        }
    }

    public void updateInventory(Container container) {
        this.a(container, container.a());
    }

    public void a(Container container, List<ItemStack> list) {
        this.playerConnection.sendPacket(new PacketPlayOutWindowItems(container.windowId, list));
        this.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.inventory.getCarried()));
        // CraftBukkit start - Send a Set Slot to update the crafting result slot
        if (java.util.EnumSet.of(InventoryType.CRAFTING,InventoryType.WORKBENCH).contains(container.getBukkitView().getType())) {
            this.playerConnection.sendPacket(new PacketPlayOutSetSlot(container.windowId, 0, container.getSlot(0).getItem()));
        }
        // CraftBukkit end
    }

    public void setContainerData(Container container, int i, int j) {
        this.playerConnection.sendPacket(new PacketPlayOutWindowData(container.windowId, i, j));
    }

    public void setContainerData(Container container, IInventory iinventory) {
        for (int i = 0; i < iinventory.g(); ++i) {
            this.playerConnection.sendPacket(new PacketPlayOutWindowData(container.windowId, i, iinventory.getProperty(i)));
        }

    }

    public void closeInventory() {
        CraftEventFactory.handleInventoryCloseEvent(this); // CraftBukkit
        this.playerConnection.sendPacket(new PacketPlayOutCloseWindow(this.activeContainer.windowId));
        this.p();
    }

    public void broadcastCarriedItem() {
        if (!this.g) {
            this.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.inventory.getCarried()));
        }
    }

    public void p() {
        this.activeContainer.b((EntityHuman) this);
        this.activeContainer = this.defaultContainer;
    }

    public void a(float f, float f1, boolean flag, boolean flag1) {
        if (this.vehicle != null) {
            if (f >= -1.0F && f <= 1.0F) {
                this.aZ = f;
            }

            if (f1 >= -1.0F && f1 <= 1.0F) {
                this.ba = f1;
            }

            this.aY = flag;
            this.setSneaking(flag1);
        }

    }

    public void a(Statistic statistic, int i) {
        if (statistic != null) {
            this.bK.b(this, statistic, i);
            Iterator iterator = this.getScoreboard().getObjectivesForCriteria(statistic.k()).iterator();

            while (iterator.hasNext()) {
                ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

                this.getScoreboard().getPlayerScoreForObjective(this.getName(), scoreboardobjective).addScore(i);
            }

            if (this.bK.e()) {
                this.bK.a(this);
            }

        }
    }

    public void a(Statistic statistic) {
        if (statistic != null) {
            this.bK.setStatistic(this, statistic, 0);
            Iterator iterator = this.getScoreboard().getObjectivesForCriteria(statistic.k()).iterator();

            while (iterator.hasNext()) {
                ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

                this.getScoreboard().getPlayerScoreForObjective(this.getName(), scoreboardobjective).setScore(0);
            }

            if (this.bK.e()) {
                this.bK.a(this);
            }

        }
    }

    public void q() {
        if (this.passenger != null) {
            this.passenger.mount(this);
        }

        if (this.sleeping) {
            this.a(true, false, false);
        }

    }

    public void triggerHealthUpdate() {
        this.bM = -1.0E8F;
        this.lastSentExp = -1; // CraftBukkit - Added to reset
    }

    // CraftBukkit start - Support multi-line messages
    public void sendMessage(IChatBaseComponent[] ichatbasecomponent) {
        for (IChatBaseComponent component : ichatbasecomponent) {
            this.sendMessage(component);
        }
    }
    // CraftBukkit end

    public void b(IChatBaseComponent ichatbasecomponent) {
        this.playerConnection.sendPacket(new PacketPlayOutChat(ichatbasecomponent));
    }

    protected void s() {
        this.playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte) 9));
        super.s();
    }

    public void a(ItemStack itemstack, int i) {
        super.a(itemstack, i);
        if (itemstack != null && itemstack.getItem() != null && itemstack.getItem().e(itemstack) == EnumAnimation.EAT) {
            this.u().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(this, 3));
        }

    }

    public void copyTo(EntityHuman entityhuman, boolean flag) {
        super.copyTo(entityhuman, flag);
        this.lastSentExp = -1;
        this.bM = -1.0F;
        this.bN = -1;
        this.removeQueue.addAll(((EntityPlayer) entityhuman).removeQueue);
    }

    protected void a(MobEffect mobeffect) {
        super.a(mobeffect);
        this.playerConnection.sendPacket(new PacketPlayOutEntityEffect(this.getId(), mobeffect));
    }

    protected void a(MobEffect mobeffect, boolean flag) {
        super.a(mobeffect, flag);
        this.playerConnection.sendPacket(new PacketPlayOutEntityEffect(this.getId(), mobeffect));
    }

    protected void b(MobEffect mobeffect) {
        super.b(mobeffect);
        this.playerConnection.sendPacket(new PacketPlayOutRemoveEntityEffect(this.getId(), mobeffect));
    }

    public void enderTeleportTo(double d0, double d1, double d2) {
        this.playerConnection.a(d0, d1, d2, this.yaw, this.pitch);
    }

    public void b(Entity entity) {
        this.u().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(entity, 4));
    }

    public void c(Entity entity) {
        this.u().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(entity, 5));
    }

    public void updateAbilities() {
        if (this.playerConnection != null) {
            this.playerConnection.sendPacket(new PacketPlayOutAbilities(this.abilities));
            this.B();
        }
    }

    public WorldServer u() {
        return (WorldServer) this.world;
    }

    public void a(WorldSettings.EnumGamemode worldsettings_enumgamemode) {
        getBukkitEntity().setGameMode(org.bukkit.GameMode.getByValue(worldsettings_enumgamemode.getId()));
        /* CraftBukkit start - defer to our setGameMode
        this.playerInteractManager.setGameMode(worldsettings_enumgamemode);
        this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(3, (float) worldsettings_enumgamemode.getId()));
        if (worldsettings_enumgamemode == WorldSettings.EnumGamemode.SPECTATOR) {
            this.mount((Entity) null);
        } else {
            this.setSpectatorTarget(this);
        }

        this.updateAbilities();
        this.bP();
        // CraftBukkit end */
    }

    public boolean isSpectator() {
        return this.playerInteractManager.getGameMode() == WorldSettings.EnumGamemode.SPECTATOR;
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        this.playerConnection.sendPacket(new PacketPlayOutChat(ichatbasecomponent));
    }

    public boolean a(int i, String s) {
        /* CraftBukkit start
        if ("seed".equals(s) && !this.server.ad()) {
            return true;
        } else if (!"tell".equals(s) && !"help".equals(s) && !"me".equals(s) && !"trigger".equals(s)) {
            if (this.server.getPlayerList().isOp(this.getProfile())) {
                OpListEntry oplistentry = (OpListEntry) this.server.getPlayerList().getOPs().get(this.getProfile());

                return oplistentry != null ? oplistentry.a() >= i : this.server.p() >= i;
            } else {
                return false;
            }
        } else {
            return true;
        }
        */
        if ("@".equals(s)) {
            return getBukkitEntity().hasPermission("minecraft.command.selector");
        }
        return true;
        // CraftBukkit end
    }

    public String w() {
        String s = this.playerConnection.networkManager.getSocketAddress().toString();

        s = s.substring(s.indexOf("/") + 1);
        s = s.substring(0, s.indexOf(":"));
        return s;
    }

    public void a(PacketPlayInSettings packetplayinsettings) {
        // PaperSpigot start - Add PlayerLocaleChangeEvent
        String oldLocale = this.locale;
        this.locale = packetplayinsettings.a();
        if (!this.locale.equals(oldLocale)) {
            CraftEventFactory.callPlayerLocaleChangeEvent(this, oldLocale, this.locale);
        }
        // PaperSpigot end
        this.bR = packetplayinsettings.c();
        this.bS = packetplayinsettings.d();
        this.getDataWatcher().watch(10, Byte.valueOf((byte) packetplayinsettings.e()));
    }

    public EntityHuman.EnumChatVisibility getChatFlags() {
        return this.bR;
    }

    public void setResourcePack(String s, String s1) {
        this.playerConnection.sendPacket(new PacketPlayOutResourcePackSend(s, s1));
    }

    public BlockPosition getChunkCoordinates() {
        return new BlockPosition(this.locX, this.locY + 0.5D, this.locZ);
    }

    public void resetIdleTimer() {
        this.bT = MinecraftServer.az();
    }

    public ServerStatisticManager getStatisticManager() {
        return this.bK;
    }

    public void d(Entity entity) {
        if (entity instanceof EntityHuman) {
            this.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(new int[] { entity.getId()}));
        } else {
            this.removeQueue.add(Integer.valueOf(entity.getId()));
        }

    }

    protected void B() {
        if (this.isSpectator()) {
            this.bj();
            this.setInvisible(true);
        } else {
            super.B();
        }

        this.u().getTracker().a(this);
    }

    public Entity C() {
        return (Entity) (this.bU == null ? this : this.bU);
    }

    public void setSpectatorTarget(Entity entity) {
        Entity entity1 = this.C();

        this.bU = (Entity) (entity == null ? this : entity);
        if (entity1 != this.bU) {
            this.playerConnection.sendPacket(new PacketPlayOutCamera(this.bU));
            this.enderTeleportTo(this.bU.locX, this.bU.locY, this.bU.locZ);
        }

    }

    public void attack(Entity entity) {
        if (this.playerInteractManager.getGameMode() == WorldSettings.EnumGamemode.SPECTATOR) {
            this.setSpectatorTarget(entity);
        } else {
            super.attack(entity);
        }

    }

    public long D() {
        return this.bT;
    }

    public IChatBaseComponent getPlayerListName() {
        return listName; // CraftBukkit
    }

    // CraftBukkit start - Add per-player time and weather.
    public long timeOffset = 0;
    public boolean relativeTime = true;

    public long getPlayerTime() {
        if (this.relativeTime) {
            // Adds timeOffset to the current server time.
            return this.world.getDayTime() + this.timeOffset;
        } else {
            // Adds timeOffset to the beginning of this day.
            return this.world.getDayTime() - (this.world.getDayTime() % 24000) + this.timeOffset;
        }
    }

    public WeatherType weather = null;

    public WeatherType getPlayerWeather() {
        return this.weather;
    }

    public void setPlayerWeather(WeatherType type, boolean plugin) {
        if (!plugin && this.weather != null) {
            return;
        }

        if (plugin) {
            this.weather = type;
        }

        if (type == WeatherType.DOWNFALL) {
            this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(2, 0));
        } else {
            this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(1, 0));
        }
    }

    private float pluginRainPosition;
    private float pluginRainPositionPrevious;

    public void updateWeather(float oldRain, float newRain, float oldThunder, float newThunder) {
        if (this.weather == null) {
            // Vanilla
            if (oldRain != newRain) {
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, newRain));
            }
        } else {
            // Plugin
            if (pluginRainPositionPrevious != pluginRainPosition) {
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, pluginRainPosition));
            }
        }

        if (oldThunder != newThunder) {
            if (weather == WeatherType.DOWNFALL || weather == null) {
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, newThunder));
            } else {
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, 0));
            }
        }
    }

    public void tickWeather() {
        if (this.weather == null) return;

        pluginRainPositionPrevious = pluginRainPosition;
        if (weather == WeatherType.DOWNFALL) {
            pluginRainPosition += 0.01;
        } else {
            pluginRainPosition -= 0.01;
        }

        pluginRainPosition = MathHelper.a(pluginRainPosition, 0.0F, 1.0F);
    }

    public void resetPlayerWeather() {
        this.weather = null;
        this.setPlayerWeather(this.world.getWorldData().hasStorm() ? WeatherType.DOWNFALL : WeatherType.CLEAR, false);
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.getName() + " at " + this.locX + "," + this.locY + "," + this.locZ + ")";
    }

    public void reset() {
        float exp = 0;
        boolean keepInventory = this.world.getGameRules().getBoolean("keepInventory");

        if (this.keepLevel || keepInventory) {
            exp = this.exp;
            this.newTotalExp = this.expTotal;
            this.newLevel = this.expLevel;
        }

        this.setHealth(this.getMaxHealth());
        this.fireTicks = 0;
        this.fallDistance = 0;
        this.foodData = new FoodMetaData(this);
        this.expLevel = this.newLevel;
        this.expTotal = this.newTotalExp;
        this.exp = 0;
        this.deathTicks = 0;
        this.removeAllEffects();
        this.updateEffects = true;
        this.activeContainer = this.defaultContainer;
        this.killer = null;
        this.lastDamager = null;
        this.combatTracker = new CombatTracker(this);
        this.lastSentExp = -1;
        if (this.keepLevel || keepInventory) {
            this.exp = exp;
        } else {
            this.giveExp(this.newExp);
        }
        this.keepLevel = false;
    }

    @Override
    public CraftPlayer getBukkitEntity() {
        return (CraftPlayer) super.getBukkitEntity();
    }
    // CraftBukkit end
}
