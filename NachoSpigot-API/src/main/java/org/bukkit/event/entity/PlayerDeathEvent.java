package org.bukkit.event.entity;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Thrown whenever a {@link Player} dies
 */
public class PlayerDeathEvent extends EntityDeathEvent {
    private int newExp = 0;
    private String deathMessage = "";
    private net.kyori.adventure.text.Component adventure$deathMessage; // Paper
    private int newLevel = 0;
    private int newTotalExp = 0;
    private boolean keepLevel = false;
    private boolean keepInventory = false;

    // Paper start
    public PlayerDeathEvent(@NotNull final Player player, @NotNull final List<ItemStack> drops, final int droppedExp, @Nullable final net.kyori.adventure.text.Component adventure$deathMessage) {
        this(player, drops, droppedExp, 0, adventure$deathMessage, null);
    }

    public PlayerDeathEvent(@NotNull final Player player, @NotNull final List<ItemStack> drops, final int droppedExp, final int newExp, @Nullable final net.kyori.adventure.text.Component adventure$deathMessage, @Nullable String deathMessage) {
        this(player, drops, droppedExp, newExp, 0, 0, adventure$deathMessage, deathMessage);
    }

    public PlayerDeathEvent(@NotNull final Player player, @NotNull final List<ItemStack> drops, final int droppedExp, final int newExp, final int newTotalExp, final int newLevel, @Nullable final net.kyori.adventure.text.Component adventure$deathMessage, @Nullable String deathMessage) {
        super(player, drops, droppedExp);
        this.newExp = newExp;
        this.newTotalExp = newTotalExp;
        this.newLevel = newLevel;
        this.deathMessage = deathMessage;
        this.adventure$deathMessage = adventure$deathMessage;
    }
    // Paper end

    @Deprecated // Paper
    public PlayerDeathEvent(final Player player, final List<ItemStack> drops, final int droppedExp, final String deathMessage) {
        this(player, drops, droppedExp, 0, deathMessage);
    }

    @Deprecated // Paper
    public PlayerDeathEvent(final Player player, final List<ItemStack> drops, final int droppedExp, final int newExp, final String deathMessage) {
        this(player, drops, droppedExp, newExp, 0, 0, deathMessage);
    }

    @Deprecated // Paper
    public PlayerDeathEvent(final Player player, final List<ItemStack> drops, final int droppedExp, final int newExp, final int newTotalExp, final int newLevel, final String deathMessage) {
        super(player, drops, droppedExp);
        this.newExp = newExp;
        this.newTotalExp = newTotalExp;
        this.newLevel = newLevel;
        this.deathMessage = deathMessage;
        this.adventure$deathMessage = deathMessage != null ? org.bukkit.Bukkit.getUnsafe().legacyComponentSerializer().deserialize(deathMessage) : net.kyori.adventure.text.Component.empty(); // Paper
    }

    @Override
    public Player getEntity() {
        return (Player) entity;
    }

    // Paper start
    /**
     * Set the death message that will appear to everyone on the server.
     *
     * @param deathMessage Message to appear to other players on the server.
     */
    public void deathMessage(@Nullable net.kyori.adventure.text.Component deathMessage) {
        this.deathMessage = null;
        this.adventure$deathMessage = deathMessage;
    }

    /**
     * Get the death message that will appear to everyone on the server.
     *
     * @return Message to appear to other players on the server.
     */
    public @Nullable net.kyori.adventure.text.Component deathMessage() {
        return this.adventure$deathMessage;
    }
    // Paper end

    /**
     * Set the death message that will appear to everyone on the server.
     *
     * @param deathMessage Message to appear to other players on the server
     * @deprecated in favour of {@link #deathMessage(net.kyori.adventure.text.Component)}
     */
    @Deprecated // Paper
    public void setDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage;
    }

    /**
     * Get the death message that will appear to everyone on the server.
     *
     * @return Message to appear to other players on the server.
     * @deprecated in favour of {@link #deathMessage()}
     */
    @Nullable
    @Deprecated // Paper
    public String getDeathMessage() {
        return this.deathMessage != null ? this.deathMessage : (this.adventure$deathMessage != null ? org.bukkit.Bukkit.getUnsafe().legacyComponentSerializer().serialize(this.adventure$deathMessage) : null); // Paper
    }

    /**
     * Gets how much EXP the Player should have at respawn.
     * <p>
     * This does not indicate how much EXP should be dropped, please see
     * {@link #getDroppedExp()} for that.
     *
     * @return New EXP of the respawned player
     */
    public int getNewExp() {
        return newExp;
    }

    /**
     * Sets how much EXP the Player should have at respawn.
     * <p>
     * This does not indicate how much EXP should be dropped, please see
     * {@link #setDroppedExp(int)} for that.
     *
     * @param exp New EXP of the respawned player
     */
    public void setNewExp(int exp) {
        newExp = exp;
    }

    /**
     * Gets the Level the Player should have at respawn.
     *
     * @return New Level of the respawned player
     */
    public int getNewLevel() {
        return newLevel;
    }

    /**
     * Sets the Level the Player should have at respawn.
     *
     * @param level New Level of the respawned player
     */
    public void setNewLevel(int level) {
        newLevel = level;
    }

    /**
     * Gets the Total EXP the Player should have at respawn.
     *
     * @return New Total EXP of the respawned player
     */
    public int getNewTotalExp() {
        return newTotalExp;
    }

    /**
     * Sets the Total EXP the Player should have at respawn.
     *
     * @param totalExp New Total EXP of the respawned player
     */
    public void setNewTotalExp(int totalExp) {
        newTotalExp = totalExp;
    }

    /**
     * Gets if the Player should keep all EXP at respawn.
     * <p>
     * This flag overrides other EXP settings
     *
     * @return True if Player should keep all pre-death exp
     */
    public boolean getKeepLevel() {
        return keepLevel;
    }

    /**
     * Sets if the Player should keep all EXP at respawn.
     * <p>
     * This overrides all other EXP settings
     * <p>
     * This doesn't prevent prevent the EXP from dropping.
     * {@link #setDroppedExp(int)} should be used stop the
     * EXP from dropping.
     *
     * @param keepLevel True to keep all current value levels
     */
    public void setKeepLevel(boolean keepLevel) {
        this.keepLevel = keepLevel;
    }

    /**
     * Sets if the Player keeps inventory on death.
     *
     * @param keepInventory True to keep the inventory
     */
    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    /**
     * Gets if the Player keeps inventory on death.
     *
     * @return True if the player keeps inventory on death
     */
    public boolean getKeepInventory() {
        return keepInventory;
    }
}