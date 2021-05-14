package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.energy.IEnergyContainer2;
import brachy84.brachydium.api.energy.IPrimitiveEnergyContainer;
import io.github.astrarre.access.v0.fabric.WorldAccess;
import io.github.astrarre.util.v0.api.Id;

public class BrachydiumAccess {

    public static final WorldAccess<IPrimitiveEnergyContainer> PRIMITIVE_ENERGY_WORLD = new WorldAccess<>(id("primitive_energy_world"));
    public static final WorldAccess<IEnergyContainer2> ENERGY_WORLD =  new WorldAccess<>(id("energy_world"), null);

    static {
        PRIMITIVE_ENERGY_WORLD.addWorldProviderFunctions();
        ENERGY_WORLD.addWorldProviderFunctions();
    }

    private static Id id(String name) {
        return Id.create("brachydium", name);
    }
}
