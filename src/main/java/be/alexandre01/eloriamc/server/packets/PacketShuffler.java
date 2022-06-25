package be.alexandre01.eloriamc.server.packets;

import com.google.common.primitives.Bytes;
import net.minecraft.server.v1_8_R3.*;

import java.util.*;

public class PacketShuffler extends Reflections {
    public void shufflePacket(Packet<?> packet){
        System.out.println(packet.getClass().getSimpleName());
        if(packet instanceof PacketPlayOutScoreboardObjective){
            System.out.println(packet.getClass().getName());
            PacketPlayOutScoreboardObjective score = (PacketPlayOutScoreboardObjective) packet;
            setValue(score,"c", (int) (Math.random() * 100));
            String value = (String) getValue(score,"a");
            StringBuilder newValue = new StringBuilder();
            for (char c : value.toCharArray()) {
                Character d = null;
                while (d == null || !SharedConstants.isAllowedChatCharacter(d) ) {
                    Random r = new Random();
                    d = (char)(r.nextInt(26) + c);
                }
                newValue.append(d);
            }
            setValue(packet,"b", newValue.toString());
        }
        if(packet instanceof PacketPlayOutScoreboardScore){
            System.out.println(packet.getClass().getName());
            PacketPlayOutScoreboardScore score = (PacketPlayOutScoreboardScore) packet;
           // setValue(packet,"a", (int) (Math.random() * 100));
            String value = (String) getValue(score,"a");
            StringBuilder newValue = new StringBuilder();
            for (char c : value.toCharArray()) {
                Character d = null;
                while (d == null || !SharedConstants.isAllowedChatCharacter(d) ) {
                    Random r = new Random();
                    d = (char)(r.nextInt(26) + c);
                }
                newValue.append(d);

                if(newValue.length() > 15){
                    break;
                }
            }
            System.out.println(value);
            setValue(packet,"a", newValue.toString());
        }

        if(packet instanceof PacketPlayOutMapChunk){
            System.out.println(packet.getClass().getName());
            PacketPlayOutMapChunk.ChunkMap chunkMap = (PacketPlayOutMapChunk.ChunkMap) getValue(packet, "c");

            byte[] data = (byte[]) getValue(chunkMap, "a");
            List<Byte> chunks = Bytes.asList(data);

            Collections.shuffle(chunks);
            //chunks to array
            byte[] newData = new byte[data.length];
            int j = 0;
            for (int i = 0; i < chunks.size(); i++) {
                if(j % 2 == 0){
                    newData[i] = chunks.get(i);
                }else {
                    newData[i] = 0x00;
                }

                j++;
            }
            setValue(chunkMap, "a", newData);

            setValue(packet,"c", chunkMap);
        }
        if(packet instanceof PacketPlayOutMapChunkBulk){
            System.out.println(packet.getClass().getName());
            PacketPlayOutMapChunk.ChunkMap[] chunkMaps = (PacketPlayOutMapChunk.ChunkMap[]) getValue(packet, "c");
            ArrayList<PacketPlayOutMapChunk.ChunkMap> newChunkMaps = new ArrayList<>();
            for (PacketPlayOutMapChunk.ChunkMap chunkMap : chunkMaps) {
                byte[] data = (byte[]) getValue(chunkMap, "a");
                List<Byte> chunks = Bytes.asList(data);

                Collections.shuffle(chunks);
                //chunks to array
                byte[] newData = new byte[data.length];
                int j = 0;
                for (int i = 0; i < chunks.size(); i++) {
                    if(j % 2 == 0){
                        newData[i] = chunks.get(i);
                    }else {
                        newData[i] = 0x00;
                    }

                    j++;
                }
                setValue(chunkMap, "a", newData);
                newChunkMaps.add(chunkMap);
            }
            setValue(packet,"c", newChunkMaps.toArray(new PacketPlayOutMapChunk.ChunkMap[0]));
        }

        if(packet instanceof PacketPlayOutChat){
            System.out.println(packet.getClass().getName());
            PacketPlayOutChat chat = (PacketPlayOutChat) packet;
            IChatBaseComponent value = (IChatBaseComponent) getValue(chat,"a");

            List<IChatBaseComponent> i = value.a();
            List<IChatBaseComponent> newI = new ArrayList<>();
            for (IChatBaseComponent iChatBaseComponent : i) {
                String text =   iChatBaseComponent.getText();
                StringBuilder newValue = new StringBuilder();
                for (char c : text.toCharArray()) {
                    Random r = new Random();
                    char d = (char)(r.nextInt(26) + c);
                    newValue.append(d);
                }
                newI.add(new ChatComponentText(newValue.toString()));
            }
            setValue( value,"a", newI);

            setValue(packet,"a",value);

        }
        if(packet instanceof PacketPlayInChat){
            System.out.println(packet.getClass().getName());
            PacketPlayInChat chat = (PacketPlayInChat) packet;
            String value = (String) getValue(chat,"a");

            StringBuilder newValue = new StringBuilder();
                for (char c : value.toCharArray()) {
                    Character d = null;
                    while (d == null || !SharedConstants.isAllowedChatCharacter(d) ) {
                        Random r = new Random();
                        d = (char)(r.nextInt(26) + c);
                    }
                    newValue.append(d);
                }
            setValue( packet,"a", newValue.toString());
        }
    }
}
