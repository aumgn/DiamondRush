package fr.aumgn.diamondrush.stage.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.aumgn.diamondrush.DiamondRush;
import fr.aumgn.diamondrush.game.Game;
import fr.aumgn.diamondrush.game.Team;
import fr.aumgn.diamondrush.stage.FightStage;

public class FightListener implements Listener {

    private DiamondRush dr;
    private FightStage stage;
    private Set<Player> pvpDeaths;

    public FightListener(DiamondRush dr, FightStage stage) {
        this.dr = dr;
        this.stage = stage;
        this.pvpDeaths = new HashSet<Player>();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        EntityDamageEvent damageCause = player.getLastDamageCause();
        if (!(damageCause instanceof EntityDamageByEntityEvent)) {
            return;
        }

        Entity damagerEntity = ((EntityDamageByEntityEvent) damageCause).getDamager();
        if (damagerEntity instanceof Projectile) {
            damagerEntity = ((Projectile) damagerEntity).getShooter();
        }
        if (!(damagerEntity instanceof Player)) {
            return;
        }

        Player damager = (Player) damagerEntity;
        Game game = dr.getGame();
        if (!game.contains(damager) || !game.contains(player)) {
            return;
        }

        Team team = game.getTeam(player);
        Team killerTeam = game.getTeam(damager);
        if (team == killerTeam) {
            return;
        }

        stage.onDeath(killerTeam, team, player);
        if (stage.getKills(killerTeam) > dr.getConfig().getMaxDiamond()) {
            event.getDrops().add(dr.getConfig().getItemForKill());
        } else {
            event.getDrops().add(new ItemStack(Material.DIAMOND));
        }
        pvpDeaths.add(player);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(dr.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (pvpDeaths.contains(player)) {
                    player.getInventory().addItem(dr.getConfig().getItemForDeath());
                    affectPlayer(player);
                    pvpDeaths.remove(player);
                }
            }
        }, 0);
    }

    private void affectPlayer(Player player) {
        int duration = (stage.getDeaths(player) - 1)
                * dr.getConfig().getDeathMalusDuration();
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getType() == PotionEffectType.SLOW) {
                duration += potionEffect.getDuration();
                PotionEffect newEffect = new PotionEffect(
                        PotionEffectType.SLOW, duration, 10);
                player.addPotionEffect(newEffect, true);
                return;
            }
        }
        PotionEffect newEffect = new PotionEffect(
                PotionEffectType.SLOW, duration, 10);
        player.addPotionEffect(newEffect);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Game game = dr.getGame();
        if (!game.contains(player)) {
            return;
        }

        ItemStack item = player.getItemInHand();
        int type = item.getTypeId();
        if (type != dr.getConfig().getSurrenderItem()) {
            return;
        }

        if (!stage.canSurrender()) {
            player.sendMessage("Une équipe s'est déjà rendue.");
            return;
        }

        Team team = game.getTeam(player);
        if (stage.getDeaths(team) >= dr.getConfig().getDeathNeededForSurrender()) {
            if (item.getAmount() == 1) {
                player.setItemInHand(new ItemStack(0));
            } else {
                item.setAmount(item.getAmount() - 1);
                player.setItemInHand(item);
            }
            stage.surrender(team);
        } else {
            player.sendMessage(ChatColor.RED + 
                    "Il faut au moins une mort (PvP) dans l'équipe pour pouvoir se rendre.");
        }
    }
}
