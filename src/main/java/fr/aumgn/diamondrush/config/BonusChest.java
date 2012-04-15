package fr.aumgn.diamondrush.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class BonusChest {

    private List<Bonus> contents = new ArrayList<Bonus>();

    public List<Bonus> getContents() {
        return Collections.unmodifiableList(contents);
    }

    public void add(Bonus bonus) {
        contents.add(bonus);
    }

    public ItemStack[] toItemStacks() {
        int size = Math.min(10, contents.size());
        ItemStack[] stacks = new ItemStack[size];
        int i = 0;
        for (Bonus bonus : contents) {
            if (i >= size) {
                break;
            }
            stacks[i] = bonus.toItemStack();
            i++;
        }
        return stacks;
    }
}