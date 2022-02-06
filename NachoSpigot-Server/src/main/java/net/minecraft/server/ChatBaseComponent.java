package net.minecraft.server;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public abstract class ChatBaseComponent implements IChatBaseComponent {

    protected List<IChatBaseComponent> siblings = Lists.newArrayList();
    private ChatModifier style;

    public ChatBaseComponent() {}

    public IChatBaseComponent addSibling(IChatBaseComponent ichatbasecomponent) {
        ichatbasecomponent.getChatModifier().setChatModifier(this.getChatModifier());
        this.siblings.add(ichatbasecomponent);
        return this;
    }

    public List<IChatBaseComponent> getSiblings() {
        return this.siblings;
    }

    public IChatBaseComponent addSibling(String s) {
        return this.addSibling(new ChatComponentText(s));
    }

    public IChatBaseComponent setChatModifier(ChatModifier chatmodifier) {
        this.style = chatmodifier;

        for (IChatBaseComponent ichatbasecomponent : this.siblings) {
            ichatbasecomponent.getChatModifier().setChatModifier(this.getChatModifier());
        }

        return this;
    }

    public ChatModifier getChatModifier() {
        if (this.style == null) {
            this.style = new ChatModifier();

            for (IChatBaseComponent ichatbasecomponent : this.siblings) {
                ichatbasecomponent.getChatModifier().setChatModifier(this.style);
            }
        }

        return this.style;
    }

    public Iterator<IChatBaseComponent> iterator() {
        return Iterators.concat(Iterators.forArray(this), a(this.siblings));
    }

    public final String getString() {
        StringBuilder stringbuilder = new StringBuilder();

        for (IChatBaseComponent ichatbasecomponent : this) {
            stringbuilder.append(ichatbasecomponent.getText());
        }

        return stringbuilder.toString();
    }

    public static Iterator<IChatBaseComponent> a(Iterable<IChatBaseComponent> iterable) {
        Iterator iterator = Iterators.concat(Iterators.transform(iterable.iterator(), new Function() {
            public Iterator<IChatBaseComponent> a(IChatBaseComponent ichatbasecomponent) {
                return ichatbasecomponent.iterator();
            }

            public Object apply(Object object) {
                return this.a((IChatBaseComponent) object);
            }
        }));

        iterator = Iterators.transform(iterator, new Function() {
            public IChatBaseComponent a(IChatBaseComponent ichatbasecomponent) {
                IChatBaseComponent ichatbasecomponent1 = ichatbasecomponent.copy();

                ichatbasecomponent1.setChatModifier(ichatbasecomponent1.getChatModifier().n());
                return ichatbasecomponent1;
            }

            public Object apply(Object object) {
                return this.a((IChatBaseComponent) object);
            }
        });
        return iterator;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatBaseComponent)) {
            return false;
        } else {
            ChatBaseComponent chatbasecomponent = (ChatBaseComponent) object;

            return this.siblings.equals(chatbasecomponent.siblings) && this.getChatModifier().equals(chatbasecomponent.getChatModifier());
        }
    }

    public int hashCode() {
        return 31 * this.getChatModifier().hashCode() + this.siblings.hashCode(); // CraftBukkit - fix null pointer
    }

    public String toString() {
        return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
    }
}
