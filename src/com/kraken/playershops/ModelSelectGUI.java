package com.kraken.playershops;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ModelSelectGUI {

	public static Main plugin;
	public static String language;
	public static Player player;
	public static Shop shop;
	public static String owner;
	public static boolean isOwner;
	public static Inventory modelSelectGUI = Bukkit.createInventory(null, 27, "Style Shop");
	
	public ModelSelectGUI(Main plugin, String language, Player player, Shop shop) {
		ModelSelectGUI.plugin = plugin;
        ModelSelectGUI.language = language;
        ModelSelectGUI.player = player;
        ModelSelectGUI.shop = shop;
        for ( String key : plugin.shopowners.getKeys(false) ) {
			if (  plugin.shopowners.getString(key + "." + shop.id).equals(shop.locStr) ) {
				ModelSelectGUI.owner = Bukkit.getOfflinePlayer( UUID.fromString(key) ).getName();
			}
		}
        ModelSelectGUI.isOwner = player.getName().equalsIgnoreCase(owner);
    }
	
	public boolean openStyleGUI() {
		
		modelSelectGUI.setItem(0, makeItemGUI( Material.BOOK, 1, ChatColor.GOLD + "" + ChatColor.BOLD + owner + "'s Shop", getDesc("'s Shop") ) );
		modelSelectGUI.setItem(1, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(2, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(3, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(4, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(5, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(6, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(7, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(8, makeItemGUI( Material.BARRIER, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Cancel", getDesc("Cancel") ) );
		
		modelSelectGUI.setItem(9, makeItemGUI( Material.SKULL_ITEM, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "FARMER (DEFAULT)", getDesc("Farmer") ) );
		modelSelectGUI.setItem(10, makeItemGUI( Material.SKULL_ITEM, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "BLACKSMITH", getDesc("Blacksmith") ) );
		modelSelectGUI.setItem(11, makeItemGUI( Material.SKULL_ITEM, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "BUTCHER", getDesc("Butcher") ) );
		modelSelectGUI.setItem(12, makeItemGUI( Material.SKULL_ITEM, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "LIBRARIAN", getDesc("Librarian") ) );
		modelSelectGUI.setItem(13, makeItemGUI( Material.SKULL_ITEM, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "NITWIT", getDesc("Nitwit") ) );
		modelSelectGUI.setItem(14, makeItemGUI( Material.SKULL_ITEM, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "PRIEST", getDesc("Priest") ) );
		modelSelectGUI.setItem(15, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(16, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(17, new ItemStack(Material.AIR, 1));
		
		modelSelectGUI.setItem(18, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(19, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(20, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(21, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(22, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(23, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(24, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(25, new ItemStack(Material.AIR, 1));
		modelSelectGUI.setItem(26, new ItemStack(Material.AIR, 1));
				
		player.openInventory(modelSelectGUI);
		
		return true;
		
	}
	
	public static ItemStack makeItemGUI(Material type, int amount, String name, ArrayList<String> desc) {
		
		ItemStack item = new ItemStack(type, amount);
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(desc);
		
		item.setItemMeta(meta);
		
		return item;
		
	}
	
	public static ArrayList<String> getDesc(String name) {
		
		ArrayList<String> desc = new ArrayList<String>();
		desc.clear();
		
		switch (name) {
		
			case "'s Shop":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Welcome to " + owner + "'s shop!" );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "ID:" + shop.id );
				break;
			case "Cancel":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Cancel this sale and go back." );
				break;
			case "Farmer":
			case "Blacksmith":
			case "Butcher":
			case "Librarian":
			case "Nitwit":
			case "Priest":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Set your shopkeeper " );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "to the [" + name + "] style!" );
				break;
			default:
				desc.add( ChatColor.GOLD + "[PLAYER SHOP]" );
				break;
				
		}
		
		return desc;
		
	}
	
}
