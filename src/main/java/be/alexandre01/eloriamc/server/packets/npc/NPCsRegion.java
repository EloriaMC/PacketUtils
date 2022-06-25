package be.alexandre01.eloriamc.server.packets.npc;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class NPCsRegion {

    public List<NPC> npcs;
    public final Integer regionSizeX;
    public final Integer regionSizeY;
    public final Integer radius;

    public final Location center;

    public NPCsRegion(Location location,int regionSizeX, int regionSizeY) {
        this.regionSizeX = regionSizeX;
        this.regionSizeY = regionSizeY;
        this.radius = null;
        this.center = location;
        npcs = new ArrayList<>();
    }

    public NPCsRegion(Location location, int radius) {
        this.regionSizeX = null;
        this.regionSizeY = null;
        this.center = location;
        this.radius = radius;
        npcs = new ArrayList<>();
    }

    public void addNPC(NPC npc) {
        npcs.add(npc);
    }
}
