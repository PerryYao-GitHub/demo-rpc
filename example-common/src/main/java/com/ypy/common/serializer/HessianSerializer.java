package com.ypy.common.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.ypy.rpc.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class HessianSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        System.out.println("hessian serialize start");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(bos);
        ho.writeObject(obj);  // Hessian should automatically handle collections
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        System.out.println("hessian deserialize start");
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        HessianInput hi = new HessianInput(bis);

        // 读取对象
        Object deserializedObject = hi.readObject();

        // 如果反序列化出来的是 List 类型，直接返回
        if (deserializedObject instanceof List) {
            List<?> list = (List<?>) deserializedObject;
            return (T) list;
        }

        // 否则返回原始对象
        return (T) deserializedObject;
    }
}
