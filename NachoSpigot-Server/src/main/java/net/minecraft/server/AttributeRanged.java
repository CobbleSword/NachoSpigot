package net.minecraft.server;

public class AttributeRanged extends AttributeBase {

    private final double a;
    public double b; // Spigot
    private String c;

    public AttributeRanged(IAttribute iattribute, String s, double d0, double d1, double d2) {
        super(iattribute, s, d0);
        this.a = d1;
        this.b = d2;
        if (d1 > d2) {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        } else if (d0 < d1) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        } else if (d0 > d2) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }
    }

    public AttributeRanged a(String s) {
        this.c = s;
        return this;
    }

    public String g() {
        return this.c;
    }

    public double a(double d0) {
        d0 = MathHelper.a(d0, this.a, this.b);
        return d0;
    }
}
