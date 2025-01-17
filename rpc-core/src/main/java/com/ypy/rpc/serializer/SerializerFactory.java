package com.ypy.rpc.serializer;

import com.ypy.rpc.spi.SpiLoader;

public class SerializerFactory {
    static {
        SpiLoader.load(Serializer.class);
    }

    public static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
