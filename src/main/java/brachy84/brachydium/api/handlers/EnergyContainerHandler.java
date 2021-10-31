package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.blockEntity.trait.TileTrait;
import brachy84.brachydium.api.energy.IEnergyContainer;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyContainerHandler extends TileTrait implements IEnergyContainer {

    private final long capacity;
    private long stored;
    private final long inputVoltage;
    private final long outputVoltage;

    public EnergyContainerHandler(TileEntity tile, long capacity, long stored, long inputVoltage, long outputVoltage) {
        super(tile);
        this.capacity = capacity;
        this.stored = stored;
        this.inputVoltage = inputVoltage;
        this.outputVoltage = outputVoltage;
    }

    public EnergyContainerHandler(TileEntity tile, long capacity, long voltage, boolean input) {
        this(tile, capacity, 0, input ? voltage : 0, input ? 0 : voltage);
    }

    @Override
    public boolean canBeExtracted() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public long changeEnergy(long dif) {
        return 0;
    }

    @Override
    public long getStoredEnergy() {
        return stored;
    }

    @Override
    public long getEnergyCapacity() {
        return capacity;
    }

    @Override
    public long getInputVoltage() {
        return inputVoltage;
    }

    @Override
    public long getOutputVoltage() {
        return outputVoltage;
    }

    @Override
    public long getInputAmperage() {
        return 0;
    }

    @Override
    public long getOutputAmperage() {
        return 0;
    }

    @Override
    public long insertEnergyFromNetwork(Direction direction, long voltage, long amperage) {
        return 0;
    }

    /*@Override
    public BlockApiHolder<?, ?>[] getApis() {
        return new BlockApiHolder[] {
                new BlockApiHolder(MechTechHandlers.ENERGY, (tile, direction) -> this)
        };
    }*/

    @Override
    public void addApis(BlockEntityType<BlockEntityHolder> type) {

    }

    @Override
    public NbtCompound serializeTag() {
        NbtCompound tag = new NbtCompound();
        tag.putLong("EnergyStored", stored);
        return tag;
    }

    @Override
    public void deserializeTag(NbtCompound tag) {
        stored = tag.getLong("EnergyStored");
    }

}
