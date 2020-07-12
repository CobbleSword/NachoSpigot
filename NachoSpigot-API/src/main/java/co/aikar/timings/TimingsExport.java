/*
 * This file is licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014 Daniel Ennis <http://aikar.co>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package co.aikar.timings;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

import static co.aikar.timings.TimingsManager.HISTORY;
import static co.aikar.util.JSONUtil.*;

@SuppressWarnings({"rawtypes", "SuppressionAnnotation"})
class TimingsExport extends Thread {

    private final CommandSender sender;
    private final Map out;
    private final TimingHistory[] history;

    TimingsExport(CommandSender sender, Map out, TimingHistory[] history) {
        super("Timings paste thread");
        this.sender = sender;
        this.out = out;
        this.history = history;
    }


    /**
     * Builds an XML report of the timings to be uploaded for parsing.
     *
     * @param sender Who to report to
     */
    static void reportTimings(CommandSender sender) {
        Map parent = createObject(
            // Get some basic system details about the server
            pair("version", Bukkit.getVersion()),
            pair("maxplayers", Bukkit.getMaxPlayers()),
            pair("start", TimingsManager.timingStart / 1000),
            pair("end", System.currentTimeMillis() / 1000),
            pair("sampletime", (System.currentTimeMillis() - TimingsManager.timingStart) / 1000)
        );
        if (!TimingsManager.privacy) {
            appendObjectData(parent,
                pair("server", Bukkit.getServerName()),
                pair("motd", Bukkit.getServer().getMotd()),
                pair("online-mode", Bukkit.getServer().getOnlineMode()),
                pair("icon", Bukkit.getServer().getServerIcon().getData())
            );
        }

        final Runtime runtime = Runtime.getRuntime();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

        parent.put("system", createObject(
                pair("timingcost", getCost()),
                pair("name", System.getProperty("os.name")),
                pair("version", System.getProperty("os.version")),
                pair("jvmversion", System.getProperty("java.version")),
                pair("arch", System.getProperty("os.arch")),
                pair("maxmem", runtime.maxMemory()),
                pair("cpu", runtime.availableProcessors()),
                pair("runtime", ManagementFactory.getRuntimeMXBean().getUptime()),
                pair("flags", StringUtils.join(runtimeBean.getInputArguments(), " ")),
                pair("gc", toObjectMapper(ManagementFactory.getGarbageCollectorMXBeans(), new Function<GarbageCollectorMXBean, JSONPair>() {
                    @Override
                    public JSONPair apply(GarbageCollectorMXBean input) {
                        return pair(input.getName(), toArray(input.getCollectionCount(), input.getCollectionTime()));
                    }
                }))
            )
        );

        Set<Material> tileEntityTypeSet = Sets.newHashSet();
        Set<EntityType> entityTypeSet = Sets.newHashSet();

        int size = HISTORY.size();
        TimingHistory[] history = new TimingHistory[size + 1];
        int i = 0;
        for (TimingHistory timingHistory : HISTORY) {
            tileEntityTypeSet.addAll(timingHistory.tileEntityTypeSet);
            entityTypeSet.addAll(timingHistory.entityTypeSet);
            history[i++] = timingHistory;
        }

        history[i] = new TimingHistory(); // Current snapshot
        tileEntityTypeSet.addAll(history[i].tileEntityTypeSet);
        entityTypeSet.addAll(history[i].entityTypeSet);


        Map handlers = createObject();
        for (TimingIdentifier.TimingGroup group : TimingIdentifier.GROUP_MAP.values()) {
            for (TimingHandler id : group.handlers) {
                if (!id.timed && !id.isSpecial()) {
                    continue;
                }
                handlers.put(id.id, toArray(
                    group.id,
                    id.name
                ));
            }
        }

        parent.put("idmap", createObject(
            pair("groups", toObjectMapper(
                TimingIdentifier.GROUP_MAP.values(), new Function<TimingIdentifier.TimingGroup, JSONPair>() {
                @Override
                public JSONPair apply(TimingIdentifier.TimingGroup group) {
                    return pair(group.id, group.name);
                }
            })),
            pair("handlers", handlers),
            pair("worlds", toObjectMapper(TimingHistory.worldMap.entrySet(), new Function<Map.Entry<String, Integer>, JSONPair>() {
                    @Override
                    public JSONPair apply(Map.Entry<String, Integer> input) {
                        return pair(input.getValue(), input.getKey());
                    }
                })),
            pair("tileentity",
                toObjectMapper(tileEntityTypeSet, new Function<Material, JSONPair>() {
                    @Override
                    public JSONPair apply(Material input) {
                        return pair(input.getId(), input.name());
                    }
                })),
            pair("entity",
                toObjectMapper(entityTypeSet, new Function<EntityType, JSONPair>() {
                    @Override
                    public JSONPair apply(EntityType input) {
                        return pair(input.getTypeId(), input.name());
                    }
                }))
        ));

        // Information about loaded plugins

        parent.put("plugins", toObjectMapper(Bukkit.getPluginManager().getPlugins(),
            new Function<Plugin, JSONPair>() {
                @Override
                public JSONPair apply(Plugin plugin) {
                    return pair(plugin.getName(), createObject(
                        pair("version", plugin.getDescription().getVersion()),
                        pair("description", String.valueOf(plugin.getDescription().getDescription()).trim()),
                        pair("website", plugin.getDescription().getWebsite()),
                        pair("authors", StringUtils.join(plugin.getDescription().getAuthors(), ", "))
                    ));
                }
            }));



        // Information on the users Config

        parent.put("config", createObject(
            pair("spigot", mapAsJSON(Bukkit.spigot().getSpigotConfig(), null)),
            pair("bukkit", mapAsJSON(Bukkit.spigot().getBukkitConfig(), null)),
            pair("paperspigot", mapAsJSON(Bukkit.spigot().getPaperSpigotConfig(), null))
        ));

        new TimingsExport(sender, parent, history).start();
    }

    static long getCost() {
        // Benchmark the users System.nanotime() for cost basis
        int passes = 100;
        TimingHandler SAMPLER1 = Timings.ofSafe("Timings Sampler 1");
        TimingHandler SAMPLER2 = Timings.ofSafe("Timings Sampler 2");
        TimingHandler SAMPLER3 = Timings.ofSafe("Timings Sampler 3");
        TimingHandler SAMPLER4 = Timings.ofSafe("Timings Sampler 4");
        TimingHandler SAMPLER5 = Timings.ofSafe("Timings Sampler 5");
        TimingHandler SAMPLER6 = Timings.ofSafe("Timings Sampler 6");

        long start = System.nanoTime();
        for (int i = 0; i < passes; i++) {
            SAMPLER1.startTiming();
            SAMPLER2.startTiming();
            SAMPLER3.startTiming();
            SAMPLER3.stopTiming();
            SAMPLER4.startTiming();
            SAMPLER5.startTiming();
            SAMPLER6.startTiming();
            SAMPLER6.stopTiming();
            SAMPLER5.stopTiming();
            SAMPLER4.stopTiming();
            SAMPLER2.stopTiming();
            SAMPLER1.stopTiming();
        }
        long timingsCost = (System.nanoTime() - start) / passes / 6;
        SAMPLER1.reset(true);
        SAMPLER2.reset(true);
        SAMPLER3.reset(true);
        SAMPLER4.reset(true);
        SAMPLER5.reset(true);
        SAMPLER6.reset(true);
        return timingsCost;
    }

    private static JSONObject mapAsJSON(ConfigurationSection config, String parentKey) {

        JSONObject object = new JSONObject();
        for (String key : config.getKeys(false)) {
            String fullKey = (parentKey != null ? parentKey + "." + key : key);
            if (fullKey.equals("database") || fullKey.equals("settings.bungeecord-addresses") || TimingsManager.hiddenConfigs.contains(fullKey)) {
                continue;
            }
            final Object val = config.get(key);

            object.put(key, valAsJSON(val, fullKey));
        }
        return object;
    }

    private static Object valAsJSON(Object val, final String parentKey) {
        if (!(val instanceof MemorySection)) {
            if (val instanceof List) {
                Iterable<Object> v = (Iterable<Object>) val;
                return toArrayMapper(v, new Function<Object, Object>() {
                    @Override
                    public Object apply(Object input) {
                        return valAsJSON(input, parentKey);
                    }
                });
            } else {
                return val.toString();
            }
        } else {
            return mapAsJSON((ConfigurationSection) val, parentKey);
        }
    }

    @SuppressWarnings("CallToThreadRun")
    @Override
    public synchronized void start() {
        if (sender instanceof RemoteConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "Warning: Timings report done over RCON will cause lag spikes.");
            sender.sendMessage(ChatColor.RED + "You should use " + ChatColor.YELLOW +
                "/timings report" + ChatColor.RED + " in game or console.");
            run();
        } else {
            super.start();
        }
    }

    @Override
    public void run() {
        sender.sendMessage(ChatColor.GREEN + "Preparing Timings Report...");


        out.put("data", toArrayMapper(history, new Function<TimingHistory, Object>() {
            @Override
            public Object apply(TimingHistory input) {
                return input.export();
            }
        }));


        String response = null;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://timings.aikar.co/post").openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("User-Agent", "Spigot/" + Bukkit.getServerName() + "/" + InetAddress.getLocalHost().getHostName());
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);

            OutputStream request = new GZIPOutputStream(con.getOutputStream()) {{
                this.def.setLevel(7);
            }};

            request.write(JSONValue.toJSONString(out).getBytes("UTF-8"));
            request.close();

            response = getResponse(con);

            if (con.getResponseCode() != 302) {
                sender.sendMessage(
                    ChatColor.RED + "Upload Error: " + con.getResponseCode() + ": " + con.getResponseMessage());
                sender.sendMessage(ChatColor.RED + "Check your logs for more information");
                if (response != null) {
                    Bukkit.getLogger().log(Level.SEVERE, response);
                }
                return;
            }

            String location = con.getHeaderField("Location");
            sender.sendMessage(ChatColor.GREEN + "View Timings Report: " + location);
            if (!(sender instanceof ConsoleCommandSender)) {
                Bukkit.getLogger().log(Level.INFO, "View Timings Report: " + location);
            }

            if (response != null && !response.isEmpty()) {
                Bukkit.getLogger().log(Level.INFO, "Timing Response: " + response);
            }
        } catch (IOException ex) {
            sender.sendMessage(ChatColor.RED + "Error uploading timings, check your logs for more information");
            if (response != null) {
                Bukkit.getLogger().log(Level.SEVERE, response);
            }
            Bukkit.getLogger().log(Level.SEVERE, "Could not paste timings", ex);
        }
    }

    private String getResponse(HttpURLConnection con) throws IOException {
        InputStream is = null;
        try {
            is = con.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            return bos.toString();

        } catch (IOException ex) {
            sender.sendMessage(ChatColor.RED + "Error uploading timings, check your logs for more information");
            Bukkit.getLogger().log(Level.WARNING, con.getResponseMessage(), ex);
            return null;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
