package brachy84.brachydium;

import brachy84.brachydium.api.blockEntity.MTBlockEntities;
import brachy84.brachydium.api.item.tool.Tools;
import brachy84.brachydium.api.recipe.RecipeTables;
import brachy84.brachydium.api.render.Textures;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.common.recipes.TestRecipes;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Brachydium implements ModInitializer {

    public static final String MOD_ID = "brachydium";
    public static final String NAME = "Brachydium";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create(MOD_ID + ":generated");

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        System.out.println("--------------------------------------------------");
        //Textures.init();
        //RecipeTables.init();
        //Tools.register();
        //MTBlockEntities.init();
        //TestRecipes.init();

        //RRPHelper.initOtherResources();
        RESOURCE_PACK.dump(new File("brachydium_assets"));
        RRPCallback.EVENT.register(a -> a.add(RESOURCE_PACK));
        System.out.println("--------------------------------------------------");
    }
}
