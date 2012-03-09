package org.zonedabone.pluglets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.zonedabone.pluglets.api.Pluglet;
import org.zonedabone.pluglets.api.PlugletException;
import org.zonedabone.pluglets.api.PlugletManager;

public class CraftPlugletManager implements PlugletManager {

	private Pluglets plugin;
	private Set<Pluglet> loadedPluglets = new HashSet<Pluglet>();
	private File plugletsDir;
	private ClassLoader loader;

	public CraftPlugletManager(Pluglets plugin,ClassLoader loader) throws IOException {
		this.plugin = plugin;
		this.loader = loader;
		plugletsDir = new File(plugin.getDataFolder().getParentFile().getParentFile(),"pluglets");
		if(!plugletsDir.exists()){
			plugletsDir.mkdir();
		}else if(!plugletsDir.isDirectory()){
			throw new IOException();
		}
		for(File f:plugletsDir.listFiles(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				String[] split = name.split("\\.");
				if(split.length!=2)return false;
				return split[1].equals("class");
			}
		})){
			try{
				Pluglet p = loadPluglet(f);
				plugin.getLogger().info("Loaded pluglet "+p.getName());
				p.setEnabled(true);
			}catch(PlugletException e){
				
			}catch(FileNotFoundException e){
				plugin.getLogger().severe("Pluglet "+f.getName().split("\\.")[0]+" does not exist!");
			}
		}
	}
	
	@Override
	public Pluglet getPluglet(String name){
		for(Pluglet p:this.loadedPluglets){
			if(p.getName().equalsIgnoreCase(name)){
				return p;
			}
		}
		return null;
	}
	
	@Override
	public void unloadPluglet(Pluglet p){
		if(!loadedPluglets.contains(p));
		p.setEnabled(false);
		loadedPluglets.remove(p);
	}
	
	@Override
	public Pluglet loadPluglet(final String name) throws FileNotFoundException, PlugletException{
		for(File f:plugletsDir.listFiles(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String n) {
				String[] split = n.split("\\.");
				if(split.length!=2)return false;
				return split[1].equals("class")&&split[0].equalsIgnoreCase(name);
			}
		})){
			return loadPluglet(f);
		}
		throw new FileNotFoundException();
	}
	
	@Override
	public Pluglet loadPluglet(File f) throws PlugletException, FileNotFoundException{
		if(!f.exists()){
			throw new FileNotFoundException();
		}
		//Generate the url to load the class from
		URL url;
		try {
			url = plugletsDir.toURI().toURL();
		} catch (MalformedURLException e) {
			plugin.getLogger().severe("Failed to load pluglet "+f.getName().split("\\.")[0]);
			e.printStackTrace();
			throw new PlugletException();
		}
		URL[] urls = {url};
		URLClassLoader ucl = new URLClassLoader(urls,this.loader);
		//Load the class
		Class<?> c;
		try {
			c = ucl.loadClass(f.getName().split("\\.")[0]);
		} catch (ClassNotFoundException e) {
			plugin.getLogger().severe("Failed to load pluglet "+f.getName().split("\\.")[0]);
			e.printStackTrace();
			throw new PlugletException();
		}
		//Validate the class as a Pluglet
		Class<?extends Pluglet> pluglet;
		try{
			pluglet = c.asSubclass(Pluglet.class);
		}catch(ClassCastException e){
			plugin.getLogger().severe("Failed to load pluglet "+f.getName().split("\\.")[0]);
			e.printStackTrace();
			throw new PlugletException();
		}
		Pluglet p;
		try {
			p = pluglet.newInstance();
			p.initialize(this);
		} catch (Exception e) {
			plugin.getLogger().severe("Failed to load pluglet "+f.getName().split("\\.")[0]);
			e.printStackTrace();
			throw new PlugletException();
		}
		loadedPluglets.add(p);
		return p;
	}

	@Override
	public Pluglets getPlugin() {
		return plugin;
	}

	@Override
	public Server getServer() {
		return plugin.getServer();
	}
	
	@Override
	public void registerEvents(Pluglet p){
		this.getServer().getPluginManager().registerEvents(p, this.getPlugin());
	}
	
	@Override
	public void unregisterEvents(Pluglet p){
		HandlerList.unregisterAll(p);
	}
	

}
