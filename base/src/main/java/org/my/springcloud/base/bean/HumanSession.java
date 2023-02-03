package org.my.springcloud.base.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HumanSession implements Serializable{

	private static final long serialVersionUID = -8089671760900486443L;

	protected Integer humanID;
	protected String humanName;
	protected Integer unitID;
	protected String humanCode;
	protected String unitName;
	protected String portrait;
	protected Integer regionID;
	protected Integer regionType;
	protected String regionCode;//市代码、区代码、街道代码等,如110000
	protected boolean validFlag = true;//session是否已经被同一用户踢掉
	protected String invalidMsg;//session被同一用户踢掉相关信息，如：IP地址等
	protected Integer logID;//登录标识
	protected double coordinateX;//人员所在区域中心点坐标
	protected double coordinateY;
	protected String ip;
	protected String serverIp;
	protected String browserVersion; //记录登陆浏览器的版本信息
	protected String osVersion;     //记录操作系统的版本和位数
	protected String proxyUrl;
	protected Integer patrolFlag;//是否监督员标识
	protected Integer autoReceiveFlag; // 接收自动分派案件
	protected Integer unionID;
	protected String telMobile;
	/**
	 *单点登录标识，记录用户从第三方单点登录进来
	 */
	protected Integer fromCas = 0 ;
	protected Integer fromToken = 0 ;

	protected String regionName;

	protected String userName;

	// 人员级别
	protected Integer leaderLevelID;



}
