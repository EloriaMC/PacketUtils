package be.alexandre01.eloriamc.server.packets.npc;

import be.alexandre01.eloriamc.server.events.players.IPlayerEvent;
import be.alexandre01.eloriamc.server.packets.Reflections;
import be.alexandre01.eloriamc.server.packets.npc.type.NPCUniversalEntity;
import be.alexandre01.eloriamc.server.packets.npc.type.NPCHuman;
import be.alexandre01.eloriamc.server.packets.npc.type.NPCInstance;
import be.alexandre01.eloriamc.server.packets.skin.SkinData;
import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class NPC extends Reflections {
    private int entityID;
    private final EntityType entityType;
    private String name;
    private Location location;
    private SkinData skinData;
    private HashMap<Player, NPCInstance> playersInstance = new HashMap<>();
    @Setter private NPCInteract interaction;
    private GameProfile gameProfile;

    private NPCFactory factory;
    private Plugin plugin;



    public NPC(String name, Location location) {
        this.entityType = EntityType.PLAYER;
        this.name = name;
        this.location = location;
        entityID = (int) Math.ceil(Math.random() * 1000) + 2000;
        gameProfile = new GameProfile(UUID.randomUUID(), name);
        this.factory = NPCFactory.getInstance();
        this.plugin = factory.getPlugin();
        if(factory.isInitialized()) {
           factory.addNPC(this);
        }
    }

    public NPC(String name, Location location, EntityType entityType) {
        this.entityType = entityType;
        this.name = name;
        this.location = location;
        entityID = (int) Math.ceil(Math.random() * 1000) + 2000;
        gameProfile = new GameProfile(UUID.randomUUID(), name);
        this.factory = NPCFactory.getInstance();
        this.plugin = factory.getPlugin();
        if(factory.isInitialized()) {
            factory.addNPC(this);
        }
    }


    public void setSkin(String texture, String signature) {
        this.skinData = new SkinData(texture, signature);
    }

    public void setSkin(SkinData skinData) {
        this.skinData = skinData;
    }

    public NPCInstance get(Player player) {
        return playersInstance.get(player);
    }
    public <T> T get(Player player, Class<T> clazz) {
        return (T) playersInstance.get(player);
    }
    public NPCInstance getOrCreate(Player player) {
        if(!playersInstance.containsKey(player)) {
            return initAndShow(player);
        }
        return playersInstance.get(player);
    }
    public NPCInstance initAndShow(Player player) {
        if(!playersInstance.containsKey(player)) {
            factory.getEventLib().getListenerPlayerManager().registerEvent(PlayerQuitEvent.class,"getPlayer",player, new IPlayerEvent<PlayerQuitEvent>() {
                @Override
                public void onPlayerEvent(PlayerQuitEvent event, Player player) {
                    playersInstance.remove(player);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        factory.getEventLib().getListenerPlayerManager().getListenersPerPlayer().get(player).getListeners().clear();
                    }
                    );

                }
            });
            NPCInstance instance;
            if(entityType == EntityType.PLAYER) {
                instance = new NPCHuman(this, player);
            }else {
                instance = new NPCUniversalEntity(this, player);
            }

            playersInstance.put(player,instance);
            instance.show();
            return instance;
    }
        return null;
    }


    public void changeName(String name) {
        for ( NPCInstance instance : playersInstance.values()) {
            instance.setCustomName(name);
        }
    }

    public interface NPCInteract {
         public void action(Player player,InteractClick click);
    }

    public enum InteractClick {
        RIGHT,LEFT;
    }

}
