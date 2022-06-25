package be.alexandre01.eloriamc.server.packets.skin;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class MojangUtils {
    //get from MojangAPI UUID from username
    public static String getUUID(String name) {
        String uuid = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream()));
            uuid = (((JsonObject)new JsonParser().parse(in)).get("id")).toString().replaceAll("\"", "");
            uuid = uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
            in.close();
        } catch (Exception e) {
            System.out.println("Unable to get UUID of: " + name + "!");
            uuid = "er";
        }
        return uuid;
    }

    //get texture and signature
    public static SkinData getSkinDataFromUUID(String uuid){
        try {
            URL mojang = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" +
                    uuid + "?unsigned=false");
            InputStreamReader reader = new InputStreamReader(mojang.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader).getAsJsonObject().
                    get("properties").getAsJsonArray().get(0).getAsJsonObject();
            if (textureProperty.get("value").getAsString() != null && textureProperty.get("signature").getAsString() != null) {
                return new SkinData(textureProperty.get("value").getAsString(),
                        textureProperty.get("signature").getAsString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
