package com.zhketech.sip.app.project.client.beans;

import java.io.Serializable;
import java.util.UUID;

/**
 * 设备 信息的实体类
 */
public class DeviceInfor implements Serializable {
	public UUID uuid;
	public String serviceURL;
	private int id;
	private String name;
	private String ipAddr;
	private boolean isOnline = false;
	private String rtspUri = "";
	public int width;
	public int height;
	public int rate;
	public String channel;
	public String username;
	public String password;
	public boolean isSuporrtPtz = false;
	public String ptz_url;
	public String token ;


	@Override
	public String toString() {
		return "DeviceInfor{" +
				"uuid=" + uuid +
				", serviceURL='" + serviceURL + '\'' +
				", id=" + id +
				", name='" + name + '\'' +
				", ipAddr='" + ipAddr + '\'' +
				", isOnline=" + isOnline +
				", rtspUri='" + rtspUri + '\'' +
				", width=" + width +
				", height=" + height +
				", rate=" + rate +
				", channel='" + channel + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", isSuporrtPtz=" + isSuporrtPtz +
				", ptz_url='" + ptz_url + '\'' +
				", token='" + token + '\'' +
				'}';
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean online) {
		isOnline = online;
	}

	public String getRtspUri() {
		return rtspUri;
	}

	public void setRtspUri(String rtspUri) {
		this.rtspUri = rtspUri;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isSuporrtPtz() {
		return isSuporrtPtz;
	}

	public void setSuporrtPtz(boolean suporrtPtz) {
		isSuporrtPtz = suporrtPtz;
	}

	public String getPtz_url() {
		return ptz_url;
	}

	public void setPtz_url(String ptz_url) {
		this.ptz_url = ptz_url;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public DeviceInfor(UUID uuid, String serviceURL, int id, String name, String ipAddr, boolean isOnline, String rtspUri, int width, int height, int rate, String channel, String username, String password, boolean isSuporrtPtz, String ptz_url, String token) {

		this.uuid = uuid;
		this.serviceURL = serviceURL;
		this.id = id;
		this.name = name;
		this.ipAddr = ipAddr;
		this.isOnline = isOnline;
		this.rtspUri = rtspUri;
		this.width = width;
		this.height = height;
		this.rate = rate;
		this.channel = channel;
		this.username = username;
		this.password = password;
		this.isSuporrtPtz = isSuporrtPtz;
		this.ptz_url = ptz_url;
		this.token = token;
	}

	public DeviceInfor() {
		super();
	}

}