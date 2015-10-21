package pw.latematt.xiv.mod.mods.player;

import net.minecraft.init.Items;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.Timer;
import pw.latematt.xiv.value.Value;

import java.util.Objects;

import static pw.latematt.xiv.utils.ItemUtils.*;

/**
 * @author Matthew
 */
public class AutoHeal extends Mod implements CommandHandler {
    private final Value<Long> delay = new Value<>("autoheal_delay", 350L);
    private final Value<Float> health = new Value<>("autoheal_health", 13.0F);
    private final Value<Boolean> soup = new Value<>("autoheal_soup", false);
    private final Value<Boolean> potion = new Value<>("autoheal_potion", true);
    private final Listener sendPacketListener;
    private final Listener motionUpdateListener;
    private final Timer timer = new Timer();
    private boolean healing;

    public AutoHeal() {
        super("AutoHeal", ModType.PLAYER, Keyboard.KEY_NONE, 0xFF85E0E0);

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (Objects.equals(event.getCurrentState(), MotionUpdateEvent.State.PRE)) {
                    if (mc.thePlayer.getHealth() <= health.getValue() && timer.hasReached(delay.getValue())) {
                        if (soup.getValue()) {
                            dropFirst(Items.bowl);
                            if (!hotbarHas(Items.mushroom_stew)) {
                                getFromInventory(Items.mushroom_stew);
                            }

                            useFirst(Items.mushroom_stew);

                            if (mc.thePlayer.getHealth() <= health.getValue()) {
                                if (!hotbarHasInstantHealth()) {
                                    getInstantHealthFromInventory();
                                }

                                if (hotbarHasInstantHealth()) {
                                    healing = true;
                                    event.setYaw(-event.getYaw());
                                    event.setPitch(85);
                                }
                            } else {
                                timer.reset();
                            }
                        } else if (potion.getValue()) {
                            if (!hotbarHasInstantHealth()) {
                                getInstantHealthFromInventory();
                            }

                            if (hotbarHasInstantHealth()) {
                                healing = true;
                                event.setYaw(-event.getYaw());
                                event.setPitch(85);
                            }
                        }
                    }
                } else if (Objects.equals(event.getCurrentState(), MotionUpdateEvent.State.POST)) {
                    if (healing) {
                        useFirstInstantHealth();
                        healing = false;
                        timer.reset();
                    }
                }
            }
        };

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    if (healing) {
                        player.setYaw(-player.getYaw());
                        player.setPitch(85);
                    }
                }
            }
        };

        Command.newCommand()
                .cmd("autoheal")
                .aliases("aheal", "autoh", "ah")
                .description("Base command for the AutoHeal mod.")
                .arguments("<action>")
                .handler(this).build();
    }

    public boolean isHealing() {
        return healing;
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "delay":
                case "d":
                    if (arguments.length >= 3) {
                        String newDelayString = arguments[2];
                        try {
                            if (arguments[2].equalsIgnoreCase("-d")) {
                                delay.setValue(delay.getDefault());
                            } else {
                                long newDelay = Long.parseLong(newDelayString);
                                if (newDelay < 10) {
                                    newDelay = 10;
                                }

                                delay.setValue(newDelay);
                            }
                            ChatLogger.print(String.format("AutoHeal Delay set to %sms", delay.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newDelayString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: autoheal delay <number>");
                    }
                    break;
                case "health":
                    if (arguments.length >= 3) {
                        String newHealthString = arguments[2];
                        try {
                            if (arguments[2].equalsIgnoreCase("-d")) {
                                health.setValue(health.getDefault());
                            } else {
                                Float newHealth = Float.parseFloat(newHealthString);
                                health.setValue(newHealth);
                            }
                            ChatLogger.print(String.format("AutoHeal Health set to %s", health.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newHealthString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: autoheal health <number>");
                    }
                    break;
                case "potion":
                case "pot":
                    useFirstInstantHealth();
                    break;
                case "soup":
                    dropFirst(Items.bowl);
                    useFirst(Items.mushroom_stew);
                    break;
                case "usepotions":
                case "usepots":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            potion.setValue(potion.getDefault());
                        } else {
                            potion.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        potion.setValue(!potion.getValue());
                    }
                    ChatLogger.print(String.format("AutoHeal will %s use potions.", (potion.getValue() ? "now" : "no longer")));
                    break;
                case "usesoups":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            soup.setValue(soup.getDefault());
                        } else {
                            soup.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        soup.setValue(!soup.getValue());
                    }
                    ChatLogger.print(String.format("AutoHeal will %s use soups.", (soup.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: delay, health, potion, soup, usepotions, usesoups");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: autoheal <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(sendPacketListener);
        XIV.getInstance().getListenerManager().add(motionUpdateListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(sendPacketListener);
        XIV.getInstance().getListenerManager().remove(motionUpdateListener);
    }
}
