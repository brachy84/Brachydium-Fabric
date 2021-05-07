package brachy84.brachydium.api.energy;

import io.github.astrarre.transfer.v0.api.Participant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple energy container similar to what forge uses
 */
public interface IPrimitiveEnergyContainer extends Participant<Long> {

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
    default long changeEnergy(long dif) {
        if(getStoredEnergy() + dif > getEnergyCapacity()) {
            long oldAmount = getStoredEnergy();
            setStoredEnergy(getEnergyCapacity());
            return getEnergyCapacity() - oldAmount;
        }

        if(getStoredEnergy() + dif < 0) {
            long oldAmount = getStoredEnergy();
            setStoredEnergy(0);
            return oldAmount;
        }

        setStoredEnergy(getStoredEnergy() + dif);
        return dif;
    }

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
     * @param amount the new amount of this container
     */
    void setStoredEnergy(long amount);

    /**
     * @return maximum amount of storeable energy
     */
    long getEnergyCapacity();

}
