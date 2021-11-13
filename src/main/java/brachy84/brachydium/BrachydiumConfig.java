package brachy84.brachydium;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

/**
 * Config class
 * Get a instance at {@link Brachydium#getConfig()}
 */
@Config(name = Brachydium.MOD_ID)
public class BrachydiumConfig implements ConfigData {

    protected BrachydiumConfig() {
    }

    @Comment("If debug messages should be logged")
    public boolean debug = true;

    @ConfigEntry.Category("Misc for convenience")
    public Misc misc = new Misc();

    public static class Misc {
        public boolean showTags = true;
        public boolean showNbt = true;
    }
}
