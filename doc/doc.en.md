The Totem game
===============

## Requirements ##

This plugin was tested on a bukkit 1.5.2. server.

## Installation ##

  * Put the .jar file (available in the /docs directory) in the /plugin directory of your bukkit server.
  * Create a "Yaml" directory in the /plugin directory.
  * Create totemTeams.yml and playerList.yml files in this directory
  * Restart your bukkit serveur.

That's it!

## Game rules ##

This game starts with 2 teams or more. When a game is launched, teams are teleported to their spawns. Then they have 2 minutes to run. At the end of that time, totems (3 blocks high) will appear at the team leader location.  

Then, the purpose of this game is to find enemy totems and bring back enough blocks on his own totem to win the game.

## Commands ##

A list of commands to manage teams and start games is available.

Team management
  * /team create teamName
  * /team add playerName
  * /team leader playerName
  * /team remove playerName
  * /team spawn
  * /team leave
  * /team disband

Spawn teleportation
  * /spawn

Launching a game
  * /start numberOfBlocksToRetrieveToWin team1 team2 team3..

Display scores during a game
  * /scores

