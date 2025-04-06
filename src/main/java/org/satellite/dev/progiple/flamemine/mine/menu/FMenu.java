package org.satellite.dev.progiple.flamemine.mine.menu;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.novasparkle.lunaspring.API.Menus.AMenu;
import org.novasparkle.lunaspring.API.Menus.Items.Item;
import org.satellite.dev.progiple.flamemine.FlameMine;
import org.satellite.dev.progiple.flamemine.configs.MainMenuConfig;

import java.util.ArrayList;
import java.util.List;

public class FMenu extends AMenu {
    private int taskId;
    private final ButtonSetter buttonSetter;
    public FMenu(Player player) {
        super(player, MainMenuConfig.getString("title"), (byte) MainMenuConfig.getInt("size"),
                MainMenuConfig.getSection("items.decoration"));
        ConfigurationSection itemsSection = MainMenuConfig.getSection("items");
        this.buttonSetter = new ButtonSetter(itemsSection);
    }

    @Override
    public void onOpen(InventoryOpenEvent e) {
        this.buttonSetter.getList().forEach(i -> {
            this.updateItemLore(i);
            i.insert(this);
        });

        this.taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(FlameMine.getINSTANCE(), () -> {
            this.updateItemLore(this.buttonSetter.getInfoButton());
            this.buttonSetter.getInfoButton().insert(this);
        }, 20L, 20L).getTaskId();
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent e) {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

    private void updateItemLore(Item item) {
        List<String> starterLore = item instanceof ButtonSetter.InfoItem infoItem ? infoItem.getDefaultLore() : item.getLore();
        List<String> lore = new ArrayList<>(starterLore);

        lore.replaceAll(line -> PlaceholderAPI.setPlaceholders(this.getPlayer(), line));
        item.setLore(lore);

        item.setDisplayName(PlaceholderAPI.setPlaceholders(this.getPlayer(), item.getDisplayName()));
    }
}
