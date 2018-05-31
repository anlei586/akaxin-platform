package com.akaxin.platform.operation.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.common.utils.ServerAddress;
import com.akaxin.platform.operation.monitor.PushMonitor;
import com.akaxin.proto.core.PushProto;

public class PushCount {
	private static final Logger logger = LoggerFactory.getLogger(PushCount.class);

	public static void addPushMonitor(String globalUserId, ServerAddress address, PushProto.PushType pushType) {
		try {
			switch (pushType) {
			case PUSH_NOTICE:
				PushMonitor.COUNTER_OTHERS.inc();
				UserVisitSiteCount.hincrOtherPush(globalUserId, address.getFullAddress());
				break;
			case PUSH_GROUP_TEXT:
				PushMonitor.COUNTER_G_TEXT.inc();
				UserVisitSiteCount.hincrGroupPush(globalUserId, address.getFullAddress());
				break;
			case PUSH_GROUP_IMAGE:
				PushMonitor.COUNTER_G_PIC.inc();
				UserVisitSiteCount.hincrGroupPush(globalUserId, address.getFullAddress());
				break;
			case PUSH_GROUP_VOICE:
				PushMonitor.COUNTER_G_AUDIO.inc();
				UserVisitSiteCount.hincrGroupPush(globalUserId, address.getFullAddress());
				break;
			case PUSH_TEXT:
				PushMonitor.COUNTER_U2_TEXT.inc();
				UserVisitSiteCount.hincrU2Push(globalUserId, address.getFullAddress());
				break;
			case PUSH_IMAGE:
				PushMonitor.COUNTER_U2_PIC.inc();
				UserVisitSiteCount.hincrU2Push(globalUserId, address.getFullAddress());
				break;
			case PUSH_VOICE:
				PushMonitor.COUNTER_U2_AUDIO.inc();
				UserVisitSiteCount.hincrU2Push(globalUserId, address.getFullAddress());
				break;
			case PUSH_SECRET_TEXT:
				PushMonitor.COUNTER_U2_TEXTS.inc();
				UserVisitSiteCount.hincrU2Push(globalUserId, address.getFullAddress());
				break;
			case PUSH_SECRET_IMAGE:
				PushMonitor.COUNTER_U2_PICS.inc();
				UserVisitSiteCount.hincrU2Push(globalUserId, address.getFullAddress());
				break;
			case PUSH_SECRET_VOICE:
				PushMonitor.COUNTER_U2_AUDIOS.inc();
				UserVisitSiteCount.hincrU2Push(globalUserId, address.getFullAddress());
				break;
			case PUSH_APPLY_FRIEND_NOTICE:
				PushMonitor.COUNTER_OTHERS.inc();
				UserVisitSiteCount.hincrOtherPush(globalUserId, address.getFullAddress());
				break;
			default:
				PushMonitor.COUNTER_OTHERS.inc();
				UserVisitSiteCount.hincrOtherPush(globalUserId, address.getFullAddress());
			}
		} catch (Exception e) {
			logger.error("add push monitor error", e);
		}
	}

}
