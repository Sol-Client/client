package io.github.solclient.client.mod;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.impl.BlockSelectionMod;
import io.github.solclient.client.mod.impl.ChunkAnimatorMod;
import io.github.solclient.client.mod.impl.ColourSaturationMod;
import io.github.solclient.client.mod.impl.FreelookMod;
import io.github.solclient.client.mod.impl.HitColourMod;
import io.github.solclient.client.mod.impl.HitboxMod;
import io.github.solclient.client.mod.impl.MenuBlurMod;
import io.github.solclient.client.mod.impl.MotionBlurMod;
import io.github.solclient.client.mod.impl.ParticlesMod;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.mod.impl.TNTTimerMod;
import io.github.solclient.client.mod.impl.TaplookMod;
import io.github.solclient.client.mod.impl.TimeChangerMod;
import io.github.solclient.client.mod.impl.ToggleSprintMod;
import io.github.solclient.client.mod.impl.TweaksMod;
import io.github.solclient.client.mod.impl.V1_7VisualsMod;
import io.github.solclient.client.mod.impl.ZoomMod;
import io.github.solclient.client.mod.impl.discordrpc.DiscordIntegrationMod;
import io.github.solclient.client.mod.impl.hud.ComboCounterMod;
import io.github.solclient.client.mod.impl.hud.CoordinatesMod;
import io.github.solclient.client.mod.impl.hud.CpsMod;
import io.github.solclient.client.mod.impl.hud.FpsMod;
import io.github.solclient.client.mod.impl.hud.PotionEffectsMod;
import io.github.solclient.client.mod.impl.hud.ReachDisplayMod;
import io.github.solclient.client.mod.impl.hud.ScoreboardMod;
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

public class DefaultMods {

	public static void register() {
		// Singleton hell, pretty much
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
