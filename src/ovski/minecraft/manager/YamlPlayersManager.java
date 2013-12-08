
package ovski.minecraft.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * YamlPlayersManager
 *
 * Manage the Yaml player file (to list players)
 * 
 * @author baptiste <baptiste.bouchereau@gmail.com>
 */
public class YamlPlayersManager {

    private YamlAccessor playersListAccessor;

    /**
     * Constructor
     */
    public YamlPlayersManager()
    {
        this.playersListAccessor = new YamlAccessor(new File("plugins/Yaml/playerList.yml"));
    }

    /**
     * Get the YamlAccessor of the player_list.yml file
     * 
     * @return playersListAccessor the YamlAccessor
     */
    public YamlAccessor getPlayersListAccessor()
    {
        return playersListAccessor;
    }

    /**
     * Set the YamlAccessor of the player_list.yml file
     * 
     * @param playersListAccessor the YamlAccessor
     */
    public void setPermissionsAccessor(YamlAccessor playersListAccessor)
    {
        this.playersListAccessor = playersListAccessor;
    }

    /**
     * Add a player
     * 
     * @param playerName
     */
    @SuppressWarnings("unchecked")
    public void addPlayer(String playerName)
    {
        try {
            playersListAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String> playerList = (List<String>) playersListAccessor.getConfig().getList("players");
        playerList.add(playerName);
        playersListAccessor.getConfig().set("players", playerList);
        playersListAccessor.saveConfig();
    }

    /**
     * Check whether or not a player is in the list
     * 
     * @param playerName
     * @return true or false
     */
    public boolean playerIsInTheList(String playerName)
    {
        try {
            playersListAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return playersListAccessor.getConfig().getList("players").contains(playerName);
    }

    /**
     * Get the player list
     * 
     * @return the list of players
     */
    @SuppressWarnings("unchecked")
    public List<String> getPlayerList()
    {
        try {
            playersListAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String> playerList = (List<String>) playersListAccessor.getConfig().getList("players");

        return playerList;
    }

    /**
     * Init the list of players
     */
    public void initList()
    {
        try {
            playersListAccessor.reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (!playersListAccessor.getConfig().contains("players")) {
            List<String> playerList = new ArrayList<String>();
            playersListAccessor.getConfig().set("players", playerList);
            playersListAccessor.saveConfig();
        }
    }
}
