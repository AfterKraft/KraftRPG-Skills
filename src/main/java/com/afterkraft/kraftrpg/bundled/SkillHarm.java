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

import com.afterkraft.kraftrpg.api.entity.IEntity;
import com.afterkraft.kraftrpg.api.entity.PartyMember;
import com.afterkraft.kraftrpg.api.skills.SkillSetting;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.afterkraft.kraftrpg.api.RPGPlugin;
import com.afterkraft.kraftrpg.api.entity.SkillCaster;
import com.afterkraft.kraftrpg.api.skills.ActiveSkill;
import com.afterkraft.kraftrpg.api.skills.Skill;
import com.afterkraft.kraftrpg.api.skills.SkillCastResult;
import com.afterkraft.kraftrpg.api.skills.arguments.EntitySkillArgument;

import java.util.Collection;
import java.util.EnumSet;


public class SkillHarm extends ActiveSkill {
    public SkillHarm(RPGPlugin plugin) {
        super(plugin, "Harm");
        setSkillArguments(new EntitySkillArgument<LivingEntity>(10.0, LivingEntity.class, null));
    }

    @Override
    public Collection<SkillSetting> getUsedConfigNodes() {
        return EnumSet.of(SkillSetting.DAMAGE, SkillSetting.MAX_DISTANCE);
    }

    @Override
    public SkillCastResult useSkill(SkillCaster caster) {
        LivingEntity target = this.<EntitySkillArgument<LivingEntity>> getArgument(0).getMatchedEntity();

        double distance = plugin.getSkillConfigManager().getUseSetting(caster, this, SkillSetting.MAX_DISTANCE, 10.0, true);
        if (target == null || target.getLocation().distance(caster.getLocation()) > distance) {
            return SkillCastResult.INVALID_TARGET;
        }

        IEntity apiEntity = plugin.getEntityManager().getEntity(target);
        if (apiEntity instanceof PartyMember) {
            if (plugin.getPartyManager().isFriendly(caster, (PartyMember) apiEntity)) {
                caster.sendMessage(apiEntity.getName() + " is friendly to you! Cannot hurt them.");
                return SkillCastResult.CUSTOM_NO_MESSAGE_FAILURE;
            }
        }

        Skill.damageEntity(target, caster,
                plugin.getSkillConfigManager().getUseSetting(caster, this, SkillSetting.DAMAGE, 5.0, false),
                DamageCause.MAGIC);
        return SkillCastResult.NORMAL;
    }
}
