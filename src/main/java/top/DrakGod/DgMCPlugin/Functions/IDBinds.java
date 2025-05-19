package top.DrakGod.DgMCPlugin.Functions;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import top.DrakGod.DgMCPlugin.Global;
import top.DrakGod.DgMCPlugin.Main;

@SuppressWarnings("rawtypes")
public class IDBinds implements Global {
    public YamlConfiguration IDBinds_Yaml;
    public HashMap<String, String> IDBinds_Data;
    public QQBot QQBot;
    public String KickMsg;
    public boolean QQBot_Running = false;
    public HashMap<UUID, String> TempSend = new HashMap<>();

    public BukkitRunnable Lookup_QQBot = new BukkitRunnable() {
        @Override
        public void run() {
            if (!QQBot_Running) {
                QQBot_Running = (HttpConnection.Get(QQBot.QQBotIP + "/Get_IDBinds") != null);
            }
        }
    };

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLogin(PlayerLoginEvent Event) {
        String Out = Get_Main().Class_IDBinds.Get_Binded(Event);
        if (Out != null) {
            TempSend.put(Event.getPlayer().getUniqueId(), Out);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent Event) {
        UUID Player_UUID = Event.getPlayer().getUniqueId();
        if (TempSend.containsKey(Player_UUID)) {
            Event.getPlayer().sendMessage(TempSend.get(Player_UUID));
            TempSend.remove(Player_UUID);
        }
    }

    public IDBinds() {
        Main Main = Get_Main();
        QQBot = Main.Class_QQBot;
        Lookup_QQBot.runTaskTimerAsynchronously(Main, 0, 20);
        Load_IDBinds();
        KickMsg = Get_Config().getString("BindKickMsg");
        RegisterEvent(this::onPlayerJoin, PlayerJoinEvent.class);
        RegisterEvent(this::onPlayerLogin, PlayerLoginEvent.class);
    }

    public final void Load_IDBinds() {
        IDBinds_Yaml = Get_Data("IDBinds.yml");
        ConfigurationSection IDBinds_Get = IDBinds_Yaml.getConfigurationSection("IDBinds");

        IDBinds_Data = new HashMap<>();
        if (IDBinds_Get != null) {
            for (String key : IDBinds_Get.getKeys(false)) {
                IDBinds_Data.put(key, IDBinds_Get.getString(key));
            }
        }
    }

    public final void Save_IDBinds() {
        Save_Data(IDBinds_Yaml, "IDBinds.yml");
    }

    public final String Get_IDBind(String Name) {
        Get_IDBinds();
        if (IDBinds_Data.containsKey(Name)) {
            return IDBinds_Data.get(Name);
        } else {
            return null;
        }
    }

    public final String Get_Binded(PlayerLoginEvent Event) {
        Player Player = Event.getPlayer();
        Get_IDBinds();
        if (IDBinds_Data.containsKey(Player.getName())) {
            return "§a你已绑定DG账号ID: " + IDBinds_Data.get(Player.getName());
        } else {
            return Add_IDBind(Event, Player);
        }
    }

    public final String Add_IDBind(PlayerLoginEvent Event, Player Player) {
        String Out = null;
        if (QQBot_Running) {
            HashMap<String, String> Data = new HashMap<>();
            Data.put("Player", Player.getName());

            Out = HttpConnection.Post(QQBot.QQBotIP + "/New_Bind_Code", Data);
            if (Out == null) {
                QQBot_Running = false;
            }
        }

        if (Out == null) {
            return "§cQQ机器人未开启,请下次登录再试";
        } else {
            Event.disallow(PlayerLoginEvent.Result.KICK_OTHER, KickMsg.replace("<code>", Out));
            return null;
        }
    }

    public final HashMap Get_IDBinds() {
        if (!QQBot_Running) {
            return null;
        }

        String Out = HttpConnection.Get(QQBot.QQBotIP + "/Get_IDBinds");
        if (Out == null) {
            QQBot_Running = false;
            return null;
        }

        TypeToken<HashMap<String, String>> Type = new TypeToken<HashMap<String, String>>() {
        };
        HashMap<String, String> IDBinds_Get = new Gson().fromJson(Out, Type.getType());
        IDBinds_Data.clear();
        for (HashMap.Entry<String, String> entry : IDBinds_Get.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            IDBinds_Data.put(entry.getValue(), entry.getKey());
        }

        IDBinds_Yaml.set("IDBinds", IDBinds_Data);
        Save_IDBinds();
        return IDBinds_Data;
    }
}
