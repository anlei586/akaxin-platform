package com.zaly.platform.connector.codec.parser;

import com.zaly.common.command.RedisCommand;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public interface IProtocolBuilder {

	public void writeAndOut(Channel ch, RedisCommand cmd, ByteBuf out);

}
