package me.kondi.JustHomes.Home;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Home {

    private String owner;
    private String homeName;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    /**
     * Constructor for Home object
     * @param owner Owner of the home (UUID).
     * @param homeName Name of the Home.
     * @param worldName World name in which home is located.
     * @param x X parameter
     * @param y Y parameter
     * @param z Z parameter
     * @param pitch Pitch parameter
     * @param yaw Yaw parameter
     */
    public Home(String owner, String homeName, String worldName, double x, double y, double z, float pitch, float yaw) {
        this.owner = owner;
        this.homeName = homeName;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public String getOwner() {
        return owner;
    }


    public String getHomeName() {
        return homeName;
    }


    public String getWorldName() {
        return worldName;
    }


    public double getX() {
        return x;
    }


    public double getY() {
        return y;
    }


    public double getZ() {
        return z;
    }


    public float getPitch() {
        return pitch;
    }


    public float getYaw() {
        return yaw;
    }

    public Location getLocation(){
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }


}
