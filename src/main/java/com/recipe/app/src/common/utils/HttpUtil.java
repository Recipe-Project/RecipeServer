package com.recipe.app.src.common.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpUtil {

    public static JSONObject getHTTP(String apiURL, Map<String, String> requestHeaders) throws IOException, ParseException {

        URL url = new URL(apiURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        for (Map.Entry<String, String> rqheader : requestHeaders.entrySet()) {
            connection.setRequestProperty(rqheader.getKey(), rqheader.getValue());
        }

        return getHTTPResponse(connection);
    }

    public static JSONObject postHTTP(String apiURL, String request) throws IOException, ParseException {

        URL url = new URL(apiURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(request);
        bw.flush();
        bw.close();

        return getHTTPResponse(connection);
    }

    private static JSONObject getHTTPResponse(HttpURLConnection connection) throws IOException, ParseException {

        InputStreamReader streamReader;
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) { // 정상 호출
            streamReader = new InputStreamReader(connection.getInputStream());
        } else { // 에러 발생
            streamReader = new InputStreamReader(connection.getErrorStream());
        }

        BufferedReader lineReader = new BufferedReader(streamReader);
        StringBuilder responseBody = new StringBuilder();

        String line;
        while ((line = lineReader.readLine()) != null) {
            responseBody.append(line);
        }

        String body = responseBody.toString();

        connection.disconnect();

        return (JSONObject) new JSONParser().parse(body);
    }
}
