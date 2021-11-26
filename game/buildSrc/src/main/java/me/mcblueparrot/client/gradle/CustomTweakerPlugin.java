package me.mcblueparrot.client.gradle;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import net.minecraftforge.gradle.common.Constants;
import net.minecraftforge.gradle.user.ReobfMappingType;
import net.minecraftforge.gradle.user.ReobfTaskFactory;
import net.minecraftforge.gradle.user.tweakers.ClientTweaker;

import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CustomTweakerPlugin extends ClientTweaker {

	@Override
	protected void setupReobf(ReobfTaskFactory.ReobfTaskWrapper reobf) {
		super.setupReobf(reobf);
		reobf.setMappingType(ReobfMappingType.SEARGE);
	}

	// Hack to get around ForgeGradle using URL that is increasingly down/unavailable, due to 1.8 not being supported
	// (which is fair enough, as it is surprisingly old)
	/*
	 * Modified from ForgeGradle.
	 * Original License:
	 *
	 * A Gradle plugin for the creation of Minecraft mods and MinecraftForge plugins.
	 * Copyright (C) 2013 Minecraft Forge
	 *
	 * This library is free software; you can redistribute it and/or
	 * modify it under the terms of the GNU Lesser General Public
	 * License as published by the Free Software Foundation; either
	 * version 2.1 of the License, or (at your option) any later version.
	 *
	 * This library is distributed in the hope that it will be useful,
	 * but WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	 * Lesser General Public License for more details.
	 *
	 * You should have received a copy of the GNU Lesser General Public
	 * License along with this library; if not, write to the Free Software
	 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
	 * USA
	 */
	@Override
	protected String getWithEtag(String strUrl, File cache, File etagFile) {
		try {
			if(project.getGradle().getStartParameter().isOffline()) { // dont even try the internet
				return Files.toString(cache, Charsets.UTF_8);
			}

			// dude, its been less than 1 minute since the last time..
			if(cache.exists() && cache.lastModified() + 60000 >= System.currentTimeMillis()) {
				return Files.toString(cache, Charsets.UTF_8);
			}

			String etag;
			if(etagFile.exists()) {
				etag = Files.toString(etagFile, Charsets.UTF_8);
			}
			else {
				etagFile.getParentFile().mkdirs();
				etag = "";
			}

			URL url = new URL(strUrl);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setInstanceFollowRedirects(true);
			con.setRequestProperty("User-Agent", Constants.USER_AGENT);
			con.setIfModifiedSince(cache.lastModified());

			if(!Strings.isNullOrEmpty(etag)) {
				con.setRequestProperty("If-None-Match", etag);
			}

			con.connect();

			String out = null;
			if(con.getResponseCode() == 304) {
				// the existing file is good
				Files.touch(cache); // touch it to update last-modified time, to wait another minute
				out = Files.toString(cache, Charsets.UTF_8);
			}
			else if(con.getResponseCode() == 200) {
				InputStream stream = con.getInputStream();
				byte[] data = ByteStreams.toByteArray(stream);
				Files.write(data, cache);
				stream.close();

				// write etag
				etag = con.getHeaderField("ETag");
				if(Strings.isNullOrEmpty(etag)) {
					Files.touch(etagFile);
				}
				else {
					Files.write(etag, etagFile, Charsets.UTF_8);
				}

				out = new String(data);
			}
			else {
				project.getLogger().warn("Etag download for " + strUrl + " failed with code " + con.getResponseCode()
						+ ". Mappings may be outdated.");

				return fallbackGetWithEtag(strUrl);
			}

			con.disconnect();

			return out;
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		if(cache.exists()) {
			try {
				return Files.toString(cache, Charsets.UTF_8);
			}
			catch(IOException e) {
				Throwables.propagate(e);
			}
		}

		try {
			return fallbackGetWithEtag(strUrl);
		}
		catch(Exception error) {
			throw new RuntimeException(error);
		}
	}

	private String fallbackGetWithEtag(String strUrl) {
		try {
			if ("http://export.mcpbot.bspk.rs/versions.json".equals(strUrl)) {
				// Sol Client - Use potentially older local version.
				return Resources.toString(getClass().getResource("/McpMappings.json"), StandardCharsets.UTF_8);
			}
		}
		catch(IOException error) {}

		throw new RuntimeException("Unable to obtain url (" + strUrl + ") with etag!");
	}

}
