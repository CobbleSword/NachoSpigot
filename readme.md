# NachSpigot
A 1.8 fork of paper which aims to optimize, give long term support for 1.8.9 and add useful apis, 
Since both Paper and Spigot no longer support the version.

## Backported Patches
```
[Spigot-2380] Hitting in the air will always load the chunk at 0,0 by md_5

[Paper-0044] Use UserCache for player heads
[Paper-0072] Fix Furnace cook time bug when lagging by Aikar
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
[Paper-168] Cache user authenticator threads by vemacs
[Paper-0207] Shame on you Mojang moves chunk loading off https thread by Aikar
[Paper-0249] Improve BlockPosition inlining by Techcable
[Paper-0313] Optimize World Time Updates by Aikar
[Paper-0347] don't go below 0 for pickupDelay, breaks picking up items by Aikar
[Paper-0389] performance improvement for Chunk.getEntities by wea_ondara
[Paper-0539] Optimize NetworkManager Exception Handling by Andrew Steinborn

[Nacho-0001] Remove stream usage when counting entities
[Nacho-0002] Check if the fuel is coal first before checking others
[Nacho-0003] Disable Snooper

[YAPFA-0030] Don't save Fireworks and Arrows by tr7zw (Arrows and firework Entities, eg stuck arrows in the ground)
```

## Discord
[Join Discord group](https://discord.gg/SBTEbSx)
