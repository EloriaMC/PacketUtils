package be.alexandre01.eloriamc.server.packets.injector;

import be.alexandre01.eloriamc.server.packets.injector.compatibility.ProtocolInjector;
import be.alexandre01.eloriamc.utils.ClassUtils;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Getter
public class PacketInjectorManager {
    private final HashMap<UUID,PacketInjector> packetInjectors;
    private final ArrayList<PacketInterceptor> interceptors;

    private boolean firstInject = true;

    public PacketInjectorManager() {
        this.packetInjectors = new HashMap<>();
        this.interceptors = new ArrayList<>();
    }

    public void addInjector(PacketInjector packetInjector){
        System.out.println("Adding injector: " + packetInjector.getClass().getName());
        if(firstInject){
            if(ClassUtils.classExist("com.comphenix.protocol.ProtocolManager")) {
                ProtocolInjector.load();
            }
            firstInject = false;
        }
        packetInjectors.put(packetInjector.getPlayer().getUniqueId(),packetInjector);
    }
    public void addInterceptor(PacketInterceptor packetInterceptor){
        interceptors.add(packetInterceptor);
    }
    public boolean isInjected(Player player){
        if(!packetInjectors.containsKey(player.getUniqueId())){
            return false;
        }
        return packetInjectors.get(player.getUniqueId()).isInjected;
    }
}
