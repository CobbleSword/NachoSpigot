package dev.cobblesword.nachospigot.commons;

import java.io.*;

public class FileUtils {

    public static void toFile(Object object, File file) {
        final String jsonContent = GsonUtils.getGsonPretty().toJson(object);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(jsonContent);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static <T> T toObject(File file, Class<T> clazz) {
        String line;
        StringBuilder jsonContent = new StringBuilder();
        BufferedReader objReader = null;
        try {
            objReader = new BufferedReader(new FileReader(file));
            while ((line = objReader.readLine()) != null) {
                jsonContent.append(line);
            }
            return GsonUtils.getGsonPretty().fromJson(jsonContent.toString(), clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}