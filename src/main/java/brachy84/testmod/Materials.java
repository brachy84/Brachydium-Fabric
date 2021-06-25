package brachy84.testmod;

import brachy84.brachydium.api.material.*;

//import static brachy84.brachydium.api.material.Flags.*;
import static brachy84.brachydium.api.tag.TagDictionary.*;

public class Materials {

    //public static final Material Aluminium = new Material("aluminium", 0x5197C2, METALL);
    //public static final Material Copper = new Material("copper", 0xDB9430, METALL)
    //        .setBlockSettings(5f, 6f, 1);

    public static final Material Aluminium = Material.builder().registryName("aluminium")
            .color(66, 135, 245)
            .type(MaterialType.METAL)
            .addFlags(Plate)
            .element(Element.Al)
            .build();

    public static final Material Copper = Material.builder().registryName("copper")
            .color(200, 80, 80)
            .type(MaterialType.METAL)
            .addFlags(Plate)
            .element(Element.Cu)
            .build();

    public static void init() {

    }

}
