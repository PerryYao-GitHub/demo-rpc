package com.ypy.rpc.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageStatusEnum {
    OK("ok", (byte) 20),
    BAD_REQUEST("badRequest", (byte) 40),
    BAD_RESPONSE("badResponse", (byte) 50);

    private final String text;

    private final byte value;

    ProtocolMessageStatusEnum(String text, byte value) {
        this.text = text;
        this.value = value;
    }

    public static ProtocolMessageStatusEnum getEnumByValue(byte value) {
        for (ProtocolMessageStatusEnum e : ProtocolMessageStatusEnum.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null;
    }
}
