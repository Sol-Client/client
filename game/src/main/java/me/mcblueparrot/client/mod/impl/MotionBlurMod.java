package me.mcblueparrot.client.mod.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PostProcessingEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.PrimaryIntegerSettingMod;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.util.access.AccessShaderGroup;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;

public class MotionBlurMod extends Mod implements PrimaryIntegerSettingMod {

	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation("minecraft:shaders/post/motion_blur.json");
	@Expose
	@ConfigOption("Blur")
	@Slider(min = 0, max = 0.99F, step = 0.01F)
	private float blur = 0.5f;
	private ShaderGroup group;
	private float groupBlur;

	public ShaderGroup getGroup() {
		return group;
	}

	public MotionBlurMod() {
		super("Motion Blur", "motion_blur", "Smooth motion blur effect.", ModCategory.VISUAL);
		Client.INSTANCE.addResource(RESOURCE_LOCATION, new MotionBlurShader());
	}

	public void update() {
		if(group == null) {
			groupBlur = blur;
			try {
				group = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), RESOURCE_LOCATION);
				group.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
			}
			catch(JsonSyntaxException | IOException error) {
				logger.error("Could not load motion blur", error);
			}
		}
		if(groupBlur != blur) {
			((AccessShaderGroup) group).getListShaders().forEach((shader) -> {
				ShaderUniform blendFactor = shader.getShaderManager().getShaderUniform("BlendFactor");
				if(blendFactor != null) {
					blendFactor.set(blur);
				}
			});
			groupBlur = blur;
		}
	}

	@EventHandler
	public void onPostProcessing(PostProcessingEvent event) {
		update();
		event.groups.add(getGroup());
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		group = null;
	}

	@Override
	public void decrement() {
		blur = Math.max(0, blur - 0.1F);
	}

	@Override
	public void increment() {
		blur = Math.min(1, blur + 0.1F);
	}

	public class MotionBlurShader implements IResource {

		@Override
		public ResourceLocation getResourceLocation() {
			return null;
		}

		@Override
		public InputStream getInputStream() {
			return IOUtils.toInputStream(String.format("{" +
					"    \"targets\": [" +
					"        \"swap\"," +
					"        \"previous\"" +
					"    ]," +
					"    \"passes\": [" +
					"        {" +
					"            \"name\": \"motion_blur\"," +
					"            \"intarget\": \"minecraft:main\"," +
					"            \"outtarget\": \"swap\"," +
					"            \"auxtargets\": [" +
					"                {" +
					"                    \"name\": \"PrevSampler\"," +
					"                    \"id\": \"previous\"" +
					"                }" +
					"            ]," +
					"            \"uniforms\": [" +
					"                {" +
					"                    \"name\": \"BlendFactor\"," +
					"                    \"values\": [ %s ]" +
					"                }" +
					"            ]" +
					"        }," +
					"        {" +
					"            \"name\": \"blit\"," +
					"            \"intarget\": \"swap\"," +
					"            \"outtarget\": \"previous\"" +
					"        }," +
					"        {" +
					"            \"name\": \"blit\"," +
					"            \"intarget\": \"swap\"," +
					"            \"outtarget\": \"minecraft:main\"" +
					"        }" +
					"    ]" +
					"}", blur, blur, blur));
		}

		@Override
		public boolean hasMetadata() {
			return false;
		}

		@Override
		public <T extends IMetadataSection> T getMetadata(String p_110526_1_) {
			return null;
		}

		@Override
		public String getResourcePackName() {
			return null;
		}

	}

}
