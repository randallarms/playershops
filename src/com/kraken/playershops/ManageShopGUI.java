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

public class ManageShopGUI {

	public static Main plugin;
	public static String language;
	public static Player player;
	public static Shop shop;
	public static String owner;
	public static boolean isOwner;
	public static boolean isRenaming;
	public static Inventory manageShopGUI = Bukkit.createInventory(null, 27, "Manage Shop");
	
	public ManageShopGUI(Main plugin, String language, Player player, Shop shop) {
		ManageShopGUI.plugin = plugin;
        ManageShopGUI.language = language;
        ManageShopGUI.player = player;
        ManageShopGUI.shop = shop;
        for ( String key : plugin.shopowners.getKeys(false) ) {
			if (  plugin.shopowners.getString(key + "." + shop.id).equals(shop.locStr) ) {
				ManageShopGUI.owner = Bukkit.getOfflinePlayer( UUID.fromString(key) ).getName();
			}
		}
        ManageShopGUI.isOwner = player.getName().equalsIgnoreCase(owner);
        ManageShopGUI.isRenaming = (plugin.players.getString(player.getUniqueId().toString() + ".naming") != null );
    }
	
	public boolean openManageShopGUI() {
		
		manageShopGUI.setItem(0, makeItemGUI( Material.BOOK, 1, ChatColor.GOLD + "" + ChatColor.BOLD + owner + "'s Shop", getDesc("'s Shop") ) );
		manageShopGUI.setItem(1, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(2, new ItemStack(Material.AIR, 1)); //Owner reserved slot: Change the model (style)
		manageShopGUI.setItem(3, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(4, new ItemStack(Material.AIR, 1)); //Admin/op reserved slot: infinite stock
		manageShopGUI.setItem(5, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(6, new ItemStack(Material.AIR, 1)); //Owner reserved slot: Change the name/title
		manageShopGUI.setItem(7, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(8, makeItemGUI( Material.BARRIER, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Close Shop", getDesc("Close Shop") ) );
		
		manageShopGUI.setItem(9, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(10, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(11, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(12, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(13, new ItemStack(Material.AIR, 1)); //Owner reserved slot: list an item to sell
		manageShopGUI.setItem(14, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(15, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(16, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(17, new ItemStack(Material.AIR, 1));
		
		manageShopGUI.setItem(18, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(19, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(20, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(21, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(22, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(23, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(24, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(25, new ItemStack(Material.AIR, 1));
		manageShopGUI.setItem(26, makeItemGUI( Material.BLAZE_POWDER, 1, ChatColor.RED + "" + ChatColor.BOLD + "Delete Shop", getDesc("Delete Shop") ) );
		
		//OP menu options
		if ( player.isOp() ) {
			manageShopGUI.setItem(4, makeItemGUI( Material.NETHER_STAR, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Infinite Stock", getDesc("Infinite Stock") ) );
		}
		
		//Owner menu options
		if ( isOwner ) {
			manageShopGUI.setItem(2, makeItemGUI( Material.SKULL_ITEM, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Change Style", getDesc("Change Style") ) );
			manageShopGUI.setItem(6, makeItemGUI( Material.NAME_TAG, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Change Name", getDesc("Change Name") ) );
			manageShopGUI.setItem(13, makeItemGUI( Material.EMERALD, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Sell an Item", getDesc("Sell an Item") ) );
		}
				
		player.openInventory(manageShopGUI);
		
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
			case "Close Shop":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Close this shop menu" );
				break;
			case "Change Style":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Change the look of the shopkeeper." );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "(Owner command only)" );
				break;
			case "Change Name":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Change the name of the shopkeeper." );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Click this, and then enter the command: " );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "\"/shops name [Name (abc123-_ )]\"" );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "(Owner command only)" );
				if (isRenaming) {
					desc.add( ChatColor.GREEN + "Currently renaming this shop!");
				}
				break;
			case "Sell an Item":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "List an item for sale." );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "(Owner command only)" );
				break;
			case "Infinite Stock":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Set the stock to never deplete." );
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "(Op command only)" );
				break;
			case "Delete Shop":
				desc.add( ChatColor.GRAY + "" + ChatColor.ITALIC + "Remove this shop permanently." );
				desc.add( ChatColor.RED + "" + ChatColor.ITALIC + "(DANGER! Cannot be reversed!)" );
				break;
			default:
				desc.add( ChatColor.GOLD + "[PLAYER SHOP]" );
				break;
				
		}
		
		return desc;
		
	}
	
}
