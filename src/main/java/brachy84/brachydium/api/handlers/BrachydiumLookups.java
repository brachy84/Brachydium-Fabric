package brachy84.brachydium.api.handlers;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.blockEntity.IControllable;
import brachy84.brachydium.api.blockEntity.IWorkable;
import brachy84.brachydium.api.energy.IEnergyContainer;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.math.Direction;

public class BrachydiumLookups {

    public static final BlockApiLookup<IEnergyContainer, Direction> ENERGY_CONTAINER = BlockApiLookup.get(Brachydium.id("energy_container"), IEnergyContainer.class, Direction.class);
    public static final BlockApiLookup<IWorkable, Direction> WORKABLE = BlockApiLookup.get(Brachydium.id("workable"), IWorkable.class, Direction.class);
    public static final BlockApiLookup<IControllable, Direction> CONTROLLABLE = BlockApiLookup.get(Brachydium.id("controllable"), IControllable.class, Direction.class);

}
