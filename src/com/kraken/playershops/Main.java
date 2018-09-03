// =========================================================================
// |PLAYER SHOPS v1.0 | for Minecraft v1.12
// | by Kraken | Link TBA
// | code inspired by various Bukkit & Spigot devs -- thank you.
// =========================================================================

package com.kraken.playershops;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;

public class Main extends JavaPlugin {
	
	//Main plugin instance
	public static Main instance;
	
	//Lang vars
	public static String VERSION = "1.0";
	String language;
	ArrayList<String> languages = new ArrayList<String>();
	Messages messenger;
	
	//Class vars
	MainListener listener;
    public static Economy econ = null;
	
	//Options
	WeakHashMap<String, Boolean> options = new WeakHashMap<>();
	
	//Banking
	public final WeakHashMap<UUID, Double> bank = new WeakHashMap<>();
	private VaultHook vaultHook;
	
	//File configs
	File shopownersFile;
	FileConfiguration shopowners;
	File shopkeepersFile;
	FileConfiguration shopkeepers;
	File stocksFile;
	FileConfiguration stocks;
	File playersFile;
	FileConfiguration players;
	
	//Enable
    @Override
    public void onEnable() {
    	
    	//Start logging on load
    	getLogger().info("Loading options...");
    	
    	//Set the instance classes
    	instanceClasses();
    	
    	//Set up the Vault dependency or quit
    	if (!setupEconomy()) {
            this.getLogger().severe("Disabled: no Vault dependency found! Please add the Vault plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        vaultHook.hook(econ);
        
		//Copies the default config.yml from within the .jar if "plugins/PlayerShops/config.yml" does not exist
		saveResource("config.yml", false);
		
		//Language/Messages handler class construction
		languages.add("english");
		loadMessageFiles();
		language = getConfig().getString("language");
		messenger = new Messages(this, "english");
		
		//Shop files & configs
		shopownersFile = new File("plugins/PlayerShops/shopowners.yml");
	  	shopowners = YamlConfiguration.loadConfiguration(shopownersFile);
		shopkeepersFile = new File("plugins/PlayerShops/shopkeepers.yml");
	  	shopkeepers = YamlConfiguration.loadConfiguration(shopkeepersFile);
		stocksFile = new File("plugins/PlayerShops/stocks.yml");
	  	stocks = YamlConfiguration.loadConfiguration(stocksFile);
		playersFile = new File("plugins/PlayerShops/players.yml");
		players = YamlConfiguration.loadConfiguration(playersFile);

		//General plugin management
    	PluginManager pm = getServer().getPluginManager();
    	listener = new MainListener(this, language);
		pm.registerEvents(listener, this);
		
		//Language setting
		setLanguage(language);
		
	    //Loading default settings into options
    	setOption( "enabled", getConfig().getBoolean("enabled") );
    	setOption( "permissions", getConfig().getBoolean("permissions") );
    	setOption( "silentMode", getConfig().getBoolean("silentMode") );
    	silencer( options.get("silentMode") );
    	
    	if ( !getConfig().getKeys(false).contains("limit") ) {
    		getConfig().set("limit", 10);
    	}
    	
    	getLogger().info("Finished loading!");
			
    }
    
    //Disable
    @Override
    public void onDisable() {
        getLogger().info("Disabling...");
        vaultHook.unhook();
    }
    
    //Instance classes
    private void instanceClasses() {
    	instance = this;
    	vaultHook = new VaultHook();
    }
    
    //Vault Economy setup
    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }
    
	//Charge players from their Vault Economy account
	public boolean charge(Player player, double amount) {
		EconomyResponse econRe = econ.withdrawPlayer(player, amount);
	    return econRe.transactionSuccess();
	}
    
    //Messages
    public void msg(Player player, String cmd) {
    	messenger.makeMsg(player, cmd);
    }
    
    public void consoleMsg(String cmd) {
    	messenger.makeConsoleMsg(cmd);
    }
    
    //Setting methods
    //Options setting
    public void setOption(String option, boolean setting) {
    	getConfig().set(option, setting);
    	saveConfig();
    	options.put(option, setting);
    	listener.setOption(option, setting);
    	getLogger().info(option + " setting: " + setting );
    }
    
    //Language setting
    public void setLanguage(String language) {
    	this.language = language;
    	getConfig().set("language", language);
    	saveConfig();
    	listener.setLanguage(language);
    	messenger.setLanguage(language);
    	getLogger().info( "Language: " + language.toUpperCase() );
    }
    
	public void loadMessageFiles() {
		for (String lang : languages) {
		    File msgFile = new File(getDataFolder() + "/lang/", lang.toLowerCase() + ".yml");
		    if ( !msgFile.exists() ) {
		    	saveResource("lang/" + lang.toLowerCase() + ".yml", false);
		    }
		}
    }
    
    //Silent mode setting
    public void silencer(boolean silentMode) {
    	messenger.silence(silentMode);
    }
    
    //Save custom file
    public void saveCustomFile(FileConfiguration fileConfig, File file) {
    	try {
			fileConfig.save(file);
		} catch (IOException e) {
			System.out.println("Error saving custom config file: " + file.getName());
		}
    }
    
    //Player Shops commands
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		//Command handling
		Commands commands = new Commands(this);
		String command = cmd.getName();
		
		//Player handling
		Player player;
		boolean isPlayer = sender instanceof Player;
		
		if (isPlayer) {
			player = (Player) sender;
		} else {
			player = Bukkit.getServer().getPlayerExact("none");
		}
		
		//Execute command & return
		return commands.execute(isPlayer, player, command, args);
		
	}
		
}
