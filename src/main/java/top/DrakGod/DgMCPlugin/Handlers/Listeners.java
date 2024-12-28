package top.DrakGod.DgMCPlugin.Handlers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

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
}
