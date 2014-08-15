package com.afterkraft.kraftrpg.bundled.skills;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.afterkraft.kraftrpg.api.RPGPlugin;
import com.afterkraft.kraftrpg.api.entity.IEntity;
import com.afterkraft.kraftrpg.api.entity.SkillCaster;
import com.afterkraft.kraftrpg.api.skills.Skill;
import com.afterkraft.kraftrpg.api.skills.SkillCastResult;
import com.afterkraft.kraftrpg.api.skills.SkillSetting;
import com.afterkraft.kraftrpg.api.skills.TargetedSkill;
import com.afterkraft.kraftrpg.bundled.CustomSkillSettings;

public class SkillMegabolt extends TargetedSkill<LivingEntity> {
    protected SkillMegabolt(RPGPlugin plugin) {
        super(plugin, "MegaBolt", LivingEntity.class, 20);
        setDefault(SkillSetting.MAX_DISTANCE, 20);
        setDefault(SkillSetting.DAMAGE, 100, 10);
        setDefault(SkillSetting.RADIUS, 10);
        setDefault(SkillSetting.REAGENT, new ItemStack(Material.SULPHUR));
        setDefault(CustomSkillSettings.LIGHTNING_VOLUME, 1.0F);
    }

    @Override
    public SkillCastResult useSkill(SkillCaster caster, IEntity target, LivingEntity entity) {
        int radius = this.plugin.getSkillConfigManager().getUsedIntSetting(caster, this, SkillSetting.RADIUS);
        double damage = this.plugin.getSkillConfigManager().getUsedDoubleSetting(caster, this, SkillSetting.DAMAGE);
        double damageIncrease = this.plugin.getSkillConfigManager().getUsedDoubleSetting(caster, this, SkillSetting.DAMAGE.scalingNode());
        damage += (damageIncrease * caster.getLevel(caster.getPrimaryRole()));

        float volume = (float) this.plugin.getSkillConfigManager().getUsedDoubleSetting(caster, this, CustomSkillSettings.LIGHTNING_VOLUME);
        target.getWorld().strikeLightning(target.getLocation());
        target.getWorld().playSound(target.getLocation(), Sound.AMBIENCE_THUNDER, volume, 1.0F);

        Skill.damageEntity(entity, caster, damage, DamageCause.MAGIC);

        List<Entity> entities = target.getNearbyEntities(radius, radius, radius);
        for (Entity nearbyEntity: entities) {
            if (!(nearbyEntity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity nearbyLivingEntity = (LivingEntity) nearbyEntity;
            if (!damageCheck(caster, nearbyLivingEntity)) {
                continue;
            }

            nearbyLivingEntity.getWorld().strikeLightning(nearbyLivingEntity.getLocation());
            nearbyLivingEntity.getWorld().playSound(nearbyLivingEntity.getLocation(), Sound.AMBIENCE_THUNDER, volume, 1.0F);
            addSkillTarget(nearbyEntity, caster);
            Skill.damageEntity(nearbyLivingEntity, caster, damage, DamageCause.MAGIC);
        }

        return null;
    }
}
