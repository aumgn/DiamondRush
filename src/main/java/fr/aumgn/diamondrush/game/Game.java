package fr.aumgn.diamondrush.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import fr.aumgn.bukkitutils.util.Vector;
import fr.aumgn.diamondrush.Util;
import fr.aumgn.diamondrush.exception.NoSuchTeam;
import fr.aumgn.diamondrush.exception.NotEnoughTeams;
import fr.aumgn.diamondrush.exception.PlayerNotInGame;
import fr.aumgn.diamondrush.region.GameSpawn;

public class Game {

    private World world;
    private GameSpawn spawn;
    private Map<String, Team> teams;
    private Map<Player, Team> players;
    private Spectators spectators;
    private int turnCount;

    public Game(Map<String, TeamColor> teamsMap, World world, Vector spawnPoint, int lives) {
        this.teams = new LinkedHashMap<String, Team>();

        lives = Math.max(4, lives);
        lives = Math.min(8, lives);
        for (Map.Entry<String, TeamColor> teamEntry : teamsMap.entrySet()) {
            Team team = new Team(teamEntry.getKey(),
                    teamEntry.getValue(), lives);
            teams.put(teamEntry.getKey(), team);
        }
        if (teams.keySet().size() < 2) {
            throw new NotEnoughTeams();
        }
        this.world = world;
        spawn = new GameSpawn(spawnPoint);
        players = new HashMap<Player, Team>();
        spectators = new Spectators();
        turnCount = -1;
    }

    public World getWorld() {
        return world;
    }

    public GameSpawn getSpawn() {
        return spawn;
    }

    public Team getTeam(String name) {
        if (!teams.containsKey(name)) {
            throw new NoSuchTeam(name);
        }
        return teams.get(name);
    }

    public Team getTeam(Player player) {
        if (!players.containsKey(player)) {
            throw new PlayerNotInGame();
        }
        return players.get(player);
    }

    public Spectators getSpectators() {
        return spectators;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void incrementTurnCount() {
        ++turnCount;
    }

    public void sendMessage(String message) {
        for (Team team : getTeams()) {
            team.sendMessage(message);
        }
        for (Player spectator : spectators) {
            spectator.sendMessage(message);
        }
    }

    public boolean contains(Player player) {
        return players.containsKey(player);
    }

    public List<Team> getTeams() {
        return new ArrayList<Team>(teams.values());
    }

    public Team getTeamWithMinimumPlayers() {
        int minimum = Integer.MAX_VALUE;
        List<Team> roulette = null; 
        for (Team team : getTeams()) {
            int size = 0;
            for (Player player : team.getPlayers()) {
                if (player.isOnline()) {
                    size++;
                }
            }

            if (size < minimum) {
                minimum = size;
                roulette = new ArrayList<Team>();
                roulette.add(team);
            } else if (size == minimum) {
                roulette.add(team);
            } 
        }

        return Util.pickRandom(roulette);
    }

    public void addPlayer(Player player, Team team) {
        team.addPlayer(player, world);
        players.put(player, team);
    }

    public void removePlayer(Player player) {
        Team team = getTeam(player);
        team.removePlayer(player);
        players.remove(player);
    }

    public void removeTeam(Team team) {
        teams.remove(team.getName());
        for (Player player : team.getPlayers()) {
            players.remove(player);
            spectators.add(player);
            player.sendMessage(ChatColor.GREEN + 
                    "Vous êtes maintenant spectateur.");
        }
    }
}
