package net.kingdomsofarden.andrew2060.kraftrpg.skills;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.afterkraft.kraftrpg.api.RPGPlugin;
import com.afterkraft.kraftrpg.api.entity.Champion;
import com.afterkraft.kraftrpg.api.entity.IEntity;
import com.afterkraft.kraftrpg.api.entity.Insentient;
import com.afterkraft.kraftrpg.api.entity.SkillCaster;
import com.afterkraft.kraftrpg.api.entity.effects.ExpirableEffect;
import com.afterkraft.kraftrpg.api.skills.ActiveSkill;
import com.afterkraft.kraftrpg.api.skills.Skill;
import com.afterkraft.kraftrpg.api.skills.SkillCastResult;
import com.afterkraft.kraftrpg.api.skills.SkillSetting;
import com.afterkraft.kraftrpg.api.skills.SkillType;

public class SkillArcaneBarrage extends ActiveSkill {

    ConcurrentHashMap<SkillCaster,BarrageData> skillData;
    public SkillArcaneBarrage(RPGPlugin plugin) {
        super(plugin, "ArcaneBarrage");
        setDescription("Fires a linear barrage (16 blocks) of arcane artillery. "
                + "While in power locus, the user can define start point and direction. "
                + "While outside of power locus, user can define direction with start point being the user's location. "
                + "Artillery strikes also apply a 40% 5 second slow.");
        this.skillData = new ConcurrentHashMap<SkillCaster,BarrageData>();
        this.setSkillTypes(SkillType.DAMAGING, SkillType.DAMAGING, SkillType.ABILITY_PROPERTY_LIGHTNING, SkillType.SILENCING);
    }

    @Override
    public Collection<SkillSetting> getUsedConfigNodes() {
        return null;
    }

    private class BarrageData {
        private Location origin;
        private BukkitTask removalTask;
        private BukkitTask guiTask;

        public BarrageData(Location origin,BukkitTask removalTask, BukkitTask guiTask) {
            this.origin = origin;
            this.removalTask = removalTask;
            this.guiTask = guiTask;
        }
        private Location getOrigin() {
            return origin;
        }
        private void cancelTask() {
            removalTask.cancel();
            guiTask.cancel();
        }
    }
    private boolean fire(final Location origin, final Location vector, final SkillCaster hero) {
        if(!origin.getWorld().equals(vector.getWorld())) {
            return false;
        }
        final List<Location> targets = getAffectedLocations(origin,vector,false);
        for(int i = 0; i < targets.size(); i++) {
            final int runtime = i;
            Bukkit.getScheduler().runTaskLater(plugin, new BukkitRunnable() {

                @Override
                public void run() {
                    Location loc = targets.get(runtime);
                    // ClientEffectSender.strikeLightningNonGlobal(loc, 48);
                    applyDamage(loc,hero);
                }



            }, 10*i);
        }
        return true;
    }
    private void applyDamage(Location origin, SkillCaster caster) {
        Arrow a = origin.getWorld().spawn(origin, Arrow.class);
        for(Entity e : a.getNearbyEntities(3, 3, 3)) {
            if(!(e instanceof LivingEntity)) {
                continue;
            }
            LivingEntity lE = (LivingEntity)e;
            if (damageCheck(caster, lE)) {
                IEntity entity = plugin.getEntityManager().getEntity(lE);
                if (entity instanceof Insentient) {
                    Insentient being = (Insentient) entity;
                    if (being.hasEffect("ArcaneBarrageCooldown")) {
                        continue;
                    }
                    addSkillTarget(lE, caster);
                    Skill.damageEntity(lE, caster.getEntity(), 60.00, DamageCause.MAGIC);
                    // ToolHandlerPlugin.instance.getPotionEffectHandler().addPotionEffectStacking(PotionEffectType.SLOW.createEffect(100, 2), lE, false);
                    being.addEffect(new ExpirableEffect(this, caster, "ArcaneBarrageCooldown", 5000));
                }
            }
        }
        a.remove();
    }
    @SuppressWarnings("deprecation")
    @Override
    public SkillCastResult useSkill(final SkillCaster caster) {
        if(skillData.containsKey(caster)) {
            List<Block> los = caster.getEntity().getLastTwoTargetBlocks(null, 100);
            final Location loc = los.get(los.size()-1).getLocation();
            final BarrageData data = skillData.get(caster);
            fire(data.getOrigin(),loc,caster);
            data.cancelTask();
            skillData.remove(caster);
            return SkillCastResult.NORMAL;
        } else {
            List<Block> los;
            if(caster.hasEffect("PowerLocusEffect")) {
                los = caster.getEntity().getLastTwoTargetBlocks(null, 100);
                BarrageData data = new BarrageData(los.get(los.size()-1).getLocation(), new CancellationTask(caster).runTaskLater(plugin,400), new DisplayStrikeTask(caster).runTaskTimer(plugin, 0, 20));
                skillData.put(caster, data);
            } else {
                los = caster.getEntity().getLastTwoTargetBlocks(null, 16);
                fire(caster.getLocation(),los.get(los.size()-1).getLocation(),caster);
                return SkillCastResult.NORMAL;
            }
            return SkillCastResult.INVALID_TARGET;
        }
    }

    private class CancellationTask extends BukkitRunnable {

        private SkillCaster hero;
        public CancellationTask(SkillCaster h) {
            this.hero = h;
        }
        @Override
        public void run() {
            if(skillData.containsKey(hero)) {
                hero.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "Skill" + ChatColor.GRAY + "] Arcane Barrage Selection Cancelled");
                skillData.get(hero).guiTask.cancel();
                skillData.remove(hero);
            }
        }

    }

    private class DisplayStrikeTask extends BukkitRunnable {

        private SkillCaster caster;
        public DisplayStrikeTask(SkillCaster h) {
            this.caster = h;
        }
        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            if(skillData.containsKey(caster)) {
                Location origin = skillData.get(caster).getOrigin().clone().add(0,1,0);
                origin = origin.getWorld().getHighestBlockAt(origin).getLocation();
                List<Block> los = caster.getEntity().getLastTwoTargetBlocks(null, 100);
                Location vector = los.get(los.size()-1).getLocation();
                if(!origin.getWorld().equals(vector.getWorld())) {
                    try {
                        this.cancel();
                        caster.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "Skill" + ChatColor.GRAY + "] Arcane Barrage Selection Cancelled due to Changing Worlds");

                    } catch (IllegalStateException e) {
                        return;
                    }
                    return;
                }
                if (caster instanceof Champion) {
                    final Player p = ((Champion) caster).getPlayer();
                    for (final Location l : getAffectedLocations(origin, vector, true)) {
                        p.sendBlockChange(l, Material.GLOWSTONE.getId(), (byte) 0);
                        Bukkit.getScheduler().runTaskLater(plugin, new BukkitRunnable() {

                            @Override
                            public void run() {
                                Block replace = l.getWorld().getBlockAt(l);
                                p.sendBlockChange(l, replace.getTypeId(), replace.getData());
                            }

                        }, 19);
                    }
                }
            }
        }

    }

    private List<Location> getAffectedLocations(Location origin, Location vector, boolean validBlockCheck) {
        Vector multiplier = vector.toVector().subtract(origin.toVector()).setY(0.00).normalize();
        List<Location> toDisplay = new LinkedList<Location>();
        int originY = origin.getBlockY()+3;
        for(int i = 0; i < 16; i++) {
            Location blockLocation = origin.clone();
            for(int y = originY; y >= originY - 6; y--) {
                blockLocation.setY(y);
                if(blockLocation.getBlock().getType() != Material.AIR) {
                    break;
                }
            }
            if(!validBlockCheck ||(blockLocation.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR && blockLocation.getBlock().getType().isSolid())) {
                if(!toDisplay.contains(blockLocation.getBlock().getLocation())) {
                    toDisplay.add(blockLocation.getBlock().getLocation());
                }
            }
            origin.add(multiplier);
        }
        return toDisplay;
    }
}