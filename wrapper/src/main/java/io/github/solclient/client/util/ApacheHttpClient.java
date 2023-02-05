/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.util;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import io.github.solclient.util.GlobalConstants;
import net.hypixel.api.http.*;

public class ApacheHttpClient implements HypixelHttpClient {

	private final UUID apiKey;
	private final HttpClient httpClient;

	public ApacheHttpClient(UUID apiKey) {
		this.apiKey = apiKey;
		this.httpClient = HttpClientBuilder.create().setUserAgent(GlobalConstants.NAME).build();
	}

	@Override
	public CompletableFuture<HypixelHttpResponse> makeRequest(String url) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				HttpResponse response = this.httpClient.execute(new HttpGet(url));
				return new HypixelHttpResponse(response.getStatusLine().getStatusCode(),
						EntityUtils.toString(response.getEntity(), "UTF-8"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, MinecraftUtils.USER_DATA);
	}

	@Override
	public CompletableFuture<HypixelHttpResponse> makeAuthenticatedRequest(String url) {
		return CompletableFuture.supplyAsync(() -> {
			HttpGet request = new HttpGet(url);
			request.addHeader("API-Key", this.apiKey.toString());
			try {
				HttpResponse response = this.httpClient.execute(request);
				return new HypixelHttpResponse(response.getStatusLine().getStatusCode(),
						EntityUtils.toString(response.getEntity(), "UTF-8"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, MinecraftUtils.USER_DATA);
	}

	@Override
	public void shutdown() {
	}

}
