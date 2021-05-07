package brachy84.brachydium.gui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public interface Serializable {

    void write(CompoundTag tag);

    void read(CompoundTag tag);
}
