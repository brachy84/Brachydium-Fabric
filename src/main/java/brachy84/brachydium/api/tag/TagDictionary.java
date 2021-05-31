package brachy84.brachydium.api.tag;

import brachy84.brachydium.api.item.CountableIngredient;
import brachy84.brachydium.api.material.Material;

public class TagDictionary {

    public static final long M = 81000L;

    public static final Entry Ingot;
    public static final Entry Dust;
    public static final Entry SmallDust;
    public static final Entry Block;
    public static final Entry Nugget;
    public static final Entry Plate;

    static {
        Ingot = Entry.of("ingot", M);
        Dust = Entry.of("dust", M);
        SmallDust = Entry.of("small_dust", M / 9);
        Block = Entry.of("block", M * 9);
        Nugget = Entry.of("nugget", M / 9);
        Plate = Entry.of("plate", M * 2);
    }

    public static CountableIngredient unify(Entry entry, int amount) {
        return entry.unify(amount);
    }

    public static class Entry {

        private final long value;
        private final String name;
        private final String tagName;

        public Entry(String name, long value, String tagName) {
            this.name = name;
            this.value = value;
            this.tagName = tagName;
        }

        public static Entry of(String name, long amount) {
            return new Entry(name, amount, name + "s");
        }

        public CountableIngredient unify(int amount) {
            return null;
        }

        public String getStringTag(Material material) {
            return String.format("c:%s_%s", material.getName(), tagName);
        }
    }
}
