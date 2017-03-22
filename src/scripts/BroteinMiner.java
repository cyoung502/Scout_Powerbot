package scripts;

import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

@Script.Manifest(
        name = "Brotein F2P Miner",
        description = "Mining, Banking, All Locations F2P, AIO",
        properties = "client = 4;"
)
public class BroteinMiner extends PollingScript<ClientContext> implements MessageListener {

    public static final int CLAY_VEIN = 0;
    public static final int[] COPPER_VEIN = {7453, 7484};
    public static final int[] TIN_VEIN = {7484, 7485};
    public static final int IRON_VEIN = 0;
    public static final int COAL_VEIN = 0;
    public static final int MITHRIL_VEIN = 0;
    public static final int ADAMANITE_VEIN = 0;
    public static final int RUNEITE_VEIN = 0;
    public static final int CLAY_ORE = 0;
    public static final int COPPER_ORE = 436;
    public static final int TIN_ORE = 0;
    public static final int IRON_ORE = 0;
    public static final int COAL_ORE = 0;
    public static final int MITHRIL_ORE = 0;
    public static final int ADAMANITE_ORE = 0;
    public static final int RUNEITE_ORE = 0;
    public static final int[] BANKERS = {2897, 2898};
    public int clayOreCount = 0;
    public int copperOreCount = 0;
    public int tinOreCount = 0;
    public int ironOreCount = 0;
    public int coalOreCount = 0;
    public int mithrilOreCount = 0;
    public int adamaniteOreCount = 0;
    public int runeiteOreCount = 0;
    public boolean useShiftDrop = false;
    public boolean useM1D1 = false;
    public boolean useBank = true;
    public BoundingBox alKharidBank = new BoundingBox(new Tile(0, 0), new Tile(0, 0));
    public BoundingBox alKharidMine = new BoundingBox(new Tile(0, 0), new Tile(0, 0));
    public Tile[] alKharidPath = {};

    public BoundingBox seVarrockMine = new BoundingBox(new Tile(3278, 3372), new Tile(3295, 3356));
    public BoundingBox seVarrockBank = new BoundingBox(new Tile(3250, 3423), new Tile(3257, 3419));
    public Tile[] seVarrockPath = {
            new Tile(3285, 3366),
            new Tile(3293, 3377),
            new Tile(3292, 3392),
            new Tile(3290, 3407),
            new Tile(3287, 3414),
            new Tile(3287, 3421),
            new Tile(3280, 3428),
            new Tile(3268, 3429),
            new Tile(3261, 3428),
            new Tile(3253, 3421)
    };

    public Location seVarrock;

    public Location[] locations = {
            new Location(ctx, alKharidBank, alKharidMine, alKharidPath)
    };

    @Override
    public void start() {
        seVarrock = new Location(ctx, seVarrockBank, seVarrockMine, seVarrockPath);
    }

    @Override
    public void poll() {

        State state = getState();
        if (state == null) {
            return;
        }
        switch (state) {
            case MINE:
                mineRock(COPPER_VEIN);
                break;
            case SHIFT_DROP:
                shiftDrop(COPPER_ORE);
                break;
            case RUN_TO_BANK:
                seVarrock.runToBank();
                break;
            case RUN_TO_MINE:
                seVarrock.runToMine();
                break;
            case BANK:
                depositInventory();
                break;
        }
    }

    public void mineRock(int[] rock) {
        GameObject vein = ctx.objects.select().id(rock).nearest().poll();
        if (vein.inViewport()) {
            vein.interact("Mine");
            Condition.sleep(600);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return isIdle();
                }
            }, 600, 10);
        } else {
            ctx.camera.turnTo(vein);
            ctx.movement.step(vein);
        }
    }

    public void shiftDrop(int ore) {
        for (int i = 0; i < 28; i++) {
            Item item = ctx.inventory.select().id(ore).poll();
            ctx.input.send("{VK_SHIFT down}");
            item.click();
            ctx.input.send("{VK_SHIFT up}");
            Condition.sleep(Random.nextInt(650, 700));
        }
    }

    public void depositInventory() {
        Npc npc = ctx.npcs.select().id(BANKERS).nearest().poll();
        if (npc.inViewport()) {
            npc.interact("Bank", "Banker");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.bank.opened();
                }
            }, 600, 10);
            ctx.bank.depositInventory();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().count() == 0;
                }
            }, 600, 10);
            ctx.bank.close();
        } else {
            ctx.camera.turnTo(npc);
            ctx.movement.step(npc);
        }
    }

    public boolean isIdle() {
        return ctx.players.local().animation() == -1;
    }

    public State getState() {
        if (seVarrock.getMiningBox().getCollision(ctx.players.local().tile()) && isIdle() && ctx.inventory.select().count() < 28) {
            return State.MINE;
        }
        if (seVarrock.getMiningBox().getCollision(ctx.players.local().tile()) && ctx.inventory.select().count() == 28 && useShiftDrop) {
            return State.SHIFT_DROP;
        }
        if (seVarrock.getBankBox().getCollision(ctx.players.local().tile()) && ctx.inventory.select().count() > 1 && useBank) {
            return State.BANK;
        }
        if (ctx.inventory.select().count() == 0) {
            return State.RUN_TO_MINE;
        }
        if (ctx.inventory.select().count() == 28 && useBank) {
            return State.RUN_TO_BANK;
        }
        return null;
    }

    @Override
    public void messaged(MessageEvent e) {
        final String msg = e.text().toLowerCase();
        if (msg.equals("you manage to mine some clay.")) {
            clayOreCount++;
        } else if (msg.equals("you manage to mine some copper.")) {
            copperOreCount++;
        } else if (msg.equals("you manage to mine some tin.")) {
            tinOreCount++;
        } else if (msg.equals("you manage to mine some iron.")) {
            ironOreCount++;
        } else if (msg.equals("you manage to mine some coal.")) {
            coalOreCount++;
        } else if (msg.equals("you manage to mine some mithril.")) {
            mithrilOreCount++;
        } else if (msg.equals("you manage to mine some adamanite.")) {
            adamaniteOreCount++;
        } else if (msg.equals("you manage to mine some runeite.")) {
            runeiteOreCount++;
        }
    }

    public enum State {
        BANK, MINE, SHIFT_DROP, RUN_TO_BANK, RUN_TO_MINE
    }
}
