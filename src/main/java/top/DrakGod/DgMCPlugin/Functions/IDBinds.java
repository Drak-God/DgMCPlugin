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

@SuppressWarnings("rawtypes")
public class IDBinds implements Global {
    public YamlConfiguration IDBinds_Yaml;
    public HashMap<String, String> IDBinds_Data;
    public Main CMain;
    public String KickMsg;
    public boolean QQBot_Running = false;

    public BukkitRunnable Lookup_QQBot = new BukkitRunnable() {
        @Override
        public void run() {
            if (!QQBot_Running) {
                QQBot_Running = (HttpConnection.Get(CMain.QQBotIP + "/exec(bdip)") != null);
            }
        }
    };

    public IDBinds() {
        CMain = Get_Main();
        Lookup_QQBot.runTaskTimerAsynchronously(CMain, 0, 20);
        Load_IDBinds();
        KickMsg = Get_Config().getString("BindKickMsg");
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
            return "§a你已绑定ID: " + IDBinds_Data.get(Player.getName());
        } else {
            return Add_IDBind(Event, Player);
        }
    }

    public final String Add_IDBind(PlayerLoginEvent Event, Player Player) {
        String Out = null;
        if (QQBot_Running) {
            Out = HttpConnection.Get(CMain.QQBotIP + "/exec(new_bc(\'" + Player.getName() + "\'))");
            if (Out != null) {
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

        String Out = HttpConnection.Get(CMain.QQBotIP + "/exec(get_bind())");
        if (Out == null) {
            QQBot_Running = false;
            return null;
        }

        TypeToken<HashMap<String, String>> Type = new TypeToken<HashMap<String, String>>() {
        };
        HashMap<String, String> IDBinds_Get = new Gson().fromJson(Out, Type.getType());
        IDBinds_Data.clear();
        for (HashMap.Entry<String, String> entry : IDBinds_Get.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }
            IDBinds_Data.put(entry.getValue(), entry.getKey());
        }

        IDBinds_Yaml.set("IDBinds", IDBinds_Data);
        Save_IDBinds();
        return IDBinds_Data;
    }
}
