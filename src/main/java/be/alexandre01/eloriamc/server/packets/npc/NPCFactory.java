package be.alexandre01.eloriamc.server.packets.npc;

import be.alexandre01.eloriamc.server.events.EventLib;
import be.alexandre01.eloriamc.server.packets.skin.SkinData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class NPCFactory {
    private HashMap<Integer,NPC> npcs = new HashMap<>();
    @Getter private boolean isInitialized = false;

    @Getter private static NPCFactory instance;

    @Getter private EventLib eventLib;

    @Getter private Plugin plugin;



    public NPCFactory(Plugin plugin) {
        instance = this;
        eventLib = EventLib.getInstance();
    }
    private NPCFactory load(boolean autoRead){
        isInitialized = true;
        if(autoRead) {
            Bukkit.getPluginManager().registerEvents(new NPCAutoPacketReadListener(this), plugin);
        }
        return this;
    }

    public NPCFactory initialize(boolean autoReadInteractions){
        return load(autoReadInteractions);
    }


    public NPC createNPC(String name, SkinData skin, Location location){
        NPC npc = new NPC(name,location);
        npc.setSkin(skin);
        npcs.put(npc.getEntityID(),npc);
        return npc;
    }

    public NPC getNPC(int entityID){
        return npcs.get(entityID);
    }

    public boolean containsNPC(int entityID){
        return npcs.containsKey(entityID);
    }

    public void removeNPC(int entityID){
        npcs.remove(entityID);
    }

    public void addNPC(NPC npc){
        npcs.put(npc.getEntityID(),npc);
    }
}
