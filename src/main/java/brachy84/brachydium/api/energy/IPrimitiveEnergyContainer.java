package brachy84.brachydium.api.energy;

import io.github.astrarre.transfer.v0.api.Participant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple energy container similar to what forge uses
 */
public interface IPrimitiveEnergyContainer {

    /**
     * @return if energy can be extracted (generator)
     */
    boolean canBeExtracted();

    /**
     * @return if energy can be received (machine)
     */
    boolean canReceive();

    /**
     * changes the stored energy by a given amount
     * @param dif amount of energy to change
     * @return amount of changed energy
     */
    long changeEnergy(long dif);

    /**
     * removes stored energy by the given amount
     * @param amount to remove
     * @return energy removed
     */
    default long removeEnergy(long amount) {
        return -changeEnergy(Math.min(amount, -amount));
    }

    /**
     * adds stored energy by the given amount
     * @param amount to add
     * @return energy added
     */
    default long addEnergy(long amount) {
        return changeEnergy(Math.max(amount, -amount));
    }

    /**
     * @return amount of currently stored energy
     */
    long getStoredEnergy();

    /**
     * @return maximum amount of storeable energy
     */
    long getEnergyCapacity();

}
