# NachoSpigot
A **maintained** version of NachoSpigot where some features have been removed or added to make sure the server is stable and works with most plugins but is also optimized.
I am planning to support Java 15 but still support most plugins like ProtocolLib. Is this going to work out? We will see in the end.

## Current State
Java 15 is now natively supported, and ProtocolLib, Citizens and ViaVersion are patched at runtime to work with Nacho's patches.
Unless other bugs are found, Nacho should now run stable.

**NachoSpigot supports Java 8 to Java 15!**

**Download:** [Click here](https://nightly.link/Sculas/NachoSpigot/workflows/build-nachospigot/master/NachoSpigot%20server%20JAR.zip)

I will maintain this version, because the official NachoSpigot is no longer maintained.
If you know any patches that will help out, please create an issue or better create a PR so I can merge it.

#### Building / Compiling
To build, clone the repo, and run `mvn clean package` in the NachoSpigot directory.
*You need Java 8-15 and Maven to compile.*

#### Building / Compiling
To build, clone the repo, and run `mvn clean package` in the NachoSpigot directory.
*You need Java 8-15 and Maven to compile.*

## Backported Patches
```
[Spigot-0097] Remove DataWatcher Locking by spottedleaf
[Spigot-0138] Branchless NibbleArray by md5
[Spigot-2380] Hitting in the air will always load the chunk at 0,0 by md_5

[Paper-0033] Optimize explosions
[Paper-0044] Use UserCache for player heads
[Paper-0072] Fix Furnace cook time bug when lagging by Aikar
[Paper-0076] Optimized Light Level Comparisons by Aikar
[Paper-0083] Waving banner workaround by Gabscap
[Paper-0085] Use a Shared Random for Entities by Aikar
[Paper-0097] Don't save empty scoreboard teams to scoreboard.dat by Aikar
[Paper-0100] Avoid blocking on Network Manager creation by Aikar
[Paper-0102] Faster redstone torch rapid clock removal by Martin Panzer.
[Paper-0112] Reduce IO ops opening a new region file by Antony Riley
[Paper-0122] Don't let fishinghooks use portals by Zach Brown
[Paper-0125] Optimize World.isLoaded(BlockPosition)Z by Aikar
[Paper-0125] Improve Maps (in item frames) performance and bug fixes by Aikar
[Paper-0141] Do not let armorstands drown
[Paper-0144] Improve Minecraft Hopper Performance by  Aikar
[Paper-0152] Disable ticking of snow blocks by killme
[Paper-0164] [MC-117075] TE Unload Lag Spike by mezz
[Paper-0168] Cache user authenticator threads by vemacs
[Paper-0207] Shame on you Mojang moves chunk loading off https thread by Aikar
[Paper-0249] Improve BlockPosition inlining by Techcable
[Paper-0254] Don't blindly send unlit chunks when lighting updates are allowed by Shane Freeder
[Paper-0266] [MC-99321] Dont check for blocked double chest for hoppers
[Paper-0302] Don't load chunks for villager door checks by Aikar
[Paper-0313] Optimize World Time Updates by Aikar
[Paper-0321] Cleanup allocated favicon ByteBuf by Shane Freeder
[Paper-0342] Always process chunk removal in removeEntity by Aikar 2018
[Paper-0344] [MC-111480] Start Entity ID's at 1
[Paper-0346] [MC-135506] Experience should save as Integers
[Paper-0347] don't go below 0 for pickupDelay, breaks picking up items by Aikar
[Paper-0350] start - use a Queue for Queueing Commands by Aikar
[Paper-0352] Optimize BlockPosition helper methods by Spottedleaf
[Paper-0353] Send nearby packets from world player list not server list by Mystiflow
[Paper-0389] performance improvement for Chunk.getEntities by wea_ondara
[Paper-0539] Optimize NetworkManager Exception Handling by Andrew Steinborn
[Paper-0451] Reduce memory footprint of NBTTagCompound by spottedleaf

[Nacho-0001] Remove stream usage when counting entities
[Nacho-0002] Check if the fuel is coal first before checking others
[Nacho-0003] Disable Snooper
[Nacho-0004] Do not repeatily allocate EnumDirection
[Nacho-0005] Do not reallocate enums via values
[Nacho-0006] Use Caffeine instead of Guava for player heads
[Nacho-0007] Add timings for packets
[Nacho-0008] Upgrade Netty version to 4.1.50 and support java 14
[Nacho-0009] Remove an extra file io call within world credit bob7l
[Nacho-0010] Use jchambers' FAST UUID methods
[Nacho-0011] Optimize weather update loops
[Nacho-0012] Don't load chunks for physics
[Nacho-0013] Use less resources for collisions
[Nacho-0014] stop timings crashing the server but still print the error
[Nacho-0015] Remove the usage of BlockPosition from getCubes
[Nacho-0016] faster getHighestBlockYAt function
[Nacho-0017] tiny winy optimization for async lighting
[Nacho-0018] more tiny winy optimization to lighting
[Nacho-0019] Avoid lock every packet send 
[Nacho-0020] Packet Listener Api
[Nacho-0021] Add setMaxPlayers within Bukkit.getServer() and SetMaxSlot Command
[Nacho-0022] Stop raytracing loading chunks
[Nacho-0023] Optimize EntityTracker for the chunk updater
[Nacho-0024] Do not create new BlockPosition when loading chunk
[Nacho-0025] Disable random tickSpeed being modified (Every call it had to convert String into int via a string key which is costly)
[Nacho-0026] Optimize packet Split by Velocity
[Nacho-0027] Netty IP_TOS 0x18
[Nacho-0028] only fire InventoryCloseEvent if inventory is open
[Nacho-0029] add leash api
[Nacho-0030] add a ChunkPreLoadEvent
[Nacho-0031] remove unused vars
[Nacho-0033] Faster Operator search method
[Nacho-0034] Remove Java 8 message from TacoSpigot which made it so you couldn't run Java 8 or higher
[Nacho-0035] Made it so you can switch the brand name in nacho.json
[Nacho-0036] Add toggles for commands "reload", "version" and "plugins"
[Nacho-0037] Add toggle for "Faster Operator"
[Nacho-0038] Fix weather desync
[Nacho-0039] Fixed a bug in Netty's epoll when using Windows
[Nacho-0040] Change deprecated Netty parameter in ResourceLeakDetector
[Nacho-0041] Fix block placement
[Nacho-0042] Remove Spigot Watchdog
[Nacho-0043] Fix ProtocolLib
[Nacho-0044] Fix Citizens
[Nacho-0045] Async obfuscation

[YAPFA-0030] Don't save Fireworks and Arrows by tr7zw (Arrows and firework Entities, eg stuck arrows in the ground)

[Akarin-0001] Avoid double I/O operation on load player file by tsao chi

[Tuinity-????] Skip updating entity tracker without players
[Tuinity-0017] Allow controlled flushing for network manager by Spottedleaf
[Tuinity-0018] Consolidate flush calls for entity tracker packets

[SportPaper-0043] get blocks in chunk api
[SportPaper-0162] Fix PlayerInteractEvent not cancelling properly
```

## Removed
``` 
[Nacho-0022] Sync is maintained higher up and is causing issues
```

## Discord
[Join the Discord server!](https://discord.gg/SBTEbSx)

## Supporters

[Henry Lanier](youtube.com/channel/UCYKmXuEo1ZVMzvsWwaBeBwQ) (MILDJELLYo#9091)

[Lucas (aka Sculas)](https://sculas.xyz/)