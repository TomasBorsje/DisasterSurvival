package io.github.tomasborsje.events;

import io.github.tomasborsje.DisasterSurvival;
import io.github.tomasborsje.core.DisasterEvent;
import net.minecraft.world.level.block.BarrierBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;

public class EarthquakeDisasterEvent extends DisasterEvent {

    public static final int INITIAL_FIRE_COUNT = 6;
    public static final double FIRE_SPREAD_PROBABILITY = 0.5;

    public EarthquakeDisasterEvent(World world) {
        super(world, "Earth Quake!", "Watch out for falling blocks!");
    }

    public void startEvent() {
        super.startEvent();
    }

    @Override
    public void tickEvent() {
        super.tickEvent();
        // Every 20 ticks, randomly replace 30 blocks with falling blocks
        if(ticksRemaining % 20 == 0) {
            for(int i = 0; i < 20; i++) {
                int x = (int) (Math.random() * 50);
                int z = (int) (Math.random() * 50);
                int y = ARENA_MAX_Y;

                // Go down from y=50 until we find a solid block
                Block block = world.getBlockAt(x, y, z);
                while((block.getType().isAir() || block.getType() == Material.BARRIER) && y > ARENA_MIN_Y) {
                    y--;
                    block = world.getBlockAt(x, y, z);
                }
                // If coordinate is at the bottom of the world, skip because no block found
                if(y == ARENA_MIN_Y) {
                    //Bukkit.getLogger().info("Skipping block at "+x+", "+y+", "+z);
                    continue;
                }

                // If solid block, replace with falling block
                if(!world.getBlockAt(x, y, z).getType().isAir()) {
                    // Delete block at position and spawn a burst of particles around the position
                    world.spawnParticle(Particle.EXPLOSION, x, y, z, 10, 0.1, 0.1, 0.1, 0.1);
                    // Spawn falling block
                    FallingBlock fallingBlock = world.spawnFallingBlock(block.getLocation(), block.getBlockData());
                    fallingBlock.setDamagePerBlock(2);
                    fallingBlock.setMaxDamage(8);
                    fallingBlock.setDropItem(false);
                    fallingBlock.setHurtEntities(true);
                    // Delete original block
                    block.setType(Material.AIR);
                }
            }
        }
    }

    @Override
    public void endEvent() {
        super.endEvent();
    }
}
