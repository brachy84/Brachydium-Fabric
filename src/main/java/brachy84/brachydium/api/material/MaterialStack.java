package brachy84.brachydium.api.material;

import java.util.Objects;

public class MaterialStack {

    protected final Material material;
    protected final int amount;

    public MaterialStack(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    public MaterialStack copy(int amount) {
        return new MaterialStack(material, amount);
    }

    public MaterialStack copy() {
        return new MaterialStack(material, amount);
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialStack that = (MaterialStack) o;
        return amount == that.amount && material == that.material;
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, amount);
    }
}
