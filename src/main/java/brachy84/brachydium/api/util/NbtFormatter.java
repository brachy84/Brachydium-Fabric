package brachy84.brachydium.api.util;

import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public class NbtFormatter {

    private final String tag;
    private int indent = 0;
    private final List<String> lines = new ArrayList<>();
    private StringBuilder currentLine;

    private NbtFormatter(String tag) {
        this.tag = tag;
        currentLine = new StringBuilder();
    }

    public static List<String> format(NbtCompound compound) {
        NbtFormatter formatter = new NbtFormatter(compound.asString());
        formatter.format();
        return formatter.lines;
    }

    public void format() {
        for (int i = 0; i < tag.length(); i++) {
            char sym = tag.charAt(i);
            switch (sym) {
                case ',' -> {
                    currentLine.append(sym);
                    newLine();
                }
                case '{', '[' -> {
                    currentLine.append(sym);
                    openIndent();
                    newLine();
                }
                case '}', ']' -> {
                    closeIndent();
                    newLine();
                    currentLine.append(sym);
                }
                case ':' -> currentLine.append(sym).append(" ");
                default -> currentLine.append(sym);
            }
        }
        lines.add(currentLine.toString());
    }

    private void openIndent() {
        indent++;
    }

    private void closeIndent() {
        indent--;
    }

    private void newLine() {
        lines.add(currentLine.toString());
        currentLine = new StringBuilder();
        currentLine.append("  ".repeat(Math.max(0, indent)));
    }
}
