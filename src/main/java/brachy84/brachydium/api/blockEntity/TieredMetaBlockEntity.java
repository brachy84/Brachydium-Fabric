package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.energy.EnergyTier;
import brachy84.brachydium.api.energy.IEnergyContainer;
import brachy84.brachydium.api.handlers.EnergyContainerHandler;
import brachy84.brachydium.api.render.OrientedCubeRender;
import brachy84.brachydium.api.render.Renderer;
import brachy84.brachydium.api.render.Textures;
import net.minecraft.util.Identifier;

public class TieredMetaBlockEntity extends MetaBlockEntity implements ITiered {

    private final int tier;
    private final long maxVoltage;
    protected IEnergyContainer energyContainer;

    public TieredMetaBlockEntity(Identifier id, int tier) {
        super(id);
        this.tier = tier;
        this.maxVoltage = EnergyTier.voltage(tier);
    }

    @Override
    public Renderer getRenderer() {
        return new OrientedCubeRender(Textures.MACHINECASING[tier]);
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void reinitializeInventories() {
        super.reinitializeInventories();
        energyContainer = new EnergyContainerHandler(this, maxVoltage * 8, maxVoltage, true);
    }
}
