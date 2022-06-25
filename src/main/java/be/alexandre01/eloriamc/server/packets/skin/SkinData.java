package be.alexandre01.eloriamc.server.packets.skin;

import com.google.gson.annotations.Expose;
import lombok.Getter;

@Getter
public class SkinData {
    @Expose private String texture;
    @Expose private String signature;

    public SkinData(String texture, String signature) {
        this.texture = texture;
        this.signature = signature;
    }

}
