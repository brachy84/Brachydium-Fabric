package brachy84.brachydium.api.handlers;

import brachy84.brachydium.api.blockEntity.BlockEntityHolder;
import brachy84.brachydium.api.blockEntity.TileEntity;
import brachy84.brachydium.api.blockEntity.TileTrait;
import brachy84.brachydium.api.energy.IEnergyContainer2;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyContainer2Handler extends TileTrait implements IEnergyContainer2 {

    private final long capacity;
    private long stored;
    private final long inputVoltage;
    private final long outputVoltage;

    public EnergyContainer2Handler(TileEntity tile, long capacity, long stored, long inputVoltage, long outputVoltage) {
        super(tile);
        this.capacity = capacity;
        this.stored = stored;
        this.inputVoltage = inputVoltage;
        this.outputVoltage = outputVoltage;
    }

    public EnergyContainer2Handler(TileEntity tile, long capacity, long voltage, boolean input) {
        this(tile, capacity, 0, input ? voltage : 0, input ? 0 : voltage);
    }

    @Override
    public String getName() {
        return "EnergyContainerHandler";
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
    public long getStoredEnergy() {
        return stored;
    }

    @Override
    public void setStoredEnergy(long amount) {
        stored = amount;
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

    /*@Override
    public BlockApiHolder<?, ?>[] getApis() {
        return new BlockApiHolder[] {
                new BlockApiHolder(MechTechHandlers.ENERGY, (tile, direction) -> this)
        };
    }*/

    @Override
    public void addApis(BlockEntityType<BlockEntityHolder> type) {
        BrachydiumAccess.ENERGY_WORLD.forBlockEntity(type, ((direction, state, world, pos, entity) -> this));
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

    @Override
    public void extract(@Nullable Transaction transaction, Insertable<Long> insertable) {

    }

    @Override
    public int insert(@Nullable Transaction transaction, @NotNull Long type, int quantity) {
        return 0;
    }
}
