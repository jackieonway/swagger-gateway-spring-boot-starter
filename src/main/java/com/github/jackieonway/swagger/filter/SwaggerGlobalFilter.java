package com.github.jackieonway.swagger.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jackieonway.swagger.constants.SwaggerConstants;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * swagger filter for 'v3/api-docs missing basePath'
 *
 * @author  Jackie
 */

@Component
public class SwaggerGlobalFilter implements GlobalFilter, Ordered {

	private static final Logger log = LoggerFactory.getLogger(SwaggerGlobalFilter.class);

	private static final String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";

	private static final String LOCAL_IPV4 = "127.0.0.1";

	private static final ObjectMapper objectMapper = new ObjectMapper();
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getPath().toString();
		String host = request.getLocalAddress().getHostString();
		if (Objects.equals(LOCAL_IPV6, host)){
			host = LOCAL_IPV4;
		}
		int port = request.getLocalAddress().getPort();
		if (!path.endsWith(SwaggerConstants.API_URI_V3)) {
			return chain.filter(exchange);
		}
		String[] pathArray = path.split(SwaggerConstants.ROOT);
		String basePath = pathArray[1];
		ServerHttpResponse originalResponse = exchange.getResponse();
		// defined new request
		String fHost = host;
		ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
			@Override
			public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
				if (Objects.equals(super.getStatusCode(), HttpStatus.OK) && body instanceof Flux) {
					Flux<? extends DataBuffer> fluxBody = Flux.from(body);
					return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
						final List<String> list = new ArrayList<>();
						dataBuffers.forEach(dataBuffer -> {
							byte[] content = new byte[dataBuffer.readableByteCount()];
							dataBuffer.read(content);
							DataBufferUtils.release(dataBuffer);
							list.add(new String(content, StandardCharsets.UTF_8));
						});
						String data = this.listToString(list);
						Map<Object, Object> jsonObject;
						try {
							jsonObject = objectMapper.readValue(data, Map.class);
							jsonObject.put("host", fHost + ":" + port);
							jsonObject.put("basePath", basePath);
							data = objectMapper.writeValueAsString(jsonObject);
						} catch (JsonProcessingException e) {
							log.error("set basePath error", e);
						}
						// help GC
						jsonObject = null;
						// set updated header content length
						int length = data.getBytes().length;
						HttpHeaders headers = originalResponse.getHeaders();
						headers.setContentLength(length);
						return bufferFactory().wrap(data.getBytes(StandardCharsets.UTF_8));
					}));
				}
				return super.writeWith(body);
			}

			@Override
			public HttpHeaders getHeaders() {
				// obtain original ServerHttpResponse header
				HttpHeaders httpHeaders = super.getHeaders();
				httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
				return httpHeaders;
			}

			private String listToString(List<String> list) {
				StringBuilder stringBuilder = new StringBuilder();
				for (String s : list) {
					stringBuilder.append(s);
				}
				return stringBuilder.toString();
			}
		};

		// replace response with decorator
		return chain.filter(exchange.mutate().response(decoratedResponse).build());
	}

	@Override
	public int getOrder() {
		return -2;
	}

}

