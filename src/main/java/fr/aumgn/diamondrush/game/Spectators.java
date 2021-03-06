package fr.aumgn.diamondrush.game;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.entity.Player;

import fr.aumgn.bukkitutils.playerref.set.PlayersRefHashSet;
import fr.aumgn.bukkitutils.playerref.set.PlayersRefSet;

public class Spectators implements Iterable<Player> {

    private PlayersRefSet spectators;

    public Spectators() {
        spectators = new PlayersRefHashSet();
    }

    @Override
    public Iterator<Player> iterator() {
        return spectators.players().iterator();
    }

    public boolean contains(Player player) {
        return spectators.contains(player);
    }

    public boolean containsByName(Player player) {
        for (Player spectator : spectators.players()) {
            if (spectator.getName().equals(player.getName())) {
                return true;
            }
        }

        return false;
    }

    public void add(Player spectator) {
        spectators.add(spectator);
    }

    public void remove(Player spectator) {
        spectators.remove(spectator);
    }

    public Collection<Player> asCollection() {
        return spectators.getPlayers();
    }
}
