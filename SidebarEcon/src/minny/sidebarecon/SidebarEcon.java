package minny.sidebarecon;

import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class SidebarEcon extends JavaPlugin implements Listener {

	Economy econ = null;
	List<String> players = new ArrayList<String>();

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		setupEconomy();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			Scoreboard board;
			board = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective obj = board.registerNewObjective(p.getName() + "econ",
					"dummy");
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			obj.setDisplayName(ChatColor.GOLD + "Money");
			Score score = obj.getScore(p);
			score.setScore((int) Math.round(econ.getBalance(p.getName())));
			p.setScoreboard(board);
			this.players.add(p.getName());
			getServer().getScheduler().scheduleSyncRepeatingTask(this,
					new UpdateScoreboard(this, p, obj), 10L, 10L);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			Scoreboard board;
			board = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective obj = board.registerNewObjective(p.getName() + "econ",
					"dummy");
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			obj.setDisplayName(ChatColor.GOLD + "Money");
			Score score = obj.getScore(p);
			score.setScore((int) Math.round(econ.getBalance(p.getName())));
			p.setScoreboard(board);
			this.players.add(p.getName());
			getServer().getScheduler().scheduleSyncRepeatingTask(this,
					new UpdateScoreboard(this, p, obj), 20L, 20L);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		this.players.remove(e.getPlayer().getName());
	}

	public boolean setupEconomy() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private class UpdateScoreboard extends BukkitRunnable {

		Objective obj;
		Player player;
		SidebarEcon sidebar;

		UpdateScoreboard(SidebarEcon econ, Player player, Objective obj) {
			this.obj = obj;
			this.player = player;
			this.sidebar = econ;
		}

		public void run() {
			if (sidebar.players.contains(player.getName())) {
				Score score = obj.getScore(player);
				score.setScore((int) Math.round(econ.getBalance(player
						.getName())));
			}
		}
	}

}