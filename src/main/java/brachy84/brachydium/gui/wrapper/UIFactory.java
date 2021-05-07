package brachy84.brachydium.gui.wrapper;

import brachy84.brachydium.api.util.BrachydiumRegistry;
import brachy84.brachydium.gui.ModularGui;
import brachy84.brachydium.gui.api.IUIHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;

public abstract class UIFactory<T extends IUIHolder> {

    public static final Identifier UI_SYNC_ID = new Identifier("brachydium", "modular_gui");
    public static final BrachydiumRegistry<Identifier, UIFactory<?>> UI_FACTORY_REGISTRY = new BrachydiumRegistry<>();

    public final void openUI(T uiHolder, ServerPlayerEntity player) {
        ModularGui gui = uiHolder.createUi(player);

        PacketByteBuf buf = PacketByteBufs.create();
        writeHolderToSyncData(buf, uiHolder);
        Identifier factoryId = UI_FACTORY_REGISTRY.tryGetKey(this);
        Objects.requireNonNull(factoryId);
        buf.writeIdentifier(factoryId);

        ServerPlayNetworking.send(player, UI_SYNC_ID, buf);

    }

    @Environment(EnvType.CLIENT)
    public final void openClientUi(PacketByteBuf buf) {
        T uiHolder = readHolderFromSyncData(buf);
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        ModularGui gui = createUITemplate(uiHolder, player);
        gui.initWidgets();

        ModularGuiScreen screen = new ModularGuiScreen(gui);
        screen.initializeInteractables();
        MinecraftClient.getInstance().openScreen(new ModularGuiScreen(gui));
    }

    public abstract ModularGui createUITemplate(T holder, PlayerEntity entityPlayer);

    @Environment(EnvType.CLIENT)
    public abstract T readHolderFromSyncData(PacketByteBuf syncData);

    public abstract void writeHolderToSyncData(PacketByteBuf syncData, T holder);
}
