package com.ypy.rpc.serializer;

import com.ypy.rpc.constant.SerializerKeys;
import com.ypy.rpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * without spi
 */
//public class SerializerFactory {
//    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>() {{
//        put(SerializerKeys.JDK, new JdkSerializer());
//        put(SerializerKeys.JSON, new JsonSerializer());
//        put(SerializerKeys.KRYO, new KryoSerializer());
//        put(SerializerKeys.HESSIAN, new HessianSerializer());
//    }};
//
//    public static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get(SerializerKeys.JDK);
//
//    public static Serializer getInstance(String key) { return KEY_SERIALIZER_MAP.get(key); }
//}

/**
 * use spi
 */
public class SerializerFactory {
    static {
        SpiLoader.load(Serializer.class);
    }

    public static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
