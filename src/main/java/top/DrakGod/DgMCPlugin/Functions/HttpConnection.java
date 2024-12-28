package top.DrakGod.DgMCPlugin.Functions;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

/**
 * 处理HTTP连接的类，提供GET和POST请求的方法
 */
public class HttpConnection {

    /**
     * 发送GET请求并返回响应内容
     *
     * @param Url 请求的URL
     * @return 返回响应内容，如果请求失败则返回null
     */
    public static String Get(String Url) {
        String Response = null;
        try {
            // 创建URL对象
            URL URL = new URL(Url);
            // 打开HTTP连接
            HttpURLConnection Connection = (HttpURLConnection) URL.openConnection();
            // 设置请求方法为GET
            Connection.setRequestMethod("GET");
            // 获取响应码
            int ResponseCode = Connection.getResponseCode();

            // 如果响应码为HTTP_OK（200），则读取响应内容
            if (ResponseCode == HttpURLConnection.HTTP_OK) {
                Scanner Scanner = new Scanner(Connection.getInputStream(), StandardCharsets.UTF_8);
                Scanner.useDelimiter("\\A");
                Response = Scanner.hasNext() ? Scanner.next() : "";
                Scanner.close();
            }
        } catch (IOException e) {
            // 捕获并忽略IOException
        }
        return Response;
    }

    /**
     * 发送POST请求并返回响应内容
     *
     * @param Url 请求的URL
     * @param Data 请求的参数
     * @return 返回响应内容，如果请求失败则返回null
     */
    public static String Post(String Url, Map<String, String> Data) {
        String Response = null;
        try {
            // 创建URL对象
            URL URL = new URL(Url);
            // 打开HTTP连接
            HttpURLConnection Connection = (HttpURLConnection) URL.openConnection();
            // 设置请求方法为POST
            Connection.setRequestMethod("POST");
            // 允许输出流
            Connection.setDoOutput(true);
            // 设置请求头的Content-Type
            Connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 构建POST请求的参数
            StringBuilder PostData = new StringBuilder();
            for (Map.Entry<String, String> entry : Data.entrySet()) {
                if (PostData.length() != 0) {
                    PostData.append('&');
                }
                PostData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                PostData.append('=');
                PostData.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            // 获取输出流并写入参数
            try (OutputStream OutputStream = Connection.getOutputStream()) {
                byte[] Input = PostData.toString().getBytes(StandardCharsets.UTF_8);
                OutputStream.write(Input, 0, Input.length);
            }

            // 获取响应码
            int ResponseCode = Connection.getResponseCode();

            // 如果响应码为HTTP_OK（200），则读取响应内容
            if (ResponseCode == HttpURLConnection.HTTP_OK) {
                Scanner Scanner = new Scanner(Connection.getInputStream(), StandardCharsets.UTF_8);
                Scanner.useDelimiter("\\A");
                Response = Scanner.hasNext() ? Scanner.next() : "";
                Scanner.close();
            }
        } catch (IOException e) {
            // 捕获并忽略IOException
        }
        return Response;
    }
}
