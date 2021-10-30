package org.sugarcanemc.sugarcane.util.yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.elier.util.Utils;

public class YamlCommenter {
    private final HashMap<String, String> comments = new HashMap<>();
    private String Header = "";

    /**
     * Add comment to a config option.<br>
     * Supports multiline comments!
     *
     * @param path    Config path to add comment to
     * @param comment Comment to add
     */
    public void addComment(String path, String comment) {
        comments.put(path, comment);
    }

    /**
     * Set the header for this config file
     *
     * @param header Header to add
     */
    public void setHeader(String header) {
        Header = header;
    }

    /**
     * Saves comments to config file
     *
     * @param file File to save to
     * @throws IOException
     */
    public void saveComments(File file) throws IOException {
        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(file.toPath());
        lines.removeIf(s -> s.trim().startsWith("#") || s.trim().length() <= 4);
        lines.add(0, "# " + Header.replace("\n", "\n# ") + "\n");
        for (Map.Entry<String, String> _comment : comments.entrySet()) {
            int line = YamlUtils.findKey(lines, _comment.getKey());
            String prefix = Utils.repeat(" ", getIndentation(lines.get(line))) + "# ";
            boolean noNewline = getIndentation(lines.get(line)) > getIndentation(lines.get(line - 1));
            if (line >= 0)
                lines.add(line, (noNewline ?"":"\n") + prefix + _comment.getValue().replace("\n", "\n" + prefix));
            else System.out.printf("Failed to find key %s in %s!", _comment.getKey(), file);
        }
        String text = String.join("\n", lines);
        FileWriter fw = new FileWriter(file);
        fw.write(text);
        fw.close();
    }

    private int getIndentation(String s){
        if(!s.startsWith(" ")) return 0;
        int i = 0;
        while((s = s.replaceFirst(" ", "")).startsWith(" ")) i++;
        return i+1;
    }
}