package top.DrakGod.DgMCPlugin.Functions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

import top.DrakGod.DgMCPlugin.Global;

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

    public static File Download(String Url, File File) {
        try {
            URL URL = new URL(Url);
            HttpURLConnection Connection = (HttpURLConnection) URL.openConnection();
            Connection.setRequestMethod("GET");

            int ResponseCode = Connection.getResponseCode();
            if (ResponseCode == HttpURLConnection.HTTP_OK) {
                int FileSize = Connection.getContentLength();
                BufferedInputStream InputStream = new BufferedInputStream(Connection.getInputStream());
                FileOutputStream OutputStream = new FileOutputStream(File);

                byte[] DataBuffer = new byte[1024];
                int BytesRead;
                int TotalBytesRead = 0;
                boolean Last_Out = false;
                while ((BytesRead = InputStream.read(DataBuffer, 0, 1024)) != -1) {
                    OutputStream.write(DataBuffer, 0, BytesRead);
                    TotalBytesRead += BytesRead;

                    int progress = (int) ((TotalBytesRead * 100.0) / FileSize);
                    if (progress % 25 == 0 && !Last_Out) {
                        Global.Module_Log_Static("INFO", "§2Download", "§6正在下载文件: " + progress + "%");
                        Last_Out = true;
                    } else if (progress % 25 != 0) {
                        Last_Out = false;
                    }

                    if (!Global.Get_Running_Static()) {
                        InputStream.close();
                        OutputStream.close();
                        File.delete();

                        Global.Module_Log_Static("ERROR", "§2Download", "文件下载被取消!");
                        return null;
                    }
                }
                InputStream.close();
                OutputStream.close();

                Global.Module_Log_Static("INFO", "§2Download", "§a文件下载成功!");
                return File;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
