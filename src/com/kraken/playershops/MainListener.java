package com.kraken.playershops;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class MainListener implements Listener {
	
	Main plugin;
	WeakHashMap<String, Boolean> options = new WeakHashMap<>();
	String language;
	ArrayList<String> immortals = new ArrayList<>();
	WeakHashMap<String, Shop> shops = new WeakHashMap<>();
	
    public MainListener(Main plugin, String language) {
    	
  	  	this.plugin = plugin;
  	  	this.language = language;
  	  	
  	  	//Add the shops to the shops list
  	  	for ( String key : plugin.shopkeepers.getKeys(false) ) {
  	  		String locStr = plugin.shopkeepers.getString(key + ".loc");
  	  		shops.put(locStr, new Shop(locStr));
  	  	}
  	  	
  	  	//Load/refresh all shop NPCs
  	  	for ( Shop shop : shops.values() ) {
  	  		shop.load();
  			//Make the NPC immortal & immobile by adding to listener list
  			immortalize(shop.npcId);
  	  	}
  	  	
    }
    
    public void setOption(String option, boolean setting) {
    	options.put(option, setting);
    }
    
    public void setLanguage(String language) {
    	this.language = language;
    }
    
	public void immortalize(String npcId) {
		immortals.add(npcId);
	}
    
    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
    	
    	Entity entity = e.getEntity();
    	String UUIDString = entity.getUniqueId().toString();
    	
    	//Cancel damage to shopkeepers
    	for (String id : plugin.shopkeepers.getKeys(false)) {
    		Location shopLoc = LocSerialization.getLocationFromString( plugin.shopkeepers.getString(id + ".loc") );
    		Location npcLoc = entity.getLocation();
    		if ( npcLoc.distance(shopLoc) < 0.5 ) {
    			e.setCancelled(true);
    		}
    	}
    	
    	//Cancel damage to immortal entities
    	if ( immortals.contains(UUIDString) ) {
    		e.setCancelled(true);    		
    	}
    	
    }
    
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
    	
    	Entity entity = e.getEntity();
    	String UUIDString = entity.getUniqueId().toString();
    	
    	//Cancel damage to immortal entities
    	if ( immortals.contains(UUIDString) ) {
    		e.setCancelled(true);    		
    	}
    	
    }
    
    public boolean isShopContract(PlayerInteractEvent e) {
  		
    	//Event info
    	Player player = e.getPlayer();
    	ItemStack item = player.getInventory().getItemInMainHand();
    	
    	//Check if it's a shop contract
  		if ( item != null && item.getType() == Material.WRITTEN_BOOK 
  				&& item.hasItemMeta() && item.getItemMeta().hasLore()
  				&& item.getItemMeta().getDisplayName().contains("Shop Contract")
  				&& item.getItemMeta().getLore().size() > 1
  				&& item.getItemMeta().getLore().get(1).contains("summon a Shopkeeper here.") ) {
  			return true;
  		} else {
  			return false;
  		}
  		
    }
    
    public boolean isShopkeeper(Location clickedLoc) {
  		
  		//Loop through shopkeepers in player's world
  		for ( String id : plugin.shopkeepers.getKeys(false) ) {
  			String locStr = plugin.shopkeepers.getString( id + ".loc" );
  	  		String world = clickedLoc.getWorld().getName();
  			String worldLocStr = locStr.split(":")[0];
  			if ( worldLocStr.equals(world) ) {
		    	if ( clickedLoc.distance( LocSerialization.getLocationFromString(locStr) ) < 1.5 ) {
		    		return true;
		    	}
  			}
  		}
    	
    	return false;
    	
    }
    
    //Shopkeeping stuff
    public void shopkeeping(Player player, Location clickedLoc) {
    	
    	//Event details
  		String locStr = LocSerialization.getStringFromLocation(clickedLoc);

    	//Open the shop interface
		Shop shop;
		if (shops.containsKey(locStr)) {
			shop = shops.get(locStr);
		} else {
			shop = new Shop(locStr);
		}
		
		ShopGUI shopGUI = new ShopGUI(plugin, plugin.language, player, shop);
		shopGUI.openShopGUI();
    	  		
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		
  		//Check for shopkeeper
		if ( isShopkeeper( e.getRightClicked().getLocation() ) ) {
			e.setCancelled(true);
			Player player = e.getPlayer();
 			if ( !player.hasPermission("shops.buy") ) {
	 			plugin.msg(player, "errorPermissions");
	 			return;
 			} else {
 				shopkeeping( e.getPlayer(), e.getRightClicked().getLocation() );
 			}
		}
  	  
    }
    
    @EventHandler 
    public void onPlayerInteract(PlayerInteractEvent e) {
    	
    	Player player = e.getPlayer();
    	
    	if ( isShopContract(e) ) {
    		
 			e.setCancelled(true);
 			
 			if ( !player.hasPermission("shops.sell") ) {
	 			plugin.msg(player, "errorPermissions");
 			}

 			Shop shop = new Shop( LocSerialization.getStringFromLocation( player.getLocation() ) );
 			if ( !shop.closeProximity(player) && !shop.overLimit(player) ) {
	 			shop.create(player);
	 			player.sendMessage(ChatColor.GOLD + "[$]" + ChatColor.GREEN + " | Your shop has been setup successfully.");
	
	 	    	PlayerInventory inv = (PlayerInventory) player.getInventory();
	 	    	ItemStack item = inv.getItemInMainHand();
	 	    	item.setAmount(item.getAmount() - 1);
 			}
 			
 		}
    	
    }
    
	//GUI handling
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {

		//Check if click is even in an inventory
		boolean clickedOut = e.getSlotType() == SlotType.OUTSIDE;
		if (clickedOut) {
			return;
		}
		
		//Basic event info
		Player player = (Player) e.getWhoClicked();
		Inventory inventory = e.getInventory();
		
		//Item clicked info
		ItemStack clicked = e.getCurrentItem();
		List<String> clickedLore = new ArrayList<>();
		if ( clicked.hasItemMeta() && clicked.getItemMeta().hasLore() ) {
			clickedLore = clicked.getItemMeta().getLore();
		}

		//Check for the shop identifier
		String id;
		ItemStack first = inventory.getItem(0);
		if ( first != null && first.hasItemMeta() && first.getItemMeta().hasLore()
				&& first.getItemMeta().getLore().size() > 1 && first.getItemMeta().getLore().get(1).split(":").length > 1
				&& plugin.shopkeepers.getKeys(false).contains(first.getItemMeta().getLore().get(1).split(":")[1]) ) {
			List<String> lore = first.getItemMeta().getLore();
			id = lore.get(1).split(":")[1];
		} else {
			return;
		}
		
		//Shop & loc info
		String locStr = plugin.shopkeepers.getString(id + ".loc");
		Shop shop = new Shop(locStr);
		
		//ShopGUI handling
		if ( inventory.getName().contains("Shop Menu") && !clickedOut ) {
			
 			if ( !player.hasPermission("shops.buy") ) {
	 			plugin.msg(player, "errorPermissions");
	 			player.closeInventory();
	 			return;
 			}
			
			//"Item for sale" buttons
			if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Price:") && clickedLore.get(1).contains("In stock:") ) {
				//Get the item price
				String currencySymbol = plugin.getEconomy().format(0.00).split("0")[0].trim();
				int price = Integer.valueOf( clickedLore.get(0).split("\\" + currencySymbol)[1].trim().split("\\.")[0].replace(",", "") );
				//Trim the lore to get the actual item without price/amount (first two lines + one line spacing)
				ItemStack actualItem = clicked;
				ItemMeta actualMeta = actualItem.getItemMeta();
				ArrayList<String> lore = (ArrayList<String>) actualMeta.getLore();
				ArrayList<String> newLore = new ArrayList<>();
				for (int ln = 3; ln < lore.size(); ln++) {
					newLore.add(lore.get(ln));
				}
				actualMeta.setLore(newLore);
				actualItem.setItemMeta(actualMeta);
				//Check if the itemstack is in stock
				if ( shop.getStock(actualItem) >= actualItem.getAmount() ) {
					//Open the buy confirmation window
					ConfirmBuyGUI buyGUI = new ConfirmBuyGUI(plugin, language, player, shop, clicked, clicked.getAmount(), price, e.getSlot());
					buyGUI.openBuyGUI();
				}
			}
			//"Manage shop" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Manage this shop's settings") ) {
				ManageShopGUI manageShopGUI = new ManageShopGUI(plugin, plugin.language, player, shop);
				manageShopGUI.openManageShopGUI();
			}
			//"Close" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Close this shop menu") ) {
				player.closeInventory();
			}
			
			e.setCancelled(true);
			
		}
		//ConfirmBuyGUI handling
		else if ( inventory.getName().contains("Confirm Purchase") && !clickedOut ) {
			
 			if ( !player.hasPermission("shops.buy") ) {
	 			plugin.msg(player, "errorPermissions");
	 			player.closeInventory();
	 			return;
 			}
			
			ItemStack item = ConfirmBuyGUI.buyItem;
			int slot = ConfirmBuyGUI.slot;
			
			//"Confirm purchase" button
			if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Confirm purchase of") ) {
				//Check if the itemstack is in stock
				if ( shop.getStock(item) >= item.getAmount() ) {
					//Check if inventory is full
					if ( player.getInventory().firstEmpty() < 0 ) {
						player.sendMessage(ChatColor.GOLD + "[$]" + ChatColor.GRAY + 
								" | You do not have enough space for this purchase.");
					} else {
						//Buy the item, deplete the stock
						shop.buy(player, item, slot);
						//Refresh the shop menu
						ShopGUI shopGUI = new ShopGUI(plugin, plugin.language, player, shop);
						shopGUI.openShopGUI();
					}
				}
			}
			//"Remove listing" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Remove listing for") ) {
				shop.unlist(item, player, true);
				ShopGUI shopGUI = new ShopGUI(plugin, plugin.language, player, shop);
				shopGUI.openShopGUI();
			}
			//"Cancel" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Cancel this purchase and go back") ) {
				ShopGUI shopGUI = new ShopGUI(plugin, plugin.language, player, shop);
				shopGUI.openShopGUI();
			}
			//"Close" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Close this shop menu") ) {
				player.closeInventory();
			}
			
			e.setCancelled(true);
			
		}
		//SellGUI handling
		else if ( inventory.getName().contains("Sell Item") && !clickedOut ) {
			
 			if ( !player.hasPermission("shops.sell") ) {
	 			plugin.msg(player, "errorPermissions");
	 			player.closeInventory();
	 			return;
 			}
			
			ItemStack confirmListing = inventory.getItem(6);
			int price = Integer.valueOf(confirmListing.getItemMeta().getLore().get(2).split(" ")[1]);
			boolean inPlayerInv = e.getRawSlot() >= e.getView().getTopInventory().getSize();
			
			//Empty slot
			if (clicked.getType() == Material.AIR) {
				e.setCancelled(true);
				return;
			//"Item for sale" slot
			} else if ( clickedLore.size() > 0 && e.getRawSlot() == 4 ) {
				e.setCancelled(true);
				return;
			}
			//"Confirm listing" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Confirm sale of") ) {
				ItemStack toSell = inventory.getItem(4);
				if ( 
						!(toSell.hasItemMeta() && toSell.getItemMeta().hasLore() 
						&& toSell.getItemMeta().getLore().get(0).contains("Click & drop your item") ) 
				) {
					shop.sell(player, toSell, price);
					SellGUI sellGUI = new SellGUI(plugin, plugin.language, player, shop, new ItemStack(Material.CHEST, 1), 0, 0);
					sellGUI.openSellGUI();
				}
			}
			//"Cancel" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Cancel this sale and go back") ) {
				ManageShopGUI manageShopGUI = new ManageShopGUI(plugin, plugin.language, player, shop);
				manageShopGUI.openManageShopGUI();
			}
			//"Close" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Close this shop menu") ) {
				player.closeInventory();
			}
			//"+1000 price" button
			else if ( clickedLore.size() > 1 && clickedLore.get(1).contains("each stack by +1000") && price < 999000 ) {
				ItemStack item = inventory.getItem(4);
				SellGUI sellGUI = new SellGUI(plugin, plugin.language, player, shop, item, item.getAmount(), price + 1000);
				sellGUI.openSellGUI();
			}
			//"+100 price" button
			else if ( clickedLore.size() > 1 && clickedLore.get(1).contains("each stack by +100") && price < 999900 ) {
				ItemStack item = inventory.getItem(4);
				SellGUI sellGUI = new SellGUI(plugin, plugin.language, player, shop, item, item.getAmount(), price + 100);
				sellGUI.openSellGUI();
			}
			//"+10 price" button
			else if ( clickedLore.size() > 1 && clickedLore.get(1).contains("each stack by +10") && price < 999990 ) {
				ItemStack item = inventory.getItem(4);
				SellGUI sellGUI = new SellGUI(plugin, plugin.language, player, shop, item, item.getAmount(), price + 10);
				sellGUI.openSellGUI();
			}
			//"+1 price" button
			else if ( clickedLore.size() > 1 && clickedLore.get(1).contains("each stack by +1") && price < 1000000 ) {
				ItemStack item = inventory.getItem(4);
				SellGUI sellGUI = new SellGUI(plugin, plugin.language, player, shop, item, item.getAmount(), price + 1);
				sellGUI.openSellGUI();
			}
			//"-1000 price" button
			else if ( clickedLore.size() > 1 && clickedLore.get(1).contains("each stack by -1000") && price > 1000 ) {
				ItemStack item = inventory.getItem(4);
				SellGUI sellGUI = new SellGUI(plugin, plugin.language, player, shop, item, item.getAmount(), price - 1000);
				sellGUI.openSellGUI();
			}
			//"-100 price" button
			else if ( clickedLore.size() > 1 && clickedLore.get(1).contains("each stack by -100") && price > 100 ) {
				ItemStack item = inventory.getItem(4);
				SellGUI sellGUI = new SellGUI(plugin, plugin.language, player, shop, item, item.getAmount(), price - 100);
				sellGUI.openSellGUI();
			}
			//"-10 price" button
			else if ( clickedLore.size() > 1 && clickedLore.get(1).contains("each stack by -10") && price > 10 ) {
				ItemStack item = inventory.getItem(4);
				SellGUI sellGUI = new SellGUI(plugin, plugin.language, player, shop, item, item.getAmount(), price - 10);
				sellGUI.openSellGUI();
			}
			//"-1 price" button
			else if ( clickedLore.size() > 1 && clickedLore.get(1).contains("each stack by -1") && price > 1 ) {
				ItemStack item = inventory.getItem(4);
				SellGUI sellGUI = new SellGUI(plugin, plugin.language, player, shop, item, item.getAmount(), price - 1);
				sellGUI.openSellGUI();
			}
			//Add item to sell slot
			else if ( inPlayerInv ) {
				SellGUI sellGUI = new SellGUI(plugin, plugin.language, player, shop, clicked, clicked.getAmount(), 1);
				sellGUI.openSellGUI();
			}
			
			e.setCancelled(true);
			
		}
		//ManageShopGUI handling
		else if ( inventory.getName().contains("Manage Shop") && !clickedOut ) {
			
			//"Sell an Item" button
			if ( clickedLore.size() > 0 && clickedLore.get(0).contains("List an item for sale") ) {
				SellGUI sellGUI = new SellGUI(plugin, plugin.language, player, shop, new ItemStack(Material.CHEST, 1), 0, 0);
				sellGUI.openSellGUI();
			}
			//"Change style" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Change the look of the shopkeeper") ) {
				//Refresh to shop GUI
				ModelSelectGUI styleGUI = new ModelSelectGUI(plugin, plugin.language, player, shop);
				styleGUI.openStyleGUI();
			}
			//"Change name" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Change the name of the shopkeeper") ) {
				//Listen for the name setting command
				plugin.players.set(player.getUniqueId().toString() + ".naming", shop.locStr);
				plugin.saveCustomFile(plugin.players, plugin.playersFile);
				//Refresh the GUI
				ManageShopGUI manageShopGUI = new ManageShopGUI(plugin, plugin.language, player, shop);
				manageShopGUI.openManageShopGUI();
			}
			//"Infinite stock" op button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Set the stock to never deplete") && player.isOp() ) {
				//Set infinite stock on the shop
				shop.setInfiniteStock( !plugin.shopkeepers.getBoolean(shop.id + ".infinite") );
				//Refresh to shop GUI
				ShopGUI shopGUI = new ShopGUI(plugin, plugin.language, player, shop);
				shopGUI.openShopGUI();
			}
			//"Delete" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Remove this shop permanently") ) {
				shop.delete(player, true);
				player.closeInventory();
			}
			//"Cancel" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Cancel this sale and go back") ) {
				ShopGUI shopGUI = new ShopGUI(plugin, plugin.language, player, shop);
				shopGUI.openShopGUI();
			}
			//"Close" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Close this shop menu") ) {
				player.closeInventory();
			}
			
			e.setCancelled(true);
			
		}
		//ModelSelectGUI handling
		else if ( inventory.getName().contains("Style Shop") && !clickedOut ) {
			
			//"Model Select" button
			if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Set your shopkeeper") ) {
				
				String model = clickedLore.get(1).split("\\[")[1].split("\\]")[0];
				shop.setModel(model);
				
				//Re-load the NPC model
				shop.load();
				
				//Return to the manage shop screen
				ManageShopGUI manageShopGUI = new ManageShopGUI(plugin, plugin.language, player, shop);
				manageShopGUI.openManageShopGUI();
				
			}
			//"Cancel" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Cancel this sale and go back") ) {
				ManageShopGUI manageShopGUI = new ManageShopGUI(plugin, plugin.language, player, shop);
				manageShopGUI.openManageShopGUI();
			}
			//"Close" button
			else if ( clickedLore.size() > 0 && clickedLore.get(0).contains("Close this shop menu") ) {
				player.closeInventory();
			}
			
			e.setCancelled(true);
			
		}
		
		e.setCancelled(true);
		
	}
    
}
