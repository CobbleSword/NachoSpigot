package org.bukkit.craftbukkit.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import net.minecraft.server.*;
import net.minecraft.server.ChatClickable.EnumClickAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CraftChatMessage {
    
    private static final Pattern LINK_PATTERN = Pattern.compile("((?:https?://)?[-\\\\w_.]{2,}\\\\.[a-z]{2,4}.*?(?=[.?!,;:]?(?:" + org.bukkit.ChatColor.COLOR_CHAR + "[§ \\\\n]|$)))");
    private static class StringMessage {
        private static final Map<Character, EnumChatFormat> formatMap;
        private static final Pattern INCREMENTAL_PATTERN = Pattern.compile("(" + org.bukkit.ChatColor.COLOR_CHAR + "[0-9a-fk-or])|(\\n)|((?:https?://)?[-\\\\w_.]{2,}\\\\.[a-z]{2,4}.*?(?=[.?!,;:]?(?:" + org.bukkit.ChatColor.COLOR_CHAR + "[§ \\\\n]|$)))", Pattern.CASE_INSENSITIVE);
        // private static final Pattern INCREMENTAL_PATTERN = Pattern.compile("(§[0-9a-fk-or])|(\\n)|(https?://[^\\s/$.?#].[^\\s§]*)", Pattern.CASE_INSENSITIVE); // KigPaper - better regex

        static {
            Builder<Character, EnumChatFormat> builder = ImmutableMap.builder();
            for (EnumChatFormat format : EnumChatFormat.values()) {
                builder.put(Character.toLowerCase(format.toString().charAt(1)), format);
            }
            formatMap = builder.build();
        }

        private final List<IChatBaseComponent> list = new ArrayList<>();
        private IChatBaseComponent currentChatComponent = new ChatComponentText("");
        private ChatModifier modifier = new ChatModifier();
        private final IChatBaseComponent[] output;
        private int currentIndex;
        private final String message;

        private StringMessage(String message,  boolean keepNewlines) {
            this.message = message;
            if (message == null) {
                output = new IChatBaseComponent[] { currentChatComponent };
                return;
            }
            list.add(currentChatComponent);

            Matcher matcher = INCREMENTAL_PATTERN.matcher(message);
            String match;
            while (matcher.find()) {
                int groupId = 0;
                while ((match = matcher.group(++groupId)) == null) {
                    // NOOP
                }
                appendNewComponent(matcher.start(groupId));
                switch (groupId) {
                case 1:
                    EnumChatFormat format = formatMap.get(match.toLowerCase().charAt(1));
                    if (format == EnumChatFormat.RESET) {
                        modifier = new ChatModifier();
                    } else if (format.isFormat()) {
                        switch (format) {
                        case BOLD:
                            modifier.setBold(true);
                            break;
                        case ITALIC:
                            modifier.setItalic(true);
                            break;
                        case STRIKETHROUGH:
                            modifier.setStrikethrough(true);
                            break;
                        case UNDERLINE:
                            modifier.setUnderline(true);
                            break;
                        case OBFUSCATED:
                            modifier.setRandom(true);
                            break;
                        default:
                            throw new AssertionError("Unexpected message format");
                        }
                    } else { // Color resets formatting
                        modifier = new ChatModifier().setColor(format);
                    }
                    break;
                case 2:
                    if (keepNewlines) {
                        currentChatComponent.addSibling(new ChatComponentText("\n"));
                    } else {
                        currentChatComponent = null;
                    }
                    break;
                case 3:
                    if ( !( match.startsWith( "http://" ) || match.startsWith( "https://" ) ) ) {
                        match = "http://" + match;
                    }
                    modifier.setChatClickable(new ChatClickable(EnumClickAction.OPEN_URL, match));
                    appendNewComponent(matcher.end(groupId));
                    modifier.setChatClickable(null);
                }
                currentIndex = matcher.end(groupId);
            }

            if (currentIndex < message.length()) {
                appendNewComponent(message.length());
            }

            output = list.toArray(new IChatBaseComponent[list.size()]);
        }

        private void appendNewComponent(int index) {
            if (index <= currentIndex) {
                return;
            }
            IChatBaseComponent addition = new ChatComponentText(message.substring(currentIndex, index)).setChatModifier(modifier);
            currentIndex = index;
            modifier = modifier.clone();
            if (currentChatComponent == null) {
                currentChatComponent = new ChatComponentText("");
                list.add(currentChatComponent);
            }
            currentChatComponent.addSibling(addition);
        }

        private IChatBaseComponent[] getOutput() {
            return output;
        }
    }

    public static IChatBaseComponent[] fromString(String message) {
        return fromString(message, false);
    }
    
    public static IChatBaseComponent[] fromString(String message, boolean keepNewlines) {
        return new StringMessage(message, keepNewlines).getOutput();
    }
    
    public static String fromComponent(IChatBaseComponent component)  {
        return fromComponent(component, EnumChatFormat.BLACK);
    }

    public static String fromComponent(IChatBaseComponent component, EnumChatFormat defaultColor) {
        if (component == null) return "";
        StringBuilder out = new StringBuilder();
        // FlamePaper - Limit iterations to 2
        int iterations = 0;

        long start = System.currentTimeMillis();

        for (IChatBaseComponent c : component) {
            if (++iterations > 2) {
                break;
            }
            ChatModifier modifier = c.getChatModifier();
            out.append(modifier.getColor() == null ? defaultColor : modifier.getColor());
            if (modifier.isBold()) {
                out.append(EnumChatFormat.BOLD);
            }
            if (modifier.isItalic()) {
                out.append(EnumChatFormat.ITALIC);
            }
            if (modifier.isUnderlined()) {
                out.append(EnumChatFormat.UNDERLINE);
            }
            if (modifier.isStrikethrough()) {
                out.append(EnumChatFormat.STRIKETHROUGH);
            }
            if (modifier.isRandom()) {
                out.append(EnumChatFormat.OBFUSCATED);
            }
            out.append(c.getText());

            // Neutron client will send a page will a long line of text, this will just escape it if
            // we spend to long loading
            if(System.currentTimeMillis() - start > 10L) {
                break;
            }
        }
        return out.toString().replaceFirst("^(" + defaultColor + ")*", "");
    }

    public static IChatBaseComponent fixComponent(IChatBaseComponent component) {
        Matcher matcher = LINK_PATTERN.matcher("");
        return fixComponent(component, matcher);
    }

    private static IChatBaseComponent fixComponent(IChatBaseComponent component, Matcher matcher) {
        if (component instanceof ChatComponentText) {
            ChatComponentText text = ((ChatComponentText) component);
            String msg = text.g();
            if (matcher.reset(msg).find()) {
                matcher.reset();

                ChatModifier modifier = text.getChatModifier() != null ?
                        text.getChatModifier() : new ChatModifier();
                List<IChatBaseComponent> extras = new ArrayList<>();
                List<IChatBaseComponent> extrasOld = new ArrayList<>(text.a());
                component = text = new ChatComponentText("");

                int pos = 0;
                while (matcher.find()) {
                    String match = matcher.group();

                    if ( !( match.startsWith( "http://" ) || match.startsWith( "https://" ) ) ) {
                        match = "http://" + match;
                    }

                    ChatComponentText prev = new ChatComponentText(msg.substring(pos, matcher.start()));
                    prev.setChatModifier(modifier);
                    extras.add(prev);

                    ChatComponentText link = new ChatComponentText(matcher.group());
                    ChatModifier linkModi = modifier.clone();
                    linkModi.setChatClickable(new ChatClickable(EnumClickAction.OPEN_URL, match));
                    link.setChatModifier(linkModi);
                    extras.add(link);

                    pos = matcher.end();
                }

                ChatComponentText prev = new ChatComponentText(msg.substring(pos));
                prev.setChatModifier(modifier);
                extras.add(prev);
                extras.addAll(extrasOld);

                for (IChatBaseComponent c : extras) {
                    text.addSibling(c);
                }
            }
        }

        List extras = component.a();
        for (int i = 0; i < extras.size(); i++) {
            IChatBaseComponent comp = (IChatBaseComponent) extras.get(i);
            if (comp.getChatModifier() != null && comp.getChatModifier().h() == null) {
                extras.set(i, fixComponent(comp, matcher));
            }
        }

        if (component instanceof ChatMessage) {
            Object[] subs = ((ChatMessage) component).j();
            for (int i = 0; i < subs.length; i++) {
                Object comp = subs[i];
                if (comp instanceof IChatBaseComponent) {
                    IChatBaseComponent c = (IChatBaseComponent) comp;
                    if (c.getChatModifier() != null && c.getChatModifier().h() == null) {
                        subs[i] = fixComponent(c, matcher);
                    }
                } else if (comp instanceof String && matcher.reset((String)comp).find()) {
                    subs[i] = fixComponent(new ChatComponentText((String) comp), matcher);
                }
            }
        }

        return component;
    }

    private CraftChatMessage() {
    }
}
