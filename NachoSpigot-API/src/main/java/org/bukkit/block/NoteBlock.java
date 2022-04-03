package org.bukkit.block;

import org.bukkit.Instrument;
import org.bukkit.Note;

/**
 * Represents a note.
 */
public interface NoteBlock extends BlockState {

    /**
     * Gets the note.
     *
     * @return The note.
     */
    Note getNote();

    /**
     * Gets the note.
     *
     * @return The note ID.
     * @deprecated Magic value
     */
    @Deprecated
    byte getRawNote();

    /**
     * Set the note.
     *
     * @param note The note.
     */
    void setNote(Note note);

    /**
     * Set the note.
     *
     * @param note The note ID.
     * @deprecated Magic value
     */
    @Deprecated
    void setRawNote(byte note);

    /**
     * Attempts to play the note at block
     * <p>
     * If the block is no longer a note block, this will return false
     *
     * @return true if successful, otherwise false
     */
    boolean play();

    /**
     * Plays an arbitrary note with an arbitrary instrument
     *
     * @param instrument Instrument ID
     * @param note Note ID
     * @return true if successful, otherwise false
     * @deprecated Magic value
     */
    @Deprecated
    boolean play(byte instrument, byte note);

    /**
     * Plays an arbitrary note with an arbitrary instrument
     *
     * @param instrument The instrument
     * @param note The note
     * @return true if successful, otherwise false
     * @see Instrument Note
     */
    boolean play(Instrument instrument, Note note);
}
