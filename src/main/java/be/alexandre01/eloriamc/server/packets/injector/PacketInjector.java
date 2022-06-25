package be.alexandre01.eloriamc.server.packets.injector;

import be.alexandre01.eloriamc.server.packets.PacketLib;
import be.alexandre01.eloriamc.server.packets.custom_events.PacketDecodeEvent;
import be.alexandre01.eloriamc.server.packets.custom_events.PacketEncodeEvent;
import io.netty.channel.*;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PacketInjector {
    protected boolean isInjected = false;
    @Getter protected final Player player;
    protected final Channel channel;
    protected final PluginManager pluginManager;
    protected final PacketInjectorManager packetInjectorManager;
    @Getter protected boolean injectedInput = false;
    @Getter protected boolean injectedOutput = false;

    @Getter protected final ArrayList<PacketInterceptor> interceptors = new ArrayList<>();

    public PacketInjector(Player player){
        System.out.println("Creating new injector for player: " + player.getName());
        this.player = player;
        this.pluginManager = Bukkit.getPluginManager();
        channel = ((CraftPlayer)this.player).getHandle().playerConnection.networkManager.channel;
        packetInjectorManager = PacketLib.getInstance().getInjectorManager();
        packetInjectorManager.addInjector(this);
    }
    public void injectAll(){
        injectInputDecoder();
        injectOutputEncoder();
    }
    public void addInterceptor(PacketInterceptor packetInterceptor){
        interceptors.add(packetInterceptor);
        System.out.println(interceptors.size());
    }

    public void injectInputDecoder(){
        if(channel.pipeline().get("PacketDecoderInjector") != null){
            System.out.println("Already injected Decoder");
            return;
        }


        channel.pipeline().addAfter("decoder","PacketDecoderInjector",new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, List<Object> list) throws Exception {
                list.add(packet);
                for( PacketInterceptor interceptor : packetInjectorManager.getInterceptors()){
                    interceptor.decode(player,packet);
                    if(interceptor.isCancelled()){
                        list.remove(packet);
                        return;
                    }
                }
                for( PacketInterceptor interceptor : interceptors){
                    interceptor.decode(player,packet);
                    if(interceptor.isCancelled()){
                        list.remove(packet);
                        return;
                    }
                }
                PacketDecodeEvent event = new PacketDecodeEvent(packet,player);
                pluginManager.callEvent(event);

                if(event.isCancelled()){
                    list.remove(packet);
                }
            }
        });
        isInjected = true;
        injectedInput = true;
    }

    public void injectOutputEncoder(){
        if(channel.pipeline().get("PacketEncoderInjector") != null){
            System.out.println("Already injected Encoder");
            return;
        }


            channel.pipeline().addAfter("encoder","PacketEncoderInjector",new MessageToMessageEncoder<Packet<?>>() {
                @Override
                protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, List<Object> list) throws Exception {
                    list.add(packet);
                    for( PacketInterceptor interceptor : packetInjectorManager.getInterceptors()){
                        interceptor.encode(player,packet);
                        if(interceptor.isCancelled()){
                            list.remove(packet);
                            return;
                        }
                    }
                    for( PacketInterceptor interceptor : interceptors){
                        System.out.println(player.getName());
                        interceptor.encode(player,packet);
                        if(interceptor.isCancelled()){
                            list.remove(packet);
                            return;
                        }
                    }
                    PacketEncodeEvent event = new PacketEncodeEvent(packet,player);
                    pluginManager.callEvent(event);


                    if(event.isCancelled()){
                        list.remove(packet);
                    }
                }
            });



        isInjected = true;
        injectedOutput = true;
    }

    public void uninjectAll(){
        uninjectDecoder();
        uninjectEncoder();

        isInjected = false;
    }

    public void uninjectDecoder(){
        ChannelPipeline pipeline = channel.pipeline();
        if(pipeline.get("PacketDecoderInjector") != null){
            pipeline.remove("PacketDecoderInjector");
        }
        injectedInput = false;
        //VERIF IFINJECT
    }
    public void uninjectEncoder(){
        ChannelPipeline pipeline = channel.pipeline();
        if(pipeline.get("PacketEncoderInjector") != null){
            pipeline.remove("PacketEncoderInjector");
        }
        injectedOutput = false;
    }
}
