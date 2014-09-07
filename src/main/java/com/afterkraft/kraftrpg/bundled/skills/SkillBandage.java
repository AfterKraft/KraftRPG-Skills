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
