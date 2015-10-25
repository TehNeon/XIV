package pw.latematt.xiv.mod.mods.combat;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author Matthew
 */
public class Criticals extends Mod implements Listener<SendPacketEvent> {
    private float fallDist;

    public Criticals() {
        super("Criticals", ModType.COMBAT, Keyboard.KEY_NONE, 0xFFA38EC7);
    }

    @Override
    public void onEventCalled(SendPacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
            if (!isSafe())
                fallDist += mc.thePlayer.fallDistance;

            if (isSafe()) {
                player.setOnGround(true);
                fallDist = 0.0F;
                mc.thePlayer.fallDistance = 0.0F;
                setTag(getName());

                if (mc.thePlayer.onGround) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.01, mc.thePlayer.posZ, false));
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    fallDist += 1.01F;
                }
            } else if (fallDist > 0.0F) {
                player.setOnGround(false);
                setTag(String.format("%s \2477%s", getName(), "*"));
            } else {
                setTag(getName());
            }
        }
    }

    public float getFallDistance() {
        return fallDist;
    }

    public void setFallDistance(float fallDist) {
        this.fallDist = fallDist;
    }

    private boolean isSafe() {
        return mc.thePlayer.isInWater() ||
                mc.thePlayer.isInsideOfMaterial(Material.lava) ||
                mc.thePlayer.isOnLadder() ||
                mc.thePlayer.getActivePotionEffects().contains(Potion.BLINDNESS) ||
                mc.thePlayer.ridingEntity != null || fallDist >= 3.0F;
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.01, mc.thePlayer.posZ, false));
        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        fallDist += 1.01F;
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        fallDist = 0.0F;
    }
}
