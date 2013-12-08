package ovski.minecraft.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * YamlTeamsManager
 *
 * Manage the Yaml teams file (to store teams, to know if a team exists, to retrieve leaders...)
 * 
 * @author baptiste <baptiste.bouchereau@gmail.com>
 */
public class YamlTeamsManager
{
    private YamlAccessor teamsAccessor;
    private YamlPlayersManager playersManager = new YamlPlayersManager();

    /**
     * Constructor
     */
    public YamlTeamsManager()
    {
        this.teamsAccessor = new YamlAccessor(new File("plugins/Yaml/totemTeams.yml"));
    }

    /**
     * Get the YamlAccessor of the totem_teams.yml file
     * 
     * @return teamsAccessor the YamlAccessor
     */
    public YamlAccessor getTeamsAccessor()
    {
        return teamsAccessor;
    }

    /**
     * Set the YamlAccessor of the totem_teams.yml file
     * 
     * @param teamsAccessor the YamlAccessor
     */
    public void setTeamsAccessor(YamlAccessor teamsAccessor)
    {
        this.teamsAccessor = teamsAccessor;
    }

    /**
     * Get the team of a given player
     * 
     * @param playerName
     * @return the name of the team
     */
    public String getTeamOfPlayer(String playerName)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return teamsAccessor.getConfig().getString("players."+playerName+".team");
    }

    /**
     * Add a player to a team
     * 
     * @param playerName
     * @param teamName
     */
    public void addPlayerInTeam(String playerName, String teamName)
    {
        teamsAccessor.getConfig().set("players."+playerName+".team", teamName);
        teamsAccessor.saveConfig();
    }

    /**
     * Remove a player from a team
     * 
     * @param playerName
     */
    public void removePlayerFromTeam(String playerName)
    {
        teamsAccessor.getConfig().set("players."+playerName, null);
        teamsAccessor.saveConfig();
    }

    /**
     * Check whether a player is in a team or not
     * 
     * @param playerName
     * @return true or false
     */
    public boolean playerIsInATeam(String playerName)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return teamsAccessor.getConfig().contains("players."+playerName);
    }

    /**
     * Check whether a player is in the same team of the given leader
     * 
     * @param playerName
     * @param leaderName
     * @return true or false
     */
    public boolean playerIsInLeaderTeam(String playerName, String leaderName)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return this.getTeamOfPlayer(leaderName).equals(this.getTeamOfPlayer(playerName));
    }

    /**
     * Add a new team
     * 
     * @param leaderName
     * @param teamName
     */
    public void addTeam(String leaderName, String teamName)
    {
        teamsAccessor.getConfig().set("teams."+teamName+".leader", leaderName);
        teamsAccessor.saveConfig();
    }

    /**
     * Remove a team
     * 
     * @param teamName
     */
    public void removeTeam(String teamName)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<String> playerList = playersManager.getPlayerList();
        for(String player : playerList){
            if (teamsAccessor.getConfig().getString("players."+player+".team") != null) {
                if (teamsAccessor.getConfig().getString("players."+player+".team").equals(teamName)) {
                    teamsAccessor.getConfig().set("players."+player, null);
                }
            }
        }
        teamsAccessor.getConfig().set("teams."+teamName, null);
        teamsAccessor.saveConfig();
    }

    /**
     * Check whether a team exists or not
     * 
     * @param teamName
     * @return true or false
     */
    public boolean teamExists(String teamName) {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return teamsAccessor.getConfig().contains("teams."+teamName);
    }

    /**
     * Check whether a player is a leader or not
     * 
     * @param playerName
     * @return true or false
     */
    public boolean isLeader(String playerName)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String teamName = teamsAccessor.getConfig().getString("players."+playerName+".team");
        String leaderName = teamsAccessor.getConfig().getString("teams."+teamName+".leader");

        if(leaderName != null) {
            return leaderName.equals(playerName);
        } else {
            return false;
        }
    }

    /**
     * Get the leader of a team
     * 
     * @param teamName
     * @return the leader name
     */
    public String getLeader(String teamName) {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return teamsAccessor.getConfig().getString("teams."+teamName+".leader");
    }

    /**
     * Get the list of online players of a team
     * 
     * @param teamName
     * @return the list of online players
     */
    public List<Player> getOnlinePlayersOfATeam(String teamName) {
        List<String> completePlayerList = (List<String>) playersManager.getPlayerList();
        List<Player> playerList = new ArrayList<Player>();
        for (String player : completePlayerList) {
            if (this.getTeamOfPlayer(player) != null) {
                if (this.getTeamOfPlayer(player).equals(teamName)) {
                    if(Bukkit.getServer().getPlayer(player)!=null) {
                        if(Bukkit.getServer().getPlayer(player).isOnline()) {
                            playerList.add(Bukkit.getServer().getPlayer(player));
                        }
                    }
                }
            }
        }
        return playerList;
    }

    /**
     * Set the leader of a team
     * 
     * @param leaderName
     * @param teamName
     */
    public void setLeader(String leaderName, String teamName)
    {
        teamsAccessor.getConfig().set("teams."+teamName+".leader", leaderName);
        teamsAccessor.saveConfig();
    }

    /**
     * Get the if of the next game
     * 
     * @return the id of the next game
     */
    public int getNextGameId()
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return teamsAccessor.getConfig().getInt("next_game_id");
    }

    /**
     * Set a new Game
     * TODO CHANGER contient le premier chiffre
     * 
     * @param teams
     */
    public void setNewGame(String teams[])
    {
        int id = getNextGameId();
        setNextGameId();
        List<String> teamList = new ArrayList<String>();
        for (int i=1; i<teams.length; i++) {
            teamList.add(teams[i]);
        }
        teamsAccessor.getConfig().set("games.game"+String.valueOf(id)+".remaining_teams", teamList);
        for (int i=1; i<teams.length; i++) {
            teamsAccessor.getConfig().set("games.game"+String.valueOf(id)+".teams_blocks."+teams[i], 3);
            teamsAccessor.getConfig().set("teams."+teams[i]+".game", id);
        }
        teamsAccessor.getConfig().set("games.game"+String.valueOf(id)+".win_number", Integer.parseInt(teams[0]));
        teamsAccessor.saveConfig();
    }

    /**
     * Reset a game
     * 
     * @param id
     * @param teamList
     */
    public void resetGame(int id, List<String> teamList)
    {
        teamsAccessor.getConfig().set("games.game"+String.valueOf(id), null);
        for (String team : teamList) {
            teamsAccessor.getConfig().set("teams."+team+".totem", null);
            teamsAccessor.getConfig().set("teams."+team+".spawn", null);
            teamsAccessor.getConfig().set("teams."+team+".game", 0);
        }
        teamsAccessor.saveConfig();
    }

    /**
     * Get the number of blocks to win for a game
     * 
     * @param id
     * @return the win number
     */
    public int getWinNumber(int id)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return teamsAccessor.getConfig().getInt("games.game"+String.valueOf(id)+".win_number");
    }

    /**
     * Set the next game id
     */
    public void setNextGameId()
    {
        int next = getNextGameId()+1;
        teamsAccessor.getConfig().set("next_game_id", next);
        teamsAccessor.saveConfig();
    }

    /**
     * Set the id of a game
     * 
     * @param teamName
     * @param id
     */
    public void setGameId(String teamName, int id)
    {
        teamsAccessor.getConfig().set("teams."+teamName+".game", id);
        teamsAccessor.saveConfig();
    }

    /**
     * Set the next game id if it's the first time a game is played
     */
    public void initNextGameId()
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (!teamsAccessor.getConfig().contains("next_game_id")) {
            teamsAccessor.getConfig().set("next_game_id", 1);
            teamsAccessor.saveConfig();
        }
    }

    /**
     * Get the team list matching the id
     * 
     * @param id
     * @return a list of teams for a game id
     */
    public List<String> getTeamListMatchingTheId(int id)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        @SuppressWarnings("unchecked")
        List<String> teamList = (List<String>) teamsAccessor.getConfig().getList("games.game"+String.valueOf(id)+".remaining_teams");

        return teamList;
    }

    /**
     * Get the number of remaining teams
     * 
     * @param id
     * @return the number of remaining teams
     */
    public int getNumberOfRemainingTeams(int id)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        @SuppressWarnings("unchecked")
        List<String> teamList = (List<String>) teamsAccessor.getConfig().getList("games.game"+String.valueOf(id)+".remaining_teams");

        return teamList.size();
    }

    /**
     * Add a looser team for a game
     * 
     * @param id
     * @param team
     */
    public void addLooser(int id, String team)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // we remove the looser team from the list of remaining teams
        @SuppressWarnings("unchecked")
        List<String> teamList = (List<String>) teamsAccessor.getConfig().getList("games.game"+String.valueOf(id)+".remaining_teams");
        teamList.remove(team);
        teamsAccessor.getConfig().set("games.game"+String.valueOf(id)+".remaining_teams", teamList);
        teamsAccessor.saveConfig();
        // if this team is the first to lose
        if (!teamsAccessor.getConfig().contains("games.game"+String.valueOf(id)+".looser_teams")) {
            List<String> looserList = new ArrayList<String>();
            looserList.add(team);
            teamsAccessor.getConfig().set("games.game"+String.valueOf(id)+".looser_teams", looserList);
            teamsAccessor.saveConfig();
        }
        // if there are already loosers
        else {
            @SuppressWarnings("unchecked")
            List<String> looserList = (List<String>) teamsAccessor.getConfig().getList("games.game"+String.valueOf(id)+".looser_teams");
            looserList.add(team);
            teamsAccessor.getConfig().set("games.game"+String.valueOf(id)+".looser_teams", looserList);
            teamsAccessor.saveConfig();
        }
    }

    /**
     * Get a list of looser teams
     * 
     * @param id
     * @return the list of looser teams
     */
    @SuppressWarnings("unchecked")
    public List<String> getLooserTeams(int id)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String> teamList = (List<String>) teamsAccessor.getConfig().getList("games.game"+String.valueOf(id)+".looser_teams");

        return teamList;
    }

    /**
     * Get a list of remaining teams
     * 
     * @param id
     * @return the list of remaining teams
     */
    @SuppressWarnings("unchecked")
    public List<String> getRemainingTeams(int id) {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String> teamList = (List<String>) teamsAccessor.getConfig().getList("games.game"+String.valueOf(id)+".remaining_teams");

        return teamList;
    }

    /**
     * Get the game id for a team
     * 
     * @param teamName
     * @return the game id
     */
    public int getGameId(String teamName) {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return Integer.parseInt(teamsAccessor.getConfig().getString("teams."+teamName+".game"));
    }

    /**
     * Set the teams block number of the totem of a team
     * 
     * @param teamName
     * @param blockNumber
     */
    public void setTeamsBlocks(String teamName, int blockNumber) {
        int id = getGameId(teamName);
        teamsAccessor.getConfig().set("games.game"+String.valueOf(id)+".teams_blocks."+teamName, blockNumber);
        teamsAccessor.saveConfig();
    }

    /**
     * Get the number of block of the totem of a team
     * 
     * @param teamName
     * @return the number of block
     */
    public int getTeamsBlocksNumber(String teamName)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int id = getGameId(teamName);

        return teamsAccessor.getConfig().getInt("games.game"+String.valueOf(id)+".teams_blocks."+teamName);
    }

    /**
     * Increase the number of block of the totem of a team
     * 
     * @param teamName
     */
    public void increaseTeamsBlocks(String teamName)
    {
        this.setTeamsBlocks(teamName, getTeamsBlocksNumber(teamName)+1);
    }

    /**
     * Decrease the number of block of the totem of a team
     * 
     * @param teamName
     */
    public void decreaseTeamsBlocks(String teamName)
    {
        this.setTeamsBlocks(teamName, getTeamsBlocksNumber(teamName)-1);
    }

    /**
     * Check whether or not a team is currently playing
     * 
     * @param teamName
     * @return true or false
     */
    public boolean isPlaying(String teamName)
    {
        if (getGameId(teamName) == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Set the spawn of a team at a player location
     * 
     * @param teamName
     * @param player
     */
    public void setSpawn(String teamName, Player player)
    {
        Location loc = player.getLocation();
        teamsAccessor.getConfig().set("teams."+teamName+".spawn.X", String.valueOf(loc.getX()));
        teamsAccessor.getConfig().set("teams."+teamName+".spawn.Y", String.valueOf(loc.getY()));
        teamsAccessor.getConfig().set("teams."+teamName+".spawn.Z", String.valueOf(loc.getZ()));
        teamsAccessor.saveConfig();
    }

    /**
     * Set the totem location for a team
     *  
     * @param teamName
     * @param loc
     */
    public void setTeamTotemLocation(String teamName, Location loc)
    {
        teamsAccessor.getConfig().set("teams."+teamName+".totem.X", String.valueOf(loc.getX()));
        teamsAccessor.getConfig().set("teams."+teamName+".totem.Z", String.valueOf(loc.getZ()));
        teamsAccessor.saveConfig();
    }

    /**
     * Check whether or not a spawn is set for a team
     * 
     * @param teamName
     * @return true or false
     */
    public boolean spawnIsSet(String teamName)
    {
        return teamsAccessor.getConfig().contains("teams."+teamName+".spawn");
    }

    /**
     * Get the X coordinates of a team spawn
     * 
     * @param teamName
     * @return the X coordinates
     */
    public double getXCoordinateOfTeamSpawn(String teamName)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return Double.valueOf(teamsAccessor.getConfig().getString("teams."+teamName+".spawn.X"));
    }

    /**
     * Get the Y coordinates of a team spawn
     * 
     * @param teamName
     * @return the Y coordinates
     */
    public double getYCoordinateOfTeamSpawn(String teamName)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return Double.valueOf(teamsAccessor.getConfig().getString("teams."+teamName+".spawn.Y"));
    }

    /**
     * Get the Z coordinates of a team spawn
     * 
     * @param teamName
     * @return the Z coordinates
     */
    public double getZCoordinateOfTeamSpawn(String teamName)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return Double.valueOf(teamsAccessor.getConfig().getString("teams."+teamName+".spawn.Z"));
    }

    /**
     * Get the X coordinates of a team totem
     * 
     * @param teamName
     * @return the X coordinates
     */
    public double getXCoordinateOfTeamTotem(String teamName)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return Double.valueOf(teamsAccessor.getConfig().getString("teams."+teamName+".totem.X"));
    }

    /**
     * Get the Z coordinates of a team totem
     * 
     * @param teamName
     * @return the Z coordinates
     */
    public double getZCoordinateOfTeamTotem(String teamName)
    {
        try {
            teamsAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return Double.valueOf(teamsAccessor.getConfig().getString("teams."+teamName+".totem.Z"));
    }
}
