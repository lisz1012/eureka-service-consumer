package com.lisz.interceptor;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
/*
所有的RestTemplate发送出去的请求和返回的响应，都会走这个拦截器
 */
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		System.out.println("拦截 Request");
		System.out.println(request.getURI());

		final ClientHttpResponse response = execution.execute(request, body);

		System.out.println("响应返回");
		System.out.println(response.getHeaders());

		return response;
	}
}
