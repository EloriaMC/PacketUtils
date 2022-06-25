package be.alexandre01.eloriamc.server.packets.npc.type;

import be.alexandre01.eloriamc.server.packets.Reflections;
import be.alexandre01.eloriamc.server.packets.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class NPCInstance extends Reflections {
    private final NPC npc;
    private final int entityID;


    private final Player player;
    private Location location;

    public NPCInstance(NPC npc, int entityID, Player player) {
        this.npc = npc;
        this.entityID = entityID;
        this.player = player;
    }

    public abstract void show();

    public abstract void setCustomName(String name);
}
