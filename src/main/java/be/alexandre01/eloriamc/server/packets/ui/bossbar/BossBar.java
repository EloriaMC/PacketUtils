package be.alexandre01.eloriamc.server.packets.ui.bossbar;

import be.alexandre01.eloriamc.server.packets.npc.NPC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@Getter
public class BossBar {

  public String text;
  public float percent;

  private NPC npc;

  public BossBar(String text, float percent) {
    this.text = text;
    this.percent = percent;
    npc = new NPC(this.text, new Location(Bukkit.getWorlds().get(0), 0, 0, 0), EntityType.WITHER);
  }



  //In face of player direction
  public Location calculLocation(Player player){
    Location location = player.getLocation();
    location.setPitch(player.getLocation().getPitch()/4);
    return  location.add(location.getDirection().normalize().multiply(25));
  }


}
