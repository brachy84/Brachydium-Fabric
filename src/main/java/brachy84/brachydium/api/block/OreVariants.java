package brachy84.brachydium.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum OreVariants implements StringIdentifiable {

    STONE("stone", "minecraft:block/stone", Blocks.STONE),
    GRANITE("granite", "minecraft:block/granite", Blocks.GRANITE),
    DIORITE("diorite", "minecraft:block/diorite", Blocks.DIORITE);

    private static Map<Block, OreVariants> map;

    @Nullable
    public static OreVariants getOf(Block block) {
        return map.get(block);
    }

    public final String texturePath;
    public final String name;

    OreVariants(String name, String texturePath, Block block) {
        this.texturePath = texturePath;
        this.name = name;
        lol(block);
    }

    private void lol(Block block) {
        if(map == null)
            map = new HashMap<>();
        map.put(block, this);
    }

    public String getName() {
        return name;
    }

    @Override
    public String asString() {
        return name;
    }
}
