package org.satellite.dev.progiple.flamemine.mine.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.satellite.dev.progiple.flamemine.configs.Config;
import org.satellite.dev.progiple.flamemine.configs.MineData;
import org.satellite.dev.progiple.flamemine.mine.MineManager;

public class BlockBreakHandler implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (MineManager.containsBlock(block)) {
            String nextLevel = MineManager.getNextLevel();

            MineData.addMineBlocks(e.getPlayer().getName(), 1);
            if (nextLevel != null && MineData.getMineBlocks() >=
                    Config.getInt(String.format("levels.%s.requirements.amount", nextLevel))) {
                MineManager.upgrade(nextLevel);
            }
        }
    }
}
