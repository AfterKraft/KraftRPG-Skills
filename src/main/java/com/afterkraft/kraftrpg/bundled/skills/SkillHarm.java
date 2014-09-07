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
        Skill.damageEntity(entity, caster, this, damage, DamageCause.MAGIC);
        return SkillCastResult.NORMAL;
    }
}
