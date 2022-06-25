package be.alexandre01.eloriamc.server.packets.skin;

import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class SkinFactory {
    @Getter private HashMap<String,SkinData> skinsData = new HashMap<String,SkinData>();

    @Getter private HashMap<String,SkinFile> skinsFile = new HashMap<String,SkinFile>();


    public SkinFactory() {
        //DO SOME THINGS
    }

    public void readFiles(String absolutePath){
        File dir;

            dir = new File(absolutePath+"/skins");

            try {
                if(!dir.exists()) {
                    dir.mkdirs();
                }

                for(File file : Objects.requireNonNull(dir.listFiles())) {
                    if(file.getName().contains(".skin")) {
                        SkinFile skinFile = new SkinFile(file);
                        skinFile.readFile();
                        skinsFile.put(file.getName().split("\\.")[0],skinFile);
                        skinsData.put(file.getName(),skinFile.getSkinData());
                    }

                }
        } catch (Exception e) {
                throw new RuntimeException(e);
        }

    }

    public void registerSkinData(String name, SkinData skinData) {
        skinsData.put(name, skinData);
    }

    public SkinData getSkinData(String name) {
        return skinsData.get(name);
    }


}
