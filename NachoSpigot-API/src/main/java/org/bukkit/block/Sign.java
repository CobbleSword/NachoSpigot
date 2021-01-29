package org.bukkit.block;

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
    @NotNull
    public java.util.List<net.kyori.adventure.text.Component> lines();

    /**
     * Gets the line of text at the specified index.
     * <p>
     * For example, getLine(0) will return the first line of text.
     *
     * @param index Line number to get the text from, starting at 0
     * @throws IndexOutOfBoundsException Thrown when the line does not exist
     * @return Text on the given line
     */
    @NotNull
    public net.kyori.adventure.text.Component line(int index) throws IndexOutOfBoundsException;

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
    public void line(int index, @NotNull net.kyori.adventure.text.Component line) throws IndexOutOfBoundsException;
    // Paper end

    /**
     * Gets all the lines of text currently on this sign.
     *
     * @return Array of Strings containing each line of text
     * @deprecated in favour of {@link #lines()}
     */
    @Deprecated // Paper
    public String[] getLines();

    /**
     * Gets the line of text at the specified index.
     * <p>
     * For example, getLine(0) will return the first line of text.
     *
     * @param index Line number to get the text from, starting at 0
<<<<<<< found
     * @throws IndexOutOfBoundsException Thrown when the line does not exist
     * @return Text on the given line
||||||| expected
     * @throws IndexOutOfBoundsException Thrown when the line does not exist
=======
     * @throws IndexOutOfBoundsException Thrown when the line does not exist
     * @deprecated in favour of {@link #line(int)}
>>>>>>> replacement
     */
    @Deprecated // Paper
    public String getLine(int index) throws IndexOutOfBoundsException;

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
<<<<<<< found
     */
    public void setLine(int index, String line) throws IndexOutOfBoundsException;
}
||||||| expected
     */
    public void setLine(int index, @NotNull String line) throws IndexOutOfBoundsException;

    /**
=======
     */
    @Deprecated // Paper
    public void setLine(int index, @NotNull String line) throws IndexOutOfBoundsException;

    /**
>>>>>>> replacement
