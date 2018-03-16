//package com.akaxin.platform.operation.constant;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.akaxin.common.utils.ServerAddress;
//
///**
// * 配置是否支持绝密聊天
// * 
// * @author Sam{@link an.guoyue254@gmail.com}
// * @since 2018-03-16 14:23:33
// */
//public class OpenSCAddress {
//	private static final Logger logger = LoggerFactory.getLogger(OpenSCAddress.class);
//
//	private static Set<String> addressSet = new HashSet<String>() {
//		{
//			add("demo.akaxin.com");
//
//		}
//
//	};
//
//	public static boolean isAllow(String address) {
//		try {
//			if (StringUtils.isNotBlank(address)) {
//				ServerAddress addrr = new ServerAddress(address);
//				if (addressSet.contains(addrr.getHost())) {
//					return true;
//				}
//			}
//		} catch (Exception e) {
//			logger.info("check open SC address error", e);
//		}
//		return false;
//	}
//}
