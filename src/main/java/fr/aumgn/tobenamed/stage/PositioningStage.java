package fr.aumgn.tobenamed.stage;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.aumgn.tobenamed.game.Game;
import fr.aumgn.tobenamed.game.Team;
import fr.aumgn.tobenamed.stage.listeners.PositioningListener;
import fr.aumgn.tobenamed.util.Vector;

public abstract class PositioningStage extends Stage {

    protected Game game;
    private Map<Team, Vector> positions;
    private Listener listener;

    public PositioningStage(Game game) {
        this.game = game;
        this.positions = new HashMap<Team, Vector>();
        this.listener = new PositioningListener(this, positions);
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
        giveBlocks();
    }

    @Override
    public void stop() {
        for (Team team : game.getTeams()) {
            Vector pos = getPosition(team);
            initPosition(team, pos);
        }
        removeBlocksFromInventories();
        removeBlocksFromWorld();
    }

    public Vector getPosition(Team team) {
        Vector pos = positions.get(team);
        if (pos == null) {
            Player foreman = team.getForeman();
            pos = new Vector(foreman.getLocation());
            foreman.teleport(pos.add(0, 0, 1).toPlayerLocation(game.getWorld()));
        }
        return pos;
    }

    public void clearPositions() {
        positions.clear();
    }

    protected void giveBlocks() {
        for (Team team : game.getTeams()) {
            Player player = team.getForeman();
            player.setItemInHand(new ItemStack(getMaterial(), 1));
        }
    }

    protected void removeBlocksFromWorld() {
        for (Vector pos : positions.values()) {
            Block block = pos.toBlock(game.getWorld());
            if (block.getType() == getMaterial()) {
                block.setType(Material.AIR);
            }
        }
    }

    protected void removeBlocksFromInventories() {
        for (Team team : game.getTeams()) {
            for (Player player : team.getPlayers()) {
                Inventory inventory = player.getInventory();
                int index = inventory.first(getMaterial());
                if (index != -1) {
                    ItemStack stack = inventory.getItem(index);
                    if (stack.getAmount() > 1) {
                        stack.setAmount(stack.getAmount() - 1);
                    } else {
                        inventory.clear(index);
                    }
                    return;
                }
            }
        }
    }

    public abstract void initPosition(Team team, Vector vector);

    public abstract Material getMaterial();
}
