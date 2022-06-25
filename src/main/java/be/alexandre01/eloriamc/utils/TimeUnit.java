package be.alexandre01.eloriamc.utils;

import java.util.HashMap;

public enum TimeUnit {
  SECONDE("Seconde(s)", "sec", 1L),
  MINUTE("Minute(s)", "min", 60L),
  HEURE("Heure(s)", "h", 3600L),
  JOUR("Jour(s)", "j", 86400L),
  MOIS("Mois", "m", 2592000L);
  
  private String name;
  
  private String shortcut;
  
  private long toSecond;
  
  private static HashMap<String, TimeUnit> id_shortcuts;
  
  static {
    id_shortcuts = new HashMap<String, TimeUnit>();
    for (TimeUnit units : values())
      id_shortcuts.put(units.shortcut, units); 
  }
  
  TimeUnit(String name, String shortcut, long toSecond) {
    this.name = name;
    this.shortcut = shortcut;
    this.toSecond = toSecond;
  }
  
  public static TimeUnit getFromShortcut(String shortcut) {
    return id_shortcuts.get(shortcut);
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getShortcut() {
    return this.shortcut;
  }
  
  public long getToSecond() {
    return this.toSecond;
  }
  
  public static boolean existFromShortcut(String shortcut) {
    return id_shortcuts.containsKey(shortcut);
  }
}
