package be.alexandre01.eloriamc.server.packets.injector.compatibility;


import be.alexandre01.eloriamc.server.packets.PacketLib;
import be.alexandre01.eloriamc.server.packets.custom_events.PacketDecodeEvent;
import be.alexandre01.eloriamc.server.packets.custom_events.PacketEncodeEvent;
import be.alexandre01.eloriamc.server.packets.injector.PacketInjector;
import be.alexandre01.eloriamc.server.packets.injector.PacketInjectorManager;
import be.alexandre01.eloriamc.server.packets.injector.PacketInterceptor;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftVersion;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class ProtocolInjector extends PacketInjector {

    public ProtocolInjector(Player player) {
        super(player);
    }

    public static void load(){
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketLib packetLib = PacketLib.getInstance();
        PacketInjectorManager pman = packetLib.getInjectorManager();
        Set<PacketType> client = PacketType.Play.Client.getInstance().values().stream().filter(packetType -> packetType.isSupported() && !packetType.isDeprecated()).collect(Collectors.toSet());
        Set<PacketType> server = PacketType.Play.Server.getInstance().values().stream().filter(packetType -> packetType.isSupported() && !packetType.isDeprecated()).collect(Collectors.toSet());
        System.out.println("Client Packets: " + client.size());
        System.out.println(server.size());
        protocolManager.addPacketListener(new PacketAdapter(packetLib.getPlugin(),
                ListenerPriority.NORMAL,client) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if(!(event.getPacket().getHandle() instanceof Packet<?>)) return;

                if(!pman.isInjected(event.getPlayer())) return;
                PacketInjector p = pman.getPacketInjectors().get(event.getPlayer().getUniqueId());
                if(!p.isInjectedInput()) return;
                Packet<?> packet = (Packet<?>) event.getPacket().getHandle();
                    for( PacketInterceptor interceptor : pman.getInterceptors()){
                        interceptor.decode(p.getPlayer(),packet);
                        if(interceptor.isCancelled()){
                            event.setCancelled(true);
                            return;
                        }
                    }
                    for( PacketInterceptor interceptor : p.getInterceptors()){
                        interceptor.decode(p.getPlayer(),packet);
                        if(interceptor.isCancelled()){
                            event.setCancelled(true);
                            return;
                        }
                    }
                    PacketDecodeEvent e = new PacketDecodeEvent(packet,p.getPlayer());
                    packetLib.getPlugin().getServer().getPluginManager().callEvent(e);

                    if(e.isCancelled()){
                        event.setCancelled(true);
                    }

                }
        });
        protocolManager.addPacketListener(new PacketAdapter(packetLib.getPlugin(),
                ListenerPriority.NORMAL,server) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if(!(event.getPacket().getHandle() instanceof Packet<?>)) return;
                if(!pman.isInjected(event.getPlayer())) return;
                PacketInjector p = pman.getPacketInjectors().get(event.getPlayer().getUniqueId());
                if(!p.isInjectedOutput()) return;
                Packet<?> packet = (Packet<?>) event.getPacket().getHandle();
                for( PacketInterceptor interceptor : pman.getInterceptors()){
                    interceptor.encode(p.getPlayer(),packet);
                    if(interceptor.isCancelled()){
                        event.setCancelled(true);
                        return;
                    }
                }
                for( PacketInterceptor interceptor : p.getInterceptors()){
                    interceptor.encode(p.getPlayer(),packet);
                    if(interceptor.isCancelled()){
                        event.setCancelled(true);
                        return;
                    }
                }
                PacketEncodeEvent e = new PacketEncodeEvent(packet,p.getPlayer());
                packetLib.getPlugin().getServer().getPluginManager().callEvent(e);

                if(e.isCancelled()){
                    event.setCancelled(true);
                }
            }
        });
    }

    @Override
    public void injectInputDecoder() {
        if(injectedInput){
            System.out.println("Already injected Decoder");
            return;
        }
        injectedInput = true;
        isInjected = true;
    }

    @Override
    public void injectOutputEncoder() {
        if(injectedOutput){
            System.out.println("Already injected Encoder");
            return;
        }
        injectedOutput = true;
        isInjected = true;
    }
}
