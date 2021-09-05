package brachy84.brachydium;

import brachy84.brachydium.api.BrachydiumInitializer;
import brachy84.brachydium.api.gui.TileEntityUiFactory;
import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.api.recipe.RecipeLoadEvent;
import brachy84.brachydium.api.render.Textures;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.resource.ResourceReloadListener;
import brachy84.brachydium.api.tag.LoadableTag;
import brachy84.brachydium.gui.Networking;
import brachy84.brachydium.gui.ServerUi;
import brachy84.brachydium.gui.UiFactoryRegistry;
import brachy84.brachydium.gui.api.ISyncedWidget;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Brachydium implements ModInitializer {

    public static final String MOD_ID = "brachydium";
    public static final String NAME = "Brachydium";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create(MOD_ID + ":generated");

    private static String currentRegisteringMod = "NONE";

    private static boolean tagsLoaded = false;

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static final List<BrachydiumInitializer> plugins = new ArrayList<>();

    @Override
    public void onInitialize() {
        /*ServerPlayNetworking.registerGlobalReceiver(ResourceReloadListener.RELOAD_CHANNEL, ((server, player, handler, buf, responseSender) -> {
            Brachydium.LOGGER.info("reloading tags server");
            Brachydium.setTagsLoaded();
            LoadableTag.loadAll();
        }));*/
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ResourceReloadListener.INSTANCE);
        ServerUi.init();
        Textures.init();
        UiFactoryRegistry.register(TileEntityUiFactory.INSTANCE);
        RecipeLoadEvent.EVENT.register(Material::runProcessors);
        plugins.addAll(FabricLoader.getInstance().getEntrypoints("brachydium", BrachydiumInitializer.class));
        System.out.println("--------------------------------------------------");
        for (BrachydiumInitializer plugin : plugins) {
            currentRegisteringMod = plugin.getModId();
            plugin.registerMaterials();
        }
        currentRegisteringMod = "NONE";
        Material.REGISTRY.freeze();
        Material.registerItems();
        Material.registerResources();
        for (BrachydiumInitializer plugin : plugins) {
            currentRegisteringMod = plugin.getModId();
            plugin.registerRecipes();
            plugin.registerGeneral();
        }
        currentRegisteringMod = "NONE";

        RRPHelper.initOtherResources();
        //RESOURCE_PACK.dump(new File("brachydium_assets"));
        RRPCallback.BEFORE_VANILLA.register(a -> a.add(RESOURCE_PACK));
        System.out.println("--------------------------------------------------");
    }

    public static boolean currentPluginIsNone() {
        return currentRegisteringMod.equals("NONE");
    }

    public static String getCurrentPlugin() {
        return currentPluginIsNone() ? "" : currentRegisteringMod;
    }

    @ApiStatus.Internal
    public static void setTagsLoaded() {
        tagsLoaded = true;
    }

    public static boolean areTagsLoaded() {
        return tagsLoaded;
    }

}
