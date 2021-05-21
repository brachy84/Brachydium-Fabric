package brachy84.brachydium;

import brachy84.brachydium.api.BrachydiumInitializer;
import brachy84.brachydium.api.blockEntity.MTBlockEntities;
import brachy84.brachydium.api.blockEntity.MetaBlockEntityUIFactory;
import brachy84.brachydium.api.item.tool.Tools;
import brachy84.brachydium.api.recipe.RecipeLoadEvent;
import brachy84.brachydium.api.recipe.RecipeTables;
import brachy84.brachydium.api.render.Textures;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.gui.ServerUi;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Brachydium implements ModInitializer {

    public static final String MOD_ID = "brachydium";
    public static final String NAME = "Brachydium";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create(MOD_ID + ":generated");

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static final List<BrachydiumInitializer> plugins = new ArrayList<>();

    @Override
    public void onInitialize() {
        plugins.addAll(FabricLoader.getInstance().getEntrypoints("main", BrachydiumInitializer.class));
        System.out.println("--------------------------------------------------");
        for(BrachydiumInitializer plugin : plugins) {
            RecipeLoadEvent.EVENT.register(plugin::registerRecipes);
            plugin.registerMaterials();
            plugin.registerGeneral();
        }
        ServerUi.init();
        Textures.init();
        RecipeTables.init();
        Tools.register();
        MetaBlockEntityUIFactory.INSTANCE.init();
        MTBlockEntities.init();

        RRPHelper.initOtherResources();
        //RESOURCE_PACK.dump(new File("brachydium_assets"));
        RRPCallback.EVENT.register(a -> a.add(RESOURCE_PACK));
        System.out.println("--------------------------------------------------");


    }
}
