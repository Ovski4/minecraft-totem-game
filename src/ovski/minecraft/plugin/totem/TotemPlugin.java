package ovski.minecraft.plugin.totem;

import java.io.FileNotFoundException;

import org.bukkit.plugin.java.JavaPlugin;

import ovski.minecraft.manager.YamlPlayersManager;
import ovski.minecraft.manager.YamlTeamsManager;
import ovski.minecraft.plugin.totem.commands.*;
import ovski.minecraft.plugin.totem.events.OnBlockBreak;
import ovski.minecraft.plugin.totem.events.OnBlockPlace;
import ovski.minecraft.plugin.totem.events.OnPlayerJoin;
import ovski.minecraft.plugin.totem.events.OnPlayerRespawn;

/*
 * TODO
 * database?
 * stop a game
 */

/**
 * TotemPlugin
 * 
 * The main class of the plugin
 *
 * @author baptiste <baptiste.bouchereau@gmail.com>
 */
public class TotemPlugin extends JavaPlugin
{
    private YamlTeamsManager teamsManager = new YamlTeamsManager();
    private YamlPlayersManager playersManager = new YamlPlayersManager();

    @Override
    public void onDisable()
    {
        System.out.println("Totem plugin is disabled");
        //reload configs file to be sure everything is saved
        YamlTeamsManager ytm = new YamlTeamsManager();
        try {
            ytm.getTeamsAccessor().reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        YamlPlayersManager ypm = new YamlPlayersManager();
        try {
            ypm.getPlayersListAccessor().reloadConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable()
    {
        System.out.println("Totem plugin is enabled");
        listenToEvents();
        getCommands();
        initYaml();
    }

    /**
     * Listen to events
     */
    public void listenToEvents()
    {
        new OnPlayerJoin(this);
        new OnPlayerRespawn(this);
        new OnBlockBreak(this);
        new OnBlockPlace(this);
    }

    /**
     * Get the commands
     */
    public void getCommands() {
        getCommand("start").setExecutor(new StartGameCommand(this));
        getCommand("team").setExecutor(new TeamCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("scores").setExecutor(new ScoresCommand());
    }

    /**
     * Init yaml files
     */
    public void initYaml() {
        teamsManager.initNextGameId();
        playersManager.initList();
    }
}