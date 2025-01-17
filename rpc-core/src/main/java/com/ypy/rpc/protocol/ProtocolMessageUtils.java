package com.ypy.rpc.protocol;

import com.ypy.rpc.model.RpcRequest;
import com.ypy.rpc.model.RpcResponse;
import com.ypy.rpc.serializer.Serializer;
import com.ypy.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * Utils class
 */
public class ProtocolMessageUtils {
    private static Serializer getSerializerFromHeader(ProtocolMessage.Header header) {
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) throw new RuntimeException("serializer not exist");
        return SerializerFactory.getInstance(serializerEnum.getValue());
    }

    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null) return Buffer.buffer();

        // append order is rule, can not change
        ProtocolMessage.Header header = protocolMessage.getHeader();
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        Serializer serializer = getSerializerFromHeader(header);

        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }

    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        byte magic = buffer.getByte(0);
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) throw new RuntimeException("magic is illegal");

        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13)); // 5 + 8 (sizeof(long)) = 13

        // Resolve the issue of package sticking by reading only data of a specified length
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength()); // 13 + 4(sizeof(int)) = 17

        Serializer serializer = getSerializerFromHeader(header);

        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (messageTypeEnum == null) throw new RuntimeException("type dose not exist");

        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, response);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("this type message is not supported");
        }
    }
}
