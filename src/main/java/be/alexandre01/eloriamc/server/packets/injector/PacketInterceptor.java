package be.alexandre01.eloriamc.server.packets.injector;

import be.alexandre01.eloriamc.server.packets.PacketUtils;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

public abstract class PacketInterceptor extends PacketUtils {
    private boolean cancelled = false;


    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /*
    READ PLAYER DECODED PACKET - THIS IS WHAT THE PLAYER SENT TO SERVER
     */
    public abstract void decode(Player player, Packet<?> packet);
    /*
    READ PLAYER ENCODED PACKET - THIS IS WHAT THE SERVER SENT TO PLAYER
    */
    public abstract void encode(Player player, Packet<?> packet);
}
