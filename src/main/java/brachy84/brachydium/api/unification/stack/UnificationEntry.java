package brachy84.brachydium.api.unification.stack;

import brachy84.brachydium.api.unification.material.Material;
import brachy84.brachydium.api.unification.ore.TagDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class UnificationEntry {

    public final TagDictionary.Entry tagDictionary;
    @Nullable
    public final Material material;

    public UnificationEntry(TagDictionary.Entry tagDictionary, @Nullable Material material) {
        this.tagDictionary = tagDictionary;
        this.material = material;
    }

    public UnificationEntry(TagDictionary.Entry tagDictionary) {
        this.tagDictionary = tagDictionary;
        this.material = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnificationEntry that = (UnificationEntry) o;

        if (tagDictionary != that.tagDictionary) return false;
        return Objects.equals(material, that.material);
    }

    @Override
    public int hashCode() {
        int result = tagDictionary.hashCode();
        result = 31 * result + (material != null ? material.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return tagDictionary.name() + (material != null ? material.toCamelCaseString() : "");
    }

}
