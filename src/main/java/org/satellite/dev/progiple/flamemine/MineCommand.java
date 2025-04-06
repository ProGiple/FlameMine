package org.satellite.dev.progiple.flamemine;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.Menus.MenuManager;
import org.satellite.dev.progiple.flamemine.configs.Config;
import org.satellite.dev.progiple.flamemine.configs.MainMenuConfig;
import org.satellite.dev.progiple.flamemine.configs.MineData;
import org.satellite.dev.progiple.flamemine.mine.MineManager;
import org.satellite.dev.progiple.flamemine.mine.menu.FMenu;

import java.util.List;

public class MineCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length >= 1) {
            if (strings[0].equalsIgnoreCase("menu")) this.openMenu(commandSender);
            else if (commandSender.hasPermission("flamemine.admin")) {
                switch (strings[0]) {
                    case "reload" -> {
                        Config.reload();
                        MineManager.reload();
                        MainMenuConfig.reload();
                        Config.sendMessage(commandSender, "reloaded");
                    }
                    case "setPos1" -> this.setPos(commandSender, 1);
                    case "setPos2" -> this.setPos(commandSender, 2);
                    case "update" -> MineManager.update();
                }
            } else Config.sendMessage(commandSender, "noPermission");
        } else this.openMenu(commandSender);
        return true;
    }

    private void openMenu(CommandSender sender) {
        if (sender instanceof Player player) {
            MenuManager.openInventory(player, new FMenu(player));
        } else Config.sendMessage(sender, "noConsole");
    }

    public void setPos(CommandSender sender, int pos) {
        if (sender instanceof Player player) {
            Block block = player.getTargetBlock(8);
            if (block == null || block.getType().isAir()) {
                Config.sendMessage(player, "blockIsEmpty");
                return;
            }

            MineData.setPos(pos, block.getLocation());
            MineManager.reload();
            Config.sendMessage(player, "newPos", String.valueOf(pos));
        } else Config.sendMessage(sender, "noConsole");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return strings.length == 1 ? List.of("reload", "setPos1", "setPos2", "update", "menu") : List.of();
    }
}
