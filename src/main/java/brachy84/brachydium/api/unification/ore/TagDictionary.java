package brachy84.brachydium.api.unification.ore;

import brachy84.brachydium.api.item.MaterialItem;
import brachy84.brachydium.api.unification.TagRegistry;
import brachy84.brachydium.api.unification.material.MarkerMaterials;
import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.material.MaterialRegistry;
import brachy84.brachydium.api.unification.material.Materials;
import brachy84.brachydium.api.unification.material.info.MaterialIconType;
import brachy84.brachydium.api.unification.material.properties.IMaterialProperty;
import brachy84.brachydium.api.unification.material.properties.PropertyKey;
import brachy84.brachydium.api.unification.stack.MaterialStack;
import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static brachy84.brachydium.api.ByValues.M;
import static brachy84.brachydium.api.unification.material.info.MaterialFlags.*;

public class TagDictionary {

    //public static final long M = 3628800;

    //public static class Conditions {
    public static final Predicate<Material> hasToolProperty = mat -> mat.hasProperty(PropertyKey.TOOL);
    public static final Predicate<Material> hasOreProperty = mat -> mat.hasProperty(PropertyKey.ORE);
    public static final Predicate<Material> hasGemProperty = mat -> mat.hasProperty(PropertyKey.GEM);
    public static final Predicate<Material> hasDustProperty = mat -> mat.hasProperty(PropertyKey.DUST);
    public static final Predicate<Material> hasIngotProperty = mat -> mat.hasProperty(PropertyKey.INGOT);
    public static final Predicate<Material> hasBlastProperty = mat -> mat.hasProperty(PropertyKey.BLAST);
    //}

    public static final BiConsumer<Entry, Material> materialItemGenerator = MaterialItem::createAndRegister;

    private final static Map<String, Entry> ENTRIES = new HashMap<>();
    private final static AtomicInteger idCounter = new AtomicInteger(0);

    // Regular Ore Prefix. Ore -> Material is a Oneway Operation! Introduced by Eloraam
    public static final Entry ore = new Entry("ore", -1, null, MaterialIconType.ore, Flags.ENABLE_UNIFICATION, hasOreProperty);
    public static final Entry oreGranite = new Entry("oreGranite", -1, null, MaterialIconType.ore, Flags.ENABLE_UNIFICATION, hasOreProperty);
    public static final Entry oreDiorite = new Entry("oreDiorite", -1, null, MaterialIconType.ore, Flags.ENABLE_UNIFICATION, hasOreProperty);
    public static final Entry oreAndesite = new Entry("oreAndesite", -1, null, MaterialIconType.ore, Flags.ENABLE_UNIFICATION, hasOreProperty);
    public static final Entry oreBlackgranite = new Entry("oreBlackgranite", -1, null, MaterialIconType.ore, Flags.ENABLE_UNIFICATION, hasOreProperty);
    public static final Entry oreRedgranite = new Entry("oreRedgranite", -1, null, MaterialIconType.ore, Flags.ENABLE_UNIFICATION, hasOreProperty);
    public static final Entry oreMarble = new Entry("oreMarble", -1, null, MaterialIconType.ore, Flags.ENABLE_UNIFICATION, hasOreProperty);
    public static final Entry oreBasalt = new Entry("oreBasalt", -1, null, MaterialIconType.ore, Flags.ENABLE_UNIFICATION, hasOreProperty);

    // In case of an Sand-Ores Mod. Ore -> Material is a Oneway Operation!
    public static final Entry oreSand = new Entry("oreSand", -1, null, MaterialIconType.ore, Flags.ENABLE_UNIFICATION, null);
    public static final Entry oreRedSand = new Entry("oreRedSand", -1, null, MaterialIconType.ore, Flags.ENABLE_UNIFICATION, null);
    public static final Entry oreGravel = new Entry("oreGravel", -1, null, MaterialIconType.ore, Flags.ENABLE_UNIFICATION, null);

    // Prefix of the Nether-Ores Mod. Causes Ores to double. Ore -> Material is a Oneway Operation!
    public static final Entry oreNetherrack = new Entry("oreNetherrack", -1, null, MaterialIconType.ore, Flags.ENABLE_UNIFICATION, hasOreProperty);
    // In case of an End-Ores Mod. Ore -> Material is a Oneway Operation!
    public static final Entry oreEndstone = new Entry("oreEndstone", -1, null, MaterialIconType.ore, Flags.ENABLE_UNIFICATION, hasOreProperty);

    public static final Entry crushedCentrifuged = new Entry("crushedCentrifuged", -1, null, MaterialIconType.crushedCentrifuged, Flags.ENABLE_UNIFICATION, hasOreProperty)
            .setRegisterer(materialItemGenerator);
    public static final Entry crushedPurified = new Entry("crushedPurified", -1, null, MaterialIconType.crushedPurified, Flags.ENABLE_UNIFICATION, hasOreProperty)
            .setRegisterer(materialItemGenerator);
    public static final Entry crushed = new Entry("crushed", -1, null, MaterialIconType.crushed, Flags.ENABLE_UNIFICATION, hasOreProperty)
            .setRegisterer(materialItemGenerator);

    // Introduced by Mekanism
    public static final Entry shard = new Entry("shard", -1, null, null, Flags.ENABLE_UNIFICATION, null);
    public static final Entry clump = new Entry("clump", -1, null, null, Flags.ENABLE_UNIFICATION, null);
    public static final Entry reduced = new Entry("reduced", -1, null, null, Flags.ENABLE_UNIFICATION, null);
    public static final Entry crystalline = new Entry("crystalline", -1, null, null, Flags.ENABLE_UNIFICATION, null);

    public static final Entry cleanGravel = new Entry("cleanGravel", -1, null, null, Flags.ENABLE_UNIFICATION, null);
    public static final Entry dirtyGravel = new Entry("dirtyGravel", -1, null, null, Flags.ENABLE_UNIFICATION, null);

    // A hot Ingot, which has to be cooled down by a Vacuum Freezer.
    public static final Entry ingotHot = new Entry("ingotHot", M, null, MaterialIconType.ingotHot, Flags.ENABLE_UNIFICATION, hasBlastProperty.and(mat -> mat.getProperty(PropertyKey.BLAST).getBlastTemperature() > 1750))
            .setRegisterer(materialItemGenerator);
    // A regular Ingot. Introduced by Eloraam
    public static final Entry ingot = new Entry("ingot", M, null, MaterialIconType.ingot, Flags.ENABLE_UNIFICATION, hasIngotProperty)
            .setRegisterer(materialItemGenerator);

    // A regular Gem worth one Dust. Introduced by Eloraam
    public static final Entry gem = new Entry("gem", M, null, MaterialIconType.gem, Flags.ENABLE_UNIFICATION, hasGemProperty)
            .setRegisterer(materialItemGenerator);
    // A regular Gem worth one small Dust. Introduced by TerraFirmaCraft
    public static final Entry gemChipped = new Entry("gemChipped", M / 4, null, MaterialIconType.gemChipped, Flags.ENABLE_UNIFICATION, hasGemProperty)
            .setRegisterer(materialItemGenerator);
    // A regular Gem worth two small Dusts. Introduced by TerraFirmaCraft
    public static final Entry gemFlawed = new Entry("gemFlawed", M / 2, null, MaterialIconType.gemFlawed, Flags.ENABLE_UNIFICATION, hasGemProperty)
            .setRegisterer(materialItemGenerator);
    // A regular Gem worth two Dusts. Introduced by TerraFirmaCraft
    public static final Entry gemFlawless = new Entry("gemFlawless", M * 2, null, MaterialIconType.gemFlawless, Flags.ENABLE_UNIFICATION, hasGemProperty)
            .setRegisterer(materialItemGenerator);
    // A regular Gem worth four Dusts. Introduced by TerraFirmaCraft
    public static final Entry gemExquisite = new Entry("gemExquisite", M * 4, null, MaterialIconType.gemExquisite, Flags.ENABLE_UNIFICATION, hasGemProperty)
            .setRegisterer(materialItemGenerator);

    // 1/4th of a Dust.
    public static final Entry dustSmall = new Entry("dustSmall", M / 4, null, MaterialIconType.dustSmall, Flags.ENABLE_UNIFICATION, hasDustProperty)
            .setRegisterer(materialItemGenerator);
    // 1/9th of a Dust.
    public static final Entry dustTiny = new Entry("dustTiny", M / 9, null, MaterialIconType.dustTiny, Flags.ENABLE_UNIFICATION, hasDustProperty)
            .setRegisterer(materialItemGenerator);
    // Dust with impurities. 1 Unit of Main Material and 1/9 - 1/4 Unit of secondary Material
    public static final Entry dustImpure = new Entry("dustImpure", M, null, MaterialIconType.dustImpure, Flags.ENABLE_UNIFICATION, hasOreProperty)
            .setRegisterer(materialItemGenerator);
    // Pure Dust worth of one Ingot or Gem. Introduced by Alblaka.
    public static final Entry dustPure = new Entry("dustPure", M, null, MaterialIconType.dustPure, Flags.ENABLE_UNIFICATION, hasOreProperty)
            .setRegisterer(materialItemGenerator);
    public static final Entry dust = new Entry("dust", M, null, MaterialIconType.dust, Flags.ENABLE_UNIFICATION, hasDustProperty)
            .setRegisterer(materialItemGenerator);

    // A Nugget. Introduced by Eloraam
    public static final Entry nugget = new Entry("nugget", M / 9, null, MaterialIconType.nugget, Flags.ENABLE_UNIFICATION, hasIngotProperty)
            .setRegisterer(materialItemGenerator);

    // 9 Plates combined in one Item.
    public static final Entry plateDense = new Entry("plateDense", M * 9, null, MaterialIconType.plateDense, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_DENSE) && !mat.hasFlag(NO_SMASHING));
    // 2 Plates combined in one Item
    public static final Entry plateDouble = new Entry("plateDouble", M * 2, null, MaterialIconType.plateDouble, Flags.ENABLE_UNIFICATION, hasIngotProperty.and(mat -> mat.hasFlag(GENERATE_PLATE) && !mat.hasFlag(NO_SMASHING)))
            .setRegisterer(materialItemGenerator);
    // Regular Plate made of one Ingot/Dust. Introduced by Calclavia
    public static final Entry plate = new Entry("plate", M, null, MaterialIconType.plate, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_PLATE))
            .setRegisterer(materialItemGenerator);

    // Round made of 1 Nugget
    public static final Entry round = new Entry("round", M / 9, null, MaterialIconType.round, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_ROUND));
    // Foil made of 1/4 Ingot/Dust.
    public static final Entry foil = new Entry("foil", M / 4, null, MaterialIconType.foil, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_FOIL))
            .setRegisterer(materialItemGenerator);

    // Stick made of an Ingot.
    public static final Entry stickLong = new Entry("stickLong", M, null, MaterialIconType.stickLong, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_LONG_ROD))
            .setRegisterer(materialItemGenerator);
    // Stick made of half an Ingot. Introduced by Eloraam
    public static final Entry stick = new Entry("stick", M / 2, null, MaterialIconType.stick, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_ROD))
            .setRegisterer(materialItemGenerator);

    // consisting out of 1/8 Ingot or 1/4 Stick.
    public static final Entry bolt = new Entry("bolt", M / 8, null, MaterialIconType.bolt, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_BOLT_SCREW))
            .setRegisterer(materialItemGenerator);
    // consisting out of 1/9 Ingot.
    public static final Entry screw = new Entry("screw", M / 9, null, MaterialIconType.screw, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_BOLT_SCREW))
            .setRegisterer(materialItemGenerator);
    // consisting out of 1/2 Stick.
    public static final Entry ring = new Entry("ring", M / 4, null, MaterialIconType.ring, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_RING))
            .setRegisterer(materialItemGenerator);
    // consisting out of 1 Fine Wire.
    public static final Entry springSmall = new Entry("springSmall", M / 4, null, MaterialIconType.springSmall, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_SPRING_SMALL) && !mat.hasFlag(NO_SMASHING))
            .setRegisterer(materialItemGenerator);
    // consisting out of 2 Sticks.
    public static final Entry spring = new Entry("spring", M, null, MaterialIconType.spring, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_SPRING) && !mat.hasFlag(NO_SMASHING))
            .setRegisterer(materialItemGenerator);
    // consisting out of 1/8 Ingot or 1/4 Wire.
    public static final Entry wireFine = new Entry("wireFine", M / 8, null, MaterialIconType.wireFine, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_FINE_WIRE))
            .setRegisterer(materialItemGenerator);
    // consisting out of 4 Plates, 1 Ring and 1 Screw.
    public static final Entry rotor = new Entry("rotor", M * 4, null, MaterialIconType.rotor, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_ROTOR))
            .setRegisterer(materialItemGenerator);
    public static final Entry gearSmall = new Entry("gearSmall", M, null, MaterialIconType.gearSmall, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_SMALL_GEAR))
            .setRegisterer(materialItemGenerator);
    // Introduced by me because BuildCraft has ruined the gear Prefix...
    public static final Entry gear = new Entry("gear", M * 4, null, MaterialIconType.gear, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_GEAR))
            .setRegisterer(materialItemGenerator);
    // 3/4 of a Plate or Gem used to shape a Lens. Normally only used on Transparent Materials.
    public static final Entry lens = new Entry("lens", (M * 3) / 4, null, MaterialIconType.lens, Flags.ENABLE_UNIFICATION, mat -> mat.hasFlag(GENERATE_LENS))
            .setRegisterer(materialItemGenerator);

    // made of 2 Ingots.
    public static final Entry toolHeadSword = new Entry("toolHeadSword", M * 2, null, MaterialIconType.toolHeadSword, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 3 Ingots.
    public static final Entry toolHeadPickaxe = new Entry("toolHeadPickaxe", M * 3, null, MaterialIconType.toolHeadPickaxe, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 1 Ingots.
    public static final Entry toolHeadShovel = new Entry("toolHeadShovel", M, null, MaterialIconType.toolHeadShovel, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 1 Ingots.
    public static final Entry toolHeadUniversalSpade = new Entry("toolHeadUniversalSpade", M, null, MaterialIconType.toolHeadUniversalSpade, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 3 Ingots.
    public static final Entry toolHeadAxe = new Entry("toolHeadAxe", M * 3, null, MaterialIconType.toolHeadAxe, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 2 Ingots.
    public static final Entry toolHeadHoe = new Entry("toolHeadHoe", M * 2, null, MaterialIconType.toolHeadHoe, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 3 Ingots.
    public static final Entry toolHeadSense = new Entry("toolHeadSense", M * 3, null, MaterialIconType.toolHeadSense, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 2 Ingots.
    public static final Entry toolHeadFile = new Entry("toolHeadFile", M * 2, null, MaterialIconType.toolHeadFile, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 6 Ingots.
    public static final Entry toolHeadHammer = new Entry("toolHeadHammer", M * 6, null, MaterialIconType.toolHeadHammer, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 2 Ingots.
    public static final Entry toolHeadSaw = new Entry("toolHeadSaw", M * 2, null, MaterialIconType.toolHeadSaw, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 4 Ingots.
    public static final Entry toolHeadBuzzSaw = new Entry("toolHeadBuzzSaw", M * 4, null, MaterialIconType.toolHeadBuzzSaw, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 1 Ingots.
    public static final Entry toolHeadScrewdriver = new Entry("toolHeadScrewdriver", M, null, MaterialIconType.toolHeadScrewdriver, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 4 Ingots.
    public static final Entry toolHeadDrill = new Entry("toolHeadDrill", M * 4, null, MaterialIconType.toolHeadDrill, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 2 Ingots.
    public static final Entry toolHeadChainsaw = new Entry("toolHeadChainsaw", M * 2, null, MaterialIconType.toolHeadChainsaw, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 4 Ingots.
    public static final Entry toolHeadWrench = new Entry("toolHeadWrench", M * 4, null, MaterialIconType.toolHeadWrench, Flags.ENABLE_UNIFICATION, hasToolProperty);
    // made of 5 Ingots.
    public static final Entry turbineBlade = new Entry("turbineBlade", M * 10, null, MaterialIconType.turbineBlade, Flags.ENABLE_UNIFICATION, hasToolProperty.and(m -> m.hasFlags(GENERATE_BOLT_SCREW, GENERATE_PLATE)));

    public static final Entry paneGlass = new Entry("paneGlass", -1, MarkerMaterials.Color.Colorless, null, Flags.SELF_REFERENCING, null);
    public static final Entry blockGlass = new Entry("blockGlass", -1, MarkerMaterials.Color.Colorless, null, Flags.SELF_REFERENCING, null);

    // Storage Block consisting out of 9 Ingots/Gems/Dusts. Introduced by CovertJaguar
    public static final Entry block = new Entry("block", M * 9, null, MaterialIconType.block, Flags.ENABLE_UNIFICATION, null);

    // Prefix used for Logs. Usually as "logWood". Introduced by Eloraam
    public static final Entry log = new Entry("log", -1, null, null, 0, null);
    // Prefix for Planks. Usually "plankWood". Introduced by Eloraam
    public static final Entry plank = new Entry("plank", -1, null, null, 0, null);

    // Prefix to determine which kind of Rock this is.
    public static final Entry stone = new Entry("stone", -1, Materials.Stone, null, Flags.SELF_REFERENCING, null);
    public static final Entry cobblestone = new Entry("cobblestone", -1, Materials.Stone, null, Flags.SELF_REFERENCING, null);
    // Prefix to determine which kind of Rock this is.
    // Cobblestone Prefix for all Cobblestones.
    public static final Entry stoneCobble = new Entry("stoneCobble", -1, Materials.Stone, null, Flags.SELF_REFERENCING, null);

    public static final Entry frameGt = new Entry("frameGt", (long) (M * 1.375), null, null, Flags.ENABLE_UNIFICATION, material -> material.hasFlag(GENERATE_FRAME));

    public static final Entry pipeTinyFluid = new Entry("pipeTinyFluid", M / 2, null, MaterialIconType.pipeTiny, Flags.ENABLE_UNIFICATION, null);
    public static final Entry pipeSmallFluid = new Entry("pipeSmallFluid", M, null, MaterialIconType.pipeSmall, Flags.ENABLE_UNIFICATION, null);
    public static final Entry pipeNormalFluid = new Entry("pipeNormalFluid", M * 3, null, MaterialIconType.pipeMedium, Flags.ENABLE_UNIFICATION, null);
    public static final Entry pipeLargeFluid = new Entry("pipeLargeFluid", M * 6, null, MaterialIconType.pipeLarge, Flags.ENABLE_UNIFICATION, null);
    public static final Entry pipeHugeFluid = new Entry("pipeHugeFluid", M * 12, null, MaterialIconType.pipeHuge, Flags.ENABLE_UNIFICATION, null);
    public static final Entry pipeQuadrupleFluid = new Entry("pipeQuadrupleFluid", M * 12, null, MaterialIconType.pipeQuadruple, Flags.ENABLE_UNIFICATION, null);
    public static final Entry pipeNonupleFluid = new Entry("pipeNonupleFluid", M * 12, null, MaterialIconType.pipeQuadruple, Flags.ENABLE_UNIFICATION, null);

    public static final Entry pipeTinyItem = new Entry("pipeTinyItem", M / 2, null, MaterialIconType.pipeTiny, Flags.ENABLE_UNIFICATION, null);
    public static final Entry pipeSmallItem = new Entry("pipeSmallItem", M, null, MaterialIconType.pipeSmall, Flags.ENABLE_UNIFICATION, null);
    public static final Entry pipeNormalItem = new Entry("pipeNormalItem", M * 3, null, MaterialIconType.pipeMedium, Flags.ENABLE_UNIFICATION, null);
    public static final Entry pipeLargeItem = new Entry("pipeLargeItem", M * 6, null, MaterialIconType.pipeLarge, Flags.ENABLE_UNIFICATION, null);
    public static final Entry pipeHugeItem = new Entry("pipeHugeItem", M * 12, null, MaterialIconType.pipeHuge, Flags.ENABLE_UNIFICATION, null);

    public static final Entry pipeSmallRestrictive = new Entry("pipeSmallRestrictive", M, null, MaterialIconType.pipeSmall, Flags.ENABLE_UNIFICATION, null);
    public static final Entry pipeNormalRestrictive = new Entry("pipeNormalRestrictive", M * 3, null, MaterialIconType.pipeMedium, Flags.ENABLE_UNIFICATION, null);
    public static final Entry pipeLargeRestrictive = new Entry("pipeLargeRestrictive", M * 6, null, MaterialIconType.pipeLarge, Flags.ENABLE_UNIFICATION, null);

    public static final Entry wireGtHex = new Entry("wireGtHex", M * 8, null, null, Flags.ENABLE_UNIFICATION, null);
    public static final Entry wireGtOctal = new Entry("wireGtOctal", M * 4, null, null, Flags.ENABLE_UNIFICATION, null);
    public static final Entry wireGtQuadruple = new Entry("wireGtQuadruple", M * 2, null, null, Flags.ENABLE_UNIFICATION, null);
    public static final Entry wireGtDouble = new Entry("wireGtDouble", M, null, null, Flags.ENABLE_UNIFICATION, null);
    public static final Entry wireGtSingle = new Entry("wireGtSingle", M / 2, null, null, Flags.ENABLE_UNIFICATION, null);

    public static final Entry cableGtHex = new Entry("cableGtHex", M * 8, null, null, Flags.ENABLE_UNIFICATION, null);
    public static final Entry cableGtOctal = new Entry("cableGtOctal", M * 4, null, null, Flags.ENABLE_UNIFICATION, null);
    public static final Entry cableGtQuadruple = new Entry("cableGtQuadruple", M * 2, null, null, Flags.ENABLE_UNIFICATION, null);
    public static final Entry cableGtDouble = new Entry("cableGtDouble", M, null, null, Flags.ENABLE_UNIFICATION, null);
    public static final Entry cableGtSingle = new Entry("cableGtSingle", M / 2, null, null, Flags.ENABLE_UNIFICATION, null);

    // Special Prefix used mainly for the Crafting Handler.
    public static final Entry craftingLens = new Entry("craftingLens", -1, null, null, 0, null);
    // Used for the 16 dyes. Introduced by Eloraam
    public static final Entry dye = new Entry("dye", -1, null, null, 0, null);

    /**
     * Electric Components.
     *
     * @see MarkerMaterials.Tier
     */
    // Introduced by Calclavia
    public static final Entry battery = new Entry("battery", -1, null, null, 0, null);
    // Introduced by Calclavia
    public static final Entry circuit = new Entry("circuit", -1, null, null, Flags.ENABLE_UNIFICATION, null);
    public static final Entry component = new Entry("component", -1, null, null, Flags.ENABLE_UNIFICATION, null);

    // Used for Gregification Addon TODO Don't do these here post De-Enum

    // Myst Ag Compat
    public static final Entry seed = new Entry("seed", -1, null, MaterialIconType.seed, Flags.ENABLE_UNIFICATION, null);
    public static final Entry crop = new Entry("crop", -1, null, MaterialIconType.crop, Flags.ENABLE_UNIFICATION, null);
    public static final Entry essence = new Entry("essence", -1, null, MaterialIconType.essence, Flags.ENABLE_UNIFICATION, null);

    public static final String DUST_REGULAR = "dustRegular";

    public static class Flags {
        public static final long ENABLE_UNIFICATION = 1;
        public static final long SELF_REFERENCING = 1 << 1;
    }


    static {
        ingotHot.heatDamage = 3.0F;
        ingotHot.maxStackSize = 16;
        gemFlawless.maxStackSize = 32;
        gemExquisite.maxStackSize = 16;

        plateDense.maxStackSize = 7;
        rotor.maxStackSize = 16;
        gear.maxStackSize = 16;

        toolHeadSword.maxStackSize = 16;
        toolHeadPickaxe.maxStackSize = 16;
        toolHeadShovel.maxStackSize = 16;
        toolHeadUniversalSpade.maxStackSize = 16;
        toolHeadAxe.maxStackSize = 16;
        toolHeadHoe.maxStackSize = 16;
        toolHeadSense.maxStackSize = 16;
        toolHeadFile.maxStackSize = 16;
        toolHeadHammer.maxStackSize = 16;
        toolHeadSaw.maxStackSize = 16;
        toolHeadBuzzSaw.maxStackSize = 16;
        toolHeadScrewdriver.maxStackSize = 16;
        toolHeadDrill.maxStackSize = 16;
        toolHeadChainsaw.maxStackSize = 16;
        toolHeadWrench.maxStackSize = 16;

        craftingLens.setMarkerPrefix(true);
        dye.setMarkerPrefix(true);
        battery.setMarkerPrefix(true);
        circuit.setMarkerPrefix(true);

        gem.setIgnored(Materials.Diamond);
        gem.setIgnored(Materials.Emerald);
        gem.setIgnored(Materials.Lapis);
        gem.setIgnored(Materials.NetherQuartz);
        gem.setIgnored(Materials.Coal);

        excludeAllGems(Materials.Charcoal);
        excludeAllGems(Materials.NetherStar);
        excludeAllGems(Materials.EnderPearl);
        excludeAllGems(Materials.EnderEye);
        excludeAllGems(Materials.Flint);

        dust.setIgnored(Materials.Redstone);
        dust.setIgnored(Materials.Glowstone);
        dust.setIgnored(Materials.Gunpowder);
        dust.setIgnored(Materials.Sugar);
        dust.setIgnored(Materials.Bone);
        dust.setIgnored(Materials.Blaze);

        stick.setIgnored(Materials.Wood);
        stick.setIgnored(Materials.Bone);
        stick.setIgnored(Materials.Blaze);
        stick.setIgnored(Materials.Paper);

        ingot.setIgnored(Materials.Iron);
        ingot.setIgnored(Materials.Gold);
        ingot.setIgnored(Materials.Wood);
        ingot.setIgnored(Materials.Paper);

        nugget.setIgnored(Materials.Wood);
        nugget.setIgnored(Materials.Gold);
        nugget.setIgnored(Materials.Paper);
        nugget.setIgnored(Materials.Iron);
        plate.setIgnored(Materials.Paper);

        block.setIgnored(Materials.Iron);
        block.setIgnored(Materials.Gold);
        block.setIgnored(Materials.Lapis);
        block.setIgnored(Materials.Emerald);
        block.setIgnored(Materials.Redstone);
        block.setIgnored(Materials.Diamond);
        block.setIgnored(Materials.Coal);
        block.setIgnored(Materials.Glass);
        block.setIgnored(Materials.Marble);
        block.setIgnored(Materials.GraniteRed);
        block.setIgnored(Materials.Stone);
        block.setIgnored(Materials.Glowstone);
        block.setIgnored(Materials.Endstone);
        block.setIgnored(Materials.Wheat);
        block.setIgnored(Materials.Oilsands);
        block.setIgnored(Materials.Wood);
        block.setIgnored(Materials.RawRubber);
        block.setIgnored(Materials.Clay);
        block.setIgnored(Materials.Brick);
        block.setIgnored(Materials.Bone);
        block.setIgnored(Materials.NetherQuartz);
        block.setIgnored(Materials.Ice);
        block.setIgnored(Materials.Netherrack);
        block.setIgnored(Materials.Concrete);
        block.setIgnored(Materials.Blaze);

        /*ore.addSecondaryMaterial(new MaterialStack(Materials.Stone, dust.materialAmount));
        oreGranite.addSecondaryMaterial(new MaterialStack(Materials.Granite, dust.materialAmount));
        oreDiorite.addSecondaryMaterial(new MaterialStack(Materials.Diorite, dust.materialAmount));
        oreAndesite.addSecondaryMaterial(new MaterialStack(Materials.Andesite, dust.materialAmount));
        oreRedgranite.addSecondaryMaterial(new MaterialStack(Materials.GraniteRed, dust.materialAmount));
        oreBlackgranite.addSecondaryMaterial(new MaterialStack(Materials.GraniteBlack, dust.materialAmount));
        oreBasalt.addSecondaryMaterial(new MaterialStack(Materials.Basalt, dust.materialAmount));
        oreMarble.addSecondaryMaterial(new MaterialStack(Materials.Marble, dust.materialAmount));
        oreSand.addSecondaryMaterial(new MaterialStack(Materials.SiliconDioxide, dustTiny.materialAmount));
        oreRedSand.addSecondaryMaterial(new MaterialStack(Materials.SiliconDioxide, dustTiny.materialAmount));
        oreGravel.addSecondaryMaterial(new MaterialStack(Materials.Flint, dustTiny.materialAmount));
        oreNetherrack.addSecondaryMaterial(new MaterialStack(Materials.Netherrack, dust.materialAmount));
        oreEndstone.addSecondaryMaterial(new MaterialStack(Materials.Endstone, dust.materialAmount));

        crushed.addSecondaryMaterial(new MaterialStack(Materials.Stone, dust.materialAmount));

        toolHeadDrill.addSecondaryMaterial(new MaterialStack(Materials.Steel, plate.materialAmount * 4));
        toolHeadChainsaw.addSecondaryMaterial(new MaterialStack(Materials.Steel, plate.materialAmount * 4 + ring.materialAmount * 2));
        toolHeadWrench.addSecondaryMaterial(new MaterialStack(Materials.Steel, ring.materialAmount + screw.materialAmount * 2));

        pipeTinyFluid.setIgnored(Materials.Wood);
        pipeHugeFluid.setIgnored(Materials.Wood);
        pipeQuadrupleFluid.setIgnored(Materials.Wood);
        pipeNonupleFluid.setIgnored(Materials.Wood);
        plate.setIgnored(Materials.BorosilicateGlass);
        foil.setIgnored(Materials.BorosilicateGlass);*/
    }

    private static void excludeAllGems(Material material) {
        gem.setIgnored(material);
        gemChipped.setIgnored(material);
        gemFlawed.setIgnored(material);
        gemFlawless.setIgnored(material);
        gemExquisite.setIgnored(material);
    }

    public static class Entry {
        public final String name;
        public final int id;

        public final String lowerCaseName;

        public final boolean isUnificationEnabled;
        public final boolean isSelfReferencing;

        private @Nullable
        Predicate<Material> generationCondition;
        public final @Nullable
        MaterialIconType materialIconType;

        public final long materialAmount;

        private BiConsumer<Entry, Material> registerer;

        /**
         * Contains a default material type for self-referencing OrePrefix
         * For self-referencing prefixes, it is always guaranteed for it to be not null
         * <p>
         * NOTE: Ore registrations with self-referencing OrePrefix still can occur with other materials
         */
        public @Nullable
        Material materialType;

        private final Map<String, IOreRegistrationHandler> oreProcessingHandlers = new HashMap<>();
        private final Set<Material> ignoredMaterials = new HashSet<>();
        private final Set<Material> generatedMaterials = new HashSet<>();
        private boolean isMarkerPrefix = false;

        public byte maxStackSize = 64;
        public final List<MaterialStack> secondaryMaterials = new ArrayList<>();
        public float heatDamage = 0.0F; // Negative for Frost Damage

        private String alternativeOreName = null;

        public Entry(String name, long materialAmount, @Nullable Material material, @Nullable MaterialIconType materialIconType, long flags, @Nullable Predicate<Material> condition) {
            Preconditions.checkArgument(!ENTRIES.containsKey(name), "OrePrefix " + name + " already registered!");
            this.name = name;
            this.lowerCaseName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
            this.id = idCounter.getAndIncrement();
            this.materialAmount = materialAmount;
            this.isSelfReferencing = (flags & Flags.SELF_REFERENCING) != 0;
            this.isUnificationEnabled = (flags & Flags.ENABLE_UNIFICATION) != 0;
            this.materialIconType = materialIconType;
            this.generationCondition = condition;
            if (isSelfReferencing) {
                Preconditions.checkNotNull(material, "Material is null for self-referencing OrePrefix");
                this.materialType = material;
            }
            ENTRIES.put(name, this);
        }

        public TagDictionary.Entry setRegisterer(BiConsumer<Entry, Material> registerer) {
            this.registerer = registerer;
            return this;
        }

        public String name() {
            return this.name;
        }

        public Identifier createTagId(Material material) {
            return new Identifier("c", material.toString() + "_" + lowerCaseName + "s");
        }

        /*@Nullable
        public MaterialItem getMaterialItem(Material material) {
            return generatedMaterials.get(material);
        }

        public ItemStack createItemStack(Material material, int amount) {
            MaterialItem item = getMaterialItem(material);
            if(item == null)
                throw new NullPointerException("Material is null");
            return new ItemStack(item, amount);
        }*/

        public void addSecondaryMaterial(MaterialStack secondaryMaterial) {
            Preconditions.checkNotNull(secondaryMaterial, "secondaryMaterial");
            secondaryMaterials.add(secondaryMaterial);
        }

        public void setMarkerPrefix(boolean isMarkerPrefix) {
            this.isMarkerPrefix = isMarkerPrefix;
        }

        public long getMaterialAmount(Material material) {
            if (this == block) {
                //glowstone and nether quartz blocks use 4 gems (dusts)
                if (material == Materials.Glowstone ||
                        material == Materials.NetherQuartz ||
                        material == Materials.Brick ||
                        material == Materials.Clay)
                    return M * 4;
                    //glass, ice and obsidian gain only one dust
                else if (material == Materials.Glass ||
                        material == Materials.Ice ||
                        material == Materials.Obsidian)
                    return M;
            } else if (this == stick) {
                if (material == Materials.Blaze)
                    return M * 4;
                else if (material == Materials.Bone)
                    return M * 5;
            }
            return materialAmount;
        }

        public boolean doGenerateItem(Material material) {
            return !isSelfReferencing && generationCondition != null && !isIgnored(material) && generationCondition.test(material);
        }

        public void setGenerationCondition(@Nullable Predicate<Material> in) {
            generationCondition = in;
        }

        public void setDefaultProcessingHandler(IOreRegistrationHandler processingHandler) {
            addProcessingHandler("default", processingHandler);
        }

        public void addProcessingHandler(String key, IOreRegistrationHandler processingHandler) {
            oreProcessingHandlers.put(Objects.requireNonNull(key), Objects.requireNonNull(processingHandler));
        }

        public boolean removeProcessingHandler(String key) {
            return oreProcessingHandlers.remove(key) != null;
        }

        public <T extends IMaterialProperty<T>> void setDefaultProcessingHandler(PropertyKey<T> propertyKey, TriConsumer<Entry, Material, T> handler) {
            addProcessingHandler("default", propertyKey, handler);
        }

        public <T extends IMaterialProperty<T>> void addProcessingHandler(String key, PropertyKey<T> propertyKey, TriConsumer<Entry, Material, T> handler) {
            addProcessingHandler(key, (orePrefix, material) -> {
                if (material.hasProperty(propertyKey)) {
                    handler.accept(orePrefix, material, material.getProperty(propertyKey));
                }
            });
        }

        public void processOreRegistration(@Nullable Material material) {
            if (this.isSelfReferencing && material == null) {
                material = materialType; //append default material for self-referencing OrePrefix
            }
            if (material != null) {
                generatedMaterials.add(material);
            }
        }

        private static final ThreadLocal<Entry> currentProcessingPrefix = new ThreadLocal<>();
        private static final ThreadLocal<Material> currentMaterial = new ThreadLocal<>();

        public static Entry getCurrentProcessingPrefix() {
            return currentProcessingPrefix.get();
        }

        public static Material getCurrentMaterial() {
            return currentMaterial.get();
        }

        private void runGeneratedMaterialHandlers() {
            currentProcessingPrefix.set(this);
            for (Material registeredMaterial : generatedMaterials) {
                currentMaterial.set(registeredMaterial);
                for (IOreRegistrationHandler registrationHandler : oreProcessingHandlers.values()) {
                    registrationHandler.processMaterial(this, registeredMaterial);
                }
                currentMaterial.set(null);
            }
            //clear generated materials for next pass
            generatedMaterials.clear();
            currentProcessingPrefix.set(null);
        }

        public void setAlternativeOreName(String name) {
            this.alternativeOreName = name;
        }

        public String getAlternativeOreName() {
            return alternativeOreName;
        }

        // todo clean this up
        public String getLocalNameForItem(Material material) {
            String specifiedUnlocalized = "item." + material.toString() + "." + this.name;
            if (I18n.hasTranslation(specifiedUnlocalized))
                return I18n.translate(specifiedUnlocalized);
            String unlocalized = "item.material.oreprefix." + this.name;
            String matLocalized = material.getLocalizedName();
            String formatted = I18n.translate(unlocalized, matLocalized);
            return formatted.equals(unlocalized) ? matLocalized : formatted;
        }

        public boolean isIgnored(Material material) {
            return ignoredMaterials.contains(material);
        }

        public void setIgnored(Material material) {
            ignoredMaterials.add(material);
        }

        public boolean isMarkerPrefix() {
            return isMarkerPrefix;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Entry &&
                    ((Entry) o).name.equals(this.name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static Collection<Entry> values() {
        return ENTRIES.values();
    }

    public static void registerComponents() {
        for (Entry entry : values()) {
            for (Material material : MaterialRegistry.MATERIAL_REGISTRY) {
                if (entry.registerer != null && entry.doGenerateItem(material)) {
                    entry.registerer.accept(entry, material);
                    entry.processOreRegistration(material);
                }
            }
        }
    }

    public static void runMaterialHandlers() {
        registerTag(gem, Materials.Diamond, "diamond");
        registerTag(gem, Materials.Emerald, "emerald");
        registerTag(ingot, Materials.Iron, "iron_ingot");
        registerTag(ingot, Materials.Gold, "gold_ingot");
        for (Entry tagDictionary : ENTRIES.values()) {
            tagDictionary.runGeneratedMaterialHandlers();
        }
    }

    private static void registerTag(Entry tag, Material material, String item) {
        TagRegistry.registerItems(tag.createTagId(material), new Identifier(item));
    }

    public static Entry getEntry(String prefixName) {
        return getEntry(prefixName, null);
    }

    public static Entry getEntry(String prefixName, @Nullable Entry replacement) {
        return ENTRIES.getOrDefault(prefixName, replacement);
    }
}
