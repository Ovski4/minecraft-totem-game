package ovski.minecraft.plugin.totem.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ovski.minecraft.manager.YamlTeamsManager;

/**
 * ScoresCommand
 * 
 * Displays the scores during a game
 * 
 * @author baptiste <baptiste.bouchereau@gmail.com>
 */
public class ScoresCommand implements CommandExecutor
{
    private YamlTeamsManager teamsManager = new YamlTeamsManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player) {
            Player commandPlayer = (Player) sender;
            String playerName = commandPlayer.getName();

            // we check if the player is in a team
            if(!teamsManager.playerIsInATeam(playerName)) {
                commandPlayer.sendMessage("You need to be part of a team to perform this command");

                return true;
            }

            // we check id the command sender is currently playing
            String teamName = teamsManager.getTeamOfPlayer(playerName);
            if (!teamsManager.isPlaying(teamName)) {
                commandPlayer.sendMessage("Cette commande est uniquement disponible durant une partie");

                return true;
            }

            int gameId = teamsManager.getGameId(teamName);
            // we retrieve the teams involve in that game
            List<String> looserList = teamsManager.getLooserTeams(gameId);
            List<String> remainingTeamList = teamsManager.getRemainingTeams(gameId);
            // we display the scores
            commandPlayer.sendMessage("--------------- Scores ---------------");
            if(looserList!=null) {
                for (String looser : looserList) {
                    commandPlayer.sendMessage("Equipe "+looser+ ": perdu");
                }
            }
            for (String remainingTeam : remainingTeamList) {
                int remainingBlocks = teamsManager.getTeamsBlocksNumber(remainingTeam);
                if (remainingBlocks > 1) {
                    commandPlayer.sendMessage("Equipe "+remainingTeam+ ": "+String.valueOf(remainingBlocks)+" blocs restants");
                }
                else {
                    commandPlayer.sendMessage("Equipe "+remainingTeam+ ": 1 seul bloc restant");
                }
            }
            
            commandPlayer.sendMessage("Nombre de blocs pour gagner : "+String.valueOf(teamsManager.getWinNumber(gameId)));
            commandPlayer.sendMessage("--------------------------------------");

            return true;
        }
        else {
            return false;
        }
    }

}