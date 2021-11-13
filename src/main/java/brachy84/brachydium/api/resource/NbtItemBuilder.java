package brachy84.brachydium.api.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.function.Consumer;

public class NbtItemBuilder {

    public static Consumer<NbtItemBuilder> require(Consumer<NbtItemBuilder> builder) {
        return builder1 -> builder1.group("require", builder);
    }

    public static Consumer<NbtItemBuilder> deny(Consumer<NbtItemBuilder> builder) {
        return builder1 -> builder1.group("require", builder);
    }

    private final JsonObject data = new JsonObject();

    public JsonObject getData() {
        return data;
    }

    public NbtItemBuilder group(String key, Consumer<NbtItemBuilder> builder) {
        NbtItemBuilder subBuilder = new NbtItemBuilder();
        builder.accept(subBuilder);
        data.add(key, subBuilder.data);
        return this;
    }

    public NbtItemBuilder entry(String key, Number value) {
        data.addProperty(key, value);
        return this;
    }

    public NbtItemBuilder entry(String key, String value) {
        data.addProperty(key, value);
        return this;
    }

    public NbtItemBuilder entry(String key, boolean value) {
        data.addProperty(key, value);
        return this;
    }

    public NbtItemBuilder entry(String key, char value) {
        data.addProperty(key, value);
        return this;
    }

    @SafeVarargs
    public final NbtItemBuilder array(String key, Consumer<NbtItemBuilder>... builders) {
        JsonArray jsonArray = new JsonArray();
        for (Consumer<NbtItemBuilder> builder : builders) {
            NbtItemBuilder subBuilder = new NbtItemBuilder();
            builder.accept(subBuilder);
            jsonArray.add(subBuilder.data);
        }
        data.add(key, jsonArray);
        return this;
    }

    public NbtItemBuilder array(String key, Number... values) {
        JsonArray jsonArray = new JsonArray();
        for (Number value : values) {
            jsonArray.add(value);
        }
        data.add(key, jsonArray);
        return this;
    }

    public NbtItemBuilder array(String key, String... values) {
        JsonArray jsonArray = new JsonArray();
        for (String value : values) {
            jsonArray.add(value);
        }
        data.add(key, jsonArray);
        return this;
    }

    public NbtItemBuilder array(String key, boolean... values) {
        JsonArray jsonArray = new JsonArray();
        for (boolean value : values) {
            jsonArray.add(value);
        }
        data.add(key, jsonArray);
        return this;
    }

    public NbtItemBuilder array(String key, char... values) {
        JsonArray jsonArray = new JsonArray();
        for (char value : values) {
            jsonArray.add(value);
        }
        data.add(key, jsonArray);
        return this;
    }
}
