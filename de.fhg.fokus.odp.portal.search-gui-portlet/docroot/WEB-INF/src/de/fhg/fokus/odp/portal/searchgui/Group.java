package de.fhg.fokus.odp.portal.searchgui;

public class Group {
    
    String key;
    String translatedName;
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getTranslatedName() { return translatedName; }
    public void setTranslatedName(String translatedName) { this.translatedName = translatedName; }

    public Group(String key, String translatedName) {
        this.key = key;
        this.translatedName = translatedName;
    }
}
