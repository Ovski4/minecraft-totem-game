package ovski.minecraft.plugin.totem.events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import ovski.minecraft.manager.YamlTeamsManager;
import ovski.minecraft.plugin.totem.TotemPlugin;

/**
 * OnBlockBreak
 * 
 * Things to do on block break event
 *
 * @author baptiste <baptiste.bouchereau@gmail.com>
 */
public class OnBlockBreak implements Listener
{
    private YamlTeamsManager teamsManager = new YamlTeamsManager();
    private String teamName;
    private int gameId;
    private String attaquedTeam;

    /**
     * Constructor
     * 
     * @param totemPlugin
     */
    public OnBlockBreak(TotemPlugin totemPlugin)
    {
        Bukkit.getServer().getPluginManager().registerEvents(this, totemPlugin);
    }

    /**
     * On block break
     * 
     * @param event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        // prevent to break a totem block of his own totem
        // or of other that are not in the sme game

        if (!event.getBlock().getType().equals(Material.SMOOTH_BRICK)) {
            return;
        }

        if (!(event.getBlock().getData()==((byte) 3))) {
            return;
        }

        if(event.getPlayer().isOp()) {
            return;
        }

        this.teamName = teamsManager.getTeamOfPlayer(event.getPlayer().getName());
        if (teamName != null) {
            this.gameId = teamsManager.getGameId(this.teamName);
        } else {
            event.getPlayer().sendMessage("You can't destroy totems when you are not in a game");
            event.setCancelled(true);

            return;
        }

        if (!teamsManager.isPlaying(teamsManager.getTeamOfPlayer(event.getPlayer().getName()))) {
            event.getPlayer().sendMessage("You can't destroy totems when you are not in a game");
            event.setCancelled(true);

            return;
        }

        if(!event.getPlayer().getItemInHand().getType().toString().contains("PICKAXE")) {
            event.getPlayer().sendMessage("You need a pickaxe!");
            event.setCancelled(true);

            return;
        }

        if (isAPlayerBlockTotem(event)) {
            event.getPlayer().sendMessage("What are you trying to do? You cannot destroy your own totem!");
            event.setCancelled(true);

            return;
        }

        if (!blockTotemBelongToThePlayerGame(event)) {
            event.getPlayer().sendMessage("This totem is not part of your game");
            event.setCancelled(true);
            return;
        }

        // A player took a block of a totem //

        List<String> teamList = teamsManager.getTeamListMatchingTheId(this.gameId);
        for(String team : teamList){
            List<Player> players = teamsManager.getOnlinePlayersOfATeam(team);
            for(Player player : players) {
                player.sendMessage("The team "+this.teamName+" just took a block from "+this.attaquedTeam);
            }
        }

        teamsManager.decreaseTeamsBlocks(this.attaquedTeam);

        int blocksLeft = teamsManager.getTeamsBlocksNumber(attaquedTeam);
        if (blocksLeft!=0) {
            for(String team : teamList){
                List<Player> players = teamsManager.getOnlinePlayersOfATeam(team);
                for(Player player : players) {
                    if (blocksLeft > 1) {
                        player.sendMessage("there are "+String.valueOf(blocksLeft)+" blocks left to the team "+attaquedTeam);
                    }
                    else {
                        player.sendMessage("there is 1 block left to "+attaquedTeam);
                    }
                }
            }
        }
        else {
            int remaining_teams = teamsManager.getNumberOfRemainingTeams(this.gameId);
            // 2 more teams? The team just lost
            if (remaining_teams > 2) {
                teamsManager.addLooser(this.gameId, attaquedTeam);
                remaining_teams--;
                for(String team : teamList){
                    List<Player> players = teamsManager.getOnlinePlayersOfATeam(team);
                    for(Player player : players) {
                        player.sendMessage("The team "+this.attaquedTeam+" lost!");
                        player.sendMessage("There are "+String.valueOf(remaining_teams)+" teams in game");
                    }
                }
            }
            // Keep playing, next block placed should be the last one
            else {
                for(String team : teamList){
                    List<Player> players = teamsManager.getOnlinePlayersOfATeam(team);
                    for(Player player : players) {
                        player.sendMessage("The team "+this.attaquedTeam+" just lost his last block!!!");
                    }
                }
            }
        }
    }

    /**
     * Check if the brocken block is part of the totem of the player
     * 
     * @param event
     * @return true or false
     */
    public boolean isAPlayerBlockTotem(BlockBreakEvent event)
    {
        String playerName = event.getPlayer().getName();
        String teamName = teamsManager.getTeamOfPlayer(playerName);
        double XBlockLoc = event.getBlock().getX();
        double XTotemLoc = teamsManager.getXCoordinateOfTeamTotem(teamName);
        // large intervale else always false
        if (Math.abs(XBlockLoc-XTotemLoc)<1) {
            double ZBlockLoc = event.getBlock().getZ();
            double ZTotemLoc = teamsManager.getZCoordinateOfTeamTotem(teamName);
            if (Math.abs(ZBlockLoc-ZTotemLoc)<1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether the totem block belongs to the player game or not
     * 
     * @param event
     * @return true or false
     */
    public boolean blockTotemBelongToThePlayerGame(BlockBreakEvent event)
    {
        List<String> teamList = teamsManager.getTeamListMatchingTheId(this.gameId);
        double XBlockLoc = event.getBlock().getX();
        double ZBlockLoc = event.getBlock().getZ();
        for(String team : teamList){
            double XTotemLoc = teamsManager.getXCoordinateOfTeamTotem(team);
            if (Math.abs(XBlockLoc-XTotemLoc)<1) {
                double ZTotemLoc = teamsManager.getZCoordinateOfTeamTotem(team);
                if (Math.abs(ZBlockLoc-ZTotemLoc)<1) {
                    this.attaquedTeam = team;

                    return true;
                }
            }
        }

        return false;
    }
}