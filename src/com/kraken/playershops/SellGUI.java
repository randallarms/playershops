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

public class SellGUI {

	public static Main plugin;
	public static String language;
	public static Player player;
	public static Shop shop;
	
	public static String owner;
	public static ItemStack buyItem;
	public static int buyAmount;
	public static int buyPrice;
	public static Inventory sellGUI = Bukkit.createInventory(null, 27, "Sell Item");
	
	public SellGUI(Main plugin, String language, Player player, Shop shop, ItemStack buyItem, int buyAmount, int buyPrice) {
		SellGUI.plugin = plugin;
        SellGUI.language = language;
        SellGUI.player = player;
        SellGUI.shop = shop;
        for ( String key : plugin.shopowners.getKeys(false) ) {
			if (  plugin.shopowners.getString(key + "." + shop.id).equals(shop.locStr) ) {
				SellGUI.owner = Bukkit.getOfflinePlayer( UUID.fromString(key) ).getName();
			}
		}
        SellGUI.buyItem = buyItem;
        SellGUI.buyAmount = buyAmount;
        SellGUI.buyPrice = buyPrice;
    }
	
	public boolean openSellGUI() {
		
		sellGUI.setItem(0, makeItemGUI( Material.BOOK, 1, ChatColor.GOLD + "" + ChatColor.BOLD + owner + "'s Shop", getDesc("'s Shop") ) );
		sellGUI.setItem(1, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(2, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(3, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(4, buyItem );
		sellGUI.setItem(5, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(6, makeItemGUI( Material.MAP, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Confirm Listing", getDesc("Confirm Listing") ) );
		sellGUI.setItem(7, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(8, makeItemGUI( Material.BARRIER, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Back", getDesc("Cancel") ) );
		
		sellGUI.setItem(9, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(10, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(11, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(12, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(13, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(14, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(15, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(16, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(17, new ItemStack(Material.AIR, 1));
		
		sellGUI.setItem(18, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(19, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(20, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(21, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(22, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(23, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(24, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(25, new ItemStack(Material.AIR, 1));
		sellGUI.setItem(26, new ItemStack(Material.AIR, 1));

		boolean emptySaleSlot = buyAmount < 1;
		
		//Owner menu options
		if ( !emptySaleSlot ) {
			sellGUI.setItem(10, makeItemGUI( Material.EMERALD, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "+1 price each", getDesc("+1 price each") ) );
			sellGUI.setItem(12, makeItemGUI( Material.DIAMOND, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "+100 price each", getDesc("+100 price each") ) );
			sellGUI.setItem(14, makeItemGUI( Material.REDSTONE, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "-1 price each", getDesc("-1 price each") ) );
			sellGUI.setItem(16, makeItemGUI( Material.BLAZE_POWDER, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "-100 price each", getDesc("-100 price each") ) );
			
			sellGUI.setItem(19, makeItemGUI( Material.EMERALD_BLOCK, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "+10 price each", getDesc("+10 price each") ) );
			sellGUI.setItem(21, makeItemGUI( Material.DIAMOND_BLOCK, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "+1000 price each", getDesc("+1000 price each") ) );
			sellGUI.setItem(23, makeItemGUI( Material.REDSTONE_BLOCK, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "-10 price each", getDesc("-10 price each") ) );
			sellGUI.setItem(25, makeItemGUI( Material.MAGMA, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "-1000 price each", getDesc("-1000 price each") ) );
		}
		
		player.openInventory(sellGUI);
		
		return true;
		
	}
	
	public static ItemStack makeSellItemGUI(ItemStack item, ArrayList<String> desc) {
		
		if (buyAmount == 0 && buyPrice == 0) {
			ItemStack newItem = new ItemStack(item.getType(), item.getAmount());
			
			ItemMeta meta = newItem.getItemMeta();
			meta.setLore(desc);
			newItem.setItemMeta(meta);
			
			return newItem;
		} else {
			return item;
		}
		
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
			case "Item to Sell":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Click & drop your item" );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "here to list it for sale!" );
				break;
			case "Confirm Listing":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Confirm sale of " );
				desc.add( ChatColor.GREEN + buyItem.getType().toString() + " (x" + buyAmount + ")"  );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "for " + buyPrice + " per stack?" );
				break;
			case "+1 price each":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Increase the buy price of" );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "each stack by +1." );
				break;
			case "+10 price each":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Modify the buy price of" );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "each stack by +10." );
				break;
			case "+100 price each":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Modify the buy price of" );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "each stack by +100." );
				break;
			case "+1000 price each":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Modify the buy price of" );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "each stack by +1000." );
				break;
			case "-1 price each":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Modify the buy price of" );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "each stack by -1." );
				break;
			case "-10 price each":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Modify the buy price of" );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "each stack by -10." );
				break;
			case "-100 price each":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Modifye the buy price of" );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "each stack by -100." );
				break;
			case "-1000 price each":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Modify the buy price of" );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "each stack by -1000." );
				break;
			case "Cancel":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Cancel this sale and go back." );
				break;
			default:
				desc.add( ChatColor.GOLD + "[PLAYER SHOP]" );
				break;
				
		}
		
		return desc;
		
	}
	
}
