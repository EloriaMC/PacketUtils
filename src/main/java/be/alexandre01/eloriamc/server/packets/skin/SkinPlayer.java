package be.alexandre01.eloriamc.server.packets.skin;

import be.alexandre01.eloriamc.server.packets.Reflections;
import be.alexandre01.eloriamc.server.packets.npc.NPCFactory;
import com.mojang.authlib.GameProfile;

import com.mojang.authlib.properties.Property;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.nio.ByteBuffer;
import java.util.HashSet;

public class SkinPlayer extends Reflections {

    SkinData defaultSkinData;
    EntityPlayer entityPlayer;

    Player player;

    NPCFactory npcFactory;

    public SkinPlayer(Player player){
        npcFactory = NPCFactory.getInstance();
        this.player = player;
        entityPlayer = ((CraftPlayer) player).getHandle();
        GameProfile gameProfile = entityPlayer.getProfile();
        if(gameProfile.getProperties().get("textures").isEmpty()){
            System.out.println("T moche");
            return;
        }
        Property property = gameProfile.getProperties().get("textures").iterator().next();
        String texture = property.getValue();
        String signature = property.getSignature();
        if(texture != null && signature != null){
            this.defaultSkinData = new SkinData(texture, signature);
        }
    }


    public void applySkin(SkinData skinData,boolean refresh) {
        entityPlayer.getProfile().getProperties().clear();
        entityPlayer.getProfile().getProperties().put("textures", new Property("textures", skinData.getTexture(), skinData.getSignature()));
        if(!refresh) return;
        PlayerConnection connectionpacket = ((CraftPlayer)player).getHandle().playerConnection;
        connectionpacket.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        connectionpacket.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        connectionpacket.sendPacket(new PacketPlayOutRespawn(player.getWorld().getEnvironment().getId(),entityPlayer.getWorld().getDifficulty(),entityPlayer.getWorld().worldData.getType(),entityPlayer.playerInteractManager.getGameMode()));
        connectionpacket.sendPacket(new PacketPlayOutPosition(entityPlayer.locX, entityPlayer.locY, entityPlayer.locZ, entityPlayer.yaw, entityPlayer.pitch,new HashSet<>()));

        entityPlayer.updateAbilities();
        player.updateInventory();
        ((CraftPlayer) player).updateScaledHealth();

        connectionpacket.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        player.hidePlayer(player);
        Bukkit.getScheduler().runTaskLater(npcFactory.getPlugin(), new Runnable() {
            @Override
            public void run() {
                player.showPlayer(player);
                connectionpacket.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.showPlayer(player);
                    connectionpacket.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
                }
            }
        },5);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.canSee(player)) {
                p.hidePlayer(player);
                connectionpacket.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
            }
        }
    }

    public void applySkin(SkinData skinData) {
        applySkin(skinData,true);
    }
}
