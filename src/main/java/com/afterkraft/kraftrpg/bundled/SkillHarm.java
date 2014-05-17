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
package com.afterkraft.kraftrpg.bundled;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.afterkraft.kraftrpg.api.RPGPlugin;
import com.afterkraft.kraftrpg.api.entity.SkillCaster;
import com.afterkraft.kraftrpg.api.skills.ActiveSkill;
import com.afterkraft.kraftrpg.api.skills.Skill;
import com.afterkraft.kraftrpg.api.skills.SkillCastResult;
import com.afterkraft.kraftrpg.api.skills.arguments.EntitySkillArgument;


public class SkillHarm extends ActiveSkill {
    public SkillHarm(RPGPlugin plugin, String name) {
        super(plugin, name);
        setSkillArguments(new EntitySkillArgument<LivingEntity>(10.0, LivingEntity.class, null));
    }

    @Override
    public SkillCastResult useSkill(SkillCaster caster) {
        Skill.damageEntity(this.<EntitySkillArgument<LivingEntity>> getArgument(0).getMatchedEntity(), caster, 5.0, DamageCause.MAGIC);
        return SkillCastResult.NORMAL;
    }

    @Override
    public boolean grantsExperienceOnCast() {
        return false;
    }
}
