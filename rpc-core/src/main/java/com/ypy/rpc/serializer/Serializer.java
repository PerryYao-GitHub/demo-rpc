package com.ypy.rpc.serializer;

import java.io.IOException;

public interface Serializer {
    /**
     * serialize
     *
     * @param obj
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * deserialize
     *
     * @param bytes
     * @param type
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}
