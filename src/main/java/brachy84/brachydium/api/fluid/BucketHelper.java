package brachy84.brachydium.api.fluid;

import io.github.astrarre.transfer.internal.mixin.BucketItemAccess_AccessFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class BucketHelper {

    public static Fluid getFluid(Item item) {
        if(item instanceof BucketItemAccess_AccessFluid) {
            return ((BucketItemAccess_AccessFluid) item).getFluid();
        }
        return Fluids.EMPTY;
    }

    public static Optional<Item> getBucketForFluid(Fluid fluid) {
        return Registry.ITEM.stream().filter(item -> {
            if(item instanceof BucketItemAccess_AccessFluid) {
                Fluid bucketFluid = ((BucketItemAccess_AccessFluid) item).getFluid();
                return fluid == bucketFluid;
            }
            return false;
        }).findFirst();
    }
}
