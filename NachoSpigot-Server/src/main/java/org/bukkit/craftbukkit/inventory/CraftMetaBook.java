package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta;
import org.bukkit.inventory.meta.BookMeta;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.AbstractList;
import net.minecraft.server.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.IChatBaseComponent;
import net.minecraft.server.NBTTagString;
import org.bukkit.craftbukkit.util.CraftChatMessage;

// Spigot start
import static org.spigotmc.ValidateUtils.*;
// Spigot end

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaBook extends CraftMetaItem implements BookMeta {
    static final ItemMetaKey BOOK_TITLE = new ItemMetaKey("title");
    static final ItemMetaKey BOOK_AUTHOR = new ItemMetaKey("author");
    static final ItemMetaKey BOOK_PAGES = new ItemMetaKey("pages");
    static final ItemMetaKey RESOLVED = new ItemMetaKey("resolved");
    static final ItemMetaKey GENERATION = new ItemMetaKey("generation");
    static final int MAX_PAGE_LENGTH = 340; // FlamePaper - Limit max page length to 320
    static final int MAX_TITLE_LENGTH = 32; // FlamePaper - Limit max title length to 32
    static final int MAX_PAGES = 50; // FlamePaper - Limit pages to 50
    static final int MAX_AUTHOR_LENGTH = 16; // FlamePaper - Limit author name length to 16

    protected String title;
    protected String author;
    public List<IChatBaseComponent> pages = new ArrayList<>();
    protected Integer generation;

    CraftMetaBook(CraftMetaItem meta) {
        super(meta);

        if (meta instanceof CraftMetaBook) {
            CraftMetaBook bookMeta = (CraftMetaBook) meta;
            this.title = bookMeta.title;
            this.author = bookMeta.author;
            pages.addAll(bookMeta.pages);
            this.generation = bookMeta.generation;
        }
    }

    CraftMetaBook(NBTTagCompound tag) {
        this(tag, true);
    }

    CraftMetaBook(NBTTagCompound tag, boolean handlePages) {
        super(tag);

        if (tag.hasKey(BOOK_TITLE.NBT)) {
            // FlamePaper - Apply title limit
            this.title = limit( tag.getString(BOOK_TITLE.NBT), MAX_TITLE_LENGTH ); // Spigot
        }

        if (tag.hasKey(BOOK_AUTHOR.NBT)) {
            // FlamePaper - Apply author limit
            this.author = limit( tag.getString(BOOK_AUTHOR.NBT), MAX_AUTHOR_LENGTH ); // Spigot
        }

        boolean resolved = false;
        if (tag.hasKey(RESOLVED.NBT)) {
            resolved = tag.getBoolean(RESOLVED.NBT);
        }
        
        if (tag.hasKey(GENERATION.NBT)) {
            generation = tag.getInt(GENERATION.NBT);
        }

        if (tag.hasKey(BOOK_PAGES.NBT) && handlePages) {
            NBTTagList pages = tag.getList(BOOK_PAGES.NBT, 8);
            // FlamePaper - Apply page limit
            for (int i = 0; i < Math.min(pages.size(), MAX_PAGES); i++) {
                String page = pages.getString(i);
                if (resolved) {
                    try {
                        this.pages.add(ChatSerializer.a(page));
                        continue;
                    } catch (Exception e) {
                        // Ignore and treat as an old book
                    }
                }
                // FlamePaper - Apply page limit
                addPage( limit( page, MAX_PAGE_LENGTH ) ); // Spigot
            }
        }
    }

    CraftMetaBook(Map<String, Object> map) {
        super(map);

        setAuthor(SerializableMeta.getString(map, BOOK_AUTHOR.BUKKIT, true));

        setTitle(SerializableMeta.getString(map, BOOK_TITLE.BUKKIT, true));

        Iterable<?> pages = SerializableMeta.getObject(Iterable.class, map, BOOK_PAGES.BUKKIT, true);
        if(pages != null) {
            int pageCount = 0;
            for (Object page : pages) {
                // FlamePaper - Limit page iterations
                if (pageCount < MAX_PAGES) {
                    if (page instanceof String) {
                        addPage((String) page);
                    }
                    pageCount++;
                } else {
                    break;
                }
            }
        }
        
        generation = SerializableMeta.getObject(Integer.class, map, GENERATION.BUKKIT, true);
    }

    @Override
    void applyToItem(NBTTagCompound itemData) {
        applyToItem(itemData, true);
    }

    void applyToItem(NBTTagCompound itemData, boolean handlePages) {
        super.applyToItem(itemData);

        if (hasTitle()) {
            itemData.setString(BOOK_TITLE.NBT, this.title);
        }

        if (hasAuthor()) {
            itemData.setString(BOOK_AUTHOR.NBT, this.author);
        }


        if (handlePages) {
            if (hasPages()) {
                NBTTagList list = new NBTTagList();
                for (IChatBaseComponent page : pages)
                {
                    list.add(new NBTTagString(CraftChatMessage.fromComponent(page)));
                }
                itemData.set(BOOK_PAGES.NBT, list);
            }

            itemData.remove(RESOLVED.NBT);
        }

        if (generation != null) {
            itemData.setInt(GENERATION.NBT, generation);
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isBookEmpty();
    }

    boolean isBookEmpty() {
        return !(hasPages() || hasAuthor() || hasTitle());
    }

    @Override
    boolean applicableTo(Material type) {
        switch (type) {
        case WRITTEN_BOOK:
        case BOOK_AND_QUILL:
            return true;
        default:
            return false;
        }
    }

    public boolean hasAuthor() {
        return !Strings.isNullOrEmpty(author);
    }

    public boolean hasTitle() {
        return !Strings.isNullOrEmpty(title);
    }

    public boolean hasPages() {
        return !pages.isEmpty();
    }

    public String getTitle() {
        return this.title;
    }

    public boolean setTitle(final String title) {
        if (title == null) {
            this.title = null;
        } else {
            // FlamePaper - Simplify & improve title handling
            this.title = title.substring(0, Math.min(title.length(), MAX_PAGE_LENGTH));
        }
        // FlamePaper - Always return true
        return true;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getPage(final int page) {
        Validate.isTrue(isValidPage(page), "Invalid page number");
        return CraftChatMessage.fromComponent(pages.get(page - 1));
    }

    public void setPage(final int page, final String text) {
        if (!isValidPage(page)) {
            throw new IllegalArgumentException("Invalid page number " + page + "/" + pages.size());
        }

        // FlamePaper - Simplify page handling
        String newText = text == null ? "" : text.substring(0, Math.min(text.length(), MAX_PAGE_LENGTH));
        pages.set(page - 1, CraftChatMessage.fromString(newText, true)[0]);
    }

    public void setPages(final String... pages) {
        this.pages.clear();

        addPage(pages);
    }

    public void addPage(final String... pages) {
        // FlamePaper - Limit page iterations
        for (int i = 0; i < Math.min(pages.length, MAX_PAGES); i++) {
            // FlamePaper - Apply page limit
            if (getPageCount() < MAX_PAGES) {
                String page = pages[i];

                if (page == null) {
                    page = "";
                } else if (page.length() > MAX_PAGE_LENGTH) {
                    page = page.substring(0, MAX_PAGE_LENGTH);
                }
                this.pages.add(CraftChatMessage.fromString(page, true)[0]);
            } else {
                break;
            }
        }
    }

    public int getPageCount() {
        return pages.size();
    }

    public List<String> getPages() {
        final List<IChatBaseComponent> copy = ImmutableList.copyOf(pages);
        return new AbstractList<String>() {

            @Override
            public String get(int index) {
                return CraftChatMessage.fromComponent(copy.get(index));
            }

            @Override
            public int size() {
                return copy.size();
            }
        };
    }

    public void setPages(List<String> pages) {
        this.pages.clear();
        // FlamePaper - Convert list to array to reuse methods
        addPage(pages.toArray(new String[0]));
    }

    private boolean isValidPage(int page) {
        return page > 0 && page <= pages.size();
    }

    @Override
    public CraftMetaBook clone() {
        CraftMetaBook meta = (CraftMetaBook) super.clone();
        meta.pages = new ArrayList<>(pages);
        return meta;
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (hasTitle()) {
            hash = 61 * hash + this.title.hashCode();
        }
        if (hasAuthor()) {
            hash = 61 * hash + 13 * this.author.hashCode();
        }
        if (hasPages()) {
            hash = 61 * hash + 17 * this.pages.hashCode();
        }
        return original != hash ? CraftMetaBook.class.hashCode() ^ hash : hash;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaBook) {
            CraftMetaBook that = (CraftMetaBook) meta;

            return (hasTitle() ? that.hasTitle() && this.title.equals(that.title) : !that.hasTitle())
                    && (hasAuthor() ? that.hasAuthor() && this.author.equals(that.author) : !that.hasAuthor())
                    && (hasPages() ? that.hasPages() && this.pages.equals(that.pages) : !that.hasPages());
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaBook || isBookEmpty());
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        if (hasTitle()) {
            builder.put(BOOK_TITLE.BUKKIT, title);
        }

        if (hasAuthor()) {
            builder.put(BOOK_AUTHOR.BUKKIT, author);
        }

        if (hasPages()) {
            List<String> pagesString = new ArrayList<>();
            for (IChatBaseComponent comp : pages) {
                pagesString.add(CraftChatMessage.fromComponent(comp));
            }
            builder.put(BOOK_PAGES.BUKKIT, pagesString);
        }
        
        if (generation != null) {
            builder.put(GENERATION.BUKKIT, generation);
        }

        return builder;
    }
}
