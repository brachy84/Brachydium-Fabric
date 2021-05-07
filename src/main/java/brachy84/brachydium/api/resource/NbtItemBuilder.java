package brachy84.brachydium.api.resource;

import java.util.function.Function;

public class NbtItemBuilder {

    private String item = "";

    protected NbtItemBuilder(String item) {
        this.item += "\"item\": \"" + item + "\"";
    }

    public NbtItemBuilder data(Function<GroupBuilder, GroupBuilder> groupBuilder) {
        return group("data", groupBuilder, "{}");
    }

    public NbtItemBuilder remainder(Function<GroupBuilder, GroupBuilder> groupBuilder) {
        return group("remainder", groupBuilder, "{}");
    }

    public NbtItemBuilder group(String name, Function<GroupBuilder, GroupBuilder> groupBuilder, String type) {
        appendComma();
        item += groupBuilder.apply(new GroupBuilder(name, type)).end();
        return this;
    }

    public NbtItemBuilder group(Function<GroupBuilder, GroupBuilder> groupBuilder) {
        appendComma();
        item += groupBuilder.apply(new GroupBuilder()).end();
        return this;
    }

    public String end() {
        return item;
    }

    private void appendComma() {
        String lastChar = Character.toString(item.charAt(item.length() - 1));
        if(",".equals(lastChar) || "{".equals(lastChar) || "[".equals(lastChar) || System.getProperty("line.separator").equals(lastChar)) {
            return;
        }
        item += ",\n";
    }

    public class GroupBuilder {

        private String group = "";
        private String type = "{}"; // {} or []

        private GroupBuilder(String name, String type) {
            this.type = type;
            group += "     \"" + name + "\": " + type.charAt(0) + "";
        }

        private GroupBuilder() {
            this.type = "[]";
            group += "      " + type.charAt(0) +"";
        }

        public GroupBuilder group(String name, Function<GroupBuilder, GroupBuilder> groupBuilder, String type) {
            appendComma();
            group += groupBuilder.apply(new GroupBuilder(name, type)).end();
            return this;
        }

        public GroupBuilder group(Function<GroupBuilder, GroupBuilder> groupBuilder) {
            appendComma();
            group += groupBuilder.apply(new GroupBuilder()).end();
            return this;
        }

        public GroupBuilder entry(String key, String value) {
            appendComma();
            group += "        \"" + key + "\": \"" + value + "\"";
            return this;
        }

        public GroupBuilder entry(String key, int value) {
            appendComma();
            group += "        \"" + key + "\": " + value + "";
            return this;
        }

        public GroupBuilder entry(String key, float value) {
            appendComma();
            group += "        \"" + key + "\": " + value + "";
            return this;
        }

        public String end() {
            group += "      " + type.charAt(1);
            return group;
        }

        private void appendComma() {
            String lastChar = Character.toString(group.charAt(group.length() - 1));
            if(",".equals(lastChar) || "{".equals(lastChar) || "[".equals(lastChar) || System.getProperty("line.separator").equals(lastChar)) {
                return;
            }
            group += ",\n";
        }
    }

}
