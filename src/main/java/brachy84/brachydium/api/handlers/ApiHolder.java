package brachy84.brachydium.api.handlers;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class ApiHolder {

    private final Map<BlockApiLookup<Object, Object>, BlockApiLookup.BlockApiProvider<Object, Object>> API_MAP = new HashMap<>();

    /**
     * Registers an api provider to this holder
     * @param apiLookup to register to
     * @param apiProvider to register
     * @param <A> api type
     * @param <C> context type
     */
    @SuppressWarnings("unchecked")
    protected final <A, C> void registerApiProvider(BlockApiLookup<A, C> apiLookup, BlockApiLookup.BlockApiProvider<A, C> apiProvider) {
        API_MAP.put((BlockApiLookup<Object, Object>) apiLookup, (BlockApiLookup.BlockApiProvider<Object, Object>) apiProvider);
    }

    /**
     * Registers an api to this holder
     * @param apiLookup to register to
     * @param api to register
     * @param <A> api type
     * @param <C> context type
     */
    @SuppressWarnings("unchecked")
    protected final  <A, C> void registerApi(BlockApiLookup<A, C> apiLookup, A api) {
        API_MAP.put((BlockApiLookup<Object, Object>) apiLookup, (world, pos, state, blockEntity, context) -> api);
    }

    @ApiStatus.Internal
    public Set<BlockApiLookup<Object, Object>> getLookups() {
        return Collections.unmodifiableSet(API_MAP.keySet());
    }

    @SuppressWarnings("unchecked")
    @ApiStatus.Internal
    public <A, C> A getApiProvider(BlockApiLookup<A, C> apiLookup, World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, C context) {
        BlockApiLookup.BlockApiProvider<A, C> provider = (BlockApiLookup.BlockApiProvider<A, C>) API_MAP.get(apiLookup);
        if(provider == null) return null;
        return provider.find(world, pos, state, blockEntity, context);
    }

    public abstract void registerApis();
}
