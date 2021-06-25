package brachy84.brachydium.api.item;

import brachy84.brachydium.api.material.Material;

@FunctionalInterface
public interface ColorProvider {
    int getColor(int layer, Material material);

    static ColorProvider allLayers() {
        return (layer, material) -> material.color.asInt();
    }

    static ColorProvider noLayer() {
        return (layer, material) -> -1;
    }

    static ColorProvider onlyLayer(int layer) {
        return (layer1, material) -> layer1 == layer ? material.color.asInt() : -1;
    }

    static ColorProvider allButLayer(int layer) {
        return (layer1, material) -> layer1 == layer ? -1 : material.color.asInt();
    }
}
