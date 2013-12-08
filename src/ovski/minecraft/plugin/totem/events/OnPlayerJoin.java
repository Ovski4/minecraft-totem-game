package ovski.minecraft.plugin.totem.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import ovski.minecraft.manager.YamlPlayersManager;
import ovski.minecraft.plugin.totem.TotemPlugin;

/**
 * OnPlayerJoin
 * 
 * Add a player on the list if he is new
 *
 * @author baptiste <baptiste.bouchereau@gmail.com>
 */
public class OnPlayerJoin implements Listener
{
    private YamlPlayersManager playerManager = new YamlPlayersManager();

    /**
     * Constructor
     * 
     * @param totemPlugin
     */
    public OnPlayerJoin(TotemPlugin totemPlugin)
    {
        Bukkit.getServer().getPluginManager().registerEvents(this, totemPlugin);
    }

    /**
     * On player join
     * 
     * @param event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        // add a player on the list if he is new
        Player player = event.getPlayer();
        if (!playerManager.playerIsInTheList(player.getName())) {
            playerManager.addPlayer(player.getName());
        }
    }
}