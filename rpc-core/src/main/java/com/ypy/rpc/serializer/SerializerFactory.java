package com.ypy.rpc.serializer;

import com.ypy.rpc.constant.SerializerKeys;

import java.util.HashMap;
import java.util.Map;

public class SerializerFactory {
    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>() {{
        put(SerializerKeys.JDK, new JdkSerializer());
        put(SerializerKeys.JSON, new JsonSerializer());
        put(SerializerKeys.KRYO, new KryoSerializer());
        put(SerializerKeys.HESSIAN, new HessianSerializer());
    }};

    public static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get(SerializerKeys.JDK);

    public static Serializer getInstance(String key) { return KEY_SERIALIZER_MAP.get(key); }
}
