package top.DrakGod.DgMCPlugin.Handlers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.commands.list.kit;
import com.Zrips.CMI.commands.list.rt;

import fr.xephi.authme.events.RegisterEvent;
import top.DrakGod.DgMCPlugin.Global;

public class Listeners implements Listener, Global {
    public HashMap<UUID, String> TempSend = new HashMap<>();

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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPortal(PlayerPortalEvent Event) {
        Player Player = Event.getPlayer();
        Location Location = Player.getLocation();
        Integer X = Location.getBlockX();
        Integer Y = Location.getBlockY();
        Integer Z = Location.getBlockZ();
        String World = Location.getWorld().getName();

        boolean Is_Main_City = World.equalsIgnoreCase("main_city");
        if ((86 >= Y && Y >= 90) || !Is_Main_City) {
            return;
        }

        String New_World_Name;
        if (-44 <= X && X <= -42 && 8 <= Z && Z <= 10) {
            New_World_Name = "world";
        } else if (-4 <= X && X <= -2 && 48 <= Z && Z <= 50) {
            New_World_Name = "resworld";
        } else if (36 <= X && X <= 38 && 8 <= Z && Z <= 10) {
            New_World_Name = "world_nether";
        } else if (-4 <= X && X <= -2 && -32 <= Z && Z <= -30) {
            New_World_Name = "world_the_end";
        } else {
            return;
        }

        try {
            CMI CMI = Get_Plugin(CMI.class);
            new rt().perform(CMI, Player, new String[] { New_World_Name });
        } catch (Exception e) {
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRegister(RegisterEvent Event) {
        Player Player = Event.getPlayer();
        if (Player.hasPermission("cmi.kit.进服礼包")) {
            try {
                CMI CMI = Get_Plugin(CMI.class);
                new kit().perform(CMI, Player, new String[] { "进服礼包" });
                Player.addAttachment(CMI, "cmi.kit.进服礼包", false);
            } catch (Exception e) {
            }
        }
    }
}
