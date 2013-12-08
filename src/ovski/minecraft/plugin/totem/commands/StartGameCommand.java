package ovski.minecraft.plugin.totem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ovski.minecraft.manager.YamlTeamsManager;
import ovski.minecraft.plugin.totem.TotemPlugin;

/**
 * StartGameCommand
 * 
 * Start a game : teleport players at 
 * 
 * @author baptiste <baptiste.bouchereau@gmail.com>
 */
public class StartGameCommand implements CommandExecutor
{
    // TODO add a command /ready. If everyone involved is not ready, do not start the game
    // TODO check if possible to set a spawn for everyone at the same location at the begging

    private YamlTeamsManager teamsManager = new YamlTeamsManager();
    private TotemPlugin totemPlugin;
    private Player player;
    private Player[] playersOnline;
    private String[] teams;
    private int countdown;

    /**
     * Constructor
     * 
     * @param totemPlugin
     */
    public StartGameCommand(TotemPlugin totemPlugin)
    {
        this.totemPlugin = totemPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player) {
            this.playersOnline = totemPlugin.getServer().getOnlinePlayers();
            this.teams = args; // TODO update (without the first arg)
            this.player = (Player) sender;

            // we check there are at least 2 teams to challenge
            if(teams.length < 3) {
                player.sendMessage("You need at least one number and 2 teams to start" +
                    " a game. Do '/start numberOfBlocksToRetrieveToWin " +
                    "team1 team2 team3...'"
                );

                return true;
            }

            // we check if the indicated teams exists
            for (int i=1; i<teams.length; i++) {
                if(!teamsManager.teamExists(teams[i])) {
                    player.sendMessage("The team "+teams[i]+" does not exist");

                    return true;
                }
            }

            // we check that at least leaders are online
            for (int i=1; i<teams.length; i++) {
                boolean hasLeader = false;
                for (int j=0; j<playersOnline.length; j++) {
                    if(teamsManager.getLeader(teams[i]).equals(playersOnline[j].getName())) {
                        hasLeader = true;
                    }
                }
                if(!hasLeader) {
                    player.sendMessage("The leader of the team "+teams[i]+" is not online");

                    return true;
                }
            }
            // we check if a team is not currently playing
            for (int i=1; i<teams.length; i++) {
                if (teamsManager.isPlaying(teams[i])) {
                    player.sendMessage("The team "+teams[i]+" is already playing");

                    return true;
                }
            }
            // we check if every team has a spawn
            for (int i=1; i<teams.length; i++) {
                if (!teamsManager.spawnIsSet(teams[i])) {
                    player.sendMessage("The team "+ChatColor.RED+teams[i]+" does not have a spawn!");

                    return true;
                }
            }

            // we check if the second parameter is an integer
            try {
                int nbBlocks = Integer.parseInt(args[0]);
                if (nbBlocks>(3*(args.length-1))) {
                    player.sendMessage("You cannot retrieve more blocks than the maximum in game");

                    return true;
                }
                if (nbBlocks<4) {
                    player.sendMessage("To win, you must at least retrieve more blocks than you already have at the begininng");

                    return true;
                }
            } catch (NumberFormatException nfe) {
                player.sendMessage("The number of blocks must be an integer");

                return true;
            }

            // we send a message to players indicated the the game will be launched
            for (int i=1; i<teams.length; i++) {
                for (int j=0; j<playersOnline.length; j++) {
                    if(teamsManager.getTeamOfPlayer(playersOnline[j].getName()).equals(teams[i])) {
                        playersOnline[j].sendMessage("The game is launching...");
                    }
                }
            }

            // we update the yml file
            teamsManager.setNewGame(args);
            this.clearInventory();
            this.teleportPlayers();
            this.startCountdown();

            return true;
        }
        return false;
    }

    /**
     * Start the countdown then pop the totem
     */
    public void startCountdown()
    {
        countdown = Bukkit.getScheduler().scheduleSyncRepeatingTask(totemPlugin, new Runnable() {

            int time = 121; // 2 minutes
            // TODO add this number in the command?
            public void run() {
                time -= 1;
                if (time < 0) {
                    Bukkit.getScheduler().cancelTask(countdown);
                }
                else if (time == 120) {
                    sendMessageToPlayersConcernedByTheCommand("Beware..");
                }
                else if (time == 119) {
                    sendMessageToPlayersConcernedByTheCommand("Ready...");
                }
                else if (time == 118) {
                    sendMessageToPlayersConcernedByTheCommand("Go !!!");
                }
                else if (time == 10) {
                    sendMessageToPlayersConcernedByTheCommand("Be careful, 10 seconds left!");
                }
                else if (time <= 5 && time > 0) {
                    sendMessageToPlayersConcernedByTheCommand(String.valueOf(time));
                }
                else if (time == 0) {
                    Player[] leaders = new Player[50];
                    // we make a list with all leaders
                    for (int i=1; i<teams.length; i++) {
                        for (int j=0; j<playersOnline.length; j++) {
                            if(teamsManager.getLeader(teams[i]).equals(playersOnline[j].getName())) {
                                leaders[i] = playersOnline[j];
                            }
                        }
                    }
                    // we plant the totems at the leaders locations
                    for (int i=0; i<leaders.length; i++) {
                        if (leaders[i] != null) { // nullpointerException sometimes? TODO check
                            setTotem(leaders[i]);
                            // add a sound indicator when the totem pop
                            for (int j=0; j<playersOnline.length; j++) {
                                playersOnline[j].playSound(leaders[i].getLocation(), Sound.ARROW_HIT, 1, 0);
                            }
                        }
                    }
                    sendMessageToPlayersConcernedByTheCommand("Totems just appeared!");
                }
            }
        }, 20, 20);
    }

    /**
     * Send a message to all players concerned by the command
     * 
     * @param message
     */
    public void sendMessageToPlayersConcernedByTheCommand(String message)
    {
        for (int i=1; i<teams.length; i++) {
            for (int j=0; j<playersOnline.length; j++) {
                if(teamsManager.getTeamOfPlayer(playersOnline[j].getName()).equals(teams[i])) {
                    playersOnline[j].sendMessage(message);
                }
            }
        }
    }

    /**
     * Set the totem at a player location
     * 
     * @param player
     */
    public void setTotem(Player player)
    {
        // we teleport the player next to the block where the totem will appear
        // so it does not hurt him
        Location totemLocation = player.getLocation();
        Location teleportationLocation =  player.getLocation();
        teleportationLocation.setX(teleportationLocation.getX()-2);
        player.teleport(teleportationLocation);
        // we save the totem coordinates in the yml file
        teamsManager.setTeamTotemLocation(teamsManager.getTeamOfPlayer(player.getName()), totemLocation);
        // as long as the totem is in the aire we do not set him
        totemLocation.setY(totemLocation.getY()-1);
        Block b1 = totemLocation.getBlock();
        while (b1.getType() == Material.AIR) {
            totemLocation.setY(totemLocation.getY()-1);
            b1 = totemLocation.getBlock();
        }
        totemLocation.setY(totemLocation.getY()+1);
        b1 = totemLocation.getBlock();
        // we set the totem at the last player location
        b1.setType(Material.SMOOTH_BRICK);
        b1.setData((byte) 3);
        totemLocation.setY(totemLocation.getY()+1);
        b1 = totemLocation.getBlock();
        b1.setType(Material.SMOOTH_BRICK);
        b1.setData((byte) 3);
        totemLocation.setY(totemLocation.getY()+1);
        b1 = totemLocation.getBlock();
        b1.setType(Material.SMOOTH_BRICK);
        b1.setData((byte) 3);
    }

    /**
     * Clear the inventory
     */
    public void clearInventory()
    {
        for (int i=1; i<teams.length; i++) {
            for (int j=0; j<playersOnline.length; j++) {
                if(teamsManager.getTeamOfPlayer(playersOnline[j].getName()).equals(teams[i])) {
                    playersOnline[j].getInventory().clear();
                    playersOnline[j].sendMessage("Clearing the inventory...");
                }
            }
        }
    }

    /**
     * Teleport players to their spawn
     */
    public void teleportPlayers() {
        for (int i=1; i<teams.length; i++) {
            for (int j=0; j<playersOnline.length; j++) {
                if(teamsManager.getTeamOfPlayer(playersOnline[j].getName()).equals(teams[i])) {
                    playersOnline[j].sendMessage("Teleportation to the spawn...");
                    Location spawn = playersOnline[j].getLocation();
                    spawn.setX(teamsManager.getXCoordinateOfTeamSpawn(teams[i]));
                    spawn.setY(teamsManager.getYCoordinateOfTeamSpawn(teams[i]));
                    spawn.setZ(teamsManager.getZCoordinateOfTeamSpawn(teams[i]));
                    playersOnline[j].teleport(spawn);
                }
            }
        }
    }
}