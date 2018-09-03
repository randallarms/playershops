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

public class ConfirmBuyGUI {

	public static Main plugin;
	public static String language;
	public static Player player;
	public static Shop shop;
	public static String owner;
	public static boolean isOwner;
	public static ItemStack buyItem;
	public static int buyAmount;
	public static int buyPrice;
	public static int slot;
	public static Inventory confirmBuyGUI = Bukkit.createInventory(null, 27, "Confirm Purchase");
	
	public ConfirmBuyGUI(Main plugin, String language, Player player, Shop shop, ItemStack buyItem, int buyAmount, int buyPrice, int slot) {
		ConfirmBuyGUI.plugin = plugin;
        ConfirmBuyGUI.language = language;
        ConfirmBuyGUI.player = player;
        ConfirmBuyGUI.shop = shop;
        for ( String key : plugin.shopowners.getKeys(false) ) {
			if (  plugin.shopowners.getString(key + "." + shop.id).equals(shop.locStr) ) {
				ConfirmBuyGUI.owner = Bukkit.getOfflinePlayer( UUID.fromString(key) ).getName();
			}
		}
        ConfirmBuyGUI.isOwner = player.getName().equalsIgnoreCase(owner);
        ConfirmBuyGUI.buyItem = buyItem;
        ConfirmBuyGUI.buyAmount = buyAmount;
        ConfirmBuyGUI.buyPrice = buyPrice;
        ConfirmBuyGUI.slot = slot;
    }
	
	public boolean openBuyGUI() {
		
		confirmBuyGUI.setItem(0, makeItemGUI( Material.BOOK, 1, ChatColor.GOLD + "" + ChatColor.BOLD + owner + "'s Shop", getDesc("'s Shop") ) );
		confirmBuyGUI.setItem(1, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(2, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(3, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(4, new ItemStack(Material.AIR, 1)); //Owner reserved slot: remove listing
		confirmBuyGUI.setItem(5, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(6, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(7, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(8, makeItemGUI( Material.BARRIER, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Close", getDesc("Close") ) );
		
		confirmBuyGUI.setItem(9, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(10, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(11, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(12, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(13, buyItem); //Item to buy
		confirmBuyGUI.setItem(14, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(15, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(16, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(17, new ItemStack(Material.AIR, 1));
		
		confirmBuyGUI.setItem(18, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(19, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(20, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(21, makeItemGUI( Material.EMERALD, 1, ChatColor.GOLD + "" + ChatColor.BOLD + buyItem.toString(), getDesc("Confirm Purchase") ) );
		confirmBuyGUI.setItem(22, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(23, makeItemGUI( Material.BARRIER, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Back", getDesc("Cancel") ) );
		confirmBuyGUI.setItem(24, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(25, new ItemStack(Material.AIR, 1));
		confirmBuyGUI.setItem(26, new ItemStack(Material.AIR, 1));

		//Owner menu options
		if ( isOwner ) {
			confirmBuyGUI.setItem(4, makeItemGUI( Material.BLAZE_POWDER, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Remove Listing", getDesc("Remove Listing") ) );
		}
		
		player.openInventory(confirmBuyGUI);
		
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
			case "Close":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Close this shop menu" );
				break;
			case "Confirm Purchase":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Confirm purchase of " + buyAmount + " of " );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + buyItem.getType().toString() );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "for " + buyPrice + " each?" );
				break;
			case "Remove Listing":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Remove listing for " );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + buyItem.getType().toString() );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "at " + buyPrice + " each?" );
				break;
			case "Cancel":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Cancel this purchase and go back." );
				break;
			default:
				desc.add( ChatColor.GOLD + "[PLAYER SHOP]" );
				break;
				
		}
		
		return desc;
		
	}
	
}
