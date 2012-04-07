package fr.aumgn.tobenamed.stage;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import fr.aumgn.tobenamed.game.Game;
import fr.aumgn.tobenamed.stage.listeners.DevelopmentListener;

public class DevelopmentStage extends Stage {

    private Game game;
    private DevelopmentListener listener;

    public DevelopmentStage(Game game) {
        this.game = game;
        this.listener = new DevelopmentListener(this);
    }

    @Override
    public List<Listener> getListeners() {
        return Collections.<Listener>singletonList(listener);
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public void start() {
        game.sendMessage(ChatColor.GREEN + "La phase de développement commence.");
        scheduleNextStage(600, new Runnable() {
            public void run() {
                game.nextStage(new FightStage(game));
            }
        });
    }
}
