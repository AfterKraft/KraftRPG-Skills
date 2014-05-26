package com.afterkraft.kraftrpg.bundled;

import com.afterkraft.kraftrpg.api.RPGPlugin;
import com.afterkraft.kraftrpg.api.entity.SkillCaster;
import com.afterkraft.kraftrpg.api.skills.ActiveSkill;
import com.afterkraft.kraftrpg.api.skills.SkillCastResult;
import com.afterkraft.kraftrpg.api.skills.SkillSetting;

import java.util.Collection;
import java.util.EnumSet;

public class SkillBandage extends ActiveSkill {
    public SkillBandage(RPGPlugin plugin) {
        super(plugin, "Bandage");
    }

    @Override
    public Collection<SkillSetting> getUsedConfigNodes() {
        return EnumSet.of(SkillSetting.REAGENT, SkillSetting.HEALING);
    }

    @Override
    public SkillCastResult useSkill(SkillCaster caster) {
        caster.setHealth(caster.getHealth() + plugin.getSkillConfigManager().getUseSetting(caster, this, SkillSetting.HEALING, 1.0, false));
        return SkillCastResult.NORMAL;
    }

    @Override
    public boolean grantsExperienceOnCast() {
        return false;
    }
}
