package com.kraken.playershops;

import java.util.WeakHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Commands {
	
	  Main plugin;
	  String VERSION;
	  
	  String language;
	  Messages messenger;
	  
	  //Options
	  WeakHashMap<String, Boolean> options = new WeakHashMap<>();
	
  //Constructor
	public Commands(Main plugin) {
		
		this.plugin = plugin;
		this.VERSION = Main.VERSION;
		this.options = plugin.options;
		
		this.language = plugin.language;
		this.messenger = new Messages(plugin, language);
	
	}
	
  //Commands
	public boolean execute(boolean isPlayer, Player player, String command, String[] args) {
		
		switch (command) {
		
			//Command: shops
			case "shops":
			case "shop":
			case "pshops":
			case "pshop":
			case "playershops":
			case "playershop":
				
				if ( args.length > 1 && (args[0].toLowerCase().equals("name") || args[0].toLowerCase().equals("rename")) ) {
					
					//Check if the player is even naming anything through the GUI
					String namingLocStr = plugin.players.getString( player.getUniqueId().toString() + ".naming" );
					
					if ( namingLocStr == null ) {
						player.sendMessage(ChatColor.GOLD + "[$]" + ChatColor.GRAY + 
								" | You must first select a shopkeeper through the shop management menu.");
						return true;
					}
					
				    //Build the name string from args
					String name = new String();
					
					int i = 0;
					for ( String arg : args ) {
						if (i == 1) {
							name = arg;
						} else if (i > 1) {
							name += " " + arg;
						}
						i++;
					}
					
					//Check that the name is not over the size limit
					if ( name.toCharArray().length > 16 || name.toCharArray().length < 2 ) {
						player.sendMessage(ChatColor.GOLD + "[$]" + ChatColor.GRAY + 
								" | Your shop name can only contain between 2 and 16 characters.");
				    	return true;
					}
					
					//Check that the name contains only acceptable letters
					String acceptableStr = "abcdefghijklmnopqrstuvwxyz0123456789-_ ";
					
					boolean failed = false;
				    for (int f = 0; !failed && f < name.toCharArray().length; f++) {
				        failed = ( acceptableStr.indexOf( name.toLowerCase().toCharArray()[f] ) < 0 );
				    }
				    
				    if (failed) {
						player.sendMessage(ChatColor.GOLD + "[$]" + ChatColor.GRAY + 
								" | Your shop name can only contain letters, numbers, spaces, hypens, and underscores.");
				    	return true;
				    }
					
					//Change the shop's config name
					Shop nameShop = new Shop(namingLocStr);
					nameShop.setName(name);
					
					//Reset the naming flag
					plugin.players.set(player.getUniqueId().toString() + ".naming", null);
					plugin.saveCustomFile(plugin.players, plugin.playersFile);
					
					//Send the confirmation message
					player.sendMessage(ChatColor.GOLD + "[$]" + ChatColor.GRAY + 
							" | Your shop has been renamed to \"" + name + "\".");
					
					return true;
					
				}
				
				switch (args.length) {
				
					case 1:
						
						if ( !isPlayer || !player.isOp() ) {
							return errorMsg(isPlayer, player);
						}
						
						//Command handling switch
						switch ( args[0].toLowerCase() ) {
						
							//Command: shops create
							case "create":
							case "make":
							case "new":
								
					 			if ( !player.hasPermission("shops.spawn") ) {
						 			plugin.msg(player, "errorPermissions");
						 			return true;
					 			}
								
								Shop shop = new Shop( LocSerialization.getStringFromLocation(player.getLocation()) );
								shop.create(player);
								player.sendMessage(ChatColor.GOLD + "[$]" + ChatColor.GRAY + 
										" | Your shop has been created successfully.");
								return true;
							
							//Command: shops item
							case "item":
							case "contract":
								
					 			if ( !player.hasPermission("shops.item") ) {
						 			plugin.msg(player, "errorPermissions");
						 			return true;
					 			}
								
								ItemStack item = new ItemSmith().spawnShop( player.getName() );
								if (player.getInventory().firstEmpty() > -1) {
									player.getInventory().addItem(item);
								} else {
									Location loc = player.getLocation();
									loc.getWorld().dropItem(loc, item);
								}
								player.sendMessage(ChatColor.GOLD + "[$]" + ChatColor.GRAY + 
										" | Your shop item has been given successfully.");
								return true;
							
							default:
								return errorMsg(isPlayer, player);
						
						}
					
					case 2:
						
						//Check if sender is a player and if that player has OP perms
						if (isPlayer) {
							
							if ( !player.isOp() ) {
								plugin.msg(player, "errorIllegalCommand");
								return true;
							}
							
						}
						
						//Command handling switch
						switch ( args[0].toLowerCase() ) {
						
							//Command: shops enable
							case "enable":
							case "enabled":
								
								switch ( args[1].toLowerCase() ) {
								
									case "true":
									case "enable":
									case "enabled":
									case "on":
									case "cierto":
										plugin.setOption("enabled", true);
										
										if ( !isPlayer ) {
											plugin.consoleMsg("cmdPluginEnabled");
										} else {
											plugin.msg(player, "cmdPluginEnabled");
										}
										
										return true;
										
									case "false":
									case "disable":
									case "disabled":
									case "off":
									case "falso":
										plugin.setOption("enabled", false);
										
										if ( !isPlayer ) {
											plugin.consoleMsg("cmdPluginDisabled");
										} else {
											plugin.msg(player, "cmdPluginDisabled");
										}
										
										return true;
								
								  //Enable command error handling
									default: 
										
										if ( !isPlayer ) {
											plugin.consoleMsg("errorCommandFormat");
										} else {
											plugin.msg(player, "errorEnableFormat");
										}
										
										return true;
								
								}
						
							//Command: shops language
							case "language":
							case "lang":
								
								String lang = args[1].toLowerCase();
								
								//Language command handling
								if ( plugin.languages.contains( lang ) ) {
									
									plugin.setLanguage(lang);
									
									if ( !isPlayer ) {
										plugin.consoleMsg("cmdLanguageSet");
									} else {
										plugin.msg(player, "cmdLang");
									}
									
									return true;
								
								//Language command error handling
								} else {
									
									if ( !isPlayer ) {
										plugin.consoleMsg("errorLanguageSet");
									} else {
										plugin.msg(player, "errorLangNotFound");
									}
									
									return true;
								}
								
							//Command: shops silentMode
							case "silentmode":
								
								switch ( args[1].toLowerCase() ) {
								
									case "true":
									case "enable":
									case "enabled":
									case "on":
									case "cierto":
										plugin.setOption("silentMode", true);
										plugin.silencer(true);
										
										if ( !isPlayer ) {
											plugin.consoleMsg("cmdSilentModeOn");
										} else {
											plugin.msg(player, "cmdSilentOn");
										}
										
										return true;
										
									case "false":
									case "disable":
									case "disabled":
									case "off":
									case "falso":
										plugin.setOption("silentMode", false);
										plugin.silencer(false);
										
										if ( !isPlayer ) {
											plugin.consoleMsg("cmdSilentModeOff");
										} else {
											plugin.msg(player, "cmdSilentOff");
										}
										
										return true;
								
								  //Silentmode command error handling
									default: 
										
										if ( !isPlayer ) {
											plugin.consoleMsg("errorCommandFormat");
										} else {
											plugin.msg(player, "errorSilentModeFormat");
										}
										
										return true;
								
								}
								
							//Command: shops limit
							case "limit":
							case "max":
								
								try {
									int arg = Integer.valueOf(args[1]);
									plugin.getConfig().set("limit", arg);
								} catch (NullPointerException e) {
									if ( !isPlayer ) {
										plugin.consoleMsg("errorLimitFormat");
									} else {
										plugin.msg(player, "errorLimitFormat");
									}
								}
		        	    	
						}
						
				}
				
				default:
					
					return errorMsg(isPlayer, player);
					
			}
				
		}
	
	public boolean errorMsg(boolean isPlayer, Player player) {
		if (isPlayer) {
			plugin.msg(player, "errorIllegalCommand");
		} else {
			plugin.consoleMsg("errorCommandFormat");
		}
		return true;
	}
	
}
