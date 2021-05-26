package brachy84.brachydium.api.util;

import java.util.HashMap;
import java.util.Map;

public class FlagManager {

    private long store;
    private final Map<String, Long> flagMap = new HashMap<>();

    public FlagManager() {
        store = 0L;
    }

    public void createFlag(String name) {
        flagMap.put(name.toLowerCase(), (long) Math.pow(flagMap.size(), 2));
    }

    public boolean hasFlag(long flag) {
        return (store & flag) >= flag;
    }

    public boolean hasFlag(String flag) {
        return hasFlag(flagMap.get(flag.toLowerCase()));
    }

    public void flag(long flag) {
        store = store & flag;
    }

    public void flag(String flag) {
        flag(flagMap.get(flag.toLowerCase()));
    }
}
