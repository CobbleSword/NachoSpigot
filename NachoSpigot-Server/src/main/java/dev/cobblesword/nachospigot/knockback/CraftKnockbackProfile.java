package dev.cobblesword.nachospigot.knockback;

public class CraftKnockbackProfile implements KnockbackProfile {

    private String name;

    private double horizontal = 0.4D;
    private double vertical = 0.4D;
    private double verticalMin = -1.0D;
    private double verticalMax = 0.4D;
    private double extraHorizontal = 0.5D;
    private double extraVertical = 0.1D;
    private double frictionHorizontal = 0.5D;
    private double frictionVertical = 0.5D;

    private boolean stopSprint = true;

    private double rodHorizontal = 0.4D;
    private double rodVertical = 0.4D;
    private double arrowHorizontal = 0.4D;
    private double arrowVertical = 0.4D;
    private double pearlHorizontal = 0.4D;
    private double pearlVertical = 0.4D;
    private double snowballHorizontal = 0.4D;
    private double snowballVertical = 0.4D;
    private double eggHorizontal = 0.4D;
    private double eggVertical = 0.4D;

    public CraftKnockbackProfile(String name) {
        this.name = name;
    }

    public void save() {
        save(true);
    }
    public void save(boolean projectiles) {

        if (projectiles) {

        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(double horizontal) {
        this.horizontal = horizontal;
    }

    public double getVertical() {
        return vertical;
    }

    public void setVertical(double vertical) {
        this.vertical = vertical;
    }

    public double getVerticalMin() {
        return verticalMin;
    }

    public void setVerticalMin(double verticalMin) {
        this.verticalMin = verticalMin;
    }

    public double getVerticalMax() {
        return verticalMax;
    }

    public void setVerticalMax(double verticalMax) {
        this.verticalMax = verticalMax;
    }

    public double getExtraHorizontal() {
        return extraHorizontal;
    }

    public void setExtraHorizontal(double extraHorizontal) {
        this.extraHorizontal = extraHorizontal;
    }

    public double getExtraVertical() {
        return extraVertical;
    }

    public void setExtraVertical(double extraVertical) {
        this.extraVertical = extraVertical;
    }

    public double getFrictionHorizontal() {
        return frictionHorizontal;
    }

    public void setFrictionHorizontal(double frictionHorizontal) {
        this.frictionHorizontal = frictionHorizontal;
    }

    public double getFrictionVertical() {
        return frictionVertical;
    }

    public void setFrictionVertical(double frictionVertical) {
        this.frictionVertical = frictionVertical;
    }

    public boolean isStopSprint() {
        return stopSprint;
    }

    public void setStopSprint(boolean stopSprint) {
        this.stopSprint = stopSprint;
    }

    public double getRodHorizontal() {
        return rodHorizontal;
    }

    public void setRodHorizontal(double rodHorizontal) {
        this.rodHorizontal = rodHorizontal;
    }

    public double getRodVertical() {
        return rodVertical;
    }

    public void setRodVertical(double rodVertical) {
        this.rodVertical = rodVertical;
    }

    public double getArrowHorizontal() {
        return arrowHorizontal;
    }

    public void setArrowHorizontal(double arrowHorizontal) {
        this.arrowHorizontal = arrowHorizontal;
    }

    public double getArrowVertical() {
        return arrowVertical;
    }

    public void setArrowVertical(double arrowVertical) {
        this.arrowVertical = arrowVertical;
    }

    public double getPearlHorizontal() {
        return pearlHorizontal;
    }

    public void setPearlHorizontal(double pearlHorizontal) {
        this.pearlHorizontal = pearlHorizontal;
    }

    public double getPearlVertical() {
        return pearlVertical;
    }

    public void setPearlVertical(double pearlVertical) {
        this.pearlVertical = pearlVertical;
    }

    public double getSnowballHorizontal() {
        return snowballHorizontal;
    }

    public void setSnowballHorizontal(double snowballHorizontal) {
        this.snowballHorizontal = snowballHorizontal;
    }

    public double getSnowballVertical() {
        return snowballVertical;
    }

    public void setSnowballVertical(double snowballVertical) {
        this.snowballVertical = snowballVertical;
    }

    public double getEggHorizontal() {
        return eggHorizontal;
    }

    public void setEggHorizontal(double eggHorizontal) {
        this.eggHorizontal = eggHorizontal;
    }

    public double getEggVertical() {
        return eggVertical;
    }

    public void setEggVertical(double eggVertical) {
        this.eggVertical = eggVertical;
    }

    public String[] getKnockbackValues() {
        return new String[] {
                "Horizontal§7: " + this.horizontal,
                "Vertical§7: " + this.vertical,
                "VerticalMax§7: " + this.verticalMax,
                "VerticalMin§7: " + this.verticalMin,
                "ExtraHorizontal§7: " + this.extraHorizontal,
                "ExtraVertical§7: " + this.extraVertical,
                "FrictionHorizontal§7: " + this.frictionHorizontal,
                "FrictionVertical§7: " + this.frictionVertical,
                "StopSprint§7: " + this.stopSprint,
        };
    }

    public String[] getProjectilesValues() {
        return new String[] {
                "RodHorizontal§7: " + this.rodHorizontal,
                "RodVertical§7: " + this.rodVertical,
                "ArrowHorizontal§7: " + this.arrowHorizontal,
                "ArrowVertical§7: " + this.arrowVertical,
                "PearlHorizontal§7: " + this.pearlHorizontal,
                "PearlVertical§7: " + this.pearlVertical,
                "SnowballHorizontal§7: " + this.snowballHorizontal,
                "SnowballVertical§7: " + this.snowballVertical,
                "EggHorizontal§7: " + this.eggHorizontal,
                "EggVertical§7: " + this.eggVertical,
        };
    }
}
