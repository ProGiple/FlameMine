package org.satellite.dev.progiple.flamemine.configs;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;
import org.novasparkle.lunaspring.API.Configuration.Configuration;
import org.satellite.dev.progiple.flamemine.FlameMine;

import java.io.File;

@UtilityClass
public class MineData {
    private final Configuration config;
    static {
        config = new Configuration(new File(FlameMine.getINSTANCE().getDataFolder(), "mine_data.yml"));
    }

    public void setPos(int i, Location location) {
        config.setLocation("pos" + i, location, false, true);
        save();
    }

    public Location getPos(int i) {
        ConfigurationSection section = config.getSection("pos" + i);
        return section == null ? null :
                (section.getInt("y") > -1 ? config.getLocation(section) : null);
    }

    public void addMineBlocks(String nick, int mineBlocks) {
        String path = "mines." + nick;
        config.setInt(path, config.getInt(path) + mineBlocks);
        save();
    }

    public int getMineBlocks(String nick) {
        return config.getInt("mines." + nick);
    }

    public int getMineBlocks() {
        ConfigurationSection section = config.getSection("mines");
        return section.getKeys(false)
                .stream()
                .mapToInt(section::getInt)
                .sum();
    }

    public void save() {
        config.save();
    }
}
