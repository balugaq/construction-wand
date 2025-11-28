package com.balugaq.constructionwand.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Final_ROOT
 * @author balugaq
 * @since 1.0
 */
@NullMarked
public class ParticleUtil {
    public static void drawLineByDistance(Plugin plugin, Particle particle, long interval, double distance,
                                          Location... locations) {
        int time = 0;
        for (int i = 0; i + 1 < locations.length; i++) {
            Location location1 = locations[i];
            Location location2 = locations[i + 1];

            if (distance == 0 || location1.getWorld() == null || location1.getWorld() != location2.getWorld()) {
                return;
            }
            World world = location1.getWorld();
            double x = location1.getX();
            double y = location1.getY();
            double z = location1.getZ();

            double d = location1.distance(location2);
            int particleCount = (int) (d / distance);
            double px = (location2.getX() - x) / (particleCount);
            double py = (location2.getY() - y) / (particleCount);
            double pz = (location2.getZ() - z) / (particleCount);

            double t = time;
            int tick = (int) (t / 50);
            int lastTick;
            List<Runnable> runnableList = new ArrayList<>();
            for (int j = 0; j < particleCount; j++) {
                x += px;
                y += py;
                z += pz;
                double fx = x;
                double fy = y;
                double fz = z;
                runnableList.add(() -> world.spawnParticle(particle, fx, fy, fz, 1, 0, 0, 0, 0));

                t += (double) interval / particleCount;
                lastTick = (int) (t / 50);
                if (tick != lastTick) {
                    final List<Runnable> finalRunnableList = runnableList;
                    plugin.getServer().getScheduler().runTaskLaterAsynchronously(
                            plugin,
                            () -> finalRunnableList.forEach(Runnable::run),
                            tick
                    );
                    tick = lastTick;
                    runnableList = new ArrayList<>();
                }
            }
            if (!runnableList.isEmpty()) {
                final List<Runnable> finalRunnableList = runnableList;
                plugin.getServer().getScheduler().runTaskLaterAsynchronously(
                        plugin,
                        () -> finalRunnableList.forEach(Runnable::run),
                        tick
                );
            }

            time += (int) interval;
        }
    }

    @SuppressWarnings("ExtractMethodRecommender")
    public static void drawRegionOutline(Plugin plugin, Particle particle, long interval, Location corner1,
                                         Location corner2) {
        World world = corner1.getWorld();
        if (world == null || corner1.getWorld() != corner2.getWorld()) {
            return;
        }

        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        Location[] corners = new Location[] {
                new Location(world, minX, minY, minZ),
                new Location(world, minX, minY, maxZ + 1),
                new Location(world, maxX + 1, minY, maxZ + 1),
                new Location(world, maxX + 1, minY, minZ),
                new Location(world, minX, maxY + 1, minZ),
                new Location(world, minX, maxY + 1, maxZ + 1),
                new Location(world, maxX + 1, maxY + 1, maxZ + 1),
                new Location(world, maxX + 1, maxY + 1, minZ)
        };

        drawLineByDistance(plugin, particle, interval, 0.25, corners[0], corners[1]);
        drawLineByDistance(plugin, particle, interval, 0.25, corners[0], corners[3]);
        drawLineByDistance(plugin, particle, interval, 0.25, corners[0], corners[4]);

        drawLineByDistance(plugin, particle, interval, 0.25, corners[1], corners[2]);
        drawLineByDistance(plugin, particle, interval, 0.25, corners[1], corners[5]);

        drawLineByDistance(plugin, particle, interval, 0.25, corners[2], corners[3]);
        drawLineByDistance(plugin, particle, interval, 0.25, corners[2], corners[6]);

        drawLineByDistance(plugin, particle, interval, 0.25, corners[3], corners[7]);
        drawLineByDistance(plugin, particle, interval, 0.25, corners[4], corners[5]);

        drawLineByDistance(plugin, particle, interval, 0.25, corners[4], corners[7]);
        drawLineByDistance(plugin, particle, interval, 0.25, corners[5], corners[6]);

        drawLineByDistance(plugin, particle, interval, 0.25, corners[6], corners[7]);
    }
}