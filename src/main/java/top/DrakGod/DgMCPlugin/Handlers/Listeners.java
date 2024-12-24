package top.DrakGod.DgMCPlugin.Handlers;

import top.DrakGod.DgMCPlugin.*;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.EventHandler;

public class Listeners implements Listener, Global {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent Event) {
        Event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        Event.setKickMessage("§c服务器正在维护中");
    }
}