package top.DrakGod.DgMCPlugin.Handlers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

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
    public void onPlayerMove(PlayerMoveEvent event) {
        // 获取触发事件的玩家对象
        Player player = event.getPlayer();
        // 获取玩家当前的位置信息
        Location location = player.getLocation();
        // 获取玩家当前位置的X坐标
        Integer x = location.getBlockX();
        // 获取玩家当前位置的Y坐标
        Integer y = location.getBlockY();
        // 获取玩家当前位置的Z坐标
        Integer z = location.getBlockZ();
        // 获取玩家当前所在的世界名称
        String world = location.getWorld().getName();

        // 检查玩家是否在名为"main_city"的世界中，并且Y坐标在86到90之间
        boolean isMainCity = world.equalsIgnoreCase("main_city");
        if ((86 >= y && y >= 90) || !isMainCity) {
            return;
        }

        // 生成两个随机整数，范围在-5000到5000之间
        Integer randomX = Random.nextInt(-5000, 5000);
        Integer randomZ = Random.nextInt(-5000, 5000);
        // 用于存储新的世界名称
        String newWorldName;

        // 根据玩家当前的X和Z坐标，确定要传送到的新的世界名称
        if (-44 <= x && x <= -42 && 8 <= z && z <= 10) {
            newWorldName = "World";
        } else if (-4 <= x && x <= -2 && 48 <= z && z <= 50) {
            newWorldName = "resworld";
        } else if (36 <= x && x <= 38 && 8 <= z && z <= 10) {
            newWorldName = "DIM-1";
        } else if (-4 <= x && x <= -2 && -32 <= z && z <= -30) {
            newWorldName = "DIM1";
        } else {
            return;
        }

        // 获取新的世界对象
        World newWorld = Server.getWorld(newWorldName);
        // 获取新的世界中随机位置的最高方块
        Block highestBlock = newWorld.getHighestBlockAt(randomX, randomZ);
        // 将玩家传送到新的世界中随机位置的最高方块处
        player.teleport(highestBlock.getLocation());
    }
}
