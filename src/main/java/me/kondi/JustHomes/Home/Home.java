package me.kondi.JustHomes.Home;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

@DatabaseTable(tableName = "HOMES")
public class Home {
    public Home() {
    }

    @DatabaseField(dataType = DataType.UUID, id = true, uniqueCombo = true)
    private UUID id;
    @DatabaseField(uniqueCombo = true)
    private String owner;
    @DatabaseField(uniqueCombo = true)
    private String homeName;
    @DatabaseField
    private String worldName;
    @DatabaseField
    private double x;
    @DatabaseField
    private double y;
    @DatabaseField
    private double z;
    @DatabaseField
    private float pitch;
    @DatabaseField
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
    public Home(UUID id, String owner, String homeName, String worldName, double x, double y, double z, float pitch, float yaw) {
        this.id = id;
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

    public void setLocation(Location loc){
        this.worldName = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.pitch = loc.getPitch();
        this.yaw = loc.getYaw();
    }
    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public UUID getId() {
        return id;
    }
}
