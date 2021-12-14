package dev.cobblesword.nachospigot.commons;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;

/**
 * @author Sculas
 */
public class IPUtils {

    public static String getExternalAddress() throws Exception {
        URL aws = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(aws.openStream()));
        return in.readLine();
    }

    public static boolean isAccessible(String external, int port) {
        try (Socket ignored = new Socket(external, port)) {
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}