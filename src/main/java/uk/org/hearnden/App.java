package uk.org.hearnden;

/**
 * Hello world!
 *
 */

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;
public class App extends JavaPlugin  implements Listener  {
	Logger log;
	int numPlayers = 0;
	int taskId = -1;
    public void onEnable(){
    	log = this.getLogger();
    	log.info( "Plugin started");
    	getConfig().options().copyDefaults(true);
    	saveConfig();
    	getServer().getPluginManager().registerEvents(this, this);
    	Player[] plyers = getServer().getOnlinePlayers();
    	numPlayers = plyers.length;
    	if( numPlayers == 0 )
    	{
    		ScheduleShutdown( getConfig().getInt( "startupshutdown") );
    	}
    }
    void ScheduleShutdown( int minutes )
    {
    	log.info( "Scheduling shutdown for " + minutes + " minutes time");
		taskId = getServer().getScheduler().scheduleSyncDelayedTask( this,  new Runnable() {
			   public void run() {
			       getServer().shutdown();
			   }
			}, minutes * 60 * 20L); //  1 minute = 60 * 20 ticks
    	
    }
    public void onDisable(){ 
    	log.info( "Plugin finished");
     
    }
    @EventHandler // EventPriority.NORMAL by default
    public void onPlayerLogin(PlayerLoginEvent event) {
    	numPlayers = numPlayers + 1;
    	log.info( "Number of players is now " + numPlayers );
    	if( taskId != -1 )
    	{
    		getServer().getScheduler().cancelTask( taskId );
    		taskId = -1;
    		log.info( "Canceled shutdown" );
    	
    	}
    }
    @EventHandler // EventPriority.NORMAL by default
    public void onPlayerLogout( PlayerQuitEvent event )
    {
    	numPlayers = numPlayers - 1;
    	Player[] plyers = getServer().getOnlinePlayers();
    	log.info( "Number of players is now " + numPlayers + " or " + plyers.length );
    	if( plyers.length == 1 ) // Hasn't finished logging out
    	{
    		ScheduleShutdown( getConfig().getInt( "logoutshutdown") );
    	}
    	
    }
}
