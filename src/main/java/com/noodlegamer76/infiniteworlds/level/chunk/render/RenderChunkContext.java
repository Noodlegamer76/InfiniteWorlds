package com.noodlegamer76.infiniteworlds.level.chunk.render;

public class RenderChunkContext {
    private static final ThreadLocal<Integer> CTX = new ThreadLocal<>();

    public static void set(int sectionY) {
        CTX.set(sectionY);
    }

    public static Integer get() {
        return CTX.get();
    }

    public static void clear() {
        CTX.remove();
    }

    public static boolean isSet() {
        return CTX.get() != null;
    }
}
