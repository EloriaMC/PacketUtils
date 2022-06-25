package be.alexandre01.eloriamc.server.packets.npc.type;

import be.alexandre01.eloriamc.server.packets.npc.NPC;
import be.alexandre01.eloriamc.server.packets.skin.SkinData;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;

public class NPCHuman extends NPCInstance {
    private final NPC npc;
    private final int entityID;

    private final GameProfile gameProfile;

    private final Player player;

    private SkinData skinData;

    private Location location;

    private boolean isVisible = false;

    public NPCHuman(NPC npc, Player player) {
        super(npc,npc.getEntityID(),player);
        this.npc = npc;
        this.entityID = npc.getEntityID();
        this.location = npc.getLocation();
        this.gameProfile = npc.getGameProfile();
        this.skinData = npc.getSkinData();
        System.out.println(skinData);
        this.player = player;
    }

    public void show() {
        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
        setValue(packet, "a", entityID);
        setValue(packet, "b", gameProfile.getId());
        this.setValue(packet, "c", this.getFixLocation(this.location.getX()));
        this.setValue(packet, "d", this.getFixLocation(this.location.getY()));
        this.setValue(packet, "e", this.getFixLocation(this.location.getZ()));
        this.setValue(packet, "f", this.getFixRotation(this.location.getYaw()));
        this.setValue(packet, "g", this.getFixRotation(this.location.getPitch()));
        this.setValue(packet, "h", 0);
        DataWatcher w = new DataWatcher((Entity)null);
        w.a(6, 20.0F);
        w.a(10, (byte)127);
        this.setValue(packet, "i", w);
        addToTablist();
        System.out.println(packet.toString());
        this.sendPacket(packet, player);
        this.isVisible = true;
        headRotation(location.getYaw(), location.getPitch());

        if(skinData != null){
            changeSkin(skinData);
        }
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

    public void addToTablist(){
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameProfile, 1, WorldSettings.EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameProfile.getName())[0]);
        @SuppressWarnings("unchecked")
        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
        players.add(data);

        setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        setValue(packet, "b", players);

        sendPacket(player,packet);
    }

    public void rmvFromTablist(){
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameProfile, 1, WorldSettings.EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameProfile.getName())[0]);
        @SuppressWarnings("unchecked")
        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
        players.add(data);

        setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
        setValue(packet, "b", players);

        sendPacket(player,packet);
    }

    public void hide(){
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] {entityID});
        rmvFromTablist();
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


    public void changeSkin(String texture, String signature){
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
    }

    public void changeSkin(SkinData skinData) {
        gameProfile.getProperties().put("textures", new Property("textures", skinData.getTexture(), skinData.getSignature()));
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
