package be.alexandre01.eloriamc.server.packets.npc.type;

import be.alexandre01.eloriamc.server.packets.npc.NPC;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class NPCUniversalEntity extends NPCInstance {
    private final NPC npc;
    private final int entityID;


    private final Player player;
    private Location location;

    private boolean isVisible = false;

    public NPCUniversalEntity(NPC npc, Player player) {
        super(npc, npc.getEntityID(),player);
        this.npc = npc;
        this.entityID = npc.getEntityID();
        this.location = npc.getLocation();
        this.player = player;
    }

    public void show() {
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
        setValue(packet, "a", entityID);
        setValue(packet, "b", (byte) npc.getEntityType().getTypeId());
        this.setValue(packet, "c", this.getFixLocation(this.location.getX()));
        this.setValue(packet, "d", this.getFixLocation(this.location.getY()));
        this.setValue(packet, "e", this.getFixLocation(this.location.getZ()));
        this.setValue(packet, "f", this.getFixRotation(this.location.getYaw()));
        this.setValue(packet, "g", this.getFixRotation(this.location.getPitch()));
        this.setValue(packet, "h", 0);

        //velocity entity
        this.setValue(packet, "i", 0);
        this.setValue(packet, "j", 0);
        this.setValue(packet, "k", 0);
        DataWatcher w = new DataWatcher((Entity)null);
        if(npc.getName() != null) {
            w.a((byte) 2,npc.getName());
            w.a((byte) 3,(byte) 1);
        }
        this.setValue(packet, "l", w);
        System.out.println(packet.toString());
        this.sendPacket(packet, player);
        this.isVisible = true;
        headRotation(location.getYaw(), location.getPitch());
    }


    public void hide(){
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] {entityID});
        this.isVisible = false;
        sendPacket(player,packet);
    }

    public void headRotation(float yaw,float pitch){
        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityID, getFixRotation(yaw),getFixRotation(pitch) , true);
        PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
        setValue(packetHead, "a", entityID);
        setValue(packetHead, "b", getFixRotation(yaw));


        sendPacket(player,packet);
        sendPacket(player,packetHead);
    }

    public void teleport(Location location){
        if(!isVisible) return;
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
        setValue(packet, "a", entityID);
        setValue(packet, "b", getFixLocation(location.getX()));
        setValue(packet, "c", getFixLocation(location.getY()));
        setValue(packet, "d", getFixLocation(location.getZ()));
        setValue(packet, "e", getFixRotation(location.getYaw()));
        setValue(packet, "f", getFixRotation(location.getPitch()));

        sendPacket(player,packet);
        headRotation(location.getYaw(), location.getPitch());
        this.location = location.clone();

    }

    public byte getFixRotation(float yawpitch) {
        return (byte)((int)(yawpitch * 256.0F / 360.0F));
    }

    public int getFixLocation(double pos) {
        return MathHelper.floor(pos * 32.0);
    }

    public void animation(int animation) {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        this.setValue(packet, "a", this.entityID);
        this.setValue(packet, "b", (byte)animation);
        this.sendPacket(this.player, packet);
    }

    public void status(int status) {
        PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus();
        this.setValue(packet, "a", this.entityID);
        this.setValue(packet, "b", (byte)status);
        this.sendPacket(this.player, packet);
    }

    public void equip(int slot, ItemStack itemstack) {
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment();
        this.setValue(packet, "a", this.entityID);
        this.setValue(packet, "b", slot);
        this.setValue(packet, "c", itemstack);
        this.sendPacket(this.player, packet);
    }

    public void setCustomName(String name){
        DataWatcher w = new DataWatcher((Entity)null);
        w.a(2,name);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(this.entityID, w, true);
        this.sendPacket(this.player, metadataPacket);
    }

    public void setCustomNameVisible(boolean visible){
        DataWatcher w = new DataWatcher((Entity)null);
        if(visible)
            w.a(3, (byte)1);
        else
            w.a(3,(byte)0);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(this.entityID, w, true);
        this.sendPacket(this.player, metadataPacket);
    }


    public void setMetadata(int i, Object o){
        DataWatcher w = new DataWatcher((Entity)null);
        w.a(i, o);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(this.entityID, w, true);
        this.sendPacket(this.player, metadataPacket);
    }

    public void setInvisible() {
        DataWatcher w = new DataWatcher((Entity)null);
        w.a(0, (byte)32);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(this.entityID, w, true);
        this.sendPacket(this.player, metadataPacket);
    }

    public void setStanding() {
        DataWatcher w = new DataWatcher((Entity)null);
        w.a(0, (byte)0);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(this.entityID, w, true);
        this.sendPacket(this.player, metadataPacket);
    }

    public void setOnFire() {
        DataWatcher w = new DataWatcher((Entity)null);
        w.a(0, (byte)1);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(this.entityID, w, true);
        this.sendPacket(this.player, metadataPacket);
    }

    public void setCrouch() {
        DataWatcher w = new DataWatcher((Entity)null);
        w.a(0, (byte)2);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(this.entityID, w, true);
        this.sendPacket(this.player, metadataPacket);
    }

    public void sleep(boolean state) {
        if (state) {
            Location bedLocation = new Location(this.player.getWorld(), this.location.getX(), 2.0, this.location.getZ());
            PacketPlayOutBed packet = new PacketPlayOutBed();
            this.setValue(packet, "a", this.entityID);
            this.setValue(packet, "b", new BlockPosition(bedLocation.getX(), bedLocation.getY(), bedLocation.getZ()));
            Iterator var4 = Bukkit.getOnlinePlayers().iterator();

            while(var4.hasNext()) {
                Player pl = (Player)var4.next();
                pl.sendBlockChange(bedLocation, Material.BED_BLOCK, (byte)0);
            }

            this.sendPacket(this.player, packet);
            this.teleport(this.location.clone().add(0.0, 0.3, 0.0));
        } else {
            this.animation(2);
            this.teleport(this.location.clone().subtract(0.0, 0.3, 0.0));
        }

    }

}
