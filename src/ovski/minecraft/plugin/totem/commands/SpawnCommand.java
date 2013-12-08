package ovski.minecraft.plugin.totem.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ovski.minecraft.manager.YamlTeamsManager;

/**
 * SpawnCommand
 * 
 * Teleport to the spawn of the team
 * 
 * @author baptiste <baptiste.bouchereau@gmail.com>
 */
public class SpawnCommand implements CommandExecutor
{
    private YamlTeamsManager teamsManager = new YamlTeamsManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player) {
            // we check if the player is in a team
            Player player = (Player) sender;
            String playerName = player.getName();
            if(!teamsManager.playerIsInATeam(playerName)) {
                player.sendMessage("You don't have a spawn as you are not in a team");

                return true;
            }
            String team = teamsManager.getTeamOfPlayer(playerName);
            // we check if the player's team has a spawn
            if (!teamsManager.spawnIsSet(team)) {
                player.sendMessage("The team "+ChatColor.AQUA+team+ChatColor.WHITE+" do not have a spawn!");

                return true;
            }
            // we check if the player performing the command is not currently in a game
            if (teamsManager.isPlaying(team)) {
                player.sendMessage("This command is disabled during a game");

                return true;
            }
            // we teleport the player
            player.sendMessage("Teleportation au spawn en cours...");
            Location spawn = player.getLocation();
            spawn.setX(teamsManager.getXCoordinateOfTeamSpawn(team));
            spawn.setY(teamsManager.getYCoordinateOfTeamSpawn(team));
            spawn.setZ(teamsManager.getZCoordinateOfTeamSpawn(team));
            player.teleport(spawn);

            return true;
        }
        else {
            return false;
        }
    }
}

