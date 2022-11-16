package io.github.solclient.client.mod;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.mod.impl.discordrpc.DiscordIntegrationMod;
import io.github.solclient.client.mod.impl.hud.*;
import io.github.solclient.client.mod.impl.hud.armour.ArmourMod;
import io.github.solclient.client.mod.impl.hud.chat.ChatMod;
import io.github.solclient.client.mod.impl.hud.crosshair.CrosshairMod;
import io.github.solclient.client.mod.impl.hud.keystrokes.KeystrokesMod;
import io.github.solclient.client.mod.impl.hud.ping.PingMod;
import io.github.solclient.client.mod.impl.hud.speedometer.SpeedometerMod;
import io.github.solclient.client.mod.impl.hud.tablist.TabListMod;
import io.github.solclient.client.mod.impl.hud.timers.TimersMod;
import io.github.solclient.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import io.github.solclient.client.mod.impl.itemphysics.ItemPhysicsMod;
import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DefaultMods {

	public void register() {
		// Singleton hell, pretty much
		// TODO maybe there's a slightly less questionable approach
		Client.INSTANCE.register(
				SolClientConfig.INSTANCE,
				FpsMod.INSTANCE,
				CoordinatesMod.INSTANCE,
				KeystrokesMod.INSTANCE,
				CpsMod.INSTANCE,
				PingMod.INSTANCE,
				SpeedometerMod.INSTANCE,
				ReachDisplayMod.INSTANCE,
				ComboCounterMod.INSTANCE,
				PotionEffectsMod.INSTANCE,
				ArmourMod.INSTANCE,
				TimersMod.INSTANCE,
				ChatMod.INSTANCE,
				TabListMod.INSTANCE,
				CrosshairMod.INSTANCE,
				ScoreboardMod.INSTANCE,
				TweaksMod.INSTANCE,
				MotionBlurMod.INSTANCE,
				MenuBlurMod.INSTANCE,
				ColourSaturationMod.INSTANCE,
				ChunkAnimatorMod.INSTANCE,
				FreelookMod.INSTANCE,
				TaplookMod.INSTANCE,
				ToggleSprintMod.INSTANCE,
				TNTTimerMod.INSTANCE,
				V1_7VisualsMod.INSTANCE,
				ItemPhysicsMod.INSTANCE,
				ZoomMod.INSTANCE,
				ParticlesMod.INSTANCE,
				TimeChangerMod.INSTANCE,
				BlockSelectionMod.INSTANCE,
				HitboxMod.INSTANCE,
				HitColourMod.INSTANCE,
				HypixelAdditionsMod.INSTANCE,
				QuickPlayMod.INSTANCE,
				DiscordIntegrationMod.INSTANCE
		);
	}

}
