package top.DrakGod.DgMCPlugin.Functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.xephi.authme.libs.com.icetar.tar.TarEntry;
import fr.xephi.authme.libs.com.icetar.tar.TarInputStream;

import top.DrakGod.DgMCPlugin.Global;

public class QQBot implements Global {

    public boolean Can_Use;
    public String QQBotIP;
    public ConfigurationSection QQBot_Config;

    public String Base_Url = "https://github.com/Mrs4s/go-cqhttp/releases/download/v1.2.0/go-cqhttp_";

    public QQBot() {
        YamlConfiguration Config = Get_Config();
        QQBot_Config = Config.getConfigurationSection("QQBot");
        QQBotIP = "http://" + QQBot_Config.getString("ip") + ":" + String.valueOf(QQBot_Config.getInt("port"));

        new Thread(this::Init).start();
    }

    public void Init() {
        if (!QQBot_Config.getBoolean("enable")) {
            new Thread(this::Start_BotEXE).start();
            return;
        }
        Can_Use = Check_Downloads();
        if (!Check_Downloads()) {
            boolean Success1 = Download();
            boolean Success2 = Install();
            if (!Success1 || !Success2) {
                Module_Log("ERROR", "§bQQBot", "QQBot功能无法使用!");
                return;
            }
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
            File Out = Get_QQBot_File();
            File QQBot_Zip_File = Get_QQBot_ZIP();
            String Zip_Name = QQBot_Zip_File.getName();

            if (Zip_Name.endsWith(".zip")) {
                ZipFile QQBot_Zip = new ZipFile(QQBot_Zip_File);
                Enumeration<? extends ZipEntry> Entries = QQBot_Zip.entries();
                while (Entries.hasMoreElements()) {
                    ZipEntry Entry = Entries.nextElement();
                    String Entry_Name = Entry.getName();

                    if (Entry_Name.contains("go-cqhttp")) {
                        Files.copy(QQBot_Zip.getInputStream(Entry), Out.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                QQBot_Zip.close();
            } else if (Zip_Name.endsWith(".tar.gz")) {
                TarInputStream QQBot_Tar = new TarInputStream(
                    new GZIPInputStream(
                        Files.newInputStream(
                            QQBot_Zip_File.toPath()
                            )
                        )
                    );
                TarEntry Entry;
                while ((Entry = QQBot_Tar.getNextEntry()) != null) {
                    String Entry_Name = Entry.getName();
                    if (Entry_Name.contains("go-cqhttp")) {
                        Files.copy(QQBot_Tar, Out.toPath(), StandardCopyOption.REPLACE_EXISTING); 
                    }
                }
                QQBot_Tar.close();
            } else {
               return false; 
            }
            
            Module_Log("INFO", "§bQQBot_Install", "§a安装QQBot成功!");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void Start() {
        new Thread(this::Start_BotEXE).start();
        new Thread(this::Start_QQBot).start(); 
    }

    public void Start_BotEXE() {
        File QQBot_Folder = Get_QQBot_Folder();
        File Main_EXE = QQBot_Folder.toPath().resolve(QQBot_Config.getString("exefile")).toFile();
        if (!Main_EXE.exists()) {
            Module_Log("ERROR", "§bQQBot", "QQBot主执行文件填写错误!");
            return;
        }
        Main_EXE.setExecutable(true);
        String EXE_file = Main_EXE.getName();

        ProcessBuilder Process_Builder;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            Process_Builder = new ProcessBuilder("cmd", "/K", ".\\" + EXE_file); 
        } else {
            Process_Builder = new ProcessBuilder("sh", "-c", "./" + EXE_file);
        }
        Process_Builder.directory(QQBot_Folder);

        try {
            Process Process = Process_Builder.start();
            Thread Read_Thread = new Thread(() -> BotEXE_Read(Process));
            Read_Thread.start();

            while (true) {
                if (!Get_Running() || !Read_Thread.isAlive()) {
                    Process.children().forEach(child -> child.destroyForcibly());
                    Process.destroyForcibly();
                    break;
                }
                Thread.sleep(100);
            }
        } catch (Exception e) {
            Module_Log("ERROR", "§bQQBot", "QQBot主执行文件出现异常!");
            e.printStackTrace();
        }
        Module_Log("INFO", "§bQQBot", "§6主执行文件已退出");
    }

    public void BotEXE_Read(Process Process) {
        try {
            BufferedReader Reader = new BufferedReader(new InputStreamReader(Process.getInputStream(), "utf-8"));
            String Line; 
            while ((Line = Reader.readLine()) != null) {
                Module_Log("INFO", "§bQQBot_BotEXE", Line);
            }
            Reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Start_QQBot() {
        String QQBot_File = Get_QQBot_File().getName();
        File QQBot_Folder = Get_QQBot_Folder();

        File executable = new File(QQBot_File);
        executable.setExecutable(true);
        
        ProcessBuilder Process_Builder;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            Process_Builder = new ProcessBuilder("cmd", "/K", ".\\" + QQBot_File); 
        } else {
            Process_Builder = new ProcessBuilder("sh", "-c", "./" + QQBot_File);
        }
        Process_Builder.directory(QQBot_Folder);

        try {
            Process Process = Process_Builder.start();
            Thread Read_Thread = new Thread(() -> QQBot_Read(Process));
            Read_Thread.start();

            while (true) {
                if (!Get_Running() || !Read_Thread.isAlive()) {
                    Process.children().forEach(child -> child.destroyForcibly());
                    Process.destroyForcibly();
                    break;
                }
                Thread.sleep(100);
            }
            Module_Log("INFO", "§bQQBot_Bot", "§6QQBot已退出");
        } catch (Exception e) {
            Module_Log("ERROR", "§bQQBot_Bot", "QQBot出现异常!");
            e.printStackTrace();
        }
    }

    public void QQBot_Read(Process Process) {
        try {
            BufferedReader Reader = new BufferedReader(new InputStreamReader(Process.getInputStream(), "utf-8"));
            String Line;
            while ((Line = Reader.readLine()) != null) {
                Matcher Matcher = Pattern.compile("\\[(INFO|WARNING|ERROR)\\]").matcher(Line);
                String Mode = "INFO";
                if (Matcher.find()) {
                    Mode = Matcher.group(1);
                    if (Mode.equalsIgnoreCase("WARNING")) {
                        Mode = "WARN";
                    }
                }

                int Start_Index = Line.indexOf("]:") + 2;
                String Log_Content = Start_Index > 1 ? Line.substring(Start_Index).trim() : Line;

                Module_Log(Mode, "§bQQBot_Bot", Log_Content);
            }
            Reader.close();
        } catch (Exception e) {
        }
    }
}