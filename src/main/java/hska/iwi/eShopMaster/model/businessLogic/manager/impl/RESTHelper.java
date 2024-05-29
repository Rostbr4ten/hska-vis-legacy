package hska.iwi.eShopMaster.model.businessLogic.manager.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RESTHelper {

    public static String get(String endpoint) throws Exception {
        System.out.println("GET " + endpoint);
        try {
            URL url = new URL(endpoint);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            System.out.println(connection.getResponseCode());
            if (connection.getResponseCode() != 200) {
                return "";
            }

            try (InputStream is = connection.getInputStream()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                return br.lines().collect(Collectors.joining());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String post(String endpoint, String body) throws Exception {
        System.out.println("POST " + endpoint + " " + body);
        try {
            URL url = new URL(endpoint);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            System.out.println(connection.getResponseCode());
            System.out.println(connection.getHeaderField("Location"));
            try (InputStream is = connection.getInputStream()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String response = br.lines().collect(Collectors.joining());
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static boolean delete(String endpoint) {
        System.out.println("DELETE " + endpoint);
        try {
            URL url = new URL(endpoint);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            System.out.println(connection.getResponseCode());


            if (connection.getResponseCode() == 200) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}