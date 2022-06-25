package be.alexandre01.eloriamc.server.packets;


import be.alexandre01.eloriamc.server.packets.injector.PacketInjectorManager;
import be.alexandre01.eloriamc.server.packets.npc.NPCFactory;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

public class PacketLib {
    @Getter
    private static PacketLib instance;

    @Getter private Plugin plugin;

    @Getter
    private final PacketInjectorManager injectorManager;

    @Getter
    private final NPCFactory npcFactory;


    public PacketLib(Plugin plugin) {
        this.plugin = plugin;
        injectorManager = new PacketInjectorManager();
        npcFactory = new NPCFactory(plugin);
    }


}
