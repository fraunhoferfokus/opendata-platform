package de.fhg.fokus.odp.portal.searchgui;

public class License {
    
    String key;
    String name;
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public License(String key, String name) {
        this.key = key;
        this.name = name;
    }

}
