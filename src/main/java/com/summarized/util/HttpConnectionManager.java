package com.summarized.util;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * http连接管理工具
 * @author chenjing03
 * Created on 2018-08-09
 */
public class HttpConnectionManager {
	private static final PoolingHttpClientConnectionManager cm;
	private static final RequestConfig config;
	private static final CloseableHttpClient httpClient;

	// TCP握手超时 毫秒
	private static final int CONNECT_TIMEOUT = 60000;
	// 数据传输超时 毫秒
	private static final int SOCKET_TIMEOUT = 120000;
	// 连接池等待超时
	private static final int CONNECTION_REQUEST_TIMEOUT = 120000;

	static {
		LayeredConnectionSocketFactory sslsf = null;
		try {
			sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if(sslsf == null){
			sslsf = SSLConnectionSocketFactory.getSocketFactory();
		}
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();
		cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(20);

		config = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT)
				.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).build();
		httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(config).build();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				if (httpClient != null) {
					try {
						httpClient.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}));
	}

	// 获取连接
	public static CloseableHttpClient getHttpClient() {
		return httpClient;
	}
}
