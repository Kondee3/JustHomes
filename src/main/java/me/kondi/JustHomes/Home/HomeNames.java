package me.kondi.JustHomes.Home;

import java.util.HashMap;
import java.util.UUID;

public class HomeNames {
    private static HashMap<String, String> homeNames = new HashMap<>();

    /**
     * Used in placeholder, to save temporary the name of home.
     * @param uuid Player's uuid.
     * @param homeName Name of the home.
     */
    public static void addHomeName(String uuid, String homeName) {
        homeNames.put(uuid, homeName);
    }

    /**
     * Used for getting a name of the home to placeholder.
     * @param uuid Player's uuid.
     * @return Name of the home.
     */
    public static String getHomeName(String uuid) {
        if (!homeNames.containsKey(uuid))
            return "";
        String homeName = homeNames.get(uuid);
        homeNames.remove(uuid);
        return homeName;

    }



}
