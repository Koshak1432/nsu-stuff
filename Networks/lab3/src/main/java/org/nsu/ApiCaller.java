package org.nsu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCaller {
    public static String getResponseAsJSON(String url) {
        String response;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            InputStream inputStream = connection.getInputStream();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = readResponse(inputStream);
                inputStream.close();
            } else {
                throw new RuntimeException("Bad response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    private static String readResponse(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String input;
        StringBuilder response = new StringBuilder();
        while ((input = in.readLine()) != null) {
            response.append(input);
        }
        return response.toString();
    }
}
