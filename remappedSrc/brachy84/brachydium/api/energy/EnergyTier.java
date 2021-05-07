package brachy84.brachydium.api.energy;

public enum EnergyTier {
    ELV(2, "ELV", "Extremely Low Voltage"),
    ULV(8, "ULV", "Ultra Low Voltage"),
    LV(32, "LV", "Low Voltage"),
    MV(128, "MV", "Medium Voltage"),
    HV(512, "HV", "High Voltage"),
    EV(2048, "EV", "Extreme Voltage"),
    IV(8192, "IV", "Insane Voltage"),
    LuV(32768, "LuV", "Ludicrous Voltage"),
    UV(131072, "UV", "Ultimate Voltage"), // BEGONE ZPM !
    GV(524287, "GV", "Galactic Voltage"),
    GMV(2097152, "GMV", "Galactic Medium Voltage"),
    GHV(8388608, "GHV", "Galactic High Voltage"),
    GEV(33554432, "GEV", "Galactic Extreme Voltage"),
    GIV(134217728, "GIV", "Galactic Insane Voltage"),
    UGV(536870912, "UGV", "Ultimate Galactic Voltage"),
    MAX(2147483647, "MAX", "Maximum Voltage");

    private final long voltage;
    private final String shortName;
    private final String longName;

    EnergyTier(int voltage, String shortName, String longName) {
        this.voltage = voltage;
        this.shortName = shortName;
        this.longName = longName;
    }

    public static long voltage(int tier) {
        return values()[tier].voltage;
    }

    public long getVoltage() {
        return voltage;
    }
}
