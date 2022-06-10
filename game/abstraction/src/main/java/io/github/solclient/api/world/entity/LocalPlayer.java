package io.github.solclient.api.world.entity;

import io.github.solclient.api.network.Connection;
import io.github.solclient.api.text.Component;

public interface LocalPlayer {

	Connection getConnection();

	void sendSystemMessage(String text);

	void sendSystemMessage(Component text);

	void chat(String text);

}
