package brachy84.brachydium.api.resource;

import java.util.function.Function;

public class RecipeItem {

    protected String key, item;
    Function<NbtItemBuilder, NbtItemBuilder> itemBuilder;
    boolean isTag;

    public RecipeItem(String key, String item, Function<NbtItemBuilder, NbtItemBuilder> itemBuilder) {
        this.key = key;
        this.item = item;
        this.itemBuilder = itemBuilder;
        this.isTag = false;
    }

    public RecipeItem(String key, String item, boolean isTag) {
        this.key = key;
        this.item = item;
        this.itemBuilder = null;
        this.isTag = isTag;
    }

    public RecipeItem(String key, String item) {
        this(key, item, false);
    }

}
