package me.mcblueparrot.client;

import java.util.List;
import java.util.Objects;

import com.sun.jna.platform.win32.WinDef;
import me.mcblueparrot.client.ChatChannelSystem.ChatChannel.DefaultChatChannel;
import me.mcblueparrot.client.ui.ChatButton;
import me.mcblueparrot.client.util.Colour;
import me.mcblueparrot.client.util.Rectangle;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.access.AccessGuiChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import tv.twitch.chat.Chat;

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

    public static class ChatChannelButton implements ChatButton {

        public static final ChatChannelButton INSTANCE = new ChatChannelButton();

        private ChatChannelButton() {}

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public int getWidth() {
            return Math.max(isOpen() ? getPopupWidth() : 0, ChatButton.super.getWidth());
        }

        @Override
        public int getPopupWidth() {
            return Math.max(ChatButton.super.getWidth(),
                    Minecraft.getMinecraft().fontRendererObj.getStringWidth(Client.INSTANCE.getChatChannelSystem()
                            .getChannels().stream().map(ChatChannel::getName).max(Utils.STRING_WIDTH_COMPARATOR).get()) + 2);
        }

        @Override
        public int getPopupHeight() {
            return (Client.INSTANCE.getChatChannelSystem().getChannels().size() * 13) - 1;
        }

        @Override
        public String getText() {
            return Client.INSTANCE.getChatChannelSystem().getChannelName() + (isOpen() ? " ▼" : " ▲");
        }

        @Override
        public void render(int x, int y, boolean mouseDown, boolean wasMouseDown, boolean wasMouseClicked, int mouseX, int mouseY) {
            FontRenderer font = Minecraft.getMinecraft().fontRendererObj;

            for(ChatChannel channel : Client.INSTANCE.getChatChannelSystem().getChannels()) {
                Rectangle optionBounds = new Rectangle(x, y, getPopupWidth(), 12);
                boolean hovered = optionBounds.contains(mouseX, mouseY);
                optionBounds.fill(hovered ? Colour.WHITE_128 : Colour.BLACK_128);
                if(hovered && wasMouseClicked) {
                    Utils.playClickSound();
                    AccessGuiChat.getInstance().setSelectedChatButton(null);
                    Client.INSTANCE.getChatChannelSystem().setChannel(channel);
                }

                font.drawString(channel.getName(),
                        optionBounds.getX() + (optionBounds.getWidth() / 2)
                                - (font.getStringWidth(channel.getName()) / 2),
                        optionBounds.getY() + (optionBounds.getHeight() / 2) - (font.FONT_HEIGHT / 2), hovered ? 0 :
                                -1);
                y += 13;
            }
        }

    }
}
