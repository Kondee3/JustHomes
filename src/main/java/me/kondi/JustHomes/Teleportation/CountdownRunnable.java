package me.kondi.JustHomes.Teleportation;

import org.bukkit.scheduler.BukkitRunnable;

public class CountdownRunnable extends BukkitRunnable {
    public Integer getCurrentCountdownValue() {
        return currentCountdownValue;
    }
    public void decrementCountdownValue() {
        currentCountdownValue--;
    }
    private Integer currentCountdownValue;
    public CountdownRunnable(Integer currentCountdownValue) {
        this.currentCountdownValue = currentCountdownValue;
    }
    @Override
    public void run() {

    }
}
