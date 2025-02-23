package top.DrakGod.DgMCPlugin.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import top.DrakGod.DgMCPlugin.Main;
import top.DrakGod.DgMCPlugin.Util.DgMCCommand;

@SuppressWarnings("deprecation")
public class idbind implements DgMCCommand {
    @Override
    public void Init() {
    }

    @Override
    public String Get_Command_Name() {
        return "idbind";
    }

    @Override
    public List<String> On_TabComplete(Main Main, CommandSender Sender, String Label, String[] Args) {
        if (Args.length == 1) {
            Stream<OfflinePlayer> Stream = Arrays.stream(Server.getOfflinePlayers());
            return Stream.map(OfflinePlayer::getName).toList();
        }
        return new ArrayList<>();
    }

    @Override
    public boolean On_Command(Main Main, CommandSender Sender, String Label, String[] Args) {
        String Name;
        switch (Args.length) {
            case 0 -> {
                if (Sender instanceof Player Player) {
                    Name = Player.getName();
                } else {
                    Sender.sendMessage("§c参数过少");
                    return true;
                }
            }

            case 1 -> {
                Name = Args[0];
                if (!Server.getOfflinePlayer(Name).hasPlayedBefore()) {
                    Sender.sendMessage("§c玩家不存在");
                    return true;
                }
            }

            default -> {
                Sender.sendMessage("§c参数过多");
                return true;
            }
        }

        String ID = Get_Main().Class_IDBinds.Get_IDBind(Name);
        if (ID == null) {
            Sender.sendMessage("§6玩家 " + Name + " §c未绑定ID");
        } else {
            Sender.sendMessage("§6玩家 " + Name + " §a已绑定ID:" + ID);
        }

        return true;
    }
}
