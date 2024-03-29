package brachy84.brachydium.api.energy;

import brachy84.brachydium.Brachydium;

public class Voltage {

    //public static final Voltage ZERO = new Voltage(Integer.MIN_VALUE, 0, "ZERO");

    //public static final Voltage ELV = new Voltage(0, 2L, "ELV");
    public static final Voltage ULV = new Voltage(0, 8L, "ULV");
    public static final Voltage LV = new Voltage(1, 32L, "LV");
    public static final Voltage MV = new Voltage(2, 128L, "MV");
    public static final Voltage HV = new Voltage(3, 512L, "HV");
    public static final Voltage EV = new Voltage(4, 2048L, "EV");
    public static final Voltage IV = new Voltage(5, 8192L, "IV");
    public static final Voltage LuV = new Voltage(6, 32768L, "LuV");
    public static final Voltage UV = new Voltage(7, 131072L, "UV"); // BEGONE ZPM !
    public static final Voltage GV = new Voltage(8, 524288L, "GV");
    public static final Voltage GMV = new Voltage(9, 2097152L, "GMV");
    public static final Voltage GHV = new Voltage(10, 8388608L, "GHV");
    public static final Voltage GEV = new Voltage(11, 33554432L, "GEV");
    public static final Voltage GIV = new Voltage(12, 134217728L, "GIV");
    public static final Voltage UGV = new Voltage(13, 536870912L, "UGV");
    public static final Voltage UXGV = new Voltage(14, 2147483648L, "UXGV");

    //             8.589.934.592                     omega     OV
    //            34.359.738.368                high omega    HOV
    //           137.438.953.472             extreme omega    EOV
    //           549.755.813.888              insane omega    IOV
    //         2.199.023.255.552           ludicrous omega   LuOV
    //         8.796.093.022.208            ultimate omega    UOV
    //        35.184.372.088.832            galactic omega    GOV
    //       140.737.488.355.328     galactic medium omega   GMOV
    //       562.949.953.421.312       galactic high omega   GHOV
    //     2.251.799.813.685.248    galactic extreme omega   GEOV
    //     9.007.199.254.740.992     galactic insane omega   GIOV
    //    36.028.797.018.963.968   ultimate galactic omega   UGOV
    //   144.115.188.075.855.872                  infinity     ∞V
    //   576.460.752.303.423.488          squared infinity    ∞²V
    // 2.305.843.009.213.693.952            cubic infinity    ∞³V
    // 9.223.372.036.854.775.807                       cum     CV      max 64 bit int

    public static final Voltage[] VALUES = {
            ULV, LV, MV, HV, EV, IV, LuV, UV, GV, GMV, GHV, GEV, GIV, UGV, UXGV
    };

    public static Voltage getVoltage(int tier) {
        return VALUES[tier];
    }

    public static long get(int tier) {
        return getVoltage(tier).voltage;
    }

    public static Voltage getByVoltage(long voltage) {
        voltage = Math.min(UXGV.voltage, voltage);
        Voltage v = ULV;
        int i = 0;
        while (v.voltage < voltage) {
            v = VALUES[++i];
        }
        return VALUES[Math.min(VALUES.length - 1, ++i)];
    }

    public final int tier;
    public final String shortName;
    public final String langKey;
    public final long voltage;

    public Voltage(int tier, long voltage, String name) {
        this.tier = tier;
        this.voltage = voltage;
        this.shortName = name.toLowerCase();
        this.langKey = Brachydium.MOD_ID + ".voltage." + name + ".name";
    }

    @Override
    public String toString() {
        return shortName;
    }
}
