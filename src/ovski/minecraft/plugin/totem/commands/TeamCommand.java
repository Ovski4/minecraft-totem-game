package ovski.minecraft.plugin.totem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ovski.minecraft.manager.YamlPlayersManager;
import ovski.minecraft.manager.YamlTeamsManager;
import ovski.minecraft.plugin.totem.TotemPlugin;

// TODO help

/**
 * TeamCommand
 * 
 * A list of command to manage teams
 * 
 * /team create teamName
 * /team add playerName
 * /team leader playerName
 * /team remove playerName
 * /team spawn
 * /team leave
 * /team disband
 *
 * @author baptiste <baptiste.bouchereau@gmail.com>
 */
public class TeamCommand implements CommandExecutor
{
    private YamlTeamsManager teamsManager = new YamlTeamsManager();
    private YamlPlayersManager playersManager = new YamlPlayersManager();
    private TotemPlugin totemPlugin;

    /**
     * Constructor
     * 
     * @param totemPlugin
     */
    public TeamCommand(TotemPlugin totemPlugin)
    {
        this.totemPlugin = totemPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerName = player.getName();
            if (args.length >= 1) {
                String playerTeam = teamsManager.getTeamOfPlayer(playerName);

                // CREATE A TEAM

                if (args[0].equals("create")) {
                    // we check if the command sender is not already in a team
                    if (teamsManager.playerIsInATeam(playerName)) {
                        player.sendMessage("You are already in a team, you cannot create a new one");

                        return true;
                    }
                    // we check if the command contains the name of the team
                    if(args.length != 2) {
                        player.sendMessage("Do /team create teamName to create a team (one word as a name)");

                        return true;
                    }
                    // we check if the given name does not already exist
                    if (teamsManager.teamExists(args[1])) {
                        player.sendMessage("This team already exist");

                        return true;
                    }
                    // we create the team, set the leader (the command sender) and add the leader as the first player
                    teamsManager.addTeam(playerName, args[1]);
                    teamsManager.addPlayerInTeam(playerName, args[1]);
                    // we set the game at 0 (not playing)
                    teamsManager.setGameId(args[1], 0);
                    player.sendMessage("The team "+teamsManager.getTeamOfPlayer(playerName)+" was created");

                    return true;
                }

                // REMOVE A TEAM

                else if (args[0].equals("disband")) {
                    // we check if the command sender is a leader
                    if (!teamsManager.isLeader(playerName)) {
                        player.sendMessage("You must be leader to perform this command");

                        return true;
                    }
                    // we check that the command sender is not playing
                    if (teamsManager.isPlaying(playerTeam)) {
                        player.sendMessage("This command is disabled during a game");

                        return true;
                    }
                    // everything is fine
                    teamsManager.removeTeam(playerTeam);
                    player.sendMessage("The team was disbanded");

                    return true;
                }

                // ADD A PLAYER IN A TEAM

                else if (args[0].equals("add")) {
                    // we check if the command sender is a leader
                    if (!teamsManager.isLeader(playerName)) {
                        player.sendMessage("You must be leader to perform this command");

                        return true;
                    }
                    // we check that the command sender is not playing
                    if (teamsManager.isPlaying(playerTeam)) {
                        player.sendMessage("This command is disabled during a game");

                        return true;
                    }
                    // we check if the command has 2 arguments
                    if(args.length != 2) {
                        player.sendMessage("Do /team add playerName to add a player");

                        return true;
                    }
                    // we check if the indicated player exists
                    if(!playersManager.playerIsInTheList(args[1])) {
                        player.sendMessage("Le joueur indiqu� n'a encore jamais rejoins ce serveur");

                        return true;
                    }
                   // we check if the indicated player is not already in a team
                    if(teamsManager.playerIsInATeam(args[1])) {
                        // we check if he is not already in the command sender team!
                        if (teamsManager.playerIsInLeaderTeam(args[1], playerName)) {
                            player.sendMessage("This player is already in your team");

                            return true;
                        } else {
                            player.sendMessage("This player is already in another team");

                            return true;
                        }
                    }
                    // everything is fine
                    teamsManager.addPlayerInTeam(args[1], playerTeam);
                    player.sendMessage(args[1]+" was added to the team");
                    if (!(totemPlugin.getServer().getPlayer(args[1]) == null)) {
                        totemPlugin.getServer().getPlayer(args[1]).sendMessage("You have been added to the team "+playerTeam);
                    }

                    return true;
                }

                // REMOVE A PLAYER OF A TEAM

                else if (args[0].equals("remove")) {
                    // we check if the command sender is a leader
                    if (!teamsManager.isLeader(playerName)) {
                        player.sendMessage("You must be leader to perform this command");

                        return true;
                    }
                    // we check that the command sender is not playing
                    if (teamsManager.isPlaying(playerTeam)) {
                        player.sendMessage("This command is disabled during a game");

                        return true;
                    }
                    // we check if the command has 2 arguments
                    if(args.length != 2) {
                        player.sendMessage("Do /team remove playerName to remove a player");

                        return true;
                    }
                    // we check if the indicated player exists
                    if(!playersManager.playerIsInTheList(args[1])) {
                        player.sendMessage("This player was never here");

                        return true;
                    }
                    // we check if the player is in the leader's team
                    if (!teamsManager.playerIsInLeaderTeam(args[1], playerName)) {
                        player.sendMessage("this player is not in your team");

                        return true;
                    }
                    // we check if the indicated player is not the command sender himself
                    if (args[1].equals(playerName)) {
                        player.sendMessage(
                            "As a leader, you can't leave this team. " +
                            "Assign the leader status to someone else and leave," +
                            "or disband your team"
                        );

                        return true;
                    }

                    // everything is fine
                    teamsManager.removePlayerFromTeam(args[1]);
                    player.sendMessage(args[1]+" was removed");
                    totemPlugin.getServer().getPlayer(args[1]).sendMessage("You have been removed from "+playerTeam);

                    return true;
                }

                // LEAVE A TEAM

                else if (args[0].equals("leave")) {
                    // we check if the player is in a team
                    if(!teamsManager.playerIsInATeam(playerName)) {
                        player.sendMessage("Before leaving a team you need to join one");

                        return true;
                    }
                    // we check if the player is currently playing
                    if (teamsManager.isPlaying(playerTeam)) {
                        player.sendMessage("This command is disabled during a game");

                        return true;
                    }
                    // we check if the player is a leader
                    if (args[1].equals(playerName)) {
                        player.sendMessage(
                            "As a leader, you can't leave this team. " +
                            "Assign the leader status to someone else and leave," +
                            "or disband your team"
                        );

                        return true;
                    }
                    // everything is fine
                    teamsManager.removePlayerFromTeam(playerName);
                    player.sendMessage("Welcome to wilderness, lonely wolf");

                    return true;
                }

             // CHANGE THE LEADER

                else if (args[0].equals("leader")) {
                    // we check if the command sender si a leader
                    if (!teamsManager.isLeader(playerName)) {
                        player.sendMessage("You must be leader to performed this command");

                        return true;
                    }
                    // we check the number of parameters
                    if(args.length != 2) {
                        player.sendMessage("Do /team leader playerName to change the leader");
                        return true;
                    }
                    // we check if the indicated player exists
                    if(!playersManager.playerIsInTheList(args[1])) {
                        player.sendMessage(args[1] + " was never here ");
                        return true;
                    }
                    // we check if the indicated player is in the command sender team
                    if (!teamsManager.playerIsInLeaderTeam(args[1], playerName)) {
                        player.sendMessage(args[1] + "is not in your team");
                        return true;
                    }
                    //we check if the command sender is not schizophrenic
                    if (args[1].equals(playerName)) {
                        player.sendMessage("What the fuck?");

                        return true;
                    }

                    // everything is fine
                    teamsManager.setLeader(args[1], playerTeam);

                    return true;
                }

                // SET THE SPAWN

                else if (args[0].equals("spawn")) {
                    // we check that the command sender is leader
                    if (!teamsManager.isLeader(playerName)) {
                        player.sendMessage("You must be leader to perform this command");

                        return true;
                    }
                    // we check if the command sender is not playing
                    if (teamsManager.isPlaying(playerTeam)) {
                        player.sendMessage("This command si disabled during a game");

                        return true;
                    }
                    // we save the coordinates
                    teamsManager.setSpawn(playerTeam, player);
                    player.sendMessage("You have got a new spawn");

                    return true;
                }

                // Syntax error
                player.sendMessage("Syntax error. Try '/help team'"); // TODO

                return true;

            }
            else {
                player.sendMessage("Some parameters are missing. Try '/help team'");

                return true;
            }

        }
        else {
            return false;
        }
    }

}