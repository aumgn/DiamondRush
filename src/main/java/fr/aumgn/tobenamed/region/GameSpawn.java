package fr.aumgn.tobenamed.region;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

import fr.aumgn.tobenamed.region.patterns.FloorPattern;
import fr.aumgn.tobenamed.util.Vector;

public class GameSpawn extends Region {

    public GameSpawn(Vector pt) {
        super(pt.subtract(3, 1, 3), pt.add(3, 5, 3));
    }

    public List<Vector> getDirections(int size) {
        List<Vector> list = new ArrayList<Vector>(size);
        list.add(min.add(3, 1, 0));
        list.add(min.add(3, 1, 6));

        if (list.size() > 2) {
            list.add(min.add(6, 1, 0));
        }
        if (list.size() > 3) {
            list.add(min.add(6, 1, 3));
        }

        Vector middle = min.getMiddle(max);
        for (int i = 4; i < size; i++) {
            list.add(middle);
        }
        return list;
    }

    public void create(World world) {
        FloorPattern base = new FloorPattern(
                min.to2D(), max.to2D(), min.getY());
        removeEverythingAbove(world, min.getY() + 1);
        base.create(world);
    }
}
