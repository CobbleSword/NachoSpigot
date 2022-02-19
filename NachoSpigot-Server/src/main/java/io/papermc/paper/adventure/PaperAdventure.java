package io.papermc.paper.adventure;

import dev.cobblesword.nachospigot.commons.minecraft.MCUtils;
import io.netty.util.AttributeKey;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.util.Codec;
import net.minecraft.server.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.inventory.CraftMetaBook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "deprecation", "SpellCheckingInspection"})
public final class PaperAdventure {
    public static final AttributeKey<Locale> LOCALE_ATTRIBUTE = AttributeKey.valueOf("adventure:locale");
    private static final Pattern LOCALIZATION_PATTERN = Pattern.compile("%(?:(\\d)\\$)?s");
    public static final ComponentFlattener FLATTENER = ComponentFlattener.basic().toBuilder()
            .complexMapper(TranslatableComponent.class, (translatable, consumer) -> {
                final @NotNull String translated = MCUtils.getLanguage().a(translatable.key()); // getOrDefault

                final Matcher matcher = LOCALIZATION_PATTERN.matcher(translated);
                final List<Component> args = translatable.args();
                int argPosition = 0;
                int lastIdx = 0;
                while (matcher.find()) {
                    // append prior
                    if (lastIdx < matcher.start()) {
                        consumer.accept(Component.text(translated.substring(lastIdx, matcher.start())));
                    }
                    lastIdx = matcher.end();

                    final @Nullable String argIdx = matcher.group(1);
                    // calculate argument position
                    if (argIdx != null) {
                        try {
                            final int idx = Integer.parseInt(argIdx) - 1;
                            if (idx < args.size()) {
                                consumer.accept(args.get(idx));
                            }
                        } catch (final NumberFormatException ex) {
                            // ignore, drop the format placeholder
                        }
                    } else {
                        if (argPosition < args.size()) {
                            consumer.accept(args.get(argPosition));
                        }
                    }
                }

                // append tail
                if (lastIdx < translated.length()) {
                    consumer.accept(Component.text(translated.substring(lastIdx)));
                }
            })
            .build();
    public static final LegacyComponentSerializer LEGACY_SECTION_UXRC = LegacyComponentSerializer.builder().flattener(FLATTENER).hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    public static final PlainComponentSerializer PLAIN_COMPONENT = PlainComponentSerializer.builder().flattener(FLATTENER).build();
    public static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.builder().flattener(FLATTENER).build();
    public static final GsonComponentSerializer GSON = GsonComponentSerializer.builder()
            .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.INSTANCE)
            .build();
    public static final GsonComponentSerializer COLOR_DOWNSAMPLING_GSON = GsonComponentSerializer.builder()
            .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.INSTANCE)
            .downsampleColors()
            .build();
    private static final Codec<NBTTagCompound, String, IOException, IOException> NBT_CODEC = new Codec<NBTTagCompound, String, IOException, IOException>() {
        @Override
        public @NotNull NBTTagCompound decode(final @NotNull String encoded) throws IOException {
            try {
                return MojangsonParser.parse(encoded);
            } catch (final MojangsonParseException e) {
                throw new IOException(e);
            }
        }

        @Override
        public @NotNull String encode(final @NotNull NBTTagCompound decoded) {
            return decoded.toString();
        }
    };
    static final WrapperAwareSerializer WRAPPER_AWARE_SERIALIZER = new WrapperAwareSerializer();

    private PaperAdventure() {
    }

    // Key

    public static MinecraftKey asVanilla(final Key key) {
        return MCUtils.newKey(key.namespace(), key.value());
    }

    public static MinecraftKey asVanillaNullable(final Key key) {
        if (key == null) {
            return null;
        }
        return MCUtils.newKey(key.namespace(), key.value());
    }

    // Component

    public static Component asAdventure(final IChatBaseComponent component) {
        return component == null ? Component.empty() : GSON.serializer().fromJson(IChatBaseComponent.ChatSerializer.toJsonTree(component), Component.class);
    }

    public static ArrayList<Component> asAdventure(final List<IChatBaseComponent> vanillas) {
        final ArrayList<Component> adventures = new ArrayList<>(vanillas.size());
        for (final IChatBaseComponent vanilla : vanillas) {
            adventures.add(asAdventure(vanilla));
        }
        return adventures;
    }

    public static ArrayList<Component> asAdventureFromJson(final List<String> jsonStrings) {
        final ArrayList<Component> adventures = new ArrayList<>(jsonStrings.size());
        for (final String json : jsonStrings) {
            adventures.add(GsonComponentSerializer.gson().deserialize(json));
        }
        return adventures;
    }

    public static List<String> asJson(final List<Component> adventures) {
        final List<String> jsons = new ArrayList<>(adventures.size());
        for (final Component component : adventures) {
            jsons.add(GsonComponentSerializer.gson().serialize(component));
        }
        return jsons;
    }

    public static IChatBaseComponent asVanilla(final Component component) {
        if (component == null) return null;
        return new AdventureComponent(component);
        //return IChatBaseComponent.ChatSerializer.fromJson(GSON.serializer().toJsonTree(component));
    }

    public static List<IChatBaseComponent> asVanilla(final List<Component> adventures) {
        final List<IChatBaseComponent> vanillas = new ArrayList<>(adventures.size());
        for (final Component adventure : adventures) {
            vanillas.add(asVanilla(adventure));
        }
        return vanillas;
    }

    public static String asJsonString(final Component component, final Locale locale) {
        return GSON.serialize(
                GlobalTranslator.render(
                        component,
                        // play it safe
                        locale != null
                                ? locale
                                : Locale.US
                )
        );
    }

    public static String asJsonString(final IChatBaseComponent component, final Locale locale) {
        if (component instanceof AdventureComponent) {
            return asJsonString(((AdventureComponent) component).adventure, locale);
        }
        return IChatBaseComponent.ChatSerializer.toJson(component); // toJson
    }

    public static String asPlain(final Component component, final Locale locale) {
        return PLAIN.serialize(
                GlobalTranslator.render(
                        component,
                        // play it safe
                        locale != null
                                ? locale
                                : Locale.US
                )
        );
    }

    // thank you for being worse than wet socks, Bukkit
    public static String superHackyLegacyRepresentationOfComponent(final Component component, final String string) {
        return LEGACY_SECTION_UXRC.serialize(component) + ChatColor.getLastColors(string);
    }

    // BossBar

    /*public static BossEvent.BossBarColor asVanilla(final BossBar.Color color) {
        return switch (color) {
            case PINK -> BossEvent.BossBarColor.PINK;
            case BLUE -> BossEvent.BossBarColor.BLUE;
            case RED -> BossEvent.BossBarColor.RED;
            case GREEN -> BossEvent.BossBarColor.GREEN;
            case YELLOW -> BossEvent.BossBarColor.YELLOW;
            case PURPLE -> BossEvent.BossBarColor.PURPLE;
            case WHITE -> BossEvent.BossBarColor.WHITE;
        };
    }

    public static BossBar.Color asAdventure(final BossEvent.BossBarColor color) {
        return switch (color) {
            case PINK -> BossBar.Color.PINK;
            case BLUE -> BossBar.Color.BLUE;
            case RED -> BossBar.Color.RED;
            case GREEN -> BossBar.Color.GREEN;
            case YELLOW -> BossBar.Color.YELLOW;
            case PURPLE -> BossBar.Color.PURPLE;
            case WHITE -> BossBar.Color.WHITE;
        };
    }

    public static BossEvent.BossBarOverlay asVanilla(final BossBar.Overlay overlay) {
        return switch (overlay) {
            case PROGRESS -> BossEvent.BossBarOverlay.PROGRESS;
            case NOTCHED_6 -> BossEvent.BossBarOverlay.NOTCHED_6;
            case NOTCHED_10 -> BossEvent.BossBarOverlay.NOTCHED_10;
            case NOTCHED_12 -> BossEvent.BossBarOverlay.NOTCHED_12;
            case NOTCHED_20 -> BossEvent.BossBarOverlay.NOTCHED_20;
        };
    }

    public static BossBar.Overlay asAdventure(final BossEvent.BossBarOverlay overlay) {
        return switch (overlay) {
            case PROGRESS -> BossBar.Overlay.PROGRESS;
            case NOTCHED_6 -> BossBar.Overlay.NOTCHED_6;
            case NOTCHED_10 -> BossBar.Overlay.NOTCHED_10;
            case NOTCHED_12 -> BossBar.Overlay.NOTCHED_12;
            case NOTCHED_20 -> BossBar.Overlay.NOTCHED_20;
        };
    }*/

    public static void setFlag(final BossBar bar, final BossBar.Flag flag, final boolean value) {
        if (value) {
            bar.addFlag(flag);
        } else {
            bar.removeFlag(flag);
        }
    }

    // Book

    public static ItemStack asItemStack(final Book book, final Locale locale) {
        final ItemStack item = new ItemStack(Items.WRITTEN_BOOK, 1);
        final NBTTagCompound tag = item.getOrCreateTag();
        tag.setString("title", asPlain(book.title(), locale));
        tag.setString("author", asPlain(book.author(), locale));
        final NBTTagList pages = new NBTTagList();
        if (book.pages().size() > CraftMetaBook.MAX_PAGES) {
            throw new IllegalArgumentException("Book provided had " + book.pages().size() + " pages, but is only allowed a maximum of " + CraftMetaBook.MAX_PAGES);
        }
        for (final Component page : book.pages()) {
            pages.add(MCUtils.valueOf(validateField(asJsonString(page, locale), CraftMetaBook.MAX_PAGE_LENGTH, "page")));
        }
        tag.set("pages", pages);
        return item;
    }

    @SuppressWarnings({"SameParameterValue", "ConstantConditions"})
    private static String validateField(final String content, final int length, final String name) {
        if (content == null) {
            return content;
        }

        final int actual = content.length();
        if (actual > length) {
            throw new IllegalArgumentException("Field '" + name + "' has a maximum length of " + length + " but was passed '" + content + "', which was " + actual + " characters long.");
        }
        return content;
    }

    /* Sounds

    public static SoundSource asVanilla(final Sound.Source source) {
        if (source == Sound.Source.MASTER) {
            return SoundSource.MASTER;
        } else if (source == Sound.Source.MUSIC) {
            return SoundSource.MUSIC;
        } else if (source == Sound.Source.RECORD) {
            return SoundSource.RECORDS;
        } else if (source == Sound.Source.WEATHER) {
            return SoundSource.WEATHER;
        } else if (source == Sound.Source.BLOCK) {
            return SoundSource.BLOCKS;
        } else if (source == Sound.Source.HOSTILE) {
            return SoundSource.HOSTILE;
        } else if (source == Sound.Source.NEUTRAL) {
            return SoundSource.NEUTRAL;
        } else if (source == Sound.Source.PLAYER) {
            return SoundSource.PLAYERS;
        } else if (source == Sound.Source.AMBIENT) {
            return SoundSource.AMBIENT;
        } else if (source == Sound.Source.VOICE) {
            return SoundSource.VOICE;
        }
        throw new IllegalArgumentException(source.name());
    }

    public static @Nullable SoundSource asVanillaNullable(final Sound.@Nullable Source source) {
        if (source == null) {
            return null;
        }
        return asVanilla(source);
    }*/

    // NBT

    public static @Nullable BinaryTagHolder asBinaryTagHolder(final @Nullable NBTTagCompound tag) {
        if (tag == null) {
            return null;
        }
        try {
            return BinaryTagHolder.encode(tag, NBT_CODEC);
        } catch (final IOException e) {
            return null;
        }
    }

    /* Colors

    public static @NotNull TextColor asAdventure(final EnumChatFormat formatting) {
        final Integer color = formatting.getColor();
        if (color == null) {
            throw new IllegalArgumentException("Not a valid color");
        }
        return TextColor.color(color);
    }

    public static @Nullable EnumChatFormat asVanilla(final TextColor color) {
        return EnumChatFormat.getByHexValue(color.value());
    }*/
}
