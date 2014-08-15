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

import com.afterkraft.kraftrpg.api.RPGPlugin;
import com.afterkraft.kraftrpg.api.entity.SkillCaster;
import com.afterkraft.kraftrpg.api.skills.ActiveSkill;
import com.afterkraft.kraftrpg.api.skills.SkillCastResult;
import com.afterkraft.kraftrpg.api.skills.SkillSetting;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

public class SkillBandage extends ActiveSkill {
    public SkillBandage(RPGPlugin plugin) {
        super(plugin, "Bandage");
        setDefault(SkillSetting.HEALING, 1.0D);
        setDefault(SkillSetting.REAGENT, new ItemStack(Material.PAPER));
    }

    @Override
    public SkillCastResult useSkill(SkillCaster caster) {
        double healAmount = this.plugin.getSkillConfigManager().getUsedDoubleSetting(caster, this, SkillSetting.HEALING);
        caster.setHealth(caster.getHealth() + healAmount);
        return SkillCastResult.NORMAL;
    }
}