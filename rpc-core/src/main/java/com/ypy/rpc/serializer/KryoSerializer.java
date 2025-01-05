package com.ypy.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer implements Serializer {
    /**
     * kryo is thread-unsafe, using ThreadLocal ensures that each thread has only one Kryo
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);
        KRYO_THREAD_LOCAL.get().writeObject(output, obj);
        output.close();
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Input input = new Input(bis);
        T result = KRYO_THREAD_LOCAL.get().readObject(input, type);
        input.close();
        return result;
    }
}
