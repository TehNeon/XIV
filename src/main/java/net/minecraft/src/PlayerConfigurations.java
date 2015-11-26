package net.minecraft.src;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class PlayerConfigurations {
    private static Map mapConfigurations = null;

    public static void renderPlayerItems(ModelBiped modelBiped, AbstractClientPlayer player, float scale, float partialTicks) {
        PlayerConfiguration cfg = getPlayerConfiguration(player);

        if (cfg != null) {
            cfg.renderPlayerItems(modelBiped, player, scale, partialTicks);
        }
    }

    public static synchronized PlayerConfiguration getPlayerConfiguration(AbstractClientPlayer player) {
        String name = getPlayerName(player);
        PlayerConfiguration pc = (PlayerConfiguration) getMapConfigurations().get(name);

        if (pc == null) {
            pc = new PlayerConfiguration();
            getMapConfigurations().put(name, pc);
            PlayerConfigurationReceiver pcl = new PlayerConfigurationReceiver(name);
            String url = "http://s.optifine.net/users/" + name + ".cfg";
            FileDownloadThread fdt = new FileDownloadThread(url, pcl);
            fdt.start();
        }

        return pc;
    }

    public static synchronized void setPlayerConfiguration(String player, PlayerConfiguration pc) {
        getMapConfigurations().put(player, pc);
    }

    private static Map getMapConfigurations() {
        if (mapConfigurations == null) {
            mapConfigurations = new HashMap();
        }

        return mapConfigurations;
    }

    public static String getPlayerName(AbstractClientPlayer player) {
        String username = player.getName();

        if (username != null && !username.isEmpty()) {
            username = StringUtils.stripControlCodes(username);
        }

        return username;
    }
}
