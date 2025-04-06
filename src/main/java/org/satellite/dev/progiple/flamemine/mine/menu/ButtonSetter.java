package org.satellite.dev.progiple.flamemine.mine.menu;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.novasparkle.lunaspring.API.Menus.Items.Item;
import org.satellite.dev.progiple.flamemine.configs.Config;
import org.satellite.dev.progiple.flamemine.configs.MineData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class ButtonSetter {
    private final List<Item> list = new ArrayList<>();
    private final InfoItem infoButton;
    public ButtonSetter(ConfigurationSection itemsSection) {
        ConfigurationSection infoButtonSection = itemsSection.getConfigurationSection("info_item");
        assert infoButtonSection != null;
        this.infoButton = new InfoItem(infoButtonSection);
        this.list.add(this.infoButton);

        ConfigurationSection levelItemsSection = itemsSection.getConfigurationSection("levels");
        assert levelItemsSection != null;

        ConfigurationSection blockedSection = Config.getSection("blocked_level_item");
        for (String key : levelItemsSection.getKeys(false)) {
            ConfigurationSection levelSection = Config.getSection(String.format("levels.%s", key));
            if (levelSection == null) continue;

            ConfigurationSection itemSection = levelItemsSection.getConfigurationSection(key);
            if (itemSection == null) continue;

            if (levelSection.getBoolean("requirements.needMineBlocks") &&
                    levelSection.getInt("requirements.amount") > MineData.getMineBlocks()) {
                this.list.add(new BlockedItem(blockedSection, itemSection));
            }
            else this.list.add(new Item(itemSection, itemSection.getInt("slot")));
        }
    }

    private static class BlockedItem extends Item {
        public BlockedItem(ConfigurationSection section, ConfigurationSection itemSection) {
            super(section, itemSection.getInt("slot"));

            setDisplayName(getDisplayName().replace("[display_name]",
                    Objects.requireNonNull(itemSection.getString("displayName"))));

            List<String> lore = new ArrayList<>(getLore());
            lore.replaceAll(line -> line.replace("[need]",
                    String.valueOf(Config.getInt(String.format("levels.%s.requirements.amount", itemSection.getName())))));
            setLore(lore);
        }

        @Override
        public void onClick(InventoryClickEvent e) {
            Config.sendMessage(e.getWhoClicked(), "mineIsLocked");
        }
    }

    @Getter
    public static class InfoItem extends Item {
        private final List<String> defaultLore;
        public InfoItem(ConfigurationSection section) {
            super(section, section.getInt("slot"));
            this.defaultLore = new ArrayList<>(getLore());
        }
    }
}
