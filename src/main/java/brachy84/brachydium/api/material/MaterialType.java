package brachy84.brachydium.api.material;

import static brachy84.brachydium.api.tag.TagDictionary.*;
//import static brachy84.brachydium.api.material.Flags.*;

public class MaterialType {

    public static final MaterialType UNDEFINED = new MaterialType();
    public static final MaterialType DUST = new MaterialType(Dust, SmallDust);
    public static final MaterialType METAL = new MaterialType(Dust, SmallDust, Ingot, Block, Nugget);
    public static final MaterialType GEM = new MaterialType(Dust, SmallDust, Gem, Block);
    public static final MaterialType FLUID = new MaterialType(MoltenFluid);
    public static final MaterialType GAS = new MaterialType();

    private final IMaterialFlag<?>[] defaultFlags;

    public MaterialType(IMaterialFlag<?>... flags) {
        this.defaultFlags = flags;
    }

    public IMaterialFlag<?>[] getDefaultFlags() {
        return defaultFlags;
    }
}
