package hska.iwi.eShopMaster.model.businessLogic.manager.impl;

import java.io.BufferedReader;
import java.io.IOException;
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
    URL url = new URL(endpoint);
    HttpURLConnection connection = null;
    InputStream is = null;
    BufferedReader br = null;
    try {
        connection = (HttpURLConnection) url.openConnection();
        System.out.println(connection.getResponseCode());
        if (connection.getResponseCode() != 200) {
            return "";
        }

        is = connection.getInputStream();
        br = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        return response.toString();

    } catch (Exception e) {
        e.printStackTrace();
        throw e;
    } finally {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

public static String post(String endpoint, String body) throws Exception {
    System.out.println("POST " + endpoint + " " + body);
    URL url = new URL(endpoint);
    HttpURLConnection connection = null;
    OutputStream os = null;
    InputStream is = null;
    BufferedReader br = null;
    try {
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        os = connection.getOutputStream();
        byte[] input = body.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);

        System.out.println(connection.getResponseCode());
        System.out.println(connection.getHeaderField("Location"));

        is = connection.getInputStream();
        br = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        return response.toString();

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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