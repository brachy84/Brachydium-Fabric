package brachy84.brachydium.api.blockEntity;

import brachy84.brachydium.api.energy.IEnergyContainer2;
import brachy84.brachydium.api.energy.Voltages;
import brachy84.brachydium.api.handlers.EnergyContainer2Handler;
import brachy84.brachydium.api.render.OrientedCubeRender;
import brachy84.brachydium.api.render.Renderer;
import brachy84.brachydium.api.render.Textures;
import net.minecraft.util.Identifier;

public class TieredMetaBlockEntity extends MetaBlockEntity implements ITiered {

    private final Voltages.Voltage tier;
    private final long maxVoltage;
    protected IEnergyContainer2 energyContainer;

    public TieredMetaBlockEntity(Identifier id, Voltages.Voltage tier) {
        super(id);
        this.tier = tier;
        this.maxVoltage = tier.voltage;
    }

    @Override
    public Renderer getRenderer() {
        return new OrientedCubeRender(Textures.MACHINECASING[getTier()]);
    }

    @Override
    public int getTier() {
        return tier.tier / 10;
    }

    @Override
    public void reinitializeInventories() {
        super.reinitializeInventories();
        energyContainer = new EnergyContainer2Handler(this, maxVoltage * 8, maxVoltage, true);
    }
}
