package pw.latematt.xiv.mod.mods.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.Timer;
import pw.latematt.xiv.value.Value;

import java.util.Objects;
import java.util.Random;

/**
 * @author TehNeon
 */
public class Triggerbot extends Mod implements Listener<MotionUpdateEvent>, CommandHandler {
    public final Value<Long> delay = new Value<>("triggerbot_delay", 125L);
    public final Value<Boolean> weaponOnly = new Value<>("triggerbot_weapon_only", true);
    private final Value<Boolean> players = new Value<>("triggerbot_players", true);
    private final Value<Boolean> mobs = new Value<>("triggerbot_mobs", false);
    private final Value<Boolean> animals = new Value<>("triggerbot_animals", false);
    private final Value<Boolean> invisible = new Value<>("triggerbot_invisible", false);
    private final Value<Boolean> team = new Value<>("triggerbot_team", true);
    private final Timer timer = new Timer();
    private final Random random = new Random();

    public Triggerbot() {
        super("Triggerbot", ModType.COMBAT, Keyboard.KEY_NONE, 0xFF287628);

        Command.newCommand()
                .cmd("triggerbot")
                .description("Base command for Triggerbot mod.")
                .arguments("<action>")
                .aliases("trigbot", "trig")
                .handler(this)
                .build();
    }

    public void onEventCalled(MotionUpdateEvent event) {
        if (Objects.equals(event.getCurrentState(), MotionUpdateEvent.State.POST)) {
            MovingObjectPosition objectMouseOver = mc.objectMouseOver;
            if (objectMouseOver == null)
                return;
            Entity entity = objectMouseOver.entityHit;
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) entity;
                long skip = getRandom(0, 75);
                if (timer.hasReached(delay.getValue() + skip)) {
                    if (!isValidEntity(living))
                        return;

                    if (!properWeapon(mc.thePlayer.getHeldItem()))
                        return;

                    mc.thePlayer.swingItem();
                    mc.playerController.attackEntity(Minecraft.getMinecraft().thePlayer, living);

                    timer.reset();
                }
            }
        }
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
        if (!invisible.getValue() && entity.isInvisibleToPlayer(mc.thePlayer))
            return false;
        if (!team.getValue() && entity.getTeam() != null && entity.getTeam().isSameTeam(mc.thePlayer.getTeam()))
            return false;
        if (entity instanceof EntityPlayer) {
            return players.getValue() && !XIV.getInstance().getFriendManager().isFriend(entity.getCommandSenderEntity().getName());
        } else if (entity instanceof IAnimals && !(entity instanceof IMob)) {
            if (entity instanceof EntityHorse) {
                EntityHorse horse = (EntityHorse) entity;
                return animals.getValue() && horse.riddenByEntity != mc.thePlayer;
            }
            return animals.getValue();
        } else if (entity instanceof IMob) {
            return mobs.getValue();
        }
        return false;
    }

    public boolean properWeapon(ItemStack itemStack) {
        if (weaponOnly.getValue()) {
            if (itemStack != null) {
                Item item = itemStack.getItem();

                if (item instanceof ItemSword || item instanceof ItemAxe) {
                    return true;
                }
            }

            return false;
        }

        return true;
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
                            long newDelay = arguments[2].equalsIgnoreCase("-d") ? delay.getDefault() : Long.parseLong(newDelayString);
                            delay.setValue(newDelay);
                            ChatLogger.print(String.format("Triggerbot delay set to %s", delay.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newDelayString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: triggerbot delay <number>");
                    }
                    break;
                case "weapononly":
                case "weapon":
                case "sword":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            weaponOnly.setValue(weaponOnly.getDefault());
                        } else {
                            weaponOnly.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        weaponOnly.setValue(!weaponOnly.getValue());
                    }
                    ChatLogger.print(String.format("Triggerbot will %s require a weapon.", (weaponOnly.getValue() ? "now" : "no longer")));
                    break;
                case "players":
                case "plyrs":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            players.setValue(players.getDefault());
                        } else {
                            players.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        players.setValue(!players.getValue());
                    }
                    ChatLogger.print(String.format("Triggerbot will %s attack players.", (players.getValue() ? "now" : "no longer")));
                    break;
                case "mobs":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            mobs.setValue(mobs.getDefault());
                        } else {
                            mobs.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        mobs.setValue(!mobs.getValue());
                    }
                    ChatLogger.print(String.format("Triggerbot will %s attack mobs.", (mobs.getValue() ? "now" : "no longer")));
                    break;
                case "animals":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            animals.setValue(animals.getDefault());
                        } else {
                            animals.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        animals.setValue(!animals.getValue());
                    }
                    ChatLogger.print(String.format("Triggerbot will %s attack animals.", (animals.getValue() ? "now" : "no longer")));
                    break;
                case "invisible":
                case "invis":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            invisible.setValue(invisible.getDefault());
                        } else {
                            invisible.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        invisible.setValue(!invisible.getValue());
                    }
                    ChatLogger.print(String.format("Triggerbot will %s attack invisible entities.", (invisible.getValue() ? "now" : "no longer")));
                    break;
                case "team":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            team.setValue(team.getDefault());
                        } else {
                            team.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        team.setValue(!team.getValue());
                    }
                    ChatLogger.print(String.format("Triggerbot will %s attack your team.", (team.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: delay, weapon, players, mobs, animals, invisible, team");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: triggerbot <action>");
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

    public int getRandom(int floor, int cap) {
        return floor + random.nextInt(cap);
    }
}
