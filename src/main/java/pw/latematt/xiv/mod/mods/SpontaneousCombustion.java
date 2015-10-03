package pw.latematt.xiv.mod.mods;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.Timer;

/**
 * @author Rederpz
 */
public class SpontaneousCombustion extends Mod implements Listener<MotionUpdateEvent> {
    private final Timer timer = new Timer();
    private BlockPos placed = null;

    public SpontaneousCombustion() {
        super("Spontaneous Combustion", ModType.PLAYER, Keyboard.KEY_0, 0xFF006600);
    }

    public void onEventCalled(MotionUpdateEvent event) {
        boolean hasLava = mc.thePlayer.getHeldItem().getItem() instanceof ItemBucket || (mc.thePlayer.getHeldItem().getItem() == Items.bucket && placed != null);
        boolean canPlace = mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFlintAndSteel || hasLava);

        for(Object o: mc.theWorld.playerEntities) {
            EntityPlayer player = (EntityPlayer) o;

            if(player != mc.thePlayer && player.getDistanceToEntity(mc.thePlayer) < 4.2F && player.onGround) {
                if(canPlace) {
                    BlockPos pos = new BlockPos(player.posX, player.posY - 1, player.posZ);

                    if(mc.thePlayer.getHeldItem().getItem() instanceof ItemFlintAndSteel) {
                        if (!(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockFire) && timer.hasReached(10L)) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(pos, 1, mc.thePlayer.getHeldItem(), -1, -1, -1));

                            timer.reset();
                        }
                    }else{
                        System.out.println("TEST");
                        if(timer.hasReached(50L)) {
                            if (placed != null) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(pos, 1, mc.thePlayer.getHeldItem(), -1, -1, -1));

                                placed = null;
                            } else {
                                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(pos, 1, mc.thePlayer.getHeldItem(), -1, -1, -1));

                                placed = pos;
                            }
                        }
                    }
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
