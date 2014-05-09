package com.afterkraft.kraftrpg.bundled;

import org.bukkit.entity.LivingEntity;

import com.afterkraft.kraftrpg.api.RPGPlugin;
import com.afterkraft.kraftrpg.api.entity.Champion;
import com.afterkraft.kraftrpg.api.entity.SkillCaster;
import com.afterkraft.kraftrpg.api.skills.ActiveSkill;
import com.afterkraft.kraftrpg.api.skills.SkillArgument;
import com.afterkraft.kraftrpg.api.skills.SkillCastResult;
import com.afterkraft.kraftrpg.api.skills.arguments.EntitySkillArgument;
import com.afterkraft.kraftrpg.api.util.ItemSkillRequirement;

public class SkillHarm extends ActiveSkill {

    public SkillHarm(RPGPlugin plugin, String name) {
        super(plugin, name);
        setSkillArguments(new EntitySkillArgument<LivingEntity>(10.0, LivingEntity.class, null));
    }

    @Override
    public SkillCastResult useSkill(SkillCaster caster) {
        this.<EntitySkillArgument<LivingEntity>> getArgument(0).getMatchedEntity().damage(5d, caster.getEntity());
        return SkillCastResult.NORMAL;
    }

    @Override
    public SkillCastResult canCast(SkillCaster caster, boolean forced) {
        return SkillCastResult.NORMAL;
    }

    @Override
    public boolean grantsExperienceOnCast() {
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
        // XXX pull up
        return false;
    }

    @Override
    public SkillCastResult canUse(SkillCaster caster) {
        // TODO
        return SkillCastResult.NORMAL;
    }

}
