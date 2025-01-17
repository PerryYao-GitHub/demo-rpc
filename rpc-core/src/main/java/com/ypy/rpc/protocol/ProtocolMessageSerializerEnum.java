package com.ypy.rpc.protocol;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum ProtocolMessageSerializerEnum {
    JDK((byte) 0, "jdk"),
    JSON((byte) 1, "json"),
    KRYO((byte) 2, "kryo"),
    HESSIAN((byte) 3, "hessian");

    private final byte key;

    private final String value;

    ProtocolMessageSerializerEnum(byte key, String value) {
        this.key = key;
        this.value = value;
    }

    public static List<String> getValues() {
        return Arrays.stream(values()).map(it -> it.value).collect(Collectors.toList());
    }

    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum e : ProtocolMessageSerializerEnum.values()) {
            if (e.key == key) {
                return e;
            }
        }
        return null;
    }

    public static ProtocolMessageSerializerEnum getEnumByValue(String value) {
        for (ProtocolMessageSerializerEnum e : ProtocolMessageSerializerEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }
}
