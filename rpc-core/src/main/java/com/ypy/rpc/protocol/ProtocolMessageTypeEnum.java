package com.ypy.rpc.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageTypeEnum {
    REQUEST((byte) 0),
    RESPONSE((byte) 1),
    HEART_BEAT((byte) 2),
    OTHERS((byte) 3);

    private final byte key;

    ProtocolMessageTypeEnum(byte key) {
        this.key = key;
    }

    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        for (ProtocolMessageTypeEnum e : ProtocolMessageTypeEnum.values()) {
            if (e.key == key) {
                return e;
            }
        }
        return null;
    }
}
