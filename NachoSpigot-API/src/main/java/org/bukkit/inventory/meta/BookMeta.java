package org.bukkit.inventory.meta;
<<<<<<< found

import java.util.List;

||||||| expected

import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
=======

import java.util.List;

import net.kyori.adventure.inventory.Book;
import net.md_5.bungee.api.chat.BaseComponent;
>>>>>>> replacement
<<<<<<< found
import org.bukkit.Material;
||||||| expected
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
=======
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
>>>>>>> replacement

/**
 * Represents a book ({@link Material#BOOK_AND_QUILL} or {@link
 * Material#WRITTEN_BOOK}) that can have a title, an author, and pages.
 */
public interface BookMeta extends ItemMeta, net.kyori.adventure.inventory.Book { // Paper

    /**
     * Checks for the existence of a title in the book.
     *
     * @return true if the book has a title
     */
    boolean hasTitle();

    /**
     * Gets the title of the book.
     * <p>
     * Plugins should check that hasTitle() returns true before calling this
     * method.
     *
     * @return the title of the book
     */
    String getTitle();

    /**
     * Sets the title of the book.
     * <p>
     * Limited to 16 characters. Removes title when given null.
     *
     * @param title the title to set
     * @return true if the title was successfully set
     */
    boolean setTitle(String title);

    /**
     * Checks for the existence of an author in the book.
     *
     * @return the author of the book
     */
    boolean hasAuthor();

    /**
     * Gets the author of the book.
     * <p>
     * Plugins should check that hasAuthor() returns true before calling this
     * method.
     *
     * @return the author of the book
     */
    String getAuthor();

    /**
     * Sets the author of the book. Removes author when given null.
     *
     * @param author the author of the book
     */
    void setAuthor(String author);

    /**
     * Checks for the existence of pages in the book.
     *
     * @return true if the book has pages
     */
    boolean hasPages();

    // Paper start
    /**
     * Gets the title of the book.
     * <p>
     * Plugins should check that hasTitle() returns true before calling this
     * method.
     *
     * @return the title of the book
     */
    @Nullable
    @Override
    net.kyori.adventure.text.Component title();

    /**
     * Sets the title of the book.
     * <p>
     * Limited to 32 characters. Removes title when given null.
     *
     * @param title the title to set
     * @return the same {@link BookMeta} instance
     */
    @NotNull
    @Override
    @This BookMeta title(@Nullable net.kyori.adventure.text.Component title);

    /**
     * Gets the author of the book.
     * <p>
     * Plugins should check that hasAuthor() returns true before calling this
     * method.
     *
     * @return the author of the book
     */
    @Nullable
    @Override
    net.kyori.adventure.text.Component author();

    /**
     * Sets the author of the book. Removes author when given null.
     *
     * @param author the author to set
     * @return the same {@link BookMeta} instance
     */
    @NotNull
    @Override
    @This BookMeta author(@Nullable net.kyori.adventure.text.Component author);
    /**
     * Gets the specified page in the book. The page must exist.
     * <p>
     * Pages are 1-indexed.
     *
     * @param page the page number to get, in range [1, getPageCount()]
     * @return the page from the book
     */
    @NotNull net.kyori.adventure.text.Component page(int page);

    /**
     * Sets the specified page in the book. Pages of the book must be
     * contiguous.
     * <p>
     * The data can be up to 256 characters in length, additional characters
     * are truncated.
     * <p>
     * Pages are 1-indexed.
     *
     * @param page the page number to set, in range [1, getPageCount()]
     * @param data the data to set for that page
     */
    void page(int page, @NotNull net.kyori.adventure.text.Component data);

    /**
     * Adds new pages to the end of the book. Up to a maximum of 50 pages with
     * 256 characters per page.
     *
     * @param pages A list of strings, each being a page
     */
    void addPages(@NotNull net.kyori.adventure.text.Component... pages);

    interface BookMetaBuilder extends Builder {

        @NotNull
        @Override
        BookMetaBuilder title(@Nullable net.kyori.adventure.text.Component title);

        @NotNull
        @Override
        BookMetaBuilder author(@Nullable net.kyori.adventure.text.Component author);

        @NotNull
        @Override
        BookMetaBuilder addPage(@NotNull net.kyori.adventure.text.Component page);

        @NotNull
        @Override
        BookMetaBuilder pages(@NotNull net.kyori.adventure.text.Component... pages);

        @NotNull
        @Override
        BookMetaBuilder pages(@NotNull java.util.Collection<net.kyori.adventure.text.Component> pages);

        @NotNull
        @Override
        BookMeta build();
    }

    @Override
    @NonNull
    BookMetaBuilder toBuilder();

    // Paper end

    /**
     * Gets the specified page in the book. The given page must exist.
     *
     * @param page the page number to get
     * @return the page from the book
     * @deprecated in favour of {@link #page(int)}
     */
    @Deprecated // Paper
    String getPage(int page);

    /**
     * Sets the specified page in the book. Pages of the book must be
     * contiguous.
     * <p>
     * The data can be up to 256 characters in length, additional characters
     * are truncated.
     *
     * @param page the page number to set
     * @param data the data to set for that page
     * @deprecated in favour of {@link #page(int, net.kyori.adventure.text.Component)}
<<<<<<< found
     */
    void setPage(int page, String data);
||||||| expected
     */
    void setPage(int page, @NotNull String data);
=======
     */
    @Deprecated // Paper
    void setPage(int page, @NotNull String data);
>>>>>>> replacement

    /**
     * Gets all the pages in the book.
     *
     * @return list of all the pages in the book
     * @deprecated in favour of {@link #pages()}
     */
    @Deprecated // Paper
    List<String> getPages();

    /**
     * Clears the existing book pages, and sets the book to use the provided
     * pages. Maximum 50 pages with 256 characters per page.
     *
     * @param pages A list of pages to set the book to use
     * @deprecated in favour of {@link #pages(List)}
<<<<<<< found
     */
    void setPages(List<String> pages);
||||||| expected
     */
    void setPages(@NotNull List<String> pages);
=======
     */
    @Deprecated // Paper
    void setPages(@NotNull List<String> pages);
>>>>>>> replacement

    /**
     * Clears the existing book pages, and sets the book to use the provided
     * pages. Maximum 50 pages with 256 characters per page.
     *
     * @param pages A list of strings, each being a page
     * @deprecated in favour of {@link #pages(net.kyori.adventure.text.Component...)}
<<<<<<< found
     */
    void setPages(String... pages);
||||||| expected
     */
    void setPages(@NotNull String... pages);
=======
     */
    @Deprecated // Paper
    void setPages(@NotNull String... pages);
>>>>>>> replacement

    /**
     * Adds new pages to the end of the book. Up to a maximum of 50 pages with
     * 256 characters per page.
     *
     * @param pages A list of strings, each being a page
     * @deprecated in favour of {@link #addPages(net.kyori.adventure.text.Component...)}
<<<<<<< found
     */
    void addPage(String... pages);
||||||| expected
     */
    void addPage(@NotNull String... pages);
=======
     */
    @Deprecated // Paper
    void addPage(@NotNull String... pages);
>>>>>>> replacement

    /**
     * Gets the number of pages in the book.
     *
     * @return the number of pages in the book
     */
    int getPageCount();

    BookMeta clone();
}
<<<<<<< found
||||||| expected
         *
         * @param page the page number to get
         * @return the page from the book
         */
        @NotNull
        public BaseComponent[] getPage(int page) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
=======
         *
         * @param page the page number to get
         * @return the page from the book
         * @deprecated in favour of {@link #page(int)}
         */
        @NotNull
        @Deprecated // Paper
        public BaseComponent[] getPage(int page) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
>>>>>>> replacement
<<<<<<< found
||||||| expected
         *
         * @param page the page number to set
         * @param data the data to set for that page
         */
        public void setPage(int page, @Nullable BaseComponent... data) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
=======
         *
         * @param page the page number to set
         * @param data the data to set for that page
         * @deprecated in favour of {@link #page(int, net.kyori.adventure.text.Component)}
         */
        @Deprecated // Paper
        public void setPage(int page, @Nullable BaseComponent... data) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
>>>>>>> replacement
<<<<<<< found
||||||| expected
         * Gets all the pages in the book.
         *
         * @return list of all the pages in the book
         */
        @NotNull
        public List<BaseComponent[]> getPages() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
=======
         * Gets all the pages in the book.
         *
         * @return list of all the pages in the book
         * @deprecated in favour of {@link #pages()}
         */
        @NotNull
        @Deprecated // Paper
        public List<BaseComponent[]> getPages() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
>>>>>>> replacement
<<<<<<< found
||||||| expected
         * pages. Maximum 50 pages with 256 characters per page.
         *
         * @param pages A list of pages to set the book to use
         */
        public void setPages(@NotNull List<BaseComponent[]> pages) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
=======
         * pages. Maximum 50 pages with 256 characters per page.
         *
         * @param pages A list of pages to set the book to use
         * @deprecated in favour of {@link #pages(java.util.List)}
         */
        @Deprecated // Paper
        public void setPages(@NotNull List<BaseComponent[]> pages) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
>>>>>>> replacement
<<<<<<< found
||||||| expected
         * pages. Maximum 50 pages with 256 characters per page.
         *
         * @param pages A list of component arrays, each being a page
         */
        public void setPages(@NotNull BaseComponent[]... pages) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
=======
         * pages. Maximum 50 pages with 256 characters per page.
         *
         * @param pages A list of component arrays, each being a page
         * @deprecated in favour of {@link #pages(net.kyori.adventure.text.Component...)}
         */
        @Deprecated // Paper
        public void setPages(@NotNull BaseComponent[]... pages) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
>>>>>>> replacement
<<<<<<< found
||||||| expected
         * with 256 characters per page.
         *
         * @param pages A list of component arrays, each being a page
         */
        public void addPage(@NotNull BaseComponent[]... pages) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
=======
         * with 256 characters per page.
         *
         * @param pages A list of component arrays, each being a page
         * @deprecated in favour of {@link #addPages(net.kyori.adventure.text.Component...)}
         */
        @Deprecated // Paper
        public void addPage(@NotNull BaseComponent[]... pages) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
>>>>>>> replacement
