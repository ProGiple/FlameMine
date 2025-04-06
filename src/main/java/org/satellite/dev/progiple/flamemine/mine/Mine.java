package org.satellite.dev.progiple.flamemine.mine;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.Util.Service.managers.ColorManager;
import org.novasparkle.lunaspring.API.Util.utilities.LunaMath;
import org.satellite.dev.progiple.flamemine.configs.Config;
import org.satellite.dev.progiple.flamemine.configs.MineData;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
public class Mine {
    @Setter private Location pos1;
    @Setter private Location pos2;
    private String levelId;
    public Mine() {
        this.rePos();
        this.update(null);
    }

    public void rePos() {
        this.pos1 = MineData.getPos(1);
        this.pos2 = MineData.getPos(2);
    }

    public void update(String newLevelId) {
        if (this.pos1 == null || this.pos2 == null || this.pos1.equals(this.pos2)) return;
        
        ConfigurationSection levelSection = Config.getSection("levels");
        List<String> levelList = levelSection.getKeys(false)
                .stream()
                .filter(k -> {
                    String path = String.format("%s.requirements.", k);
                    return !levelSection.getBoolean(path + "needMineBlocks") ||
                            MineData.getMineBlocks() >= levelSection.getInt(path + "amount");
                })
                .toList();
        String levelId = 
                newLevelId != null &&
                levelSection.getKeys(false).contains(newLevelId) ?
                        newLevelId :
                        (levelList.isEmpty() ?
                        null :
                        levelList.get(LunaMath.getRandomInt(0, levelList.size())));
        if (levelId == null) return;

        int minX = Math.min(this.pos1.getBlockX(), this.pos2.getBlockX());
        int maxX = Math.max(this.pos1.getBlockX(), this.pos2.getBlockX());
        int minY = Math.min(this.pos1.getBlockY(), this.pos2.getBlockY());
        int maxY = Math.max(this.pos1.getBlockY(), this.pos2.getBlockY());
        int minZ = Math.min(this.pos1.getBlockZ(), this.pos2.getBlockZ());
        int maxZ = Math.max(this.pos1.getBlockZ(), this.pos2.getBlockZ());

        ConfigurationSection oreSection = Config.getSection(String.format("levels.%s.ores", levelId));
        Location respawnLocation = Config.getLocation("settings.respawnLocation");

        World world = this.pos1.getWorld();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    world.getPlayers().forEach(p -> {
                        if (p.getLocation().getBlock().equals(block)) p.teleport(respawnLocation);
                    });

                    Material material = this.getMaterial(oreSection);
                    block.setType(material);
                }
            }
        }

        this.levelId = levelId;
        this.setHologram();
    }

    private Material getMaterial(ConfigurationSection oreSection) {
        double chance = Math.random() * 100;
        Set<String> keys = oreSection.getKeys(false);

        List<Material> materials = keys
                .stream()
                .filter(k -> oreSection.getDouble(k) >= chance)
                .map(Material::getMaterial)
                .toList();
        return materials.isEmpty() ? keys
                .stream()
                .max(Comparator.comparingDouble(oreSection::getDouble))
                .map(Material::getMaterial)
                .orElse(Material.getMaterial(keys.stream().toList().get(0)))
                : materials.get(LunaMath.getRandomInt(0, materials.size()));
    }

    public void setHologram() {
        String holoName = "flamemine_holo";
        ConfigurationSection holoSection = Config.getSection("settings.hologram");

        Location location = Config.getLocation("settings.hologram.position").clone();
        location.add(0.5, 0, 0.5);

        if (DHAPI.getHologram(holoName) != null) DHAPI.removeHologram(holoName);
        Hologram hologram = DHAPI.createHologram(holoName, location);

        List<String> lines = holoSection.getStringList("lines");
        lines.forEach(l -> {
            if (l.startsWith("Material.")) DHAPI.addHologramLine(hologram, Objects.requireNonNull(Material.getMaterial(
                        l.replace("Material.", ""))));
            else DHAPI.addHologramLine(hologram, ColorManager.color(l));
        });
    }
}
