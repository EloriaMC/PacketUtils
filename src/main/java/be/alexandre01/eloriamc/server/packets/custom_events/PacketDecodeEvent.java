package be.alexandre01.eloriamc.server.packets.custom_events;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.lang.reflect.Field;

public class PacketDecodeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    @Getter private final Packet<?> packet;
    @Getter private final Player player;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PacketDecodeEvent(Packet<?> packet,Player player){
        this.packet = packet;
        this.player = player;

    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public void setValue(String name,Object value){
        try{
            Field field = packet.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(packet, value);
        }catch(Exception ignored){}
    }

    public Object getValue(String name){
        try{
            Field field = packet.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(packet);
        }catch(Exception e){
            return null;
        }
    }

    public <T> T getValue(String name,Class<T> type){
        try{
            Field field = packet.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(packet);
        }catch(Exception e){
            return null;
        }
    }

    public Class<?> getPacketClass(){
        return packet.getClass();
    }
}
