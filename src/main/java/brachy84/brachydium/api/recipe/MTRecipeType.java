package brachy84.brachydium.api.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class MTRecipeType implements RecipeType<MTRecipe>, RecipeSerializer<MTRecipe> {

    private final Identifier id;

    public MTRecipeType(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public MTRecipe read(Identifier id, JsonObject json) {
        System.out.println("Reading recipe from JSON");
        return null;
    }

    @Override
    public MTRecipe read(Identifier id, PacketByteBuf buf) {
        System.out.println("Reading recipe from Packet");
        return null;
    }

    @Override
    public void write(PacketByteBuf buf, MTRecipe recipe) {
        System.out.println("Writing recipe to Packet");
    }
}
