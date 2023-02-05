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

/**
 *
 * Modified from original.
 *
 * MIT License
 *
 * Copyright (c) 2021 Hypixel Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
