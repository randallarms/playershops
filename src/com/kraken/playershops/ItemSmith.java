package com.kraken.playershops;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class ItemSmith {
	
	public ItemSmith() {
		
    }
	
	public ItemStack makeItem(Material m, String name, String bookAuthor, ArrayList<String> desc, int amount, Integer data, boolean unbreakable) {
		
		net.minecraft.server.v1_12_R1.ItemStack nmsItem;
		
	    //Gets rid of durability
      	nmsItem = CraftItemStack.asNMSCopy(new ItemStack(m, amount));
      	NBTTagCompound tag = new NBTTagCompound(); //Create the NMS Stack's NBT (item data)
      	tag.setBoolean("Unbreakable", unbreakable); //Set unbreakable value
      	nmsItem.setTag(tag); //Apply the tag to the item
      	ItemStack item = CraftItemStack.asCraftMirror(nmsItem); //Get the bukkit version of the stack
		
    	//Create the item's meta data (name, lore/desc text, etc.)
      	item.setDurability( (short) data.shortValue() );
    	if (m == Material.WRITTEN_BOOK) {
    		BookMeta bm = (BookMeta) item.getItemMeta();
    		bm.setAuthor(bookAuthor);
    		item.setItemMeta(bm);
    	}
    	ItemMeta im = item.getItemMeta();
    	im.setDisplayName(name);
    	//Creates the lore
    	ArrayList<String> lore = new ArrayList<String>();
    	lore.addAll(desc);
    	im.setLore(lore);
    	//Hides the vanilla Minecraft tooltip text
    	im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    	im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    	//Sets the item's meta data to the custom "im" meta data
    	item.setItemMeta(im);
    	
    	return item;
		
	}
	
	public ItemStack spawnShop(String contractor) {
		
		Material m = Material.WRITTEN_BOOK;
		String name = ChatColor.GREEN + "Shop Contract";
		ArrayList<String> desc = new ArrayList<>();
		desc.addAll( Arrays.asList("Use this contract to ", "summon a Shopkeeper here.") );
		return makeItem(m, name, contractor, desc, 1, 0, true);
		
	}
	
}
