package org.satellite.dev.progiple.flamemine;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.novasparkle.lunaspring.API.Util.Service.managers.ColorManager;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.flamemine.configs.Config;
import org.satellite.dev.progiple.flamemine.configs.MineData;
import org.satellite.dev.progiple.flamemine.mine.MineManager;
import org.satellite.dev.progiple.flamemine.mine.listeners.BlockBreakHandler;

public final class FlameMine extends LunaPlugin {
    @Getter private static FlameMine INSTANCE;
    private long lastTime = System.currentTimeMillis();

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.initialize();

        this.saveDefaultConfig();
        this.loadFiles(true, "mine_data.yml", "main_menu.yml");

        this.registerListeners(new BlockBreakHandler());
        this.registerTabExecutor(new MineCommand(), "flamemine");

        int interval = Config.getInt("settings.update_time");
        this.createPlaceholder("flamemine", ((offlinePlayer, s) -> {
            if (s.equalsIgnoreCase("mines")) {
                return String.valueOf(MineData.getMineBlocks());
            }
            else if (s.equalsIgnoreCase("stats")) {
                return String.valueOf(MineData.getMineBlocks(offlinePlayer.getName()));
            }
            else if (s.startsWith("mine_name_")) {
                String id = s.replace("mine_name_", "");
                return ColorManager.color(Config.getString(String.format("levels.%s.name", id)));
            }
            else if (s.equalsIgnoreCase("now_mine")) {
                String now = Config.getString(String.format("levels.%s.name", MineManager.getMine().getLevelId()));
                return now == null ? "" : ColorManager.color(now);
            }
            else if (s.equalsIgnoreCase("need")) {
                String nextLevel = MineManager.getNextLevel();
                if (nextLevel == null) return "-1";

                return String.valueOf(Config.getInt("levels.%s.requirements.amount") - MineData.getMineBlocks());
            }
            else if (s.equalsIgnoreCase("timer")) {
                long timeLeftMillis = this.lastTime + (interval * 1000L) - System.currentTimeMillis();
                if (timeLeftMillis <= 0) {
                    return "00:00";
                }

                long minutes = timeLeftMillis / 1000 / 60;
                long seconds = (timeLeftMillis / 1000) % 60;

                return String.format("%02d:%02d", minutes, seconds);
            }
            return "";
        }));

        Bukkit.getScheduler().runTaskTimer(INSTANCE, () -> {
            MineManager.update(null);
            this.lastTime = System.currentTimeMillis();
        }, 300L, 20L * interval);
    }
}
