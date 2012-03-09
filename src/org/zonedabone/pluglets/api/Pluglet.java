package org.zonedabone.pluglets.api;

import org.bukkit.event.Listener;

public class Pluglet implements Listener {

	private boolean enabled = false;

	private PlugletManager plugletManager;
	private boolean initialized = false;
	
	public Pluglet(){
		
	}
	
	public void initialize(PlugletManager pm){
		if(this.initialized)return;
		this.plugletManager = pm;
		this.initialized = true;
	}

	public PlugletManager getPlugletManager() {
		return plugletManager;
	}
	
	public void onEnable(){}
	
	public void onDisable(){}
	
	public String getName(){
		return this.getClass().getSimpleName();
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		if(!this.enabled&&enabled){
			this.onEnable();
			this.getPlugletManager().registerEvents(this);
		}else if(this.enabled&&!enabled){
			this.onDisable();
			this.getPlugletManager().unregisterEvents(this);
		}
		this.enabled = enabled;
	}
	
}
