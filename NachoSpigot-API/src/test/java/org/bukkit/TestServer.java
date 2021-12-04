package org.bukkit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import com.google.common.collect.ImmutableMap;

public class TestServer implements InvocationHandler {
    private interface MethodHandler {
        Object handle(TestServer server, Object[] args);
    }

    private static final Map<Method, MethodHandler> methods;

    static {
        try {
            ImmutableMap.Builder<Method, MethodHandler> methodMap = ImmutableMap.builder();
            methodMap.put(
                    Server.class.getMethod("isPrimaryThread"),
                    (server, args) -> Thread.currentThread().equals(server.creatingThread)
            );
            methodMap.put(
                    Server.class.getMethod("getPluginManager"),
                    (server, args) -> server.pluginManager
            );
            methodMap.put(
                    Server.class.getMethod("getLogger"),
                    new MethodHandler() {
                        final Logger logger = Logger.getLogger(TestServer.class.getCanonicalName());

                        public Object handle(TestServer server, Object[] args) {
                            return logger;
                        }
                    }
            );
            methodMap.put(
                    Server.class.getMethod("getName"),
                    (server, args) -> TestServer.class.getSimpleName()
            );
            methodMap.put(
                    Server.class.getMethod("getVersion"),
                    (server, args) -> "Version_" + TestServer.class.getPackage().getImplementationVersion()
            );
            methodMap.put(
                    Server.class.getMethod("getBukkitVersion"),
                    (server, args) -> "BukkitVersion_" + TestServer.class.getPackage().getImplementationVersion()
            );
            // Nacho start
            methodMap.put(
                    Server.class.getMethod("versionCommandEnabled"),
                    (server, args) -> false
            );
            methodMap.put(
                    Server.class.getMethod("reloadCommandEnabled"),
                    (server, args) -> false
            );
            methodMap.put(
                    Server.class.getMethod("pluginsCommandEnabled"),
                    (server, args) -> false
            );
            methodMap.put(
                    Server.class.getMethod("getCommandMap"),
                    new MethodHandler() {
                        CommandMap map;

                        @Override
                        public Object handle(TestServer server, Object[] args) {
                            if(map == null) map = new SimpleCommandMap(getInstance());
                            return map;
                        }
                    }
            );
            // Nacho end
            methods = methodMap.build();

            TestServer server = new TestServer();
            Server instance = Proxy.getProxyClass(Server.class.getClassLoader(), Server.class).asSubclass(Server.class).getConstructor(InvocationHandler.class).newInstance(server);
            Bukkit.setServer(instance);
            server.pluginManager = new SimplePluginManager(instance, (SimpleCommandMap) instance.getCommandMap());
        } catch (Throwable t) {
            throw new Error(t);
        }
    }

    private Thread creatingThread = Thread.currentThread();
    private PluginManager pluginManager;

    private TestServer() {
    }

    public static Server getInstance() {
        return Bukkit.getServer();
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
        MethodHandler handler = methods.get(method);
        if (handler != null) {
            return handler.handle(this, args);
        }
        throw new UnsupportedOperationException(String.valueOf(method));
    }
}
