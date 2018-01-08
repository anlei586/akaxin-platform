package com.akaxin.platform.operation.business.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.proto.platform.ApiPushNotificationProto;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.08 15:11:48
 * @param <Command>
 */
public class ApiPushHandler extends AbstractApiHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ApiPushHandler.class);

	/**
	 * api.push.notification
	 * 
	 * @param command
	 * @return
	 */
	public boolean notification(Command command) {

		try {
			ApiPushNotificationProto.ApiPushNotificationRequest request = ApiPushNotificationProto.ApiPushNotificationRequest
					.parseFrom(command.getParams());

			return true;
		} catch (InvalidProtocolBufferException e) {
			logger.error("push notification error.", e);
		}
		return false;
	}

}
