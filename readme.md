# NachSpigot
A 1.8 fork of paper which aims to optimize, give long term support for 1.8.9 and add useful apis, 
Since both Paper and Spigot no longer support the version.

**The most up to date, optimized 1.8.9 on the market**

## Features
Since 1.8 is ~5 years old now, it has missed out on a lot of critial fixes, updates and optimizations,
So we plan on offering those much needed updates to 1.8!
By the end, we plan to support Java 14, Netty 4.1, allow higher player counts and still support a large number of plugins

## Backported Patches
```
[Spigot-2380] Hitting in the air will always load the chunk at 0,0 by md_5

[Paper-0044] Use UserCache for player heads
[Paper-0072] Fix Furnace cook time bug when lagging by Aikar
[Paper-0076] Optimized Light Level Comparisons by Aikar
[Paper-0083] Waving banner workaround by Gabscap
[Paper-0097] Don't save empty scoreboard teams to scoreboard.dat by Aikar
[Paper-0100] Avoid blocking on Network Manager creation by Aikar
[Paper-0102] Faster redstone torch rapid clock removal by Martin Panzer.
[Paper-0112] Reduce IO ops opening a new region file by Antony Riley
[Paper-0122] Don't let fishinghooks use portals by Zach Brown
[Paper-0125] Optimize World.isLoaded(BlockPosition)Z by Aikar
[Paper-0125] Improve Maps (in item frames) performance and bug fixes by Aikar
[Paper-0141] Do not let armorstands drown
[Paper-0144] Improve Minecraft Hopper Performance by  Aikar
[Paper-0164] [MC-117075] TE Unload Lag Spike by mezz
[Paper-0168] Cache user authenticator threads by vemacs
[Paper-0207] Shame on you Mojang moves chunk loading off https thread by Aikar
[Paper-0249] Improve BlockPosition inlining by Techcable
[Paper-0313] Optimize World Time Updates by Aikar
[Paper-0344] [MC-111480] Start Entity ID's at 1
[Paper-0346] [MC-135506] Experience should save as Integers
[Paper-0347] don't go below 0 for pickupDelay, breaks picking up items by Aikar
[Paper-0352] Optimize BlockPosition helper methods by Spottedleaf
[Paper-0353] Send nearby packets from world player list not server list by Mystiflow
[Paper-0389] performance improvement for Chunk.getEntities by wea_ondara
[Paper-0539] Optimize NetworkManager Exception Handling by Andrew Steinborn

[Nacho-0001] Remove stream usage when counting entities
[Nacho-0002] Check if the fuel is coal first before checking others
[Nacho-0003] Disable Snooper
[Nacho-0004] Do not repeatily allocate EnumDirection
[Nacho-0005] Do not reallocate enums via values

[YAPFA-0030] Don't save Fireworks and Arrows by tr7zw (Arrows and firework Entities, eg stuck arrows in the ground)

[Akarin-0001] Avoid double I/O operation on load player file by tsao chi
```

## Discord
[Join Discord group](https://discord.gg/SBTEbSx)
