package com.bukkit.gemo.BukkitHTTP;

import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public class HTTPCore extends JavaPlugin {
    private static String pluginName = "BukkitHTTP";
    public static Server server;

    public static HttpServer httpServer = null;
    public static HTTPHandler httpHandler = null;

    // ////////////////////////////////
    //
    // PRINT IN CONSOLE
    //
    // ////////////////////////////////
    public static void printInConsole(String str) {
        System.out.println("[" + pluginName + "] " + str);
    }

    // ////////////////////////////////
    //
    // ON DISABLE
    //
    // ////////////////////////////////
    @Override
    public void onDisable() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
        System.out.println(pluginName + " disabled");
    }

    // ////////////////////////////////
    //
    // ON ENABLE
    //
    // ////////////////////////////////
    @Override
    public void onEnable() {
        HTTPCore.server = getServer();
        // CREATE WEBSERVER
        try {
            httpHandler = new HTTPHandler();
            InetSocketAddress addr = new InetSocketAddress(8000);
            httpServer = HttpServer.create(addr, 0);
            httpServer.createContext("/", httpHandler);
            httpServer.setExecutor(Executors.newCachedThreadPool());
            httpServer.start();
            printInConsole("HTTP-Server started on port 8000.");
        } catch (Exception e) {
            printInConsole("SERVER NOT STARTED!");
        }
    }

    // ////////////////////////////////
    //
    // REGISTER PLUGIN
    //
    // ////////////////////////////////
    public HTTPPlugin registerPlugin(String rootAlias, String pluginName, String root, boolean useAuth) {
        HTTPPlugin plugin = new HTTPPlugin(rootAlias, pluginName, root, useAuth);

        if (registerPlugin(plugin)) {
            return plugin;
        } else {
            return null;
        }
    }

    // ////////////////////////////////
    //
    // REGISTER PLUGIN
    //
    // ////////////////////////////////
    public boolean registerPlugin(HTTPPlugin plugin) {
        if (!httpHandler.registeredPlugins.containsKey(plugin.getRootAlias())) {
            httpHandler.registeredPlugins.put(plugin.getRootAlias(), plugin);
            if (httpHandler.registeredPlugins.containsKey(plugin.getRootAlias())) {
                printInConsole("Virtual folder '" + plugin.getRootAlias() + "' is now registered by '" + plugin.getPluginName() + "'!");
            } else {
                printInConsole("Error while registering '" + plugin.getRootAlias() + "'!");
            }
            return httpHandler.registeredPlugins.containsKey(plugin.getRootAlias());
        } else {
            printInConsole("Virtual folder '" + plugin.getRootAlias() + "' is already registered by '" + httpHandler.registeredPlugins.get(plugin.getRootAlias()).getPluginName() + "'!");
            return false;
        }
    }
}
