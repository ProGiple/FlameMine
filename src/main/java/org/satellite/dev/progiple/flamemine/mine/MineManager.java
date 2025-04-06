package org.satellite.dev.progiple.flamemine.mine;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.satellite.dev.progiple.flamemine.configs.Config;
import org.satellite.dev.progiple.flamemine.configs.MineData;

import java.util.Comparator;
import java.util.List;

@UtilityClass
public class MineManager {
    @Getter private final Mine mine;
    static {
        mine = new Mine();
        mine.setHologram();
    }

    public void update(String newLevelId) {
        mine.update(null);
    }

    public void upgrade(String nextLevel) {
        String levelId = Config.getString("settings.action_in_upgrade_mine").equals("RANDOM") ? null : nextLevel;
        mine.update(levelId);
    }

    public void reload() {
        mine.rePos();
        mine.setHologram();
    }

    public boolean containsBlock(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        if (mine.getPos1() == null || mine.getPos2() == null) return false;
        int minX = Math.min(mine.getPos1().getBlockX(), mine.getPos2().getBlockX());
        int maxX = Math.max(mine.getPos1().getBlockX(), mine.getPos2().getBlockX());
        int minY = Math.min(mine.getPos1().getBlockY(), mine.getPos2().getBlockY());
        int maxY = Math.max(mine.getPos1().getBlockY(), mine.getPos2().getBlockY());
        int minZ = Math.min(mine.getPos1().getBlockZ(), mine.getPos2().getBlockZ());
        int maxZ = Math.max(mine.getPos1().getBlockZ(), mine.getPos2().getBlockZ());

        return minX <= x && x <= maxX
                && minY <= y && y<= maxY
                && minZ <= z && z <= maxZ;
    }

    public String getNextLevel() {
        int mines = MineData.getMineBlocks();

        ConfigurationSection section = Config.getSection("levels");
        List<String> list = section.getKeys(false).stream().toList();

        return list.stream()
                .filter(k -> {
                    boolean b = section.getBoolean(String.format("%s.requirements.needMineBlocks", k));
                    return b && mines < section.getInt(String.format("%s.requirements.amount", k));
                })
                .min(Comparator.comparingInt(k -> section.getInt(String.format("%s.requirements.amount", k))))
                .orElse(null);
    }
}
