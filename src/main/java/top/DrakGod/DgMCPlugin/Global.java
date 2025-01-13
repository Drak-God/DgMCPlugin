package top.DrakGod.DgMCPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 全局接口，定义了一些常用的方法和常量
 */
public interface Global {

    // 获取服务器实例
    public Server Server = Bukkit.getServer();
    // 获取插件管理器实例
    public PluginManager Plugin_Manager = Server.getPluginManager();

    // 获取服务器日志记录器
    public Logger Server_Logger = Server.getLogger();
    // 获取插件日志记录器
    public Logger Plugin_Logger = Bukkit.getLogger();
    // 获取控制台命令发送者
    public CommandSender Console = Server.getConsoleSender();

    /**
     * 获取插件实例
     *
     * @param <T> 插件类的类型
     * @param clazz 插件类的Class对象
     * @return 返回插件实例
     */
    public default <T extends JavaPlugin> T Get_Plugin(@Nonnull Class<T> clazz) {
        return Main.getPlugin(clazz);
    }

    /**
     * 获取主插件实例
     *
     * @return 返回主插件实例
     */
    public default Main Get_Main() {
        return Get_Plugin(Main.class);
    }

    /**
     * 获取数据文件夹
     *
     * @return 返回数据文件夹对象
     */
    public default File Get_Data_Folder() {
        return Get_Main().getDataFolder();
    }

    /**
     * 获取配置文件
     *
     * @return 返回配置文件的YamlConfiguration对象
     */
    public default YamlConfiguration Get_Config() {
        YamlConfiguration Config = Get_Data("config.yml");
        Save_Data(Config, "config.yml");
        return Config;
    }

    /**
     * 向服务器控制台发送日志信息
     *
     * @param Mode 日志级别（INFO, WARN, ERROR）
     * @param Msg 日志消息
     */
    public default void Server_Log(String Mode, String Msg) {
        if (Mode.equalsIgnoreCase("INFO")) {
            Msg = "§f" + Msg;
        } else if (Mode.equalsIgnoreCase("WARN")) {
            Msg = "§e" + Msg;
        } else if (Mode.equalsIgnoreCase("ERROR")) {
            Msg = "§c" + Msg;
        }
        Console.sendMessage(Msg);
    }

    /**
     * 向插件日志发送信息
     *
     * @param Mode 日志级别（INFO, WARN, ERROR）
     * @param Msg 日志消息
     */
    public default void Plugin_Log(String Mode, String Msg) {
        if (Mode.equalsIgnoreCase("INFO")) {
            Msg = "§f" + Msg;
        } else if (Mode.equalsIgnoreCase("WARN")) {
            Msg = "§e" + Msg;
        } else if (Mode.equalsIgnoreCase("ERROR")) {
            Msg = "§c" + Msg;
        }
        Console.sendMessage("§6[§1Dg§4MC§bPlugin§6] " + Msg);
    }

    /**
     * 获取数据文件
     *
     * @param File_Name 数据文件名
     * @return 返回数据文件的YamlConfiguration对象
     */
    public default YamlConfiguration Get_Data(String File_Name) {
        try {
            YamlConfiguration Config = YamlConfiguration.loadConfiguration(new File(Get_Data_Folder(), File_Name));
            if (Config.getKeys(false).isEmpty()) {
                throw new Exception("数据文件: " + File_Name + " 错误");
            } else {
                return Config;
            }
        } catch (Exception e) {
            Plugin_Log("ERROR", "无法加载数据文件: " + File_Name + " " + e.toString());
            try {
                Files.copy(new File(Get_Data_Folder(), File_Name).toPath(),
                        new File(Get_Data_Folder(), File_Name + ".bak").toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                Plugin_Log("WARN", "已使用初始数据文件，源文件已备份至: " + File_Name + ".bak，请检查后重新加载");
            } catch (IOException ex) {
                Plugin_Log("ERROR", "无法备份数据文件: " + File_Name + " " + ex.toString());
            }
            return YamlConfiguration.loadConfiguration(
                    new InputStreamReader(
                            Get_Main().getResource("Data/" + File_Name),
                            StandardCharsets.UTF_8
                    ));
        }
    }

    /**
     * 保存数据文件
     *
     * @param config 要保存的YamlConfiguration对象
     * @param File_Name 数据文件名
     */
    public default void Save_Data(YamlConfiguration config, String File_Name) {
        try {
            config.save(new File(Get_Data_Folder(), File_Name));
        } catch (IOException e) {
            Plugin_Log("ERROR", "无法保存数据文件: " + File_Name + " " + e.toString());
        }
    }
}
