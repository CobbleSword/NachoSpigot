package org.bukkit.craftbukkit.command;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class ServerCommandSender implements CommandSender {
    // private static PermissibleBase blockPermInst; // KigPaper - fix memory leak
    private final PermissibleBase perm;
    private net.kyori.adventure.pointer.Pointers adventure$pointers; // Paper - implement pointers

    public ServerCommandSender() {
        /* if (this instanceof CraftBlockCommandSender) {
            if (blockPermInst == null) {
                blockPermInst = new PermissibleBase(this);
            }
            this.perm = blockPermInst;
        } else {
            this.perm = new PermissibleBase(this);
        } */// KigPaper
        this.perm = new PermissibleBase(this);
    }

    public boolean isPermissionSet(String name) {
        return perm.isPermissionSet(name);
    }

    public boolean isPermissionSet(Permission perm) {
        return this.perm.isPermissionSet(perm);
    }

    public boolean hasPermission(String name) {
        return perm.hasPermission(name);
    }

    public boolean hasPermission(Permission perm) {
        return this.perm.hasPermission(perm);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return perm.addAttachment(plugin, name, value);
    }

    public PermissionAttachment addAttachment(Plugin plugin) {
        return perm.addAttachment(plugin);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return perm.addAttachment(plugin, name, value, ticks);
    }

    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return perm.addAttachment(plugin, ticks);
    }

    public void removeAttachment(PermissionAttachment attachment) {
        perm.removeAttachment(attachment);
    }

    public void recalculatePermissions() {
        perm.recalculatePermissions();
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return perm.getEffectivePermissions();
    }

    public boolean isPlayer() {
        return false;
    }

    public Server getServer() {
        return Bukkit.getServer();
    }

    // Paper start - implement pointers
    @Override
    public net.kyori.adventure.pointer.@NotNull Pointers pointers() {
        if (this.adventure$pointers == null) {
            this.adventure$pointers = net.kyori.adventure.pointer.Pointers.builder()
                .withDynamic(net.kyori.adventure.identity.Identity.DISPLAY_NAME, this::name)
                .withStatic(net.kyori.adventure.permission.PermissionChecker.POINTER, this::permissionValue)
                .build();
        }

        return this.adventure$pointers;
    }
    // Paper end
}
