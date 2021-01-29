package org.bukkit.map;

/**
 * Represents a cursor on a map.
 */
public final class MapCursor {
    private byte x, y;
    private byte direction, type;
    private boolean visible;
<<<<<<< found
||||||| expected
    private String caption;
=======
    private net.kyori.adventure.text.Component caption; // Paper
>>>>>>> replacement

    /**
     * Initialize the map cursor.
     *
     * @param x The x coordinate, from -128 to 127.
     * @param y The y coordinate, from -128 to 127.
     * @param direction The facing of the cursor, from 0 to 15.
<<<<<<< found
||||||| expected
     */
    @Deprecated
    public MapCursor(byte x, byte y, byte direction, byte type, boolean visible) {
        this(x, y, direction, type, visible, null);
    }

    /**
=======
     */
    @Deprecated
    public MapCursor(byte x, byte y, byte direction, byte type, boolean visible) {
        this(x, y, direction, type, visible, (String) null); // Paper
    }

    /**
>>>>>>> replacement
<<<<<<< found
||||||| expected
     * @param visible Whether the cursor is visible by default.
     */
    public MapCursor(byte x, byte y, byte direction, @NotNull Type type, boolean visible) {
        this(x, y, direction, type, visible, null);
    }

    /**
=======
     * @param visible Whether the cursor is visible by default.
     */
    public MapCursor(byte x, byte y, byte direction, @NotNull Type type, boolean visible) {
        this(x, y, direction, type, visible, (String) null); // Paper
    }

    /**
>>>>>>> replacement
     * @param type The type (color/style) of the map cursor.
     * @param visible Whether the cursor is visible by default.
     * @deprecated Magic value. Use {@link #MapCursor(byte, byte, byte, byte, boolean, net.kyori.adventure.text.Component)}
     */
    @Deprecated
    public MapCursor(byte x, byte y, byte direction, byte type, boolean visible) {
        this.x = x;
        this.y = y;
        setDirection(direction);
        setRawType(type);
<<<<<<< found
||||||| expected
        this.visible = visible;
        this.caption = caption;
    }

    /**
     * Initialize the map cursor.
=======
        this.visible = visible;
        this.caption = caption == null ? null : org.bukkit.Bukkit.getUnsafe().legacyComponentSerializer().deserialize(caption); // Paper
    }
    // Paper start
    /**
     * Initialize the map cursor.
     *
     * @param x The x coordinate, from -128 to 127.
     * @param y The y coordinate, from -128 to 127.
     * @param direction The facing of the cursor, from 0 to 15.
     * @param type The type (color/style) of the map cursor.
     * @param visible Whether the cursor is visible by default.
     * @param caption cursor caption
     * @deprecated Magic value
     */
    @Deprecated
    public MapCursor(byte x, byte y, byte direction, byte type, boolean visible, @Nullable net.kyori.adventure.text.Component caption) {
        this.x = x; this.y = y; this.visible = visible; this.caption = caption;
        setDirection(direction);
        setRawType(type);
    }
    /**
     * Initialize the map cursor.
     *
     * @param x The x coordinate, from -128 to 127.
     * @param y The y coordinate, from -128 to 127.
     * @param direction The facing of the cursor, from 0 to 15.
     * @param type The type (color/style) of the map cursor.
     * @param visible Whether the cursor is visible by default.
     * @param caption cursor caption
     */
    public MapCursor(byte x, byte y, byte direction, @NotNull Type type, boolean visible, @Nullable net.kyori.adventure.text.Component caption) {
        this.x = x; this.y = y; this.visible = visible; this.caption = caption;
        setDirection(direction);
        setType(type);
    }
    // Paper end

    /**
     * Initialize the map cursor.
>>>>>>> replacement
<<<<<<< found
||||||| expected
        setDirection(direction);
        setType(type);
        this.visible = visible;
        this.caption = caption;
    }

    /**
=======
        setDirection(direction);
        setType(type);
        this.visible = visible;
        this.caption = caption == null ? null : org.bukkit.Bukkit.getUnsafe().legacyComponentSerializer().deserialize(caption); // Paper
    }

    /**
>>>>>>> replacement
        this.visible = visible;
    }

    // Paper start
    /**
     * Get the X position of this cursor.
     *
     * @return The X coordinate.
     */
    public @Nullable net.kyori.adventure.text.Component caption() {
        return this.caption;
    }
    /**
     * Sets the caption on this cursor.
     *
     * @param caption new caption
     */
    public void caption(@Nullable net.kyori.adventure.text.Component caption) {
        this.caption = caption;
    }
    // Paper end
    /**
     * Gets the caption on this cursor.
     *
     * @return caption
     * @deprecated in favour of {@link #caption()}
<<<<<<< found
     */
    public byte getX() {
        return x;
||||||| expected
     */
    @Nullable
    public String getCaption() {
        return caption;
=======
     */
    @Nullable
    @Deprecated // Paper
    public String getCaption() {
        return this.caption == null ? null : org.bukkit.Bukkit.getUnsafe().legacyComponentSerializer().serialize(this.caption); // Paper
>>>>>>> replacement
    }

    /**
     * Get the Y position of this cursor.
     *
     * @return The Y coordinate.
     * @deprecated in favour of {@link #caption(net.kyori.adventure.text.Component)}
<<<<<<< found
     */
    public byte getY() {
        return y;
||||||| expected
     */
    public void setCaption(@Nullable String caption) {
        this.caption = caption;
=======
     */
    @Deprecated // Paper
    public void setCaption(@Nullable String caption) {
        this.caption = caption == null ? null : org.bukkit.Bukkit.getUnsafe().legacyComponentSerializer().deserialize(caption); // Paper
>>>>>>> replacement
    }

    /**
     * Get the direction of this cursor.
     *
     * @return The facing of the cursor, from 0 to 15.
     */
    public byte getDirection() {
        return direction;
    }

    /**
     * Get the type of this cursor.
     *
     * @return The type (color/style) of the map cursor.
     */
    public Type getType() {
        return Type.byValue(type);
    }

    /**
     * Get the type of this cursor.
     *
     * @return The type (color/style) of the map cursor.
     * @deprecated Magic value
     */
    @Deprecated
    public byte getRawType() {
        return type;
    }

    /**
     * Get the visibility status of this cursor.
     *
     * @return True if visible, false otherwise.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set the X position of this cursor.
     *
     * @param x The X coordinate.
     */
    public void setX(byte x) {
        this.x = x;
    }

    /**
     * Set the Y position of this cursor.
     *
     * @param y The Y coordinate.
     */
    public void setY(byte y) {
        this.y = y;
    }

    /**
     * Set the direction of this cursor.
     *
     * @param direction The facing of the cursor, from 0 to 15.
     */
    public void setDirection(byte direction) {
        if (direction < 0 || direction > 15) {
            throw new IllegalArgumentException("Direction must be in the range 0-15");
        }
        this.direction = direction;
    }

    /**
     * Set the type of this cursor.
     *
     * @param type The type (color/style) of the map cursor.
     */
    public void setType(Type type) {
        setRawType(type.value);
    }

    /**
     * Set the type of this cursor.
     *
     * @param type The type (color/style) of the map cursor.
     * @deprecated Magic value
     */
    @Deprecated
    public void setRawType(byte type) {
        if (type < 0 || type > 15) {
            throw new IllegalArgumentException("Type must be in the range 0-15");
        }
        this.type = type;
    }

    /**
     * Set the visibility status of this cursor.
     *
     * @param visible True if visible.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Represents the standard types of map cursors. More may be made
     * available by texture packs - the value is used by the client as an
     * index in the file './misc/mapicons.png' from minecraft.jar or from a
     * texture pack.
     */
    public enum Type {
        WHITE_POINTER(0),
        GREEN_POINTER(1),
        RED_POINTER(2),
        BLUE_POINTER(3),
        WHITE_CROSS(4);

        private byte value;

        private Type(int value) {
            this.value = (byte) value;
        }

        /**
         *
         * @return the value 
         * @deprecated Magic value
         */
        @Deprecated
        public byte getValue() {
            return value;
        }

        /**
         *
         * @param value the value
         * @return the matching type
         * @deprecated Magic value
         */
        @Deprecated
        public static Type byValue(byte value) {
            for (Type t : values()) {
                if (t.value == value) return t;
            }
            return null;
        }
    }

}
