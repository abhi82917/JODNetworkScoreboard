package com.jodnetwork.scoreboard;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class JODNetworkScoreboard extends JavaPlugin {

    private Economy economy;
    private int animationIndex = 0;
    private final String[] animatedHeader = {
            ChatColor.GOLD + "JOD NETWORK",
            ChatColor.YELLOW + "JOD NETWORK",
            ChatColor.GREEN + "JOD NETWORK",
            ChatColor.BLUE + "JOD NETWORK",
            ChatColor.RED + "JOD NETWORK"
    };

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Vault is required for this plugin. Disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("JODNetworkScoreboard enabled!");

        // Schedule scoreboard updates
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateScoreboard(player);
                }
                animationIndex = (animationIndex + 1) % animatedHeader.length;
            }
        }.runTaskTimer(this, 0L, 20L); // Update every second
    }

    @Override
    public void onDisable() {
        getLogger().info("JODNetworkScoreboard disabled!");
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    private void updateScoreboard(Player player) {
        String header = animatedHeader[animationIndex];
        String footer = ChatColor.AQUA + "jodnetwork.falixsrv.me";

        player.setPlayerListHeaderFooter(header, footer);

        // Create the scoreboard
        var scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        var objective = scoreboard.registerNewObjective("JODStats", "dummy", ChatColor.BOLD + "Player Stats");
        objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

        // Add stats
        objective.getScore(ChatColor.GOLD + "Name: " + ChatColor.WHITE + player.getName()).setScore(7);
        objective.getScore(ChatColor.RED + "Hearts: " + ChatColor.WHITE + (int) (player.getHealth() / 2) + " ❤").setScore(6);
        objective.getScore(ChatColor.GREEN + "Money: " + ChatColor.WHITE + economy.getBalance(player)).setScore(5);
        objective.getScore(ChatColor.AQUA + "Playtime: " + ChatColor.WHITE + getPlaytime(player)).setScore(4);
        objective.getScore(ChatColor.YELLOW + "Rank: " + ChatColor.WHITE + getRank(player)).setScore(3);
        objective.getScore(ChatColor.DARK_RED + "Kills: " + ChatColor.WHITE + getKillCount(player)).setScore(2);
        objective.getScore(ChatColor.DARK_GRAY + "Deaths: " + ChatColor.WHITE + getDeathCount(player)).setScore(1);

        player.setScoreboard(scoreboard);
    }

    private String getPlaytime(Player player) {
        long playtime = player.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE) / 20L;
        long hours = playtime / 3600;
        long minutes = (playtime % 3600) / 60;
        return hours + "h " + minutes + "m";
    }

    private String getRank(Player player) {
        return player.isOp() ? "Admin" : "Player"; // Example, customize for your permissions plugin
    }

    private int getKillCount(Player player) {
        return player.getStatistic(org.bukkit.Statistic.PLAYER_KILLS);
    }

    private int getDeathCount(Player player) {
        return player.getStatistic(org.bukkit.Statistic.DEATHS);
    }
}
