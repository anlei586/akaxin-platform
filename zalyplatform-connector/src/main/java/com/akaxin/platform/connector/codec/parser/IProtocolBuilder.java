package com.akaxin.platform.connector.codec.parser;

import com.akaxin.common.command.RedisCommand;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public interface IProtocolBuilder {

	public void writeAndOut(Channel ch, RedisCommand cmd, ByteBuf out);

}
