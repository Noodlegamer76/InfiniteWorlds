package com.noodlegamer76.infiniteworlds.level.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;

public class ServerLevelStorage {
    //offset in block y
    public static final int SUBLEVEL_HEIGHT = 128;
    //Integer is the y offset relative to the parents y=0
    public static Map<ServerLevel, Map<Integer, StackedServerLevel>> subLevels = new HashMap<>();

    public static void addSubLevel(ServerLevel level, int yOffset, StackedServerLevel subLevel) {
        subLevels.computeIfAbsent(level, l -> new HashMap<>()).put(yOffset, subLevel);
    }

    public static StackedServerLevel getSubLevel(ServerLevel level, int y) {
        Map<Integer, StackedServerLevel> levels = getSubLevels(level);
        if (levels == null) {
            return null;
        }
        return levels.get(y);
    }

    public static StackedServerLevel getSubLevel(ServerLevel parent, BlockPos pos) {
        return getSubLevel(parent, getCorrectYOffset(parent, pos));
    }

    public static Map<Integer, StackedServerLevel> getSubLevels(ServerLevel level) {
        return getSubLevels().get(level);
    }

    public static Map<ServerLevel, Map<Integer, StackedServerLevel>> getSubLevels() {
        return subLevels;
    }

    public static boolean hasSubLevel(ServerLevel level, int y) {
        Map<Integer, StackedServerLevel> levels = getSubLevels(level);
        if (levels == null) {
            return false;
        }
        return levels.containsKey(getCorrectYOffset(level, new BlockPos(0, y, 0)));
    }


    public static int getCorrectYOffset(ServerLevel level, BlockPos pos) {
        ServerLevel parent = level instanceof StackedServerLevel stackedServerLevel ? stackedServerLevel.getParentLevel() : level;
        int y = pos.getY();
        int minY = parent.getMinBuildHeight();
        int maxY = parent.getMaxBuildHeight();

        if (y < minY) {
            return floorToMultiple(y - minY, SUBLEVEL_HEIGHT);
        } else if (y >= maxY) {
            return ceilToMultiple(y - maxY, SUBLEVEL_HEIGHT);
        } else {
            return 0;
        }
    }

    /**
     * Floors the given value to the nearest multiple of the specified multiple.
     * Works correctly for negative values as well.
     */
    public static int floorToMultiple(int value, int multiple) {
        return Math.floorDiv(value, multiple) * multiple;
    }

    /**
     * Ceils the given value to the nearest multiple of the specified multiple.
     * Works correctly for negative values as well.
     */
    public static int ceilToMultiple(int value, int multiple) {
        return Math.floorDiv(value + multiple - 1, multiple) * multiple;
    }


}
