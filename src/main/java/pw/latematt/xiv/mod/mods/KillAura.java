package pw.latematt.xiv.mod.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.aura.AuraMode;
import pw.latematt.xiv.mod.mods.aura.Singular;
import pw.latematt.xiv.mod.mods.aura.Switch;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 */
public class KillAura extends Mod implements CommandHandler {
    private final Listener motionUpdateListener;
    private final Listener sendPacketListener;
    public final Value<Long> delay = new Value<>("killaura_delay", 125L);
    public final Value<Double> range = new Value<>("killaura_range", 3.9);
    private final Value<Boolean> players = new Value<>("killaura_players", true);
    private final Value<Boolean> mobs = new Value<>("killaura_mobs", false);
    private final Value<Boolean> animals = new Value<>("killaura_animals", false);
    private final Value<Boolean> invisible = new Value<>("killaura_invisible", true);
    private final Value<Boolean> team = new Value<>("killaura_team", false);
    public final Value<Boolean> silent = new Value<>("killaura_silent", true);
    public final Value<Boolean> autosword = new Value<>("killaura_autosword", true);
    private final Value<Boolean> toggledeath = new Value<>("killaura_toggledeath", false);
    public final Value<Boolean> autoblock = new Value<>("killaura_autoblock", false);
    private final Value<AuraMode> mode = new Value<>("killaura_mode", new Singular(this));

    public KillAura() {
        super("Kill Aura", ModType.COMBAT, Keyboard.KEY_R, 0xFFC6172B);

        Command.newCommand()
                .cmd("killaura")
                .description("Base command for the Kill Aura mod.")
                .aliases("killa", "ka")
                .arguments("<action>")
                .handler(this)
                .build();

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (toggledeath.getValue() && mc.thePlayer.isDead) {
                    toggle();
                }

                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    mode.getValue().onPreMotionUpdate(event);
                } else if (event.getCurrentState() == MotionUpdateEvent.State.POST) {
                    mode.getValue().onPostMotionUpdate(event);
                }
            }
        };

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    mode.getValue().onMotionPacket(player);
                }
            }
        };

        setTag(String.format("%s \2477%s", getName(), mode.getValue().getName()));
    }

    public void attack(EntityLivingBase target) {
        final boolean wasSprinting = mc.thePlayer.isSprinting();
        if (autosword.getValue()) {
            mc.thePlayer.inventory.currentItem = EntityUtils.getBestWeapon(target);
            mc.playerController.updateController();
        }

        // stop sprinting
        mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));

        mc.thePlayer.swingItem();
        int oldDamage = 0;
        if (mc.thePlayer.getCurrentEquippedItem() != null) {
            oldDamage = mc.thePlayer.getCurrentEquippedItem().getItemDamage();
        }
        mc.playerController.attackEntity(mc.thePlayer, target);

        if (mc.thePlayer.getCurrentEquippedItem() != null) {
            mc.thePlayer.getCurrentEquippedItem().setItemDamage(oldDamage);
        }

        // continue sprinting
        if (wasSprinting) {
            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        }
        mc.thePlayer.setSprinting(wasSprinting);
    }

    public boolean isValidEntity(EntityLivingBase entity) {
        if (entity == null)
            return false;
        if (entity == mc.thePlayer)
            return false;
        if (!entity.isEntityAlive())
            return false;
        if (entity.ticksExisted < 20)
            return false;
        if (mc.thePlayer.getDistanceToEntity(entity) > range.getValue())
            return false;
        if (!(invisible.getValue()) && entity.isInvisibleToPlayer(mc.thePlayer))
            return false;
        if (team.getValue() && entity.getTeam() != null && entity.getTeam().isSameTeam(mc.thePlayer.getTeam()))
            return false;
        // 85.136.70.107
        if (entity instanceof EntityPlayer) {
            return players.getValue() && !XIV.getInstance().getFriendManager().isFriend(entity.getCommandSenderEntity().getName());
        } else if (entity instanceof IAnimals && !(entity instanceof IMob)) {
            return animals.getValue();
        } else if (entity instanceof IMob) {
            return mobs.getValue();
        }
        return false;
    }

    // used in autoblock
    public boolean isAttacking() {
        return mode.getValue().isAttacking();
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
                            long newDelay = Long.parseLong(newDelayString);
                            delay.setValue(newDelay);
                            ChatLogger.print(String.format("Kill Aura Delay set to %s", delay.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newDelayString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura delay <number>");
                    }
                    break;
                case "aps":
                    if (arguments.length >= 3) {
                        String newApsString = arguments[2];
                        try {
                            long newAps = Long.parseLong(newApsString);
                            delay.setValue(1000 / newAps);
                            ChatLogger.print(String.format("Kill Aura Attacks/second set to %s (%sms)", newAps, delay.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newApsString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura aps <number>");
                    }
                    break;
                case "range":
                case "r":
                    if (arguments.length >= 3) {
                        String newRangeString = arguments[2];
                        try {
                            double newRange = Double.parseDouble(newRangeString);
                            range.setValue(newRange);
                            ChatLogger.print(String.format("Kill Aura Range set to %s", range.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newRangeString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura range <number>");
                    }
                    break;
                case "players":
                case "plyrs":
                    players.setValue(!players.getValue());
                    ChatLogger.print(String.format("Kill Aura will %s attack players.", (players.getValue() ? "now" : "no longer")));
                    break;
                case "mobs":
                    mobs.setValue(!mobs.getValue());
                    ChatLogger.print(String.format("Kill Aura will %s attack mobs.", (mobs.getValue() ? "now" : "no longer")));
                    break;
                case "animals":
                    animals.setValue(!animals.getValue());
                    ChatLogger.print(String.format("Kill Aura will %s attack animals.", (animals.getValue() ? "now" : "no longer")));
                    break;
                case "invisible":
                case "invis":
                    invisible.setValue(!invisible.getValue());
                    ChatLogger.print(String.format("Kill Aura will %s attack invisible entities.", (invisible.getValue() ? "now" : "no longer")));
                    break;
                case "toggledeath":
                case "tdeath":
                    toggledeath.setValue(!toggledeath.getValue());
                    ChatLogger.print(String.format("Kill Aura will %s toggle on death.", (toggledeath.getValue() ? "now" : "no longer")));
                    break;
                case "team":
                    team.setValue(!team.getValue());
                    ChatLogger.print(String.format("Kill Aura will %s attack your team.", (team.getValue() ? "no longer" : "now")));
                    break;
                case "silent":
                case "sil":
                    silent.setValue(!silent.getValue());
                    ChatLogger.print(String.format("Kill Aura will %s silently aim.", (silent.getValue() ? "now" : "no longer")));
                    break;
                case "autosword":
                case "as":
                    autosword.setValue(!autosword.getValue());
                    ChatLogger.print(String.format("Kill Aura will %s automatically switch to your sword.", (autosword.getValue() ? "now" : "no longer")));
                    break;
                case "autoblock":
                case "ab":
                    autoblock.setValue(!autoblock.getValue());
                    ChatLogger.print(String.format("Kill Aura will %s automatically block for you.", (autoblock.getValue() ? "now" : "no longer")));
                    break;
                case "mode":
                    if (arguments.length >= 3) {
                        String mode = arguments[2];
                        switch (mode) {
                            case "singular":
                                this.mode.setValue(new Singular(this));
                                ChatLogger.print(String.format("Kill Aura Mode set to %s", this.mode.getValue().getName()));
                                break;
                            case "switch":
                                this.mode.setValue(new Switch(this));
                                ChatLogger.print(String.format("Kill Aura Mode set to %s", this.mode.getValue().getName()));
                                break;
                            default:
                                ChatLogger.print("Invalid mode, valid: singular, switch");
                                break;
                        }
                        setTag(String.format("%s \2477%s", getName(), this.mode.getValue().getName()));
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura mode <mode>");
                    }
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: delay, aps, range, players, mobs, animals, invisible, team, silent, autosword, autoblock, mode");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: killaura <action>");
        }
    }

    public AuraMode getMode() {
        return mode.getValue();
    }

    public void setMode(AuraMode mode) {
        this.mode.setValue(mode);
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(motionUpdateListener);
        XIV.getInstance().getListenerManager().add(sendPacketListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(motionUpdateListener);
        XIV.getInstance().getListenerManager().remove(sendPacketListener);
        mode.getValue().onDisabled();
    }
}