package brachy84.brachydium.api.energy;

import net.minecraft.util.math.Direction;

public interface IEnergyContainer extends IPrimitiveEnergyContainer{

    long getInputVoltage();

    long getOutputVoltage();

    long getInputAmperage();

    long getOutputAmperage();

    long insertEnergyFromNetwork(Direction direction, long voltage, long amperage);

}
