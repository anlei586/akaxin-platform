package com.akaxin.platform.operation.business.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.SiteProto;
import com.akaxin.proto.platform.ApiRecommendSitesProto;

/**
 * 客户端推荐站点，调用此接口获取站点推荐
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-29 16:13:04
 */
public class ApiRecommandHandler extends AbstractApiHandler<Command, CommandResponse> {
	private static final Logger logger = LoggerFactory.getLogger(ApiRecommandHandler.class);

	/**
	 * api.recommend.sites 推荐站点地址
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse sites(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiRecommendSitesProto.ApiRecommendSitesRequest request = ApiRecommendSitesProto.ApiRecommendSitesRequest
					.parseFrom(command.getParams());
			int pageNum = request.getPageNum();
			int pageSize = request.getPageSize();

			if (pageNum == 0 && pageSize == 0) {
				pageNum = 1;
				pageSize = 20;
			}

			ApiRecommendSitesProto.ApiRecommendSitesResponse.Builder resposneBuilder = ApiRecommendSitesProto.ApiRecommendSitesResponse
					.newBuilder();

			SiteProto.SiteInfo site = SiteProto.SiteInfo.newBuilder().setSiteAddress("demo.akaxin.com")
					.setSitePort(2021).setSiteName("阿卡信体验站").setSiteLogo("").setSiteIntroduction("体验阿卡信的首选站点服务")
					.build();

			for (int i = 0; i < pageSize; i++) {
				resposneBuilder.addSite(site);
			}

			commandResponse.setParams(resposneBuilder.build().toByteArray());
			errCode = ErrorCode2.SUCCESS;

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

}
