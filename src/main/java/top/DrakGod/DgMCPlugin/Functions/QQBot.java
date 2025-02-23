package top.DrakGod.DgMCPlugin.Functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import top.DrakGod.DgMCPlugin.Global;
import top.DrakGod.DgMCPlugin.Util.Jython;

public class QQBot implements Global {
    public Jython Python;

    public boolean Can_Use;
    public String QQBotIP;
    public ConfigurationSection QQBot_Config;

    public String Base_Url = "https://github.com/Mrs4s/go-cqhttp/releases/download/v1.2.0/go-cqhttp_";

    public QQBot() {
        YamlConfiguration Config = Get_Config();
        QQBot_Config = Config.getConfigurationSection("QQBot");
        QQBotIP = "http://" + QQBot_Config.getString("ip") + ":" + String.valueOf(QQBot_Config.getInt("port"));

        Path QQBot_Path = Get_Data_Folder().toPath();
        QQBot_Path = QQBot_Path.resolve("Libs");
        Python = new Jython(QQBot_Path.toString());

        new Thread(this::Init).start();
    }

    public void Init() {
        Can_Use = Check_Downloads();
        if (!Check_Downloads()) {
            boolean Success1 = Download();
            boolean Success2 = Install();
            if (!Success1 || !Success2) {
                Module_Log("ERROR", "§bQQBot", "QQBot功能无法使用!");
                return;
            }
            Module_Log("INFO", "§bQQBot", "§6第一次请手动运行创建配置文件(选择3: 反向 Websocket 通信)");
            Can_Use = true;
        }
        Start();
    }

    public File Get_QQBot_Folder() {
        Path QQBot_Path = Get_Data_Folder().toPath();
        QQBot_Path = QQBot_Path.resolve("QQBot");
        return QQBot_Path.toFile();
    }

    public File Get_QQBot_File() {
        Path QQBot_Path = Get_QQBot_Folder().toPath();

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            QQBot_Path = QQBot_Path.resolve("qqbot.exe");
        } else {
            QQBot_Path = QQBot_Path.resolve("qqbot");
        }
        return QQBot_Path.toFile();
    }

    public File Get_QQBot_ZIP() {
        Path QQBot_Path = Get_QQBot_Folder().toPath();

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            QQBot_Path = QQBot_Path.resolve("qqbot.zip");
        } else {
            QQBot_Path = QQBot_Path.resolve("qqbot.tar.gz");
        }
        return QQBot_Path.toFile();
    }

    public String Get_Download_Url() {
        String OS_Name = System.getProperty("os.name").toLowerCase();
        String Arch = System.getProperty("os.arch").toLowerCase();

        if (OS_Name.contains("win")) {
            if (Arch.contains("amd64")) {
                return Base_Url + "windows_amd64.zip";
            } else if (Arch.contains("arm64")) {
                return Base_Url + "windows_arm64.zip";
            } else {
                return Base_Url + "windows_386.zip";
            }
        } else if (OS_Name.contains("mac")) {
            if (Arch.contains("arm64")) {
                return Base_Url + "darwin_arm64.tar.gz";
            } else {
                return Base_Url + "darwin_amd64.tar.gz";
            }
        } else if (OS_Name.contains("linux")) {
            if (Arch.contains("amd64")) {
                return Base_Url + "linux_amd64.tar.gz";
            } else if (Arch.contains("arm64")) {
                return Base_Url + "linux_arm64.tar.gz";
            } else if (Arch.contains("arm")) {
                return Base_Url + "linux_armv7.tar.gz";
            } else {
                return Base_Url + "linux_386.tar.gz";
            }
        }

        return null;
    }

    public boolean Check_Downloads() {
        File QQBot_Flie = Get_QQBot_File();
        if (QQBot_Flie.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean Download() {
        File QQBot_Flie = Get_QQBot_ZIP();

        Module_Log("INFO", "§bQQBot_Download", "§6正在下载QQBot...");
        String Url = Get_Download_Url();
        File Out = HttpConnection.Download(Url, QQBot_Flie);
        if (Out != null) {
            Module_Log("INFO", "§bQQBot_Download", "§a下载QQBot成功!");
            return true;
        }

        Module_Log("ERROR", "§bQQBot_Download", "下载QQBot失败!");
        return false;
    }

    public boolean Install() {
        try {
            ZipFile QQBot_Zip = new ZipFile(Get_QQBot_ZIP());

            Enumeration<? extends ZipEntry> Entries = QQBot_Zip.entries();
            while (Entries.hasMoreElements()) {
                ZipEntry Entry = Entries.nextElement();
                String Entry_Name = Entry.getName();

                if (Entry_Name.contains("go-cqhttp")) {
                    File Out = Get_QQBot_File();
                    Files.copy(QQBot_Zip.getInputStream(Entry), Out.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            QQBot_Zip.close();
            Module_Log("INFO", "§bQQBot_Install", "§a安装QQBot成功!");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void Start() {
        new Thread(this::Start_Py).start();
        new Thread(this::Start_QQBot).start(); 
    }

    public void Start_Py() {
        File Main_Py = new File(QQBot_Config.getString("pyfile"));
        if (!Main_Py.exists()) {
            Module_Log("ERROR", "§bQQBot", "QQBot主py文件填写错误!");
            return;
        }

        boolean Success = Python.Run_Py(Main_Py);
        if (!Success) {
            Module_Log("ERROR", "§bQQBot", "QQBot主py文件运行失败!");
            return;
        }
        Module_Log("INFO", "§bQQBot", "§6主py文件已退出");
    }

    public void Start_QQBot() {
        String QQBot_File = Get_QQBot_File().toString();
        File QQBot_Folder = Get_QQBot_Folder();

        ProcessBuilder Process_Builder;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            Process_Builder = new ProcessBuilder("cmd", "/c", QQBot_File);
        } else {
            Process_Builder = new ProcessBuilder("sh", "-c", QQBot_File);
        }
        Process_Builder.directory(QQBot_Folder);

        try {
            Process Process = Process_Builder.start();

            BufferedReader Reader = new BufferedReader(new InputStreamReader(Process.getInputStream()));
            String Line;
            while ((Line = Reader.readLine()) != null) {
                Module_Log("INFO", "§bQQBot_Bot", Line);
                if (!Get_Running()) {
                    Process.destroy();
                    break;
                }
            }
            Module_Log("INFO", "§bQQBot_Bot", "§6QQBot已退出");
        } catch (Exception e) {
            Module_Log("ERROR", "§bQQBot_Bot", "QQBot出现异常!");
            e.printStackTrace();
        }
    }
}