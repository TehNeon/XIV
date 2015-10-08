package pw.latematt.xiv.mod.mods;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.MoveEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.Timer;

import java.util.Objects;

/**
 * @author Jack
 * @author Matthew
 */
public class Freecam extends Mod {
    private Timer timer = new Timer();
    private double x, y, z;
    private float yaw, pitch;
    private final Listener packetListener;
    private final Listener motionListener;
    private final Listener moveListener;
    private EntityOtherPlayerMP entity;

    public Freecam() {
        super("Freecam", ModType.RENDER, Keyboard.KEY_V);

        this.motionListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                mc.thePlayer.renderArmPitch += 400.0F;

                if (timer.hasReached(7000L)) {
                    mc.renderGlobal.loadRenderers();
                    timer.reset();
                }
            }
        };

        this.moveListener = new Listener<MoveEvent>() {
            @Override
            public void onEventCalled(MoveEvent event) {
                mc.thePlayer.noClip = true;
                double speed = 4.0D;

                if (mc.gameSettings.ofKeyBindZoom.getIsKeyPressed()) {
                    speed /= 2.0D;
                }

                event.setMotionY(0.0D);
                mc.thePlayer.motionY = 0.0D;

                if (!mc.inGameHasFocus) {
                    return;
                }

                event.setMotionX(event.getMotionX() * speed);
                event.setMotionZ(event.getMotionZ() * speed);

                if (mc.thePlayer.movementInput.jump) {
                    event.setMotionY(speed / 4.0D);
                } else if (mc.thePlayer.movementInput.sneak) {
                    event.setMotionY(-(speed / 4.0D));
                }
            }
        };

        this.packetListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C0BPacketEntityAction) {
                    event.setCancelled(true);
                }

                if (event.getPacket() instanceof C03PacketPlayer) {
                    final C03PacketPlayer packetPlayer = (C03PacketPlayer) event.getPacket();
                    packetPlayer.setX(x);
                    packetPlayer.setY(y);
                    packetPlayer.setZ(z);
                    packetPlayer.setYaw(yaw);
                    packetPlayer.setPitch(pitch);
                }
            }
        };
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this.packetListener);
        XIV.getInstance().getListenerManager().add(this.motionListener);
        XIV.getInstance().getListenerManager().add(this.moveListener);

        if (Objects.nonNull(mc.thePlayer)) {
            this.x = mc.thePlayer.posX;
            this.y = mc.thePlayer.posY;
            this.z = mc.thePlayer.posZ;
            this.yaw = mc.thePlayer.rotationYaw;
            this.pitch = mc.thePlayer.rotationPitch;
            entity = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            entity.copyLocationAndAnglesFrom(mc.thePlayer);
            entity.clonePlayer(mc.thePlayer, true);
            mc.theWorld.addEntityToWorld(-1, entity);
            mc.renderGlobal.loadRenderers();
        }
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this.packetListener);
        XIV.getInstance().getListenerManager().remove(this.motionListener);
        XIV.getInstance().getListenerManager().remove(this.moveListener);

        if (Objects.nonNull(mc.thePlayer) && Objects.nonNull(entity)) {
            mc.thePlayer.noClip = false;
            mc.thePlayer.copyLocationAndAnglesFrom(entity);
            mc.theWorld.removeEntityFromWorld(-1);
            entity = null;
            mc.renderGlobal.loadRenderers();
        }
    }
}
