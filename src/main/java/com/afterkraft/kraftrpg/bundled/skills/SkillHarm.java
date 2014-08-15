/*
 * Copyright 2014 Gabriel Harris-Rouquette
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afterkraft.kraftrpg.bundled.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.afterkraft.kraftrpg.api.RPGPlugin;
import com.afterkraft.kraftrpg.api.entity.IEntity;
import com.afterkraft.kraftrpg.api.entity.SkillCaster;
import com.afterkraft.kraftrpg.api.skills.Skill;
import com.afterkraft.kraftrpg.api.skills.SkillCastResult;
import com.afterkraft.kraftrpg.api.skills.SkillSetting;
import com.afterkraft.kraftrpg.api.skills.TargetedSkill;


public class SkillHarm extends TargetedSkill<LivingEntity> {
    public SkillHarm(RPGPlugin plugin) {
        super(plugin, "Harm", LivingEntity.class, 10);
        setDefault(SkillSetting.DAMAGE, 10);
    }

    @Override
    public SkillCastResult useSkill(SkillCaster caster, IEntity target, LivingEntity entity) {
        double damage = this.plugin.getSkillConfigManager().getUsedDoubleSetting(caster, this, SkillSetting.DAMAGE);
        Skill.damageEntity(entity, caster, damage, DamageCause.MAGIC);
        return SkillCastResult.NORMAL;
    }
}