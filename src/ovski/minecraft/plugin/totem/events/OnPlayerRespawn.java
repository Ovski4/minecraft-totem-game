package ovski.minecraft.plugin.totem.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

import ovski.minecraft.manager.YamlTeamsManager;
import ovski.minecraft.plugin.totem.TotemPlugin;

/**
 * OnPlayerRespawn
 * 
 * Teleport a player on respawn
 *
 * @author baptiste <baptiste.bouchereau@gmail.com>
 */
public class OnPlayerRespawn implements Listener
{
    private YamlTeamsManager teamsManager = new YamlTeamsManager();

    /**
     * Constructor
     * 
     * @param totemPlugin
     */
    public OnPlayerRespawn(TotemPlugin totemPlugin)
    {
        Bukkit.getServer().getPluginManager().registerEvents(this, totemPlugin);
    }

    /**
     * On player respawn
     * 
     * Teleport a player if spawn is set
     * 
     * @param event
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        String playerTeam = teamsManager.getTeamOfPlayer(player.getName());
        if(teamsManager.spawnIsSet(playerTeam)) {
            Location spawn = player.getLocation();
            spawn.setX(teamsManager.getXCoordinateOfTeamSpawn(playerTeam));
            spawn.setY(teamsManager.getYCoordinateOfTeamSpawn(playerTeam));
            spawn.setZ(teamsManager.getZCoordinateOfTeamSpawn(playerTeam));
            event.setRespawnLocation(spawn);
        }
    }
}