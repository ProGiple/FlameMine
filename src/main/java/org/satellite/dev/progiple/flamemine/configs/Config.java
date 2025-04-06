package org.satellite.dev.progiple.flamemine.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.Configuration.IConfig;
import org.novasparkle.lunaspring.API.Events.CooldownPrevent;
import org.novasparkle.lunaspring.API.Util.Service.managers.ColorManager;
import org.satellite.dev.progiple.flamemine.FlameMine;

@UtilityClass
public class Config {
    private final IConfig config;
    private final CooldownPrevent<CommandSender> cd;
    static {
        config = new IConfig(FlameMine.getINSTANCE());
        cd = new CooldownPrevent<>(50);
    }

    public void reload() {
        config.reload(FlameMine.getINSTANCE());
    }

    public ConfigurationSection getSection(String path) {
        return config.getSection(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public boolean getBool(String path) {
        return config.getBoolean(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public Location getLocation(String path) {
        return config.getLocation(config.getSection(path));
    }

    public double getDouble(String path) {
        return config.self().getDouble(path);
    }

    public void sendMessage(CommandSender sender, String id, String... rpl) {
        if (!cd.isCancelled(null, sender)) {
            String message = config.getString(String.format("messages.%s", id));

            int i = 0;
            for (String string : rpl) {
                message = ColorManager.color(message.replace("{" + i + "}", string));
                i++;
            }
            sender.sendMessage(message);
        }
    }
}
