package com.ypy.rpc.protocol;

public interface ProtocolConstant {
    int MESSAGE_HEADER_LENGTH = 17;

    byte PROTOCOL_MAGIC = (byte) 0x1;

    byte PROTOCOL_VERSION = (byte) 0x1;
}
