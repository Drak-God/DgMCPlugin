package top.DrakGod.DgMCPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import top.DrakGod.DgMCPlugin.Functions.IDBinds;
import top.DrakGod.DgMCPlugin.Handlers.Commands;
import top.DrakGod.DgMCPlugin.Handlers.Listeners;

/**
 * 主插件类，继承自JavaPlugin，实现Global接口
 */
public class Main extends JavaPlugin implements Global {

    // 命令处理器实例
    public Commands Class_Commands;
    // 监听器实例
    public Listeners Class_Listeners;
    // ID绑定实例
    public IDBinds Class_IDBinds;
    // 插件版本
    public String Version;
    // QQBot的IP地址
    public String QQBotIP;

    /**
     * 插件启用时调用的方法
     */
    @Override
    public void onEnable() {
        // 获取插件版本
        Version = getDescription().getVersion();
        // 记录日志信息
        Server_Log("INFO", "");
        Server_Log("INFO", "§1┏━━  ┏━━┓ §4┏┓┏┓ ┏━━┓   §fDgMCPlugin");
        Server_Log("INFO", "§1┃  ┃ ┃ ━┓ §4┃┗┛┃ ┃      §fv" + Version);
        Server_Log("INFO", "§1┗━━  ┗━━┛ §4┗  ┛ ┗━━┛   §fBy DrakGod");
        Server_Log("INFO", "");

        // 检查数据文件夹是否存在
        Check_Data_Folder();
        // 如果插件未启用，则直接返回
        if (!isEnabled()) {
            return;
        }

        // 获取配置文件
        YamlConfiguration Config = Get_Config();
        // 获取QQBot配置节
        ConfigurationSection QQBot = Config.getConfigurationSection("QQBot");
        // 构建QQBot的IP地址
        QQBotIP = "http://" + QQBot.getString("ip") + ":" + String.valueOf(QQBot.getInt("port"));

        // 初始化命令处理器
        Class_Commands = new Commands();
        // 初始化监听器
        Class_Listeners = new Listeners();
        // 初始化ID绑定处理器
        Class_IDBinds = new IDBinds();

        // 注册监听器
        Plugin_Manager.registerEvents(Class_Listeners, this);
        // 记录日志信息
        Plugin_Log("INFO", "§1Dg§4MC§b专属插件§a已启用!");
    }

    /**
     * 插件禁用时调用的方法
     */
    @Override
    public void onDisable() {
        // 记录日志信息
        Plugin_Log("INFO", "§1Dg§4MC§b专属插件§4已禁用!");
    }

    /**
     * 获取插件文件
     *
     * @return 返回插件文件对象
     */
    public File Get_File() {
        return getFile();
    }

    /**
     * 检查数据文件夹是否存在，如果不存在则创建并复制默认数据文件
     */
    public void Check_Data_Folder() {
        // 获取数据文件夹对象
        File Data_Folder = Get_Data_Folder();
        // 如果数据文件夹不存在
        if (!Data_Folder.exists()) {
            // 记录日志信息
            Plugin_Log("WARN", "未检测到数据文件夹,正在初始化数据文件夹");
            // 创建数据文件夹
            Data_Folder.mkdirs();
            try {
                // 复制默认数据文件
                Copy_Data();
            } catch (IOException e) {
                // 记录日志信息
                Plugin_Log("ERROR", "数据文件夹初始化失败:" + e.toString());
                // 禁用插件
                setEnabled(false);
                return;
            }
            // 记录日志信息
            Plugin_Log("INFO", "数据文件夹初始化成功");
        }
    }

    /**
     * 复制默认数据文件到数据文件夹
     *
     * @throws IOException 如果复制过程中发生I/O错误
     */
    public void Copy_Data() throws IOException {
        try (JarFile Jar_File = new JarFile(Get_Main().Get_File())) {
            // 获取JAR文件中的所有条目
            Enumeration<JarEntry> Entries = Jar_File.entries();
            // 获取数据文件夹对象
            File Data_Folder = Get_Data_Folder();

            // 遍历JAR文件中的所有条目
            while (Entries.hasMoreElements()) {
                JarEntry Entry = Entries.nextElement();
                String Entry_Name = Entry.getName();

                // 如果条目是数据文件夹中的文件
                if (Entry_Name.startsWith("Data/") && !Entry.isDirectory()) {
                    // 获取输入流
                    InputStream Input_Stream = getClass().getClassLoader().getResourceAsStream(Entry_Name);
                    if (Input_Stream != null) {
                        // 创建目标文件对象
                        File Target_File = new File(Data_Folder, Entry_Name.substring(5));
                        // 如果目标文件的父文件夹不存在，则创建父文件夹
                        if (!Target_File.getParentFile().exists()) {
                            Target_File.getParentFile().mkdirs();
                        }
                        // 复制文件
                        Files.copy(Input_Stream, Target_File.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }

            // 关闭JAR文件
            Jar_File.close();
        }
    }
}
