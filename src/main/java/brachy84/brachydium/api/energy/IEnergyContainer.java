package brachy84.brachydium.api.energy;

import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public interface IEnergyContainer {

    long getCapacity();

    long getStored();

    long insert(@Nullable Transaction transaction, long amount);

    long extract(@Nullable Transaction transaction, long amount);

}
