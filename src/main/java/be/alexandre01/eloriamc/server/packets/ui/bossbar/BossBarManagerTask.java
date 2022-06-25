package be.alexandre01.eloriamc.server.packets.ui.bossbar;

import be.alexandre01.eloriamc.server.packets.npc.type.NPCUniversalEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BossBarManagerTask {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
    private HashMap<Player, BossBar> tasks = new HashMap<>();
    private HashMap<String,BossBar> idTasks = new HashMap<>();

    boolean started = false;
    public void addBossBar(String identifier, BossBar bossBar) {
        idTasks.put(identifier, bossBar);
    }

    public void setBossBar(Player player, String identifier) {
        BossBar bossBar = idTasks.get(identifier);
        if (bossBar != null) {
            tasks.put(player, bossBar);
            Location loc = bossBar.calculLocation(player);
            bossBar.getNpc().initAndShow(player);
            bossBar.getNpc().get(player, NPCUniversalEntity.class).setInvisible();
            bossBar.getNpc().get(player, NPCUniversalEntity.class).teleport(loc);
        }
        if(!started) {
            startTaskForAll();
        }
    }

    public void removeBossBar(Player player) {
        tasks.remove(player);
    }

    public BossBar getBossBar(Player player) {
        return tasks.get(player);
    }

    public void startTaskForAll() {
        started = true;
        executorService.scheduleAtFixedRate(() -> {
            for (Player player : tasks.keySet()) {
                if(!player.isOnline()){
                    tasks.remove(player);
                    continue;
                }
                BossBar bossBar = tasks.get(player);
                Location loc = bossBar.calculLocation(player);
                bossBar.getNpc().get(player, NPCUniversalEntity.class).teleport(loc);
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    public void stopTaskForAll() {
        started = false;
        executorService.shutdown();
    }

}
