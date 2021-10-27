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

package me.mcblueparrot.client.util;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import me.mcblueparrot.client.Client;
import net.hypixel.api.http.HypixelHttpClient;
import net.hypixel.api.http.HypixelHttpResponse;

public class ApacheHttpClient implements HypixelHttpClient {

    private final UUID apiKey;
    private final ExecutorService executorService;
    private final HttpClient httpClient;

    public ApacheHttpClient(UUID apiKey) {
        this.apiKey = apiKey;
        this.executorService = Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors(), 2));
        this.httpClient = HttpClientBuilder.create().setUserAgent(Client.NAME).build();
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
        }, this.executorService);
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
        }, this.executorService);
    }

    @Override
    public void shutdown() {
        this.executorService.shutdown();
    }

}
