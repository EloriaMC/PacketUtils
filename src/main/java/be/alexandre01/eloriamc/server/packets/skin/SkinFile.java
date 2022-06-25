package be.alexandre01.eloriamc.server.packets.skin;

import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.*;

@Getter
public class SkinFile {
    File file;
    SkinData skinData;
    public SkinFile(File file){
        this.file = file;
    }

    public void readFile(){
            try {
                String data = FileUtils.readFileToString(file, "UTF-8");
                String[] lines = data.split("\n");
                skinData = new SkinData(lines[0],lines[1]);
                System.out.println("Texture:"+skinData.getTexture());
                System.out.println("Sign:"+skinData.getSignature());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
}
