package org.paperspigot.exception;

import org.bukkit.plugin.Plugin;

import static com.google.common.base.Preconditions.checkNotNull;

public class ServerPluginException extends ServerException {
    public ServerPluginException(String message, Throwable cause, Plugin responsiblePlugin) {
        super(message, cause);
        this.responsiblePlugin = checkNotNull(responsiblePlugin, "responsiblePlugin");
    }

    public ServerPluginException(Throwable cause, Plugin responsiblePlugin) {
        super(cause);
        this.responsiblePlugin = checkNotNull(responsiblePlugin, "responsiblePlugin");
    }

    protected ServerPluginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Plugin responsiblePlugin) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.responsiblePlugin = checkNotNull(responsiblePlugin, "responsiblePlugin");
    }

    private final Plugin responsiblePlugin;

    /**
     * Gets the plugin which is directly responsible for the exception being thrown
     *
     * @return plugin which is responsible for the exception throw
     */
    public Plugin getResponsiblePlugin() {
        return responsiblePlugin;
    }
}