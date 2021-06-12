package brachy84.brachydium.api.material;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MaterialFlag implements IMaterialFlag<MaterialFlag> {

    private final List<IFlagRegistrationHandler<MaterialFlag>> tagProcessors = new ArrayList<>();
    private final String id;

    public MaterialFlag(String id) {
        this.id = id;
        register();
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    @Override
    public void addProcessor(IFlagRegistrationHandler<MaterialFlag> processor) {
        Objects.requireNonNull(processor);
        tagProcessors.add(processor);
    }

    @Override
    public void runProcessors(Material material) {
        for(IFlagRegistrationHandler<MaterialFlag> processor : tagProcessors) {
            processor.processMaterial(material, this);
        }
    }

    @Override
    public void addResourceProvider(IFlagRegistrationHandler<MaterialFlag> processor) {

    }

    @Override
    public void runResourceProviders(Material material) {

    }

    @Override
    public IMaterialFlag<?>[] getRequiredFlags() {
        return new IMaterialFlag[0];
    }
}
