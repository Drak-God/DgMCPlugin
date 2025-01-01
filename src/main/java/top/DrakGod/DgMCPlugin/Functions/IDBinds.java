package top.DrakGod.DgMCPlugin.Functions;

import java.util.HashMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import top.DrakGod.DgMCPlugin.Global;
import top.DrakGod.DgMCPlugin.Main;

/**
 * 处理ID绑定相关功能的类
 */
public class IDBinds implements Global {

    // 用于存储ID绑定数据的Yaml配置文件
    public YamlConfiguration IDBinds_Yaml;
    // 用于存储ID绑定数据的HashMap
    public HashMap<String, String> IDBinds_Data;
    // 主插件实例
    public Main CMain;
    // 玩家绑定ID时的踢出消息
    public String KickMsg;
    // 标记QQBot是否正在运行
    public boolean QQBot_Running = false;

    /**
     * 定时任务，检查QQBot是否运行
     */
    public BukkitRunnable Lookup_QQBot = new BukkitRunnable() {
        @Override
        public void run() {
            // 如果QQBot未运行，则尝试通过HTTP连接检查
            if (!QQBot_Running) {
                QQBot_Running = (HttpConnection.Get(CMain.QQBotIP + "/exec(bdip)") != null);
            }
        }
    };

    /**
     * 构造函数，初始化ID绑定相关数据
     */
    public IDBinds() {
        // 获取主插件实例
        CMain = Get_Main();
        // 启动定时任务，每20个游戏刻（约1秒）检查一次QQBot是否运行
        Lookup_QQBot.runTaskTimerAsynchronously(CMain, 0, 20);
        // 加载ID绑定数据
        Load_IDBinds();
        // 获取玩家绑定ID时的踢出消息
        KickMsg = Get_Config().getString("BindKickMsg");
    }

    /**
     * 加载ID绑定数据
     */
    public final void Load_IDBinds() {
        // 从文件中获取ID绑定数据的Yaml配置
        IDBinds_Yaml = Get_Data("IDBinds.yml");
        // 获取ID绑定数据的配置节
        ConfigurationSection IDBinds_Get = IDBinds_Yaml.getConfigurationSection("IDBinds");
        // 初始化ID绑定数据的HashMap
        IDBinds_Data = new HashMap<>();
        // 如果配置节不为空，则遍历并存储ID绑定数据
        if (IDBinds_Get != null) {
            for (String key : IDBinds_Get.getKeys(false)) {
                IDBinds_Data.put(key, IDBinds_Get.getString(key));
            }
        }
    }

    /**
     * 保存ID绑定数据
     */
    public final void Save_IDBinds() {
        // 将ID绑定数据保存到文件中
        Save_Data(IDBinds_Yaml, "IDBinds.yml");
    }

    /**
     * 根据玩家名称获取其绑定的ID
     *
     * @param Name 玩家名称
     * @return 绑定的ID，如果未找到则返回null
     */
    public final String Get_IDBind(String Name) {
        // 调用Get_IDBinds方法获取最新的ID绑定数据
        Get_IDBinds();
        // 检查ID绑定数据中是否包含给定的玩家名称
        if (IDBinds_Data.containsKey(Name)) {
            // 如果包含，则返回该玩家名称对应的ID
            return IDBinds_Data.get(Name);
        } else {
            // 如果不包含，则返回null
            return null;
        }
    }

    /**
     * 获取玩家绑定的ID信息
     *
     * @param Event 玩家登录事件
     * @return 返回绑定信息的字符串，如果未绑定则返回添加绑定的提示信息
     */
    public final String Get_Binded(PlayerLoginEvent Event) {
        // 获取登录的玩家对象
        Player Player = Event.getPlayer();
        // 获取最新的ID绑定数据
        Get_IDBinds();
        // 如果玩家已绑定ID，则返回绑定信息
        if (IDBinds_Data.containsKey(Player.getName())) {
            // 如果玩家已绑定ID，则返回绑定信息
            return "§a你已绑定ID: " + IDBinds_Data.get(Player.getName());
        } else {
            // 如果玩家未绑定ID，则返回添加绑定的提示信息
            return Add_IDBind(Event, Player);
        }
    }

    /**
     * 添加玩家ID绑定
     *
     * @param Event 玩家登录事件
     * @param Player 玩家对象
     * @return 返回添加绑定的提示信息，如果QQBot未运行则返回错误信息
     */
    public final String Add_IDBind(PlayerLoginEvent Event, Player Player) {
        String Out = null;
        // 如果QQBot正在运行，则通过HTTP连接添加绑定
        if (QQBot_Running) {
            Out = HttpConnection.Get(CMain.QQBotIP + "/exec(new_bc(\'" + Player.getName() + "\'))");
            if (Out != null) {
                QQBot_Running = false;
            }
        }
        // 如果添加绑定失败，则返回错误信息
        if (Out == null) {
            return "§cQQ机器人未开启,请下次登录再试";
        } else {
            // 如果添加绑定成功，则踢出玩家并提示绑定信息
            Event.disallow(PlayerLoginEvent.Result.KICK_OTHER, KickMsg.replace("<code>", Out));
            return null;
        }
    }

    /**
     * 获取ID绑定数据
     *
     * @return 返回ID绑定数据的HashMap，如果QQBot未运行或获取数据失败则返回null
     */
    public final HashMap Get_IDBinds() {
        // 如果QQBot未运行，则返回null
        if (!QQBot_Running) {
            return null;
        }
        // 通过HTTP连接获取ID绑定数据的JSON字符串
        String Out = HttpConnection.Get(CMain.QQBotIP + "/exec(get_bind())");
        // 如果获取数据失败，则返回null
        if (Out == null) {
            QQBot_Running = false;
            return null;
        }
        // 使用Gson库将JSON字符串转换为HashMap
        TypeToken<HashMap<String, String>> Type = new TypeToken<HashMap<String, String>>() {
        };
        HashMap<String, String> IDBinds_Get = new Gson().fromJson(Out, Type.getType());
        // 清空当前的ID绑定数据
        IDBinds_Data.clear();
        // 将从QQBot获取的数据转换为合适的格式并存储
        for (HashMap.Entry<String, String> entry : IDBinds_Get.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }
            IDBinds_Data.put(entry.getValue(), entry.getKey());
        }
        // 将更新后的ID绑定数据保存到Yaml文件中
        IDBinds_Yaml.set("IDBinds", IDBinds_Data);
        Save_IDBinds();
        // 返回更新后的ID绑定数据
        return IDBinds_Data;
    }
}
