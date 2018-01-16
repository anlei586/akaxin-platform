package com.akaxin.platform.operation.business.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.common.crypto.HashCrypto;
import com.akaxin.common.utils.ValidatorPattern;
import com.akaxin.platform.operation.business.dao.PhoneCodeDao;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.operation.utils.PhoneCodeUtils;
import com.akaxin.proto.platform.ApiUserRealNameProto;
import com.akaxin.proto.platform.ApiUserUploadProto;
import com.zaly.platform.storage.bean.UserRealNameBean;
import com.zaly.platform.storage.bean.UserBean;

/**
 * service:ApiUserInfo methods:
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 */
public class ApiUserHandler extends AbstractApiHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ApiUserHandler.class);

	/**
	 * 推送用户token给平台
	 * 
	 * @param command
	 * @return
	 */
	public boolean pushToken(Command command) {
		return upload(command);
	}

	/**
	 * 上传/更新用户个人信息
	 */
	public boolean upload(Command command) {
		logger.info("----- api.user.upload -----");
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errCode = ErrorCode.ERROR;
		try {
			ApiUserUploadProto.ApiUserUploadRequest request = ApiUserUploadProto.ApiUserUploadRequest
					.parseFrom(command.getParams());
			String userIdPubk = request.getUserIdPubk();

			String userId = HashCrypto.SHA1(userIdPubk);

			UserBean userBean = new UserBean();
			userBean.setUserId(userId);
			userBean.setUserIdPrik(request.getUserIdPrik());
			userBean.setUserIdPubk(userIdPubk);
			userBean.setUserName(request.getUserName());
			userBean.setUserPhoneId(request.getPhoneId());
			if (StringUtils.isNotEmpty(request.getUserPhoto())) {
				userBean.setUserPhoto(request.getUserPhoto());
			}
			userBean.setClientType(request.getClientType());
			userBean.setPushToken(request.getPushToken());
			userBean.setRom(request.getRom());

			logger.info("userInfoBean=" + userBean.toString());

			if (UserInfoDao.getInstance().uploadUserInfo(userBean)) {
				errCode = ErrorCode.SUCCESS;
			}

		} catch (Exception e) {
			commandResponse.setErrInfo("api.user.upload exception!");
			logger.error("upload user info error.", e);
		}
		command.setResponse(commandResponse.setErrCode(errCode));
		return true;
	}

	/**
	 * 实名认证,绑定手机号码以及设置密码
	 * 
	 */
	public boolean realName(Command command) {
		logger.info("------------api.user.realName-----------");
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errorCode = ErrorCode.ERROR;
		try {
			ApiUserRealNameProto.ApiUserRealNameRequest request = ApiUserRealNameProto.ApiUserRealNameRequest
					.parseFrom(command.getParams());

			String userIdPrik = request.getUserIdPrik();
			String userIdPubk = request.getUserIdPubk();
			String userId = HashCrypto.SHA1(userIdPubk);
			String phoneId = request.getPhoneId();
			String verifyCode = request.getPhoneVerifyCode();
			String password = HashCrypto.MD5(request.getPassword());

			UserRealNameBean bean = new UserRealNameBean();
			bean.setUserId(userId);
			bean.setUserIdPrik(userIdPrik);
			bean.setUserIdPubk(userIdPubk);
			bean.setUserPhoneId(phoneId);
			bean.setPassword(password);
			bean.setPhoneRoaming("+86");

			if (!ValidatorPattern.isPhoneId(phoneId) || StringUtils.isEmpty(userIdPrik)
					|| StringUtils.isEmpty(userIdPubk) || StringUtils.isEmpty(phoneId)
					|| StringUtils.isEmpty(password)) {
				command.setResponse(commandResponse.setErrCode(errorCode));
				return false;
			}

			String redisKey = PhoneCodeUtils.getPhoneVCKey(phoneId);
			String realVerifyCode = PhoneCodeDao.getInstance().getPhoneCode(redisKey);

			logger.info("Phone code={} realCode={} bean={}", verifyCode, realVerifyCode, bean.toString());

			if (StringUtils.isNotEmpty(realVerifyCode) && realVerifyCode.equals(verifyCode)) {
				if (UserInfoDao.getInstance().updateRealUserInfo(bean)) {
					errorCode = ErrorCode.SUCCESS;
				}
			} else {
				commandResponse.setErrInfo("verify phone code error.");
			}

		} catch (Exception e) {
			commandResponse.setErrInfo("api.user.realName exception");
			logger.error("api.user.realName error.", e);
		}

		command.setResponse(commandResponse.setErrCode(errorCode));
		return true;
	}

}
