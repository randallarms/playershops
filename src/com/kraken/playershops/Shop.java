package com.kraken.playershops;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Shop {
	
	private Main plugin = Main.instance;
	FileConfiguration shopowners;
	FileConfiguration shopkeepers;
	FileConfiguration stocks;
	String locStr;
	Location loc;
	String id;
	String npcId;
	String shopName;
	String model;
	
	int SHOP_MAX_SIZE = 18;
	
	public Shop(String locStr) {
		
		//Get basic objs & vars
		this.locStr = locStr;
		this.loc = LocSerialization.getLocationFromString(locStr);
		this.shopowners = plugin.shopowners;
		this.shopkeepers = plugin.shopkeepers;
		this.stocks = plugin.stocks;
		//Generate a random unique ID for the shop & NPC temoporarily
		this.id = UUID.randomUUID().toString();
		this.npcId = UUID.randomUUID().toString();
		//Check if shop already exists in file
		for ( String key : shopkeepers.getKeys(false) ) {
			String keyLocStr = shopkeepers.getString(key + ".loc");
			Location keyLoc = LocSerialization.getLocationFromString(keyLocStr);
			if ( keyLoc.distance(this.loc) < 1 ) {
				//Add shop and NPC IDs
				this.id = key;
				this.npcId = shopkeepers.getString(key + ".npc");
				break;
			}
		}
		this.shopName = "Shopkeeper";
		this.model = "VILLAGER";
		
	}
	
	//Get the shop's unique ID 
	public String getId() {
		
		//Loop through each shop entry for a location match and send the UUID
		for ( String key : shopkeepers.getKeys(false) ) {
			String keyLocStr = shopkeepers.getString(key + ".loc");
			Location keyLoc = LocSerialization.getLocationFromString(keyLocStr);
			Location shopLoc = LocSerialization.getLocationFromString(locStr);
			if ( keyLoc.equals(shopLoc) ) {
				return key;
			}
		}
		
		//Return a random UUID
		return UUID.randomUUID().toString();
		
	}
	
	//Get the shop's unique ID 
	public String getNpcId() {
		
		//Loop through each shop entry for a location match and send the UUID
		for ( Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 1, 1) ) {
			if ( entity.getName().equals(shopName) ) {
				this.npcId = entity.getUniqueId().toString();
			}
		}
		
		//Return a random UUID
		return npcId;
		
	}
	
	//Create the shop
	public void create(Player player) {
		
		//Player info
		String UUIDString = player.getUniqueId().toString();
		
		//Check for proximity to other shops
		if ( closeProximity(player) ) {
			return;
		} else if ( overLimit(player) ) {
			return;
		}
		
		//Date info
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = new Date();
		String simpleDate = dateFormat.format(date);
		
		//Load the shopkeeper NPC & info
		load();
		
		//Save the shop info in the shopkeepers & shoponwers YMLs
		shopkeepers.set(id + ".owner", UUIDString);
		shopkeepers.set(id + ".loc", locStr);
		shopkeepers.set(id + ".npc", npcId);
		shopkeepers.set(id + ".est", simpleDate);
		shopkeepers.set(id + ".infinite", false);
		
		shopowners.set(UUIDString + "." + id, locStr);
		
		//Save the files/configs
		plugin.saveCustomFile(shopkeepers, plugin.shopkeepersFile);
		plugin.saveCustomFile(shopowners, plugin.shopownersFile);
		
		//Add the shop to the shops list in listener
		plugin.listener.shops.put(locStr, this);
		plugin.listener.immortalize(this.npcId);
		
	}
	
	//Check if the shop is too close to another shop
	public boolean closeProximity(Player player) {
		
		for ( String key : shopkeepers.getKeys(false) ) {
			String keyLocStr = shopkeepers.getString(key + ".loc");
			Location keyLoc = LocSerialization.getLocationFromString(keyLocStr);
			Location shopLoc = LocSerialization.getLocationFromString(locStr);
			if ( shopLoc.distance(keyLoc) < 1.5 ) {
				//Send error
				player.sendMessage(ChatColor.GOLD + "[$]" + ChatColor.RED + 
						" | Sorry, but this is too close to another shop!");
				return true;
			}
		}
		
		return false;
		
	}
	
	//Check if the shop count of the player is over the limit
	public boolean overLimit(Player player) {

		//Player info
		String UUIDString = player.getUniqueId().toString();
		int shopCount = 0;
		if ( plugin.shopowners.getKeys(false).contains(UUIDString) ) { 
			shopCount = plugin.shopowners.getConfigurationSection(UUIDString).getKeys(false).size();
		}
		
		//Limit of shops in config
		int limit = plugin.getConfig().getInt("limit");
		
		if ( shopCount >= limit && limit >= 0 && !player.isOp() ) {
			player.sendMessage(ChatColor.GOLD + "[$]" + ChatColor.GREEN + " | You have reached your concurrent shop limit.");
			return true;
		}
		
		return false;
		
	}
	
	//Delete the shop (use dummy player if no player involved)
	public void delete(Player player, boolean isPlayer) {
		
		//Loop through items in stocks
		for (int invNum = 1; invNum < SHOP_MAX_SIZE+1; invNum++) {
			//Check if the slot has an item associated with it
			String itemStr = stocks.getString(id + "." + invNum + ".item");
			int amount = stocks.getInt(id + "." + invNum + ".amount");
			//Check if item in the slot and item to buy are the same
			if ( itemStr != null ) {
					//Set the entry to null
					while (amount > 0) {
						//Put the item into an array for serialization
						ItemStack item;
						try {
							item = ItemSerialization.stacksFromBase64(itemStr)[0];
							//Add the item and decrement the amount
							if (isPlayer && player.getInventory().firstEmpty() > -1) { 
								//Give the item to a player if they have space
								player.getInventory().addItem(item);
							} else {
								//Drop all the items on the floor
								loc.getWorld().dropItem(loc, item);
							}
							amount -= item.getAmount();
						} catch (NullPointerException npe) {
							System.out.println( "Error: item not found at shop id: " + id );
						} catch (IOException e) {
							System.out.println( "Error: shop item could not be de-serialized for player: " + player.getName() );
						}
					}
			}
		}

		//Remove the shopkeeper NPC
		unload();
		//Remove the shop in the shopkeepers yml
		shopkeepers.set(id, null);
		plugin.saveCustomFile(shopkeepers, plugin.shopkeepersFile);
		stocks.set(id, null);
		plugin.saveCustomFile(stocks, plugin.stocksFile);
		//Remove the shop in the shopowners yml
		for ( String key : shopowners.getKeys(false) ) {
			if ( shopowners.getString(key + "." + id).equals(locStr) ) {
				shopowners.set(key + "." + id, null);
				plugin.saveCustomFile(shopowners, plugin.shopownersFile);
				break;
			}
		}
		//Remove from the shops listener list
		plugin.listener.shops.remove(locStr);
		
	}
	
	public void removeSold(Player player, ItemStack item) {
		
		Inventory playerInv = player.getInventory();
		int playerItemSlot = 0;
		
		for (ItemStack playerItem : playerInv) {
			if ( item.equals(playerItem) ) {
				if ( playerItem.getAmount() > 1) {
					playerItem.setAmount( playerItem.getAmount() - item.getAmount() );
				} else {
					playerInv.setItem( playerItemSlot, new ItemStack(Material.AIR, 1) );
				}
				break;
			}
			playerItemSlot++;
		}
		
	}
	
	//List an item for sale
	public boolean sell(Player player, ItemStack item, int price) {
		
		//Put the item into an array for serialization
		ItemStack[] is = new ItemStack[1];
		is[0] = item;
		int invNum = 1;
				
		//Check if the store is in the stocks file
		if ( stocks.getKeys(false).contains(id) ) {
			//Loop through available shop stock slots
			for (invNum = 1; invNum < SHOP_MAX_SIZE+1; invNum++) {
				//Check if the slot has an item associated with it, and get it as an item
				String itemStr = stocks.getString(id + "." + invNum + ".item");
				//Item already in the shop
				if ( itemStr != null && itemStr.equals( ItemSerialization.toBase64(is) ) ) {
					//Increment inventory in stocks
					stocks.set(id + "." + invNum + ".amount", getStock(item) + item.getAmount());
					stocks.set(id + "." + invNum + ".price", price);
					plugin.saveCustomFile(stocks, plugin.stocksFile);
					removeSold(player, item);
					return true;
				//Free shop slot
				} else if ( stocks.getInt(id + "." + invNum + ".price") <= 0 ) {
					stocks.set(id + "." + invNum + ".item", ItemSerialization.toBase64(is).toString());
					stocks.set(id + "." + invNum + ".price", price);
					stocks.set(id + "." + invNum + ".amount", item.getAmount());
					plugin.saveCustomFile(stocks, plugin.stocksFile);
					removeSold(player, item);
					return true;
				//Shop is full
				} else if ( invNum >= SHOP_MAX_SIZE && itemStr != null ) {
					return false;
				}
			}
			
		//If id not saved, created a new section for the shop
		} else {
			stocks.set(id + "." + 1 + ".item", ItemSerialization.toBase64(is).toString());
			stocks.set(id + "." + 1 + ".price", price);
			stocks.set(id + "." + 1 + ".amount", item.getAmount());
			plugin.saveCustomFile(stocks, plugin.stocksFile);
			removeSold(player, item);
			return true;
		}
		
		return false;
		
	}
	
	public boolean buy(Player player, ItemStack item, int slot) {
		
		//Put the item into an array for serialization
		ItemStack[] itemStock = new ItemStack[1];
		itemStock[0] = item;
		
		//Get price
		double price = 0;
		price = Double.valueOf( getPrice(item) );
		
		//Deduct price from balance
		boolean paid = plugin.charge(player, price);
		if ( paid ) {
			
			//Add the item to the player's inventory
			try {
				ItemStack is = ItemSerialization.stacksFromBase64( stocks.getString(id + "." + (slot - 8) + ".item") )[0];
				player.getInventory().addItem(is);
			} catch (NullPointerException npe) {
				System.out.println( "Error: item not found at shop id: " + id );
			} catch (IOException e) {
				System.out.println( "Error: shop item could not be de-serialized for player: " + player.getName() );
			}
			
			//Decrement stock by amount
			if ( !shopkeepers.getBoolean(id + ".infinite") ) {
				setStock( item, getStock(item) - item.getAmount() );
			}
			
			//Remove if stock is 0
			if ( getStock(item) == 0 ) {
				unlist(item, player, false);
			}
			
		}
		
		return true;
		
	}
	
	//Unlist an item for sale
	public void unlist(ItemStack item, Player player, boolean toBeReturned) {
		
		//Put the item into an array for serialization
		ItemStack[] itemStock = new ItemStack[1];
		itemStock[0] = item;
		int spot = -1;
		
		//Loop through items in stocks
		for (int invNum = 1; invNum < SHOP_MAX_SIZE+1; invNum++) {
			//Check if the slot has an item associated with it
			String itemStr = stocks.getString(id + "." + invNum + ".item");
			int price = stocks.getInt(id + "." + invNum + ".price");
			int amount = stocks.getInt(id + "." + invNum + ".amount");
			//Check if item in the slot and item to buy are the same
			if ( itemStr != null && itemStr.equals(ItemSerialization.toBase64(itemStock)) ) {
				//Set the entry to null
				stocks.set(id + "." + invNum, null);
				plugin.saveCustomFile(stocks, plugin.stocksFile);
				//Return an item if it's to be returned
				if (toBeReturned) {
					while (amount > 0) {
						//If the player has space, add the item
						if (player.getInventory().firstEmpty() > -1) {
							player.getInventory().addItem(item);
						} else {
							//Drop it on the ground otherwise
							Location loc = player.getLocation();
							loc.getWorld().dropItem(loc, item);
						}
						//Decrement amount counter
						amount -= item.getAmount();
					}
				}
				spot = invNum;
			}
			if (spot > 0 && invNum > spot && itemStr != null) {
				int newId = invNum - 1;
				stocks.set(id + "." + invNum, null);
				stocks.set(id + "." + newId + ".item", itemStr);
				stocks.set(id + "." + newId + ".price", price);
				stocks.set(id + "." + newId + ".amount", amount);
				plugin.saveCustomFile(stocks, plugin.stocksFile);
			}
		}
		
	}
	
	//Refresh the inventory
	public LinkedHashMap<ItemStack, Integer[]> getInventory() {
		
		LinkedHashMap<ItemStack, Integer[]> inv = new LinkedHashMap<>();
		
		//Loop through items in stocks
		for (int invNum = 1; invNum < SHOP_MAX_SIZE+1; invNum++) {
			//Check if the slot has an item associated with it
			String itemStr = stocks.getString(id + "." + invNum + ".item");
			int amount = stocks.getInt(id + "." + invNum + ".amount");
			int price = stocks.getInt(id + "." + invNum + ".price");
			//Add the itemstack & amount/price to the shop inventory
			try {
				if (itemStr != null) {
					ItemStack is = ItemSerialization.stacksFromBase64( itemStr )[0];
					Integer[] details = new Integer[2];
					details[0] = amount;
					details[1] = price;
					inv.put(is, details);
				}
			} catch (NullPointerException npe) {
				System.out.println( "Error: item not found at shop id: " + id );
			} catch (IOException e) {
				System.out.println( "Error: shop item could not be de-serialized for inventory at shop id: " + id );
			}
		}
		
		return inv;
		
	}
	
	//Get the amount of an item for sale
	public int getStock(ItemStack item) {
		
		//Put the item into an array for serialization
		ItemStack[] itemStock = new ItemStack[1];
		itemStock[0] = item;
		
		//Loop through items in stocks
		for (int invNum = 1; invNum < SHOP_MAX_SIZE+1; invNum++) {
			
			//Check if the slot has an item associated with it
			String itemStr = stocks.getString(id + "." + invNum + ".item");
			
			//Check if item in the slot and item to buy are the same
			if ( itemStr != null ) {
				
				boolean match = false;
				try {
					match = item.equals(ItemSerialization.stacksFromBase64(itemStr)[0]);
				} catch (IOException e) {
					System.out.println("Error deserializing shop stock...");
				}
				
				if (match) {
					//Get the stock amount
					return stocks.getInt(id + "." + invNum + ".amount");
				}
				
			}
			
		}
		
		return 0;
		
	}
	
	//Change the amount of an item for sale
	public void setStock(ItemStack item, int amount) {
		
		//Put the item into an array for serialization
		ItemStack[] itemStock = new ItemStack[1];
		itemStock[0] = item;
		
		//Loop through items in stocks
		for (int invNum = 1; invNum < SHOP_MAX_SIZE+1; invNum++) {
			
			//Check if the slot has an item associated with it
			String itemStr = stocks.getString(id + "." + invNum + ".item");
			
			//Check if item in the slot and item to buy are the same
			if ( itemStr != null ) {
				
				boolean match = false;
				try {
					match = item.equals(ItemSerialization.stacksFromBase64(itemStr)[0]);
				} catch (IOException e) {
					System.out.println("Error deserializing shop stock...");
				}
				
				if (match) {
					//Set the stock amount
					stocks.set(id + "." + invNum + ".amount", amount);
					plugin.saveCustomFile(stocks, plugin.stocksFile);
				}
				
			}
			
		}
		
	}
	
	//Toggle infinite stocsk on all items in the shop
	public void setInfiniteStock(boolean unlimited) {
		shopkeepers.set(id + ".infinite", unlimited);
		plugin.saveCustomFile(shopkeepers, plugin.shopkeepersFile);
	}
	
	//Get the price of an item for sale
	public int getPrice(ItemStack item) {
		
		//Put the item into an array for serialization
		ItemStack[] itemStock = new ItemStack[1];
		itemStock[0] = item;
		
		//Loop through items in stocks
		for (int invNum = 1; invNum < SHOP_MAX_SIZE+1; invNum++) {
			String itemStr = stocks.getString(id + "." + invNum + ".item");
			//Check if item in the slot and item to buy are the same
			if ( itemStr != null && itemStr.equals( ItemSerialization.toBase64(itemStock) ) ) {
				//Get the price
				return stocks.getInt(id + "." + invNum + ".price");
			}
		}
		
		return 0;
		
	}
	
	//Change the price of an item for sale
	public void setPrice(ItemStack item, int price) {
		
		//Put the item into an array for serialization
		ItemStack[] itemStock = new ItemStack[1];
		itemStock[0] = item;
		
		//Loop through items in stocks
		for (int invNum = 1; invNum < SHOP_MAX_SIZE+1; invNum++) {
			//Check if the slot has an item associated with it
			String itemStr = stocks.getString(id + "." + invNum + ".item");
			//Check if item in the slot and item to buy are the same
			if ( itemStr != null && itemStr == ItemSerialization.toBase64(itemStock) ) {
				//Set the price
				stocks.set(id + "." + invNum + ".price", price);
				plugin.saveCustomFile(stocks, plugin.stocksFile);
			}
		}
		
	}
	
	//Set the model of the shopkeeper
	public void setModel(String model) {
		
  		plugin.shopkeepers.set(id + ".model", model);
  		this.model = model;
		plugin.saveCustomFile(shopkeepers, plugin.shopkeepersFile);
		refresh();
		
	}
	
	//Set the name of the shopkeeper
	public void setName(String name) {
		
  		plugin.shopkeepers.set(id + ".name", name);
  		this.shopName = name;
		plugin.saveCustomFile(shopkeepers, plugin.shopkeepersFile);
		refresh();
		
	}
	
	//Load the shopkeeper
	public void load() {

  		String model = plugin.shopkeepers.getString(id + ".model");
  		Entity shopkeeper;
		UUID npcUUID = UUID.fromString( getNpcId() );
		
		boolean isSpawned = false;
		for ( Entity e : loc.getWorld().getEntities() ) {
			if ( e.getUniqueId().toString().equals(npcId) ) {
				isSpawned = true;
				break;
			}
		}
  		
		if ( !isSpawned ) {
			//Place a shopkeeper NPC at the location
			shopkeeper = loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
		} else {
			//Get the shopkeeper NPC at location
			shopkeeper = Bukkit.getServer().getEntity( npcUUID );
		}
		
  		if (model != null) {
	  		switch ( model.toUpperCase() ) {
	  		
		  		case "BLACKSMITH":
	  				((Villager) shopkeeper).setProfession(Villager.Profession.BLACKSMITH);
	  				break;
	  			case "BUTCHER":
	  				((Villager) shopkeeper).setProfession(Villager.Profession.BUTCHER);
	  				break;
		  		case "LIBRARIAN":
	  				((Villager) shopkeeper).setProfession(Villager.Profession.LIBRARIAN);
	  				break;
  				case "NITWIT":
	  				((Villager) shopkeeper).setProfession(Villager.Profession.NITWIT);
	  				break;
  				case "PRIEST":
	  				((Villager) shopkeeper).setProfession(Villager.Profession.PRIEST);
	  				break;
	  			case "FARMER":
	  			default:
	  				((Villager) shopkeeper).setProfession(Villager.Profession.FARMER);
	  				break;
	  		
	  		}
  		} else {
  			model = "FARMER";
			((Villager) shopkeeper).setProfession(Villager.Profession.FARMER);
  		}
  		
		//Set the shopkeeper name
		if ( plugin.shopkeepers.getString(id + ".name") != null ) {
			//Set the custom NPC name
			this.shopName = plugin.shopkeepers.getString(id + ".name");
			shopkeeper.setCustomName(shopName);
		} else {
			//Default shopkeeper name
			shopkeeper.setCustomName("Shopkeeper");
		}
		shopkeeper.setCustomNameVisible(true);
    	
    	//Disable the AI that controls things like behavior and motor skills
		((LivingEntity) shopkeeper).setAI(false);
        
		//Add the NPC's UUID to shopkeepers, save it
        String UUIDString = shopkeeper.getUniqueId().toString();
        this.npcId = UUIDString;
		shopkeepers.set(id + ".npc", UUIDString);
		plugin.saveCustomFile(shopkeepers, plugin.shopkeepersFile);
		
	}
	
	//Unload the shopkeeper
	public void unload() {
		
		//Remove the villager NPC at the location
		String locStr = plugin.shopkeepers.getString( id + ".loc" );
		Location shopLoc = LocSerialization.getLocationFromString(locStr);
		for ( Entity entity : loc.getWorld().getEntities() ) {
			Location loc = entity.getLocation();
	    	if ( (entity instanceof Villager) && loc.distance(shopLoc) < 1 ) {
	    		entity.remove();
	    	}
		}
		
		//Remove the NPC's UUID from shopkeepers
		shopkeepers.set(id + ".npc", null);
		if ( plugin.listener.immortals.contains(npcId) ) {
			plugin.listener.immortals.remove(npcId);
		}
		plugin.saveCustomFile(shopkeepers, plugin.shopkeepersFile);
		
	}
	
	//Return the shopkeeper NPC to its locations
	public void refresh() {
		
		Entity entity = Bukkit.getServer().getEntity( UUID.fromString( getNpcId() ) );
		
		for ( Entity e : loc.getWorld().getEntities() ) {
			if ( LocSerialization.getLocationFromString( plugin.shopkeepers.getString(id + ".loc") ).distance(e.getLocation()) < 0.5 ) {
				entity = e;
				break;
			}
		}
		
		//Set the shopkeeper name
		if ( plugin.shopkeepers.getString(id + ".name") != null ) {
			//Set the custom NPC name
			this.shopName = plugin.shopkeepers.getString(id + ".name");
			entity.setCustomName(this.shopName);
		} else {
			//Default shopkeeper name
			this.shopName = "Shopkeeper";
			plugin.shopkeepers.set(id + ".name", shopName);
			entity.setCustomName(shopName);
		}
		entity.setCustomNameVisible(true);
		//Check if the location of the NPC is more than a block from loc
		if ( entity.getLocation().distance(loc) > 1 ) {
			//If so, teleport the NPC back to the loc
			entity.teleport(loc);
		}
		
	}
    
}
