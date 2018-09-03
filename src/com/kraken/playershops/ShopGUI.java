package com.kraken.playershops;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopGUI {

	public static Main plugin;
	public static String language;
	public static Player player;
	public static FileConfiguration shopowners;
	public static Shop shop;
	public static String owner;
	public static boolean isOwner;
	public static Inventory shopGUI = Bukkit.createInventory(null, 27, "Shop Menu");
	
	public ShopGUI(Main plugin, String language, Player player, Shop shop) {
		ShopGUI.plugin = plugin;
        ShopGUI.language = language;
        ShopGUI.player = player;
        ShopGUI.shopowners = ShopGUI.plugin.shopowners;
        ShopGUI.shop = shop;
        for ( String key : shopowners.getKeys(false) ) {
			if ( shopowners.getString(key + "." + shop.id).equals(shop.locStr) ) {
				ShopGUI.owner = Bukkit.getOfflinePlayer( UUID.fromString(key) ).getName();
			}
		}
        ShopGUI.isOwner = player.getName().equalsIgnoreCase(ShopGUI.owner);
    }
	
	public boolean openShopGUI() {
		
		shopGUI.setItem(0, makeItemGUI( Material.BOOK, 1, ChatColor.GOLD + "" + ChatColor.BOLD + owner + "'s Shop", getDesc("'s Shop") ) );
		shopGUI.setItem(1, new ItemStack(Material.AIR, 1));
		shopGUI.setItem(2, new ItemStack(Material.AIR, 1));
		shopGUI.setItem(3, new ItemStack(Material.AIR, 1));
		shopGUI.setItem(4, new ItemStack(Material.AIR, 1)); //Admin/op/owner menu slot (reserved)
		shopGUI.setItem(5, new ItemStack(Material.AIR, 1));
		shopGUI.setItem(6, new ItemStack(Material.AIR, 1));
		shopGUI.setItem(7, new ItemStack(Material.AIR, 1));
		shopGUI.setItem(8, makeItemGUI( Material.BARRIER, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Close Shop", getDesc("Close Shop") ) );
		
		shopGUI.setItem(9, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(10, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(11, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(12, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(13, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(14, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(15, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(16, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(17, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		
		shopGUI.setItem(18, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(19, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(20, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(21, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(22, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(23, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(24, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(25, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		shopGUI.setItem(26, new ItemStack(Material.AIR, 1)); //Item for sale slot (reserved)
		
		//OP menu options
		if ( player.isOp() || isOwner ) {
			shopGUI.setItem(4, makeItemGUI( Material.NETHER_STAR, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Manage Shop", getDesc("Manage Shop") ) );
		}
		
		//Item slot counter
		int n = 9; //start at 9: first slot in the second row
		
		LinkedHashMap<ItemStack, Integer[]> inv = shop.getInventory();
		
		//Add each inventory item from stock to shop
		for ( ItemStack invItem : inv.keySet() ) {
			
			//Don't go out of bounds on slots
			if (n > 26) {
				break;
			}
			
			//Get item details
			int amount = inv.get(invItem)[0];
			int price = inv.get(invItem)[1];
			
			//Add item to slot
			shopGUI.setItem(n, makeSellItemGUI( invItem, amount, price ) );
			n++;
			
		}

		player.openInventory(shopGUI);
		
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
	
	public static ItemStack makeSellItemGUI(ItemStack item, int amount, int price) {
		
		String amountStr = String.valueOf(amount);
		if ( plugin.shopkeepers.getBoolean(shop.id + ".infinite") ) {
			amountStr = "unlimited";
		}
		
		ArrayList<String> desc = new ArrayList<String>();
		desc.clear();
		
		desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "> Price: " + plugin.getEconomy().format(price) );
		desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "> In stock: " + amountStr );
		
		if ( item.hasItemMeta() && item.getItemMeta().hasLore() ) {
			desc.add(" ");
			desc.addAll( (ArrayList<String>) item.getItemMeta().getLore() );
		}
		
		ItemMeta meta = item.getItemMeta();
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
			case "Close Shop":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Close this shop menu" );
				break;
			case "Manage Shop":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Manage this shop's settings" );
				break;
			default:
				desc.add( ChatColor.GOLD + "[PLAYER SHOP]" );
				break;
				
		}
		
		return desc;
		
	}
	
}
