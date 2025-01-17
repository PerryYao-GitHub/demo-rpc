package com.ypy.rpc.server.tcp;

import com.ypy.rpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;



public class TcpBufferHandlerWrapper implements Handler<Buffer> {
    private final RecordParser recordParser;

    private RecordParser initRecordParse(Handler<Buffer> bufferHandler) {
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            int bodyLength = -1;  // Body的长度，将会从Header中解析出来
            Buffer resBuffer = Buffer.buffer();  // 用来缓存接收到的数据

            @Override
            public void handle(Buffer buffer) {
                if (-1 == bodyLength) {
                    // 1. 首先解析 Header（固定长度 17 字节）
                    bodyLength = buffer.getInt(13); // 获取 Body 的长度，假设Header的索引 13..=16 字节表示 BodyLength
                    parser.fixedSizeMode(bodyLength);  // 根据Body的长度设置接下来的解析模式
                    resBuffer.appendBuffer(buffer);  // 将Header数据缓存起来
                } else {
                    // 2. 接收 Body 数据
                    resBuffer.appendBuffer(buffer);  // 将接收到的Body数据添加到缓存中

                    // 3. 检查是否收到了完整的 Body 数据
                    if (resBuffer.length() >= bodyLength + 17) { // 通常, 这一层 if 也可以省略, 加上的话防止数据遗漏
                        bufferHandler.handle(resBuffer);  // 调用外部传入的回调，处理完整的消息

                        // 4. 处理完后，重置状态，准备处理下一个数据包
                        parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH); // 回到Header模式
                        bodyLength = -1;  // 重置Body长度
                        resBuffer = Buffer.buffer();  // 重置缓存
                    }
                }
            }
        });
        return parser;
    }

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        recordParser = initRecordParse(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }
}
