package org.bukkit.block;

import org.jetbrains.annotations.NotNull;

/**
 * Represents either a SignPost or a WallSign
 */
public interface Sign extends BlockState {
    // Paper start
    /**
     * Gets all the lines of text currently on this sign.
     *
     * @return Array of Strings containing each line of text
     */
    @NotNull java.util.List<net.kyori.adventure.text.Component> lines();

    /**
     * Gets the line of text at the specified index.
     * <p>
     * For example, getLine(0) will return the first line of text.
     *
     * @param index Line number to get the text from, starting at 0
     * @throws IndexOutOfBoundsException Thrown when the line does not exist
     * @return Text on the given line
     */
    @NotNull net.kyori.adventure.text.Component line(int index) throws IndexOutOfBoundsException;

    /**
     * Sets the line of text at the specified index.
     * <p>
     * For example, setLine(0, "Line One") will set the first line of text to
     * "Line One".
     *
     * @param index Line number to set the text at, starting from 0
     * @param line New text to set at the specified index
     * @throws IndexOutOfBoundsException If the index is out of the range 0..3
     */
    void line(int index, @NotNull net.kyori.adventure.text.Component line) throws IndexOutOfBoundsException;
    // Paper end

    /**
     * Gets all the lines of text currently on this sign.
     *
     * @return Array of Strings containing each line of text
     * @deprecated in favour of {@link #lines()}
     */
    @Deprecated // Paper
    String[] getLines();

    /**
     * Gets the line of text at the specified index.
     * <p>
     * For example, getLine(0) will return the first line of text.
     *
     * @param index Line number to get the text from, starting at 0
     * @throws IndexOutOfBoundsException Thrown when the line does not exist
     * @return Text on the given line
     * @deprecated in favour of {@link #line(int)}
     */
    @Deprecated // Paper
    String getLine(int index) throws IndexOutOfBoundsException;

    /**
     * Sets the line of text at the specified index.
     * <p>
     * For example, setLine(0, "Line One") will set the first line of text to
     * "Line One".
     *
     * @param index Line number to set the text at, starting from 0
     * @param line New text to set at the specified index
     * @throws IndexOutOfBoundsException If the index is out of the range 0..3
     * @deprecated in favour of {@link #line(int, net.kyori.adventure.text.Component)}
     */
    @Deprecated // Paper
    void setLine(int index, @NotNull String line) throws IndexOutOfBoundsException;
}
