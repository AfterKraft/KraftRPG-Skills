package com.afterkraft.kraftrpg.bundled;

import org.bukkit.plugin.java.JavaPlugin;

import com.afterkraft.kraftrpg.api.ExternalProviderRegistration;
import com.afterkraft.kraftrpg.api.RPGPlugin;

public class BundledSkillsPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        RPGPlugin plugin = ExternalProviderRegistration.getPlugin();

        ExternalProviderRegistration.registerSkill("blink", new SkillBlink(plugin, "blink"));
    }
}
