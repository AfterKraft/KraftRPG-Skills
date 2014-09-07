/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Gabriel Harris-Rouquette
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.afterkraft.kraftrpg.bundled.skills;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.afterkraft.kraftrpg.api.RPGPlugin;
import com.afterkraft.kraftrpg.api.entity.IEntity;
import com.afterkraft.kraftrpg.api.entity.Insentient;
import com.afterkraft.kraftrpg.api.entity.SkillCaster;
import com.afterkraft.kraftrpg.api.events.entity.damage.InsentientDamageEvent.DamageType;
import com.afterkraft.kraftrpg.api.skills.Skill;
import com.afterkraft.kraftrpg.api.skills.SkillCastResult;
import com.afterkraft.kraftrpg.api.skills.SkillSetting;
import com.afterkraft.kraftrpg.api.skills.SkillType;
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
        setDescription("Strikes down lightning strikes at the targeted entity dealing damage and striking down entities near the target.");
        setSkillTypes(SkillType.DAMAGING, SkillType.ABILITY_PROPERTY_LIGHTNING, SkillType.AREA_OF_EFFECT, SkillType.AGGRESSIVE);
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

        Skill.damageEntity((Insentient) target, caster, this, ImmutableMap.of(DamageType.MAGICAL, damage), DamageCause.MAGIC);

        List<Entity> entities = target.getNearbyEntities(radius, radius, radius);
        for (Entity nearbyEntity : entities) {
            if (!(nearbyEntity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity nearbyLivingEntity = (LivingEntity) nearbyEntity;
            Insentient insentient = (Insentient) this.plugin.getEntityManager().getEntity(nearbyLivingEntity);
            if (!damageCheck(caster, nearbyLivingEntity)) {
                continue;
            }

            nearbyLivingEntity.getWorld().strikeLightning(nearbyLivingEntity.getLocation());
            nearbyLivingEntity.getWorld().playSound(nearbyLivingEntity.getLocation(), Sound.AMBIENCE_THUNDER, volume, 1.0F);
            addSkillTarget(nearbyEntity, caster);
            Skill.damageEntity(insentient, caster, this, ImmutableMap.of(DamageType.MAGICAL, damage), DamageCause.MAGIC);
        }

        return null;
    }
}
