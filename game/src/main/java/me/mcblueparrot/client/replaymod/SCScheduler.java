/*
 * Includes modified decompiled Replay Mod class files.
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

package me.mcblueparrot.client.replaymod;

import com.google.common.util.concurrent.ListenableFutureTask;
import com.replaymod.core.mixin.MinecraftAccessor;
import com.replaymod.core.versions.scheduler.Scheduler;
import lombok.AllArgsConstructor;
import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.EventBus;
import me.mcblueparrot.client.events.EventHandler;
import me.mcblueparrot.client.events.PreRenderTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ReportedException;

import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SCScheduler implements Scheduler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final EventBus BUS = Client.INSTANCE.bus;
    private boolean inRunLater = false;

    public void runSync(Runnable runnable) throws InterruptedException, ExecutionException, TimeoutException {
        if(mc.isCallingFromMinecraftThread()) {
            runnable.run();
        }
        else {
            FutureTask<Void> future = new FutureTask<>(runnable, null);
            runLater(future);
            future.get(30L, TimeUnit.SECONDS);
        }
    }

    public void runPostStartup(Runnable runnable) {
        this.runLater(runnable);
    }

    public void runLaterWithoutLock(Runnable runnable) {
        this.runLater(() -> this.runLaterWithoutLock(runnable), runnable);
    }

    public void runLater(Runnable runnable) {
        this.runLater(runnable, () -> this.runLater(runnable));
    }

    private void runLater(Runnable runnable, final Runnable defer) {
        if(mc.isCallingFromMinecraftThread() && inRunLater) {
            Client.INSTANCE.bus.register(new TickListener(defer));
        }
        else {
            Queue<FutureTask<?>> tasks = ((MinecraftAccessor) mc).getScheduledTasks();

            synchronized(tasks) {
                tasks.add(ListenableFutureTask.create(() -> {
                    this.inRunLater = true;

                    try {
                        runnable.run();
                    }
                    catch (ReportedException error) {
                        error.printStackTrace();
                        System.err.println(error.getCrashReport().getCompleteReport());
                        mc.crashed(error.getCrashReport());
                    }
                    finally {
                        this.inRunLater = false;
                    }
                }, null));
            }
        }
    }

    public void runTasks() {
    }

    @AllArgsConstructor
    static class TickListener {

        private Runnable defer;

        @EventHandler
        public void onTick(PreRenderTickEvent event) {
            Client.INSTANCE.bus.unregister(this);
            defer.run();
        }

    }

}

