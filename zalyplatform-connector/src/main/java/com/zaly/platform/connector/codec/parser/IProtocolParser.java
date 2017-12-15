package com.zaly.platform.connector.codec.parser;

import java.util.List;

import com.zaly.platform.connector.codec.protocol.MessageDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * Decoder中使用IProtocolParser，将Byte转成protocolPacket,并交给out传递给InboundHandler
 * 
 * @author ay.sam
 * @since 2017.09.27
 *
 */
public interface IProtocolParser {

	public void readAndOut(Channel ch, ByteBuf inByte, List<Object> out, MessageDecoder decoder);

}
