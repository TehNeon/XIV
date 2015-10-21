package pw.latematt.xiv.mod.mods.misc;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.StringUtils;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

import java.util.regex.Matcher;

/**
 * @author Matthew
 */
public class DashNames extends Mod implements Listener<SendPacketEvent> {
    public DashNames() {
        super("DashNames", ModType.MISCELLANEOUS);
        setEnabled(true);
    }

    @Override
    public void onEventCalled(SendPacketEvent event) {
        if (event.getPacket() instanceof C01PacketChatMessage) {
            C01PacketChatMessage message = (C01PacketChatMessage) event.getPacket();
            for (Object o : mc.ingameGUI.getTabList().getPlayerList()) {
                NetworkPlayerInfo playerInfo = (NetworkPlayerInfo) o;
                String mcName = StringUtils.stripControlCodes(mc.ingameGUI.getTabList().getPlayerName(playerInfo));
                if (XIV.getInstance().getFriendManager().isFriend(mcName)) {
                    String alias = XIV.getInstance().getFriendManager().getContents().get(mcName);
                    message.setMessage(message.getMessage().replaceAll("(?i)" + Matcher.quoteReplacement("-" + alias), mcName));
                }
            }
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
    }
}
