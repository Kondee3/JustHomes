package me.kondi.JustHomes.PlayerData;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "PLAYERDATA")
public class PlayerDataAdditional {
    public PlayerDataAdditional() {
    }

    public PlayerDataAdditional(String uuid, long cooldown) {
        this.uuid = uuid;
        this.cooldown = cooldown;
    }

    @DatabaseField(id = true)
    private String uuid;
    @DatabaseField
    private long cooldown;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }
}
