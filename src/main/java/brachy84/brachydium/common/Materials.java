package brachy84.brachydium.common;

import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.api.material.MaterialFlag;

import static brachy84.brachydium.api.material.Flags.*;

public class Materials {

    private static final MaterialFlag[] METALL = {DUST, INGOT, PLATE, STICK, GEAR, CRAFTING_TOOLS, BOLT_SCREW};

    public static final Material Aluminium = new Material("aluminium", 0x5197C2, METALL);
    public static final Material Copper = new Material("copper", 0xDB9430, METALL)
            .setBlockSettings(5f, 6f, 1);


    public static void init() {

    }

}
