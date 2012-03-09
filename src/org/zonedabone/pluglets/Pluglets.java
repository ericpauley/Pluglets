package org.zonedabone.pluglets;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.zonedabone.pluglets.api.PlugletException;
import org.zonedabone.pluglets.api.PlugletManager;

public class Pluglets extends JavaPlugin {

	private PlugletManager manager;
	
	public void onEnable(){
		try{
			manager = new CraftPlugletManager(this,this.getClassLoader());
		}catch(IOException e){
			this.getLogger().severe("Could not write pluglet directory. Does a file exist there?");
			this.setEnabled(false);
		}
	}

	public PlugletManager getManager() {
		return manager;
	}
	
	@Override
	public boolean onCommand(CommandSender cs,Command c,String label, String[] args){
		if(args[0].equalsIgnoreCase("unload")){
			manager.unloadPluglet(manager.getPluglet(args[1]));
		}else if(args[0].equalsIgnoreCase("load")){
			try {
				manager.loadPluglet(args[1]).setEnabled(true);
				
			} catch (FileNotFoundException e) {
				cs.sendMessage("Pluglet does not exist!");
			} catch (PlugletException e) {
				cs.sendMessage("Could not load pluglet!");
			}
		}
		return true;
	}
	
}
