package org.sugarcanemc.sugarcane.util.yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dev.cobblesword.nachospigot.Nacho;
import dev.cobblesword.nachospigot.commons.StringUtils;

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
     * @param loadWorldConfig Whether to load world configuration comments
     * @throws IOException io
     */
    public void saveComments(File file, boolean loadWorldConfig) throws IOException {
        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(file.toPath());
        lines.removeIf(s -> s.trim().startsWith("#") || s.trim().length() <= 4);
        lines.add(0, "# " + Header.replace("\n", "\n# ") + "\n");
        for (Map.Entry<String, String> _comment : comments.entrySet()) {
            if (!loadWorldConfig && _comment.getKey().startsWith("world-settings.")) continue; // World settings are initialized after NachoConfig, while NachoConfig loads their comments.
            //       This causes an issue on new configs, with comments not working for world settings, and causing an exception due to being unable to find the configuration option.
            int line = YamlUtils.findKey(lines, _comment.getKey());

            if(line == -1) {
                throw new IllegalStateException(String.format(
                        "You are trying to add a comment to key \"%s\" which does not exist!",
                        _comment.getKey()
                ));
            }

            String prefix = StringUtils.repeat(" ", StringUtils.getIndentation(lines.get(line))) + "# ";
            boolean noNewline = StringUtils.getIndentation(lines.get(line)) > StringUtils.getIndentation(lines.get(line - 1));
            if (line >= 0)
                lines.add(line, (noNewline ?"":"\n") + prefix + _comment.getValue().replace("\n", "\n" + prefix));
            else Nacho.LOGGER.warn("Failed to find key %s in %s!", _comment.getKey(), file);
        }
        String text = String.join("\n", lines);
        FileWriter fw = new FileWriter(file);
        fw.write(text);
        fw.close();
    }
}
