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

package io.github.solclient.client.mod.impl.replay.fix;

import java.util.Queue;
import java.util.concurrent.*;

import com.google.common.util.concurrent.ListenableFutureTask;
import com.replaymod.core.mixin.MinecraftAccessor;
import com.replaymod.core.versions.scheduler.Scheduler;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PreRenderTickEvent;
import lombok.AllArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashException;

/*
 * Includes modified decompiled Replay Mod class files (I didn't want to remove all the preprocessor comments :P).
 *
 * License for Replay Mod:
 *
 *     Copyright (C) <year>  <name of author>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
public class SCScheduler implements Scheduler {

	private static final MinecraftClient mc = MinecraftClient.getInstance();
	private boolean inRunLater = false;

	@Override
	public void runSync(Runnable runnable) throws InterruptedException, ExecutionException, TimeoutException {
		if (mc.isOnThread()) {
			runnable.run();
		} else {
			FutureTask<Void> future = new FutureTask<>(runnable, null);
			runLater(future);
			future.get(30L, TimeUnit.SECONDS);
		}
	}

	@Override
	public void runPostStartup(Runnable runnable) {
		this.runLater(runnable);
	}

	@Override
	public void runLaterWithoutLock(Runnable runnable) {
		this.runLater(() -> this.runLaterWithoutLock(runnable), runnable);
	}

	@Override
	public void runLater(Runnable runnable) {
		this.runLater(runnable, () -> this.runLater(runnable));
	}

	private void runLater(Runnable runnable, final Runnable defer) {
		if (mc.isOnThread() && inRunLater) {
			Client.INSTANCE.getEvents().register(new TickListener(defer));
		} else {
			Queue<FutureTask<?>> tasks = ((MinecraftAccessor) mc).getScheduledTasks();

			synchronized (tasks) {
				tasks.add(ListenableFutureTask.create(() -> {
					this.inRunLater = true;

					try {
						runnable.run();
					} catch (CrashException error) {
						error.printStackTrace();
						System.err.println(error.getReport().asString());
						mc.crash(error.getReport());
					} finally {
						this.inRunLater = false;
					}
				}, null));
			}
		}
	}

	@Override
	public void runTasks() {
	}

	@AllArgsConstructor
	static class TickListener {

		private Runnable defer;

		@EventHandler
		public void onTick(PreRenderTickEvent event) {
			Client.INSTANCE.getEvents().unregister(this);
			defer.run();
		}

	}

}
