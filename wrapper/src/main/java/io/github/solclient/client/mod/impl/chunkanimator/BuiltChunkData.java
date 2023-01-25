package io.github.solclient.client.mod.impl.chunkanimator;

public interface BuiltChunkData {

	long getAnimationStart();

	void setAnimationStart(long animationStart);

	boolean isAnimationComplete();

	void skipAnimation();

}
