package com.kraken.playershops;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ShopKeeper {
	
	private Main plugin = Main.instance;
	FileConfiguration shopowners;
	FileConfiguration shopkeepers;
	String locStr;
	String owner;
	String id;
	
	Shop shop;
	
	public ShopKeeper(Shop shop) {
		this.shop = shop;
		this.locStr = shop.locStr;
		this.shopkeepers = plugin.shopkeepers;
		this.shopowners = plugin.shopowners;
		this.id = shop.getId();
		for ( String key : shopowners.getKeys(false) ) {
			if ( shopowners.getString(key + "." + id).equals(locStr) ) {
				this.owner = Bukkit.getOfflinePlayer( UUID.fromString(key) ).getName();
			}
		}
	}
	
	public void openShopGUI(Player player) {
		ShopGUI shopGUI = new ShopGUI(plugin, plugin.language, player, shop);
		shopGUI.openShopGUI();
	}
    
}
