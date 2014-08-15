package com.afterkraft.kraftrpg.bundled;

import com.afterkraft.kraftrpg.api.skills.SkillSetting;

public class CustomSkillSettings extends SkillSetting {

    public static final CustomSkillSettings PLANT_RADIUS = new CustomSkillSettings("plant-radius");
    public static final CustomSkillSettings LIGHTNING_VOLUME = new CustomSkillSettings("lightning-volume");

    protected CustomSkillSettings(String node) {
        super(node);
    }

    protected CustomSkillSettings(String node, boolean scaled) {
        super(node, scaled);
    }
}
