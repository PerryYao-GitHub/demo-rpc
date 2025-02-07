package com.ypy.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {
    @Data
    public static class Header {
        private byte magic;

        private byte version;

        private byte serializer;

        private byte type;

        private byte status;

        private long requestId;

        private int bodyLength;
    }

    private Header header;

    private T body;
}
