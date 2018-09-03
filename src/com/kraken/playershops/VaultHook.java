package com.kraken.playershops;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import net.milkbowl.vault.economy.Economy;

public class VaultHook {
	
	private Main plugin = Main.instance;
	
	private Economy provider;
	
	public void hook(Economy econ) {
		provider = econ;
		Bukkit.getServicesManager().register(Economy.class, this.provider, this.plugin, ServicePriority.Normal);	
	}
	
	public void unhook() {
		Bukkit.getServicesManager().unregister(Economy.class, this.provider);	
	}
    
}
