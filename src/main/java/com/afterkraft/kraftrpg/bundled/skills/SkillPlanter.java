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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.afterkraft.kraftrpg.api.RPGPlugin;
import com.afterkraft.kraftrpg.api.entity.SkillCaster;
import com.afterkraft.kraftrpg.api.skills.ActiveSkill;
import com.afterkraft.kraftrpg.api.skills.SkillCastResult;
import com.afterkraft.kraftrpg.bundled.CustomSkillSettings;


public class SkillPlanter extends ActiveSkill {

    public SkillPlanter(RPGPlugin plugin) {
        super(plugin, "Planter");
        setDescription("Plant the Ground");
        setDefault(CustomSkillSettings.PLANT_RADIUS, 2);
    }

    @Override
    public SkillCastResult useSkill(SkillCaster caster) {
        int radius = this.plugin.getSkillConfigManager().getUsedIntSetting(caster, this, CustomSkillSettings.PLANT_RADIUS);
        final World world = caster.getWorld();
        final int x = caster.getLocation().getBlockX();
        final int y = caster.getLocation().getBlockY();
        final int z = caster.getLocation().getBlockZ();
        final int minX = x - radius;
        final int minZ = z - radius;
        final int maxX = x + radius;
        final int maxZ = z + radius;
        int carrotsPlanted = 0;
        int potatoesPlanted = 0;
        int count = 0;
        int count2 = 0;

        if (caster.isOnGround()) {
            for (int counterX = minX; counterX <= maxX; counterX++) {
                for (int counterZ = minZ; counterZ <= maxZ; counterZ++) {
                    final Block blockName = world.getBlockAt(counterX, y, counterZ);
                    final Block lowerblock = world.getBlockAt(counterX, y - 1, counterZ);
                    final Location lowerLoc = lowerblock.getLocation();
                    final Location locBlock = blockName.getLocation();
                    if (caster.getItemInHand().getType().equals(Material.SEEDS)) {
                        if (caster.getInventory().contains(Material.SEEDS)) {
                            if (locBlock.getBlock().getType().equals(Material.AIR) && lowerLoc.getBlock().getType().equals(Material.SOIL)) {
                                locBlock.getBlock().setType(Material.CROPS);
                                caster.getInventory().removeItem(new ItemStack(Material.SEEDS, 1));
                                caster.updateInventory();
                            } else {
                                count2++;
                            }
                        } else {
                            count++;
                        }
                    }
                    if (caster.getItemInHand().getType().equals(Material.CARROT_ITEM)) {
                        if (locBlock.getBlock().getType().equals(Material.AIR) && lowerLoc.getBlock().getType().equals(Material.SOIL)) {
                            locBlock.getBlock().setType(Material.CARROT);
                            carrotsPlanted += 1;
                        } else {
                            count2++;
                        }
                    }
                    if (caster.getItemInHand().getType().equals(Material.POTATO_ITEM)) {
                        if ((locBlock.getBlock().getType().equals(Material.AIR)) && (lowerLoc.getBlock().getType().equals(Material.SOIL))) {
                            locBlock.getBlock().setType(Material.POTATO);
                            potatoesPlanted += 1;
                        }
                    } else {
                        count2++;
                    }
                }
            }
        } else {
            caster.sendMessage("You must be on the ground to Plant.");
            return SkillCastResult.FAIL;
        }
        if (count > 0)
            caster.sendMessage("You have run out of seeds. " + count + " seeds not planted.");
        if (count2 > 0)
            caster.sendMessage(count2 + " crops have not been planted due to crops already being there.");
        caster.getInventory().removeItem(new ItemStack(Material.CARROT_ITEM, carrotsPlanted));
        caster.getInventory().removeItem(new ItemStack(Material.POTATO_ITEM, potatoesPlanted));
        caster.updateInventory();
        //        broadcastExecuteText(caster); // TODO Implement skill message broadcasting
        return SkillCastResult.NORMAL;

    }
}
