package dev.cobblesword.nachospigot.commons;

import lombok.SneakyThrows;

import java.io.*;

public class FileUtils
{
    @SneakyThrows
    public static void toFile(Object object, File file)
    {
        final String jsonContent = GsonUtils.getGsonPretty().toJson(object);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(jsonContent);
        bw.close();
    }


    @SneakyThrows
    public static <T> T toObject(File file, Class<T> clazz)
    {
        String line;
        StringBuilder jsonContent = new StringBuilder();
        BufferedReader objReader = new BufferedReader(new FileReader(file));

        while ((line = objReader.readLine()) != null) {
            jsonContent.append(line);
        }
        return (T) GsonUtils.getGsonPretty().fromJson(jsonContent.toString(), clazz);

    }
}