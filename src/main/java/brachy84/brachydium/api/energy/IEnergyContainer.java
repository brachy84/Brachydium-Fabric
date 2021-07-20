package brachy84.brachydium.api.energy;

import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public interface IEnergyContainer {

    long getCapacity();

    long getStored();

    /**
     * This will be called every time on insertion and extraction
     * see {@link EnergyPacket}
     * @return energy packet
     */
    EnergyPacket createPacket();

    long insert(@Nullable Transaction transaction, long amount);

    long extract(@Nullable Transaction transaction, long amount);

}
