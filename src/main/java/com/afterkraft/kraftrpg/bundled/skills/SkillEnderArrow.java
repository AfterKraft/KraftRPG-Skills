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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.afterkraft.kraftrpg.api.RPGPlugin;
import com.afterkraft.kraftrpg.api.effects.EffectType;
import com.afterkraft.kraftrpg.api.effects.common.ProjectileShotEffect;
import com.afterkraft.kraftrpg.api.entity.Insentient;
import com.afterkraft.kraftrpg.api.entity.SkillCaster;
import com.afterkraft.kraftrpg.api.skills.SkillSetting;
import com.afterkraft.kraftrpg.api.skills.SkillType;
import com.afterkraft.kraftrpg.api.skills.common.ArrowSkill;
import com.afterkraft.kraftrpg.bundled.utils.FireworksUtil;

/**
 * An ArrowSkill that attempts to teleport the shooter wherever the arrow lands. This is a clear
 * example of utilizing the provided common skills in KraftRPG-API to minimize writing extra
 * listeners and boilerplate code. These type of skills should be easy to read and easily
 * understood.
 *
 * Our objective with this skill is to hook when the SkillCaster shoots an Arrow (from {@link
 * ArrowSkill}, spawn a visual firework effect that trails the arrow, and finally, teleport the
 * shooter to the arrows landed location. For all intents and purposes, this should be a clear
 * example of how an {@link ArrowSkill} should be written. Extra listeners should not be required.
 *
 * Methods that are not needed to be overridden are: {@link #useSkill(SkillCaster)}.
 *
 * It is possible to apply customized settings for SkillCasters to designate, such as new skill
 * arguments; however, it should be noted that normally these skills are single use.
 */
public class SkillEnderArrow extends ArrowSkill {

    /**
     * Creates the skill.
     *
     * @param plugin The RPGPlugin implementation
     */
    public SkillEnderArrow(RPGPlugin plugin) {
        super(plugin, "EnderArrow");
        // We always have a description for when skills are listed
        setDescription("Shooting an enchanted arrow that teleports the shooter to the landing "
                               + "location.");
        // Since this is a teleporting skill, we should note it
        setSkillTypes(SkillType.TELEPORTING, SkillType.UNINTERRUPTIBLE, SkillType.UNSILENCABLE);

        // Setting our default values. Remember, we can't fetch any configured values unless they
        // are written as defaults as well. KraftRPG WILL yell and scream if a skill is
        // attempting to fetch a skill setting without having noted the defaults before hand.
        // NOTE: We do not need to set the custom skill setting for "max-uses" as KraftRPG-API's
        // ArrowSkill does that for us.
        // This is specifically when the arrow is shot.
        setDefault(SkillSetting.APPLY_TEXT, "You shoot a mystical arrow bending time and space...");
        // Yet again, another setting. For when the player is teleported.
        setDefault(SkillSetting.EXPIRE_TEXT, "Your arrow has moved you to your location");
    }

    // Since we want to apply our own custom ProjectileShotEffect, we need to override this
    // method and fetch any necessary configuration settings.
    @Override
    public void addImbueEffect(SkillCaster caster, int maxUses) {
        // We need to actually get the configured settings. In this case, we're getting the
        // configured application text, expiration text, and maximum shots text.
        String applyText = this.plugin.getSkillConfigManager()
                .getUsedStringSetting(caster, this, SkillSetting.APPLY_TEXT);
        String expireText = this.plugin.getSkillConfigManager()
                .getUsedStringSetting(caster, this, SkillSetting.EXPIRE_TEXT);

        // We now construct our new effect for the caster
        EnderArrowShotEffect effect = new EnderArrowShotEffect(this, caster, maxUses, applyText,
                                                               expireText);
        // Add the effect to our caster
        caster.addEffect(effect);
    }

    // This projectile effect is an extension to avoid having to write all the internal stuff to
    // implement an IEffect. We can further customize a few things accordingly.
    class EnderArrowShotEffect extends ProjectileShotEffect {
        // We want to store the runnable that we'll use to shoot fireworks on our arrows. We also
        // don't want to use the actual objects because we don't need to.
        private Map<UUID, Integer> projectileFireworkMap = new HashMap<UUID, Integer>();

        public EnderArrowShotEffect(SkillEnderArrow skill, Insentient applier, int shots,
                                    String applyText,
                                    String expireText) {
            // We want to keep our effect uniquely named to avoid confusion with other effects.
            super(skill, applier, "EnderArrowShot",
                  ImmutableSet.<PotionEffect>of(), false,
                  // We can apply a Jump Boos effect type to avoid fall damage if damage is taken
                  // immediately upon teleporting. We also might apply an expirable effect
                  // afterwards that grants the shooter some sort of safe-fall for a few seconds
                  // after teleportation.
                  ImmutableList.of(EffectType.JUMP_BOOST),
                  applyText,
                  expireText, shots);
        }

        @Override
        public boolean applyToProjectile(Projectile projectile) {
            // We must call the super to reduce the uses of this effect, otherwise we risk
            // allowing the caster to shoot unlimited Imbued shots!
            if (!super.applyToProjectile(projectile)) {
                return false;
            }
            // We construct our new runnable
            BukkitRunnable runnable = new EnderArrowShotRunnable(projectile);
            // Get our task id
            int id = runnable.runTaskTimer(SkillEnderArrow.this.plugin, 0, 2).getTaskId();
            // And store it into our map for retrieval later.
            this.projectileFireworkMap.put(projectile.getUniqueId(), id);
            return true;
        }

        @Override
        public void onProjectileLand(Projectile projectile, Location location) {
            // We can get our shooter from the getApplier() method.
            Insentient applier = getApplier();
            // Cancel the repeating task from our map
            int id = this.projectileFireworkMap.remove(projectile.getUniqueId());
            Bukkit.getScheduler().cancelTask(id);
            applier.teleport(location, true);
        }
    }

    // We have our runnable to play a firework effect. We can probably delegate this out to the
    // FireworksUtil at some point, but for now, let's keep as much in this skill class as possible.
    class EnderArrowShotRunnable extends BukkitRunnable {
        private Projectile arrow;

        private EnderArrowShotRunnable(Projectile arrow) {
            this.arrow = arrow;
        }

        @Override
        public void run() {
            // We don't want to do anything if the arrow is invalid or on the ground. Just some
            // validation.
            if (this.arrow.isValid() && !this.arrow.isOnGround()) {
                // We need our desired FireworkEffect
                FireworkEffect effect = FireworkEffect.builder()
                        .withFlicker()
                        .withColor(Color.BLACK)
                        .withFade(Color.GRAY)
                        .with(Type.BALL)
                        .build();
                // Get the utility to spawn the firework
                FireworksUtil.playFireworkEffect(this.arrow.getLocation(), effect);
                // Just some added particles for show
                this.arrow.getWorld().playEffect(this.arrow.getLocation(),
                                                 Effect.ENDER_SIGNAL, 0);
            }
        }
    }
}
