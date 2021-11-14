package brachy84.brachydium;

import brachy84.brachydium.api.BrachydiumInitializer;
import brachy84.brachydium.api.block.BrachydiumBlocks;
import brachy84.brachydium.api.gui.TileEntityUiFactory;
import brachy84.brachydium.api.item.BrachydiumItem;
import brachy84.brachydium.api.item.BrachydiumItems;
import brachy84.brachydium.api.item.tool.ToolItem;
import brachy84.brachydium.api.render.Textures;
import brachy84.brachydium.api.resource.CraftingRecipe;
import brachy84.brachydium.api.resource.RRPHelper;
import brachy84.brachydium.api.resource.ResourceReloadListener;
import brachy84.brachydium.api.unification.Elements;
import brachy84.brachydium.api.unification.TagRegistry;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.material.MaterialRegistry;
import brachy84.brachydium.api.unification.material.Materials;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import brachy84.brachydium.gui.internal.UIFactory;
import brachy84.brachydium.loaders.tag_processing.IngotProcessor;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static brachy84.brachydium.api.unification.material.Materials.EXT2_METAL;
import static brachy84.brachydium.api.unification.material.Materials.Neutronium;
import static brachy84.brachydium.api.unification.material.info.MaterialFlags.*;

public class Brachydium implements ModInitializer {

    public static final String MOD_ID = "brachydium";
    public static final String NAME = "Brachydium";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create(MOD_ID + ":generated");

    private static BrachydiumConfig config;

    private static String currentRegisteringMod = MOD_ID;
    private static String currentRegisteringModName = NAME;

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static final List<BrachydiumInitializer> plugins = new ArrayList<>();

    // dummy item to load the void texture
    public static final Item VOID_ITEM = new Item(new Item.Settings()) {
        @Override
        public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        }
    };

    @Override
    public void onInitialize() {
        LOGGER.info("-------------- Loading Brachydium --------------");
        AutoConfig.register(BrachydiumConfig.class, Toml4jConfigSerializer::new);
        config = AutoConfig.getConfigHolder(BrachydiumConfig.class).getConfig();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ResourceReloadListener.INSTANCE);

        Textures.init();
        UIFactory.register(TileEntityUiFactory.INSTANCE);
        plugins.addAll(FabricLoader.getInstance().getEntrypoints("brachydium", BrachydiumInitializer.class));
        Registry.register(Registry.ITEM, id("void"), VOID_ITEM);

        Materials.register();
        MaterialRegistry.EVENT.invoker().register();

        runPlugin(BrachydiumInitializer::registerMaterials);
        //MaterialRegistry.finalizeMaterials(false);
        //TODO: load custom materials (kubeJS, json, ...)

        // in case some addon sets the default material to null
        if(Materials.Neutronium == null) {
            Materials.Neutronium = new Material.Builder(127, "neutronium")
                    .ingot(6).fluid()
                    .color(0xFAFAFA)
                    .flags(EXT2_METAL, GENERATE_ROTOR, GENERATE_SMALL_GEAR, GENERATE_FRAME)
                    .element(Elements.Nt)
                    .toolStats(24.0f, 12.0f, 655360, 21)
                    .fluidPipeProperties(1000000, 2800, true)
                    .build();
        }
        MaterialRegistry.finalizeMaterials(true);

        BrachydiumItems.init();
        BrachydiumBlocks.init();
        IngotProcessor.init();
        runPlugin(BrachydiumInitializer::registerGeneral);
        BrachydiumItem.registerItems();
        runPlugin(BrachydiumInitializer::registerRecipes);
        ToolItem.createAndRegister();

        TagDictionary.registerComponents();
        TagDictionary.runMaterialHandlers();

        CraftingRecipe.init();

        TagRegistry.EVENT.invoker().load();

        RRPHelper.initOtherResources();
        //RESOURCE_PACK.dump(new File("brachydium_assets"));
        RRPCallback.BEFORE_VANILLA.register(a -> a.add(RESOURCE_PACK));

        LOGGER.info("-------------- Finished loading Brachydium --------------");
    }

    private static void runPlugin(Consumer<BrachydiumInitializer> pluginConsumer) {
        for (BrachydiumInitializer plugin : plugins) {
            currentRegisteringMod = plugin.getModId();
            currentRegisteringModName = plugin.getModName();
            pluginConsumer.accept(plugin);
        }
        currentRegisteringMod = MOD_ID;
        currentRegisteringModName = NAME;
    }

    public static String getCurrentPlugin() {
        return currentRegisteringMod;
    }

    public static String getCurrentRegisteringModName() {
        return currentRegisteringModName;
    }

    public static BrachydiumConfig getConfig() {
        return config;
    }

    public static boolean isDebug() {
        return config.debug;
    }
}
