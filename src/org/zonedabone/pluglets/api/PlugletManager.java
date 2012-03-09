package org.zonedabone.pluglets.api;

import java.io.File;
import java.io.FileNotFoundException;

import org.bukkit.Server;
import org.zonedabone.pluglets.Pluglets;

public interface PlugletManager {

	Pluglets getPlugin();
	Server getServer();
	Pluglet loadPluglet(File f) throws PlugletException, FileNotFoundException;
	void registerEvents(Pluglet p);
	void unregisterEvents(Pluglet p);
	Pluglet getPluglet(String name);
	void unloadPluglet(Pluglet p);
	Pluglet loadPluglet(String name) throws FileNotFoundException,
			PlugletException;
	
}
