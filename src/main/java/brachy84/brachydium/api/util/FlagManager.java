package brachy84.brachydium.api.util;

import java.util.HashMap;
import java.util.Map;

public class FlagManager {

    private long store;
    private final Map<String, Long> flagMap = new HashMap<>();

    public FlagManager() {
        store = 0L;
    }

    public long createFlag(String name) {
        long val = (long) Math.pow(2, flagMap.size());
        flagMap.put(name.toLowerCase(), val);
        return val;
    }

    public boolean hasFlag(long flag) {
        return (store & flag) >= flag;
    }

    public boolean hasFlag(String flag) {
        return hasFlag(flagMap.get(flag.toLowerCase()));
    }

    public void flag(long flag) {
        store = store | flag;
    }

    public void flag(String flag) {
        flag(flagMap.get(flag.toLowerCase()));
    }
}
