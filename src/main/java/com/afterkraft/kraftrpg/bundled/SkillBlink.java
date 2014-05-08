package com.afterkraft.kraftrpg.bundled;

import com.afterkraft.kraftrpg.api.RPGPlugin;
import com.afterkraft.kraftrpg.api.entity.Champion;
import com.afterkraft.kraftrpg.api.entity.SkillCaster;
import com.afterkraft.kraftrpg.api.skills.ActiveSkill;
import com.afterkraft.kraftrpg.api.skills.SkillCastResult;
import com.afterkraft.kraftrpg.api.skills.arguments.SkillArgument;
import com.afterkraft.kraftrpg.api.util.SkillRequirement;

public class SkillBlink extends ActiveSkill {

    public SkillBlink(RPGPlugin plugin, String name) {
        super(plugin, name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public SkillArgument parse(SkillCaster caster, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SkillCastResult useSkill(SkillCaster caster, SkillArgument argument) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SkillRequirement getSkillRequirement(SkillCaster caster) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasSkillRequirement(SkillRequirement requirement, SkillCaster caster) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean grantsExperienceOnCast() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub

    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isInMessageRange(Champion broadcaster, Champion receiver) {
        // TODO Auto-generated method stub
        return false;
    }

}
