package brachy84.brachydium.api.blockEntity.trait;

import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.energy.IEnergyContainer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

public class EnergyContainerHandler extends TileTrait implements IEnergyContainer {

    private long voltage;
    private long amps;

    private long storedEu;
    private long capacity;

    public EnergyContainerHandler(TileEntity tile) {
        super(tile);
    }

    @Override
    public void addApis(BlockEntityType<BlockEntityHolder> type) {

    }

    @Override
    public NbtCompound serializeTag() {
        return null;
    }

    @Override
    public void deserializeTag(NbtCompound tag) {

    }

    @Override
    public long getInputVoltage() {
        return voltage;
    }

    @Override
    public long getOutputVoltage() {
        return voltage;
    }

    @Override
    public long getInputAmperage() {
        return amps;
    }

    @Override
    public long getOutputAmperage() {
        return amps;
    }

    @Override
    public long insertEnergyFromNetwork(Direction direction, long voltage, long amperage) {
        return 0;
    }

    @Override
    public boolean canBeExtracted() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public long changeEnergy(long dif) {
        return 0;
    }

    @Override
    public long getStoredEnergy() {
        return storedEu;
    }

    public void setStoredEnergy(long amount) {
        storedEu = amount;
    }

    @Override
    public long getEnergyCapacity() {
        return capacity;
    }
}
