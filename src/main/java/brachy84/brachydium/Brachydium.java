package brachy84.brachydium;

import brachy84.brachydium.api.BrachydiumInitializer;
import brachy84.brachydium.api.gui.TileEntityUiFactory;
import brachy84.brachydium.api.recipe.RecipeLoadEvent;
import brachy84.brachydium.api.render.Textures;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.resource.ResourceReloadListener;
import brachy84.brachydium.api.unification.TagRegistry;
import brachy84.brachydium.api.unification.material.MaterialRegistry;
import brachy84.brachydium.api.unification.material.Materials;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import brachy84.brachydium.gui.internal.UIFactory;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Brachydium implements ModInitializer {

    public static final String MOD_ID = "brachydium";
    public static final String NAME = "Brachydium";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create(MOD_ID + ":generated");

    private static String currentRegisteringMod = MOD_ID;

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static final List<BrachydiumInitializer> plugins = new ArrayList<>();

    public static final Item VOID_ITEM = new Item(new Item.Settings());

    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ResourceReloadListener.INSTANCE);
        RecipeLoadEvent.EVENT.register(() -> {
            for (BrachydiumInitializer plugin : plugins) {
                currentRegisteringMod = plugin.getModId();
                plugin.registerRecipes();
            }
            currentRegisteringMod = MOD_ID;
        });
        Textures.init();
        UIFactory.register(TileEntityUiFactory.INSTANCE);
        plugins.addAll(FabricLoader.getInstance().getEntrypoints("brachydium", BrachydiumInitializer.class));
        System.out.println("-------------- Loading Brachydium --------------");
        Registry.register(Registry.ITEM, id("void"), VOID_ITEM);

        Materials.register();
        MaterialRegistry.EVENT.invoker().register();
        for (BrachydiumInitializer plugin : plugins) {
            currentRegisteringMod = plugin.getModId();
            plugin.registerMaterials();
            plugin.registerGeneral();
        }
        currentRegisteringMod = MOD_ID;
        //MaterialRegistry.finalizeMaterials(false);
        //TODO: load custom materials (kubeJS, json, ...)

        MaterialRegistry.finalizeMaterials(true);

        TagDictionary.registerComponents();
        TagDictionary.runMaterialHandlers();

        TagRegistry.EVENT.invoker().load();
        RRPHelper.initOtherResources();
        RESOURCE_PACK.dump(new File("brachydium_assets"));
        RRPCallback.BEFORE_VANILLA.register(a -> a.add(RESOURCE_PACK));

        System.out.println("-------------- Finished loading Brachydium --------------");
    }

    public static boolean currentPluginIsNone() {
        return currentRegisteringMod.equals("NONE");
    }

    public static String getCurrentPlugin() {
        return currentPluginIsNone() ? "" : currentRegisteringMod;
    }
}
