package fr.aumgn.tobenamed.stage;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import fr.aumgn.tobenamed.game.Game;
import fr.aumgn.tobenamed.game.Team;
import fr.aumgn.tobenamed.util.TBNUtil;

public abstract class JoinStage extends Stage {

    protected Game game;

    public JoinStage(Game game) {
        this.game = game;
    }

    @Override
    public List<Listener> getListeners() {
        return Collections.<Listener>emptyList();
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public void start() {
        TBNUtil.broadcast(ChatColor.YELLOW + "Une nouvelle partie de TBN va commencer !");
        TBNUtil.broadcast(ChatColor.YELLOW + "Equipes : ");
        for (Team team : game.getTeams()) {
            TBNUtil.broadcast(" - " + team.getDisplayName());
        }
    }

    public abstract void addPlayer(Player player, Team team);

    public abstract boolean contains(Player player);

    public abstract void ensureIsReady();

    public void prepare() {
    }
}
