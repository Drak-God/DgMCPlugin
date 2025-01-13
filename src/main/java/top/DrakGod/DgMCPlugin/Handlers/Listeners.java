package top.DrakGod.DgMCPlugin.Handlers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.commands.list.rt;

import top.DrakGod.DgMCPlugin.Global;

/**
 * 监听器类，处理玩家登录和加入事件
 */
public class Listeners implements Listener, Global {

    // 用于临时存储玩家登录时的绑定信息
    public HashMap<UUID, String> TempSend = new HashMap<>();

    /**
     * 处理玩家登录事件的方法
     *
     * @param Event 玩家登录事件
     */
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent Event) {
        // 获取玩家绑定的ID信息
        String Out = Get_Main().Class_IDBinds.Get_Binded(Event);
        // 如果绑定信息不为空，则将其存储到临时Map中
        if (Out != null) {
            TempSend.put(Event.getPlayer().getUniqueId(), Out);
        }
    }

    /**
     * 处理玩家加入事件的方法
     *
     * @param Event 玩家加入事件
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent Event) {
        // 获取玩家的唯一ID
        UUID Player_UUID = Event.getPlayer().getUniqueId();
        // 如果临时Map中包含该玩家的绑定信息，则发送给玩家并从临时Map中移除
        if (TempSend.containsKey(Player_UUID)) {
            Event.getPlayer().sendMessage(TempSend.get(Player_UUID));
            TempSend.remove(Player_UUID);
        }
    }

    /**
     * 处理玩家移动事件的方法
     *
     * @param Event 玩家移动事件
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent Event) {
        // 获取触发事件的玩家对象
        Player Player = Event.getPlayer();
        // 获取玩家当前的位置信息
        Location Location = Player.getLocation();
        // 获取玩家当前位置的X坐标
        Integer X = Location.getBlockX();
        // 获取玩家当前位置的Y坐标
        Integer Y = Location.getBlockY();
        // 获取玩家当前位置的Z坐标
        Integer Z = Location.getBlockZ();
        // 获取玩家当前所在的世界名称
        String World = Location.getWorld().getName();

        // 检查玩家是否在名为"main_city"的世界中，并且Y坐标在86到90之间
        boolean Is_Main_City = World.equalsIgnoreCase("main_city");
        if ((86 >= Y && Y >= 90) || !Is_Main_City) {
            return;
        }

        // 用于存储新的世界名称
        String New_World_Name;
        // 根据玩家当前的X和Z坐标，确定要传送到的新的世界名称
        if (-44 <= X && X <= -42 && 8 <= Z && Z <= 10) {
            New_World_Name = "World";
        } else if (-4 <= X && X <= -2 && 48 <= Z && Z <= 50) {
            New_World_Name = "resworld";
        } else if (36 <= X && X <= 38 && 8 <= Z && Z <= 10) {
            New_World_Name = "DIM-1";
        } else if (-4 <= X && X <= -2 && -32 <= Z && Z <= -30) {
            New_World_Name = "DIM1";
        } else {
            return;
        }

        // 尝试使用CMI插件的命令处理器将玩家传送到新的世界
        try {
            CMI CMI = Get_Plugin(CMI.class);
            new rt().perform(CMI, Player, new String[]{New_World_Name});
        } catch (Exception e) {
        }
    }
}
