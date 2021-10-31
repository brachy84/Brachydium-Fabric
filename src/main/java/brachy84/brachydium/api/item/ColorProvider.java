package brachy84.brachydium.api.item;

import brachy84.brachydium.api.unification.material.Material;

@FunctionalInterface
public interface ColorProvider {

    /**
     * Specifies which layers should be colored
     * @param layer texture layer
     * @param material material
     * @return color in rgba format
     */
    int getColor(int layer, Material material);

    static ColorProvider allLayers() {
        return (layer, material) -> material.getMaterialRGB();
    }

    static ColorProvider noLayer() {
        return (layer, material) -> -1;
    }

    static ColorProvider onlyLayer(int layer) {
        return (layer1, material) -> layer1 == layer ? material.getMaterialRGB() : -1;
    }

    static ColorProvider allButLayer(int layer) {
        return (layer1, material) -> layer1 == layer ? -1 : material.getMaterialRGB();
    }
}
