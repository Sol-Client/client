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

package io.github.solclient.wrapper;

import org.apache.logging.log4j.*;
import org.spongepowered.asm.logging.*;
import org.spongepowered.asm.logging.Level;

/**
 * Implementation of ILogger for Log4j.
 */
final class Log4jLogger implements ILogger {

	private final Logger backingLogger = LogManager.getLogger();
	private final String name;

	public Log4jLogger(String name) {
		this.name = name;
	}

	@Override
	public String getId() {
		return name;
	}

	@Override
	public String getType() {
		return "log4j";
	}

	private static org.apache.logging.log4j.Level l4j(Level level) {
		switch (level) {
			case DEBUG:
				return org.apache.logging.log4j.Level.DEBUG;
			case ERROR:
				return org.apache.logging.log4j.Level.ERROR;
			case FATAL:
				return org.apache.logging.log4j.Level.FATAL;
			case TRACE:
				return org.apache.logging.log4j.Level.TRACE;
			case WARN:
				return org.apache.logging.log4j.Level.WARN;
			default:
			case INFO:
				return org.apache.logging.log4j.Level.INFO;
		}
	}

	@Override
	public void catching(Level level, Throwable t) {
		backingLogger.catching(l4j(level), t);
	}

	@Override
	public void catching(Throwable t) {
		backingLogger.catching(t);
	}

	@Override
	public void debug(String message, Object... params) {
		backingLogger.debug(message, params);
	}

	@Override
	public void debug(String message, Throwable t) {
		backingLogger.debug(message, t);
	}

	@Override
	public void error(String message, Object... params) {
		backingLogger.error(message, params);
	}

	@Override
	public void error(String message, Throwable t) {
		backingLogger.error(message, t);
	}

	@Override
	public void fatal(String message, Object... params) {
		backingLogger.fatal(message, params);
	}

	@Override
	public void fatal(String message, Throwable t) {
		backingLogger.fatal(message, t);
	}

	@Override
	public void info(String message, Object... params) {
		backingLogger.info(message, params);
	}

	@Override
	public void info(String message, Throwable t) {
		backingLogger.info(message, t);
	}

	@Override
	public void log(Level level, String message, Object... params) {
		backingLogger.log(l4j(level), message, params);
	}

	@Override
	public void log(Level level, String message, Throwable t) {
		backingLogger.log(l4j(level), message, t);
	}

	@Override
	public <T extends Throwable> T throwing(T t) {
		return backingLogger.throwing(t);
	}

	@Override
	public void trace(String message, Object... params) {
		backingLogger.trace(message, params);
	}

	@Override
	public void trace(String message, Throwable t) {
		backingLogger.trace(message, t);
	}

	@Override
	public void warn(String message, Object... params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void warn(String message, Throwable t) {
		// TODO Auto-generated method stub

	}

}
