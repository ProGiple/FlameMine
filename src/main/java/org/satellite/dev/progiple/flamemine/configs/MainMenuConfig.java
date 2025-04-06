package org.satellite.dev.progiple.flamemine.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.Configuration.IConfig;
import org.satellite.dev.progiple.flamemine.FlameMine;

import java.io.File;

@UtilityClass
public class MainMenuConfig {
    private final IConfig config;
    static {
        config = new IConfig(new File(FlameMine.getINSTANCE().getDataFolder(), "main_menu.yml"));
    }

    public void reload() {
        config.reload();
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public ConfigurationSection getSection(String path) {
        return config.getSection(path);
    }
}
