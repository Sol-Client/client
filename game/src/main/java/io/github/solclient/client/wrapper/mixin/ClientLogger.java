package io.github.solclient.client.wrapper.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.logging.LoggerAdapterAbstract;

public class ClientLogger extends LoggerAdapterAbstract {

	private static final org.apache.logging.log4j.Level[] BY_ORDINAL = {
			org.apache.logging.log4j.Level.FATAL,
			org.apache.logging.log4j.Level.ERROR, org.apache.logging.log4j.Level.WARN,
			org.apache.logging.log4j.Level.INFO, org.apache.logging.log4j.Level.DEBUG,
			org.apache.logging.log4j.Level.TRACE
	};

	private final Logger logger;

	public ClientLogger(String name) {
		super(name);
		logger = LogManager.getLogger(name);
	}

	@Override
	public String getType() {
		return "Sol Client";
	}

	private org.apache.logging.log4j.Level map(Level level) {
		return BY_ORDINAL[level.ordinal()];
	}

	@Override
	public void catching(Level level, Throwable t) {
		logger.catching(map(level), t);
	}

	@Override
	public void log(Level level, String message, Object... params) {
		logger.log(map(level), message, params);
	}

	@Override
	public void log(Level level, String message, Throwable t) {
		logger.log(map(level), message, t);
	}

	@Override
	public <T extends Throwable> T throwing(T t) {
		return logger.throwing(t);
	}

}
