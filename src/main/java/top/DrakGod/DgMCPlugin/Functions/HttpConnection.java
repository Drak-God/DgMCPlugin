package top.DrakGod.DgMCPlugin.Functions;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class HttpConnection {
    public static String Get(String Url) {
        String Response = null;
        try {
            URL URL = new URL(Url);
            HttpURLConnection Connection = (HttpURLConnection) URL.openConnection();
            Connection.setRequestMethod("GET");

            int ResponseCode = Connection.getResponseCode();
            if (ResponseCode == HttpURLConnection.HTTP_OK) {
                Scanner Scanner = new Scanner(Connection.getInputStream(), StandardCharsets.UTF_8);
                Scanner.useDelimiter("\\A");
                Response = Scanner.hasNext() ? Scanner.next() : "";
                Scanner.close();
            }
        } catch (IOException e) {
        }
        return Response;
    }

    public static String Post(String Url, Map<String, String> Data) {
        String Response = null;
        try {
            URL URL = new URL(Url);
            HttpURLConnection Connection = (HttpURLConnection) URL.openConnection();
            Connection.setRequestMethod("POST");
            Connection.setDoOutput(true);
            Connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            StringBuilder PostData = new StringBuilder();
            for (Map.Entry<String, String> entry : Data.entrySet()) {
                if (PostData.length() != 0) {
                    PostData.append('&');
                }
                PostData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                PostData.append('=');
                PostData.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            try (OutputStream OutputStream = Connection.getOutputStream()) {
                byte[] Input = PostData.toString().getBytes(StandardCharsets.UTF_8);
                OutputStream.write(Input, 0, Input.length);
            }

            int ResponseCode = Connection.getResponseCode();
            if (ResponseCode == HttpURLConnection.HTTP_OK) {
                Scanner Scanner = new Scanner(Connection.getInputStream(), StandardCharsets.UTF_8);
                Scanner.useDelimiter("\\A");
                Response = Scanner.hasNext() ? Scanner.next() : "";
                Scanner.close();
            }
        } catch (IOException e) {
        }
        return Response;
    }
}
