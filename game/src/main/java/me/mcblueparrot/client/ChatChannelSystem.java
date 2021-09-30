package me.mcblueparrot.client;

import java.util.List;
import java.util.Objects;

import me.mcblueparrot.client.ChatChannelSystem.ChatChannel.DefaultChatChannel;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C01PacketChatMessage;

public abstract class ChatChannelSystem {

    public static final ChatChannel ALL = new DefaultChatChannel("All", null);

    private ChatChannel channel = ALL;

    public abstract List<ChatChannel> getChannels();

    public static ChatChannel getPrivateChannel(String player) {
        return new DefaultChatChannel(player, "msg " + player);
    }

    public ChatChannel getChannel() {
        return channel;
    }

    public String getChannelName() {
        return channel.getName();
    }

    public void setChannel(ChatChannel channel) {
        this.channel = channel;
    }

    public interface ChatChannel {

        public String getName();

        public void sendMessage(EntityPlayerSP player, String message);

        public class DefaultChatChannel implements ChatChannel {

            private String name;
            private String command;

            public DefaultChatChannel(String name, String command) {
                this.name = name;
                this.command = command;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public void sendMessage(EntityPlayerSP player, String message) {
                if(command == null) {
                    player.sendQueue.addToSendQueue(new C01PacketChatMessage(message));
                }
                else {
                    player.sendChatMessage("/" + command + " " + message);
                }
            }

            @Override
            public int hashCode() {
                return Objects.hash(command);
            }

            @Override
            public boolean equals(Object obj) {
                if(this == obj) return true;

                if(obj == null) return false;

                if(getClass() != obj.getClass()) return false;

                DefaultChatChannel other = (DefaultChatChannel) obj;
                return Objects.equals(command, other.command);
            }

        }

    }

}
