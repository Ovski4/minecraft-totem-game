package ovski.minecraft.plugin.totem.events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import ovski.minecraft.manager.YamlTeamsManager;
import ovski.minecraft.plugin.totem.TotemPlugin;

/**
 * OnBlockPlace
 * 
 * Things to do on block break event
 *
 * @author baptiste <baptiste.bouchereau@gmail.com>
 */
public class OnBlockPlace implements Listener
{
    private YamlTeamsManager teamsManager = new YamlTeamsManager();
    private int gameId;
    private String teamName;

    /**
     * Constructor
     * 
     * @param totemPlugin
     */
    public OnBlockPlace(TotemPlugin totemPlugin)
    {
        Bukkit.getServer().getPluginManager().registerEvents(this, totemPlugin);
    }

    /**
     * On block place
     * 
     * @param event
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        // prevent from placing a totem block somewhere else than on his own totem

        if (!event.getBlock().getType().equals(Material.SMOOTH_BRICK)) {
            return;
        }

        if (event.getBlock().getData()!=((byte) 3)) {
            return;
        }

        if(event.getPlayer().isOp()) {
            return;
        }

        this.teamName = teamsManager.getTeamOfPlayer(event.getPlayer().getName());
        if (teamName != null) {
            this.gameId = teamsManager.getGameId(this.teamName);
        } else {
            event.getPlayer().sendMessage("You cant' place totem blocks when you are not in a game");
            event.setCancelled(true);

            return;
        }

        String playerName = event.getPlayer().getName();
        String teamName = teamsManager.getTeamOfPlayer(playerName);
        double XBlockLoc = event.getBlock().getX();
        double XTotemLoc = teamsManager.getXCoordinateOfTeamTotem(teamName);
        if (!(Math.abs(XBlockLoc-XTotemLoc)<1)) {
            event.getPlayer().sendMessage("You can place totem blocks only on your totem");
            event.setCancelled(true);

            return;
        }
        double ZBlockLoc = event.getBlock().getZ();
        double ZTotemLoc = teamsManager.getZCoordinateOfTeamTotem(teamName);
        if (!(Math.abs(ZBlockLoc-ZTotemLoc)<1)) {
            event.getPlayer().sendMessage("You can place totem blocks only on your totem");
            event.setCancelled(true);

            return;
        }

        // the player placed a totem block

        teamsManager.increaseTeamsBlocks(this.teamName);
        int winNumber = teamsManager.getWinNumber(this.gameId);
        int blockNumber = teamsManager.getTeamsBlocksNumber(this.teamName);
        this.gameId = teamsManager.getGameId(this.teamName);
        List<String> teamList = teamsManager.getTeamListMatchingTheId(this.gameId);
        for(String team : teamList){
            List<Player> players = teamsManager.getOnlinePlayersOfATeam(team);
            for(Player player : players) {
                player.sendMessage(
                    "The team "+this.teamName+" just placed a block on his totem." +
                    "It has now "+String.valueOf(blockNumber)+" blocks"
               );
            }
        }

        if(winNumber==blockNumber) {
            for(String team : teamList) {
                List<Player> players = teamsManager.getOnlinePlayersOfATeam(team);
                for(Player player : players) {
                    player.sendMessage("The team "+this.teamName+" won!!");
                }
            }
            //We display the podium
            for(String team : teamList) {
                List<Player> players = teamsManager.getOnlinePlayersOfATeam(team);
                List<String> looserTeamList = teamsManager.getLooserTeams(gameId);
                List<String> remainingTeamList = teamsManager.getRemainingTeams(gameId);
                for(Player player : players) {
                    player.sendMessage("The team "+this.teamName+" won!!");
                    player.sendMessage("--------------- Podium ---------------");
                    player.sendMessage("1 - "+this.teamName);
                    int i = 2;
                    for (String remainingTeam : remainingTeamList) {
                        if (!remainingTeam.equals(this.teamName)) {
                            player.sendMessage(String.valueOf(i)+" - "+remainingTeam);
                            i++;
                        }
                    }
                    if(looserTeamList!=null) {
                        for (String looser : looserTeamList) {
                            player.sendMessage(String.valueOf(i)+" - "+looser);
                            i++;
                        }
                    }
                    player.sendMessage("--------------------------------------");
                }
            }
            teamsManager.resetGame(this.gameId, teamList);
        }
    }
}