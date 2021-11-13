package brachy84.brachydium.api.util;

import net.minecraft.util.Identifier;

import java.util.List;

public interface ITagHolder {

    void addTag(Identifier id);

    List<Identifier> getTags();
}
