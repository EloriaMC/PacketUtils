package be.alexandre01.eloriamc.server.packets.npc;

import be.alexandre01.eloriamc.server.packets.custom_events.PacketDecodeEvent;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCAutoPacketReadListener implements Listener {

    private final NPCFactory npcFactory;

    public NPCAutoPacketReadListener(NPCFactory factory) {
        npcFactory = factory;
    }

    @EventHandler
    public void onNPCAutoPacketRead(PacketDecodeEvent event) {
        if(!(event.getPacket() instanceof PacketPlayInUseEntity)) return;

        PacketPlayInUseEntity packet = (PacketPlayInUseEntity) event.getPacket();
        if(packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) return;

        int entityID = event.getValue("a",Integer.class);
        if(npcFactory.containsNPC(entityID))
        npcFactory.getNPC(entityID).getInteraction().action(
                event.getPlayer(),
                (packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) ?
                        NPC.InteractClick.RIGHT : NPC.InteractClick.LEFT);
    }
}
