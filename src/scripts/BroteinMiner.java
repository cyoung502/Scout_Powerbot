package scripts;

import org.powerbot.bot.rt4.Con;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;

import java.awt.*;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "Brotein F2P Miner",
        description = "Mining, Banking, All Locations F2P, AIO",
        properties = "client = 4;"
)
public class BroteinMiner extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    public static final int[] STAIRS = {16671, 16672, 16673};

    public static final int[] CLAY_VEIN = {7454, 7487};
    public static final int[] COPPER_VEIN = {7453, 7484};
    public static final int[] TIN_VEIN = {7485, 7486};
    public static final int IRON_VEIN = 0;
    public static final int[] COAL_VEIN = {7456, 7489};
    public static final int[] SILVER_VEIN = {7457, 7490};
    public static final int GOLD_VEIN = 0;
    public static final int MITHRIL_VEIN = 0;
    public static final int ADAMANITE_VEIN = 0;
    public static final int RUNEITE_VEIN = 0;
    public static final int[] BANKERS = {394, 395, 2897, 2898};
    public static final int[] DEPOSIT_BOX = {6948};

    public static final int CLAY_ORE = 434;
    public static final int COPPER_ORE = 436;
    public static final int TIN_ORE = 438;
    public static final int IRON_ORE = 0;
    public static final int COAL_ORE = 0;
    public static final int SILVER_ORE = 442;
    public static final int GOLD_ORE = 0;
    public static final int MITHRIL_ORE = 0;
    public static final int ADAMANITE_ORE = 0;
    public static final int RUNEITE_ORE = 0;

    public int clayOreCount = 0;
    public int copperOreCount = 0;
    public int tinOreCount = 0;
    public int ironOreCount = 0;
    public int silverOreCount = 0;
    public int goldOreCount = 0;
    public int coalOreCount = 0;
    public int mithrilOreCount = 0;
    public int adamaniteOreCount = 0;
    public int runeiteOreCount = 0;

    public boolean useShiftDrop = false;
    public boolean useM1D1 = false;
    public boolean useBank = true;

    public static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);

    public BoundingBox seLumbridgeStairs = new BoundingBox(new Tile(3205,3211), new Tile(3208, 3206));

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

    public BoundingBox swVarrockMine = new BoundingBox(new Tile(3171, 3378), new Tile(3184, 3364));
    public BoundingBox swVarrockBank = new BoundingBox(new Tile(3180, 3447), new Tile(3185, 3433));
    public Tile[] swVarrockPath = {
            new Tile(3180, 3370),
            new Tile(3179, 3380),
            new Tile(3176, 3389),
            new Tile(3172, 3398),
            new Tile(3172, 3410),
            new Tile(3172, 3422),
            new Tile(3181, 3429),
            new Tile(3183, 3439)
    };

    public BoundingBox barbVillageMine = new BoundingBox(new Tile(3078,3423), new Tile(3084,3417));
    public BoundingBox barbVillageBank = new BoundingBox(new Tile(3091,3499), new Tile(3098,3486));
    public Tile[] barbVillagePath = {
            new Tile(3080,3422),
            new Tile(3090,3432),
            new Tile(3090,3446),
            new Tile(3088,3460),
            new Tile(3080,3468),
            new Tile(3080,3476),
            new Tile(3087,3487),
            new Tile(3093,3490)
    };

    public BoundingBox seLumbridgeMine = new BoundingBox(new Tile(3222,3149), new Tile(3231,3144));
    public BoundingBox seLumbridgeBank = new BoundingBox(new Tile(3207,3220), new Tile(3210,3216));
    public Tile[] seLumbridgePath = {
            new Tile(3227,3147),
            new Tile(3234,3155),
            new Tile(3238,3166),
            new Tile(3239,3178),
            new Tile(3244,3191),
            new Tile(3236,3201),
            new Tile(3235,3214),
            new Tile(3225,3218),
            new Tile(3215,3213),
            new Tile(3206,3209),

    };

    public Location seVarrock,swVarrock,barbVillage, seLumbridge;

    public Experience experience;

    public Location[] locations = {
            new Location(ctx, alKharidBank, alKharidMine, alKharidPath)
    };

    @Override
    public void start() {
        experience = new Experience();
        seVarrock = new Location(ctx, seVarrockBank, seVarrockMine, seVarrockPath);
        swVarrock = new Location(ctx, swVarrockBank, swVarrockMine, swVarrockPath);
        barbVillage = new Location(ctx, barbVillageBank, barbVillageMine, barbVillagePath);
        seLumbridge = new Location(ctx, seLumbridgeBank,seLumbridgeMine, seLumbridgePath);
    }

    @Override
    public void poll() {

        State state = getState();
        if (state == null) {
            return;
        }

        switch (state) {
            case MINE:
                mineRock(TIN_VEIN);
                break;
            case SHIFT_DROP:
                shiftDrop(TIN_ORE);
                break;
            case RUN_TO_BANK:
                seLumbridge.runToBank();
                break;
            case RUN_TO_MINE:
                seLumbridge.runToMine();
                break;
            case BANK:
                depositInventory();
                //bankInventory();
                break;
            case UP_STAIRS:
                runToSeLumbridgeBank();
                break;
            case DOWN_STAIRS:
                runToSeLumbridgeMine();
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

    public void bankInventory() {
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

    public void depositInventory() {
        GameObject obj = ctx.objects.select().id(DEPOSIT_BOX).nearest().poll();
        ctx.movement.step(obj);
        ctx.camera.turnTo(obj);
        if (obj.inViewport()) {
            obj.interact(false,"Deposit");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.widget(192).component(1).component(1).visible();
                }
            }, 600, 15);
            ctx.widgets.widget(192).component(4).click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().count() == 0;
                }
            }, 600, 10);
            ctx.widgets.widget(192).component(1).component(11).click();
        }
    }

    public boolean isIdle() {
        return ctx.players.local().animation() == -1;
    }

    public State getState() {
        if(!ctx.objects.select().id(STAIRS[0]).isEmpty() && seLumbridgeStairs.getCollision(ctx.players.local().tile()) && ctx.inventory.select().count() == 28){
            return State.UP_STAIRS;
        }
        if(!ctx.objects.select().id(STAIRS[2]).isEmpty() && ctx.inventory.select().count() < 28){
            return State.DOWN_STAIRS;
        }
        if (seLumbridge.getMiningBox().getCollision(ctx.players.local().tile()) && isIdle() && ctx.inventory.select().count() < 28) {
            return State.MINE;
        }
        if (seLumbridge.getMiningBox().getCollision(ctx.players.local().tile()) && ctx.inventory.select().count() == 28 && useShiftDrop) {
            return State.SHIFT_DROP;
        }
        if (ctx.inventory.select().count() == 0) {
            return State.RUN_TO_MINE;
        }
        if (seLumbridge.getBankBox().getCollision(ctx.players.local().tile()) && ctx.inventory.select().count() > 1 && useBank) {
            return State.BANK;
        }
        if (ctx.inventory.select().count() == 28 && useBank) {
            return State.RUN_TO_BANK;
        }
        return null;
    }

    private String formatTime(long time) {
        long l;
        String s;
        l = Math.abs((time / 3600000) % 24);
        if (l < 10) {
            s = "0" + (int) l + ":";
        } else {
            s = l + ":";
        }
        l = Math.abs((time / 60000) % 60);
        if(l < 10){
            s += "0" + (int) l + ":";
        } else {
            s += l + ":";
        }
        l = Math.abs((time / 1000) % 60);
        if(l < 10){
            s += "0" + (int) l;
        } else {
            s += l;
        }
        return s;
    }

    public void runToSeLumbridgeBank(){
        GameObject obj = ctx.objects.select().id(STAIRS[0]).nearest().poll();
        if(!obj.valid()){
            return;
        }

        ctx.camera.turnTo(obj);

        if(obj.inViewport()){
            Condition.sleep(Random.nextInt(1200, 1800));
            obj.interact("Climb-up", "Staircase");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.objects.select().id(STAIRS[1]).isEmpty();
                }
            },600,10);
            obj = ctx.objects.select().id(STAIRS[1]).nearest().poll();
            if(obj.inViewport()){
                obj.interact("Climb-up", "Staircase");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.objects.select().id(STAIRS[2]).isEmpty();
                    }
                }, 600, 10);
                Tile tile = new Tile(3208,3218);
                ctx.movement.step(tile);
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return seLumbridgeBank.getCollision(ctx.players.local().tile());
                    }
                }, 600, 10);
            }else{
                ctx.camera.turnTo(obj);
                ctx.movement.step(obj);
            }
        }
    }

    public void runToSeLumbridgeMine(){
        GameObject obj = ctx.objects.select().id(STAIRS[2]).nearest().poll();
        ctx.movement.step(obj);
        ctx.camera.turnTo(obj);
        if(obj.inViewport()){
            Condition.sleep(Random.nextInt(1200, 1800));
            obj.interact(false,"Climb-down", "Staircase");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.objects.select().id(STAIRS[1]).isEmpty();
                }
            },600,15);
            obj = ctx.objects.select().id(STAIRS[1]).nearest().poll();
            if(obj.inViewport()){
                Condition.sleep(Random.nextInt(1200, 1800));
                obj.interact(false,"Climb-down", "Staircase");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.objects.select().id(STAIRS[0]).isEmpty();
                    }
                }, 600, 10);
            }else{
                ctx.camera.turnTo(obj);
                ctx.movement.step(obj);
            }
        }
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
        } else if (msg.equals("you manage to mine some silver.")) {
            silverOreCount++;
        } else if (msg.equals("you manage to mine some gold.")) {
            goldOreCount++;
        } else if (msg.equals("you manage to mine some mithril.")) {
            mithrilOreCount++;
        } else if (msg.equals("you manage to mine some adamanite.")) {
            adamaniteOreCount++;
        } else if (msg.equals("you manage to mine some runeite.")) {
            runeiteOreCount++;
        }
    }

    @Override
    public void repaint(Graphics graphics){
        if(experience == null){
            return;
        }
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);
        g.setColor(new Color(43, 43, 43));
        g.fillRect(0,0,200,300);
        g.setColor(new Color(183, 183, 183));
        g.drawString(String.format("Time Running: %s", formatTime(getRuntime())), 5, 15);
        g.drawString(String.format("XP Gained: %d", experience.getExperienceGained(Constants.SKILLS_MINING)),5, 35);
        g.drawString(String.format("XP/HR: %d", experience.getExperienceHour(Constants.SKILLS_MINING)), 5, 55);
        g.drawString(String.format("%d%s to Level %d(%s)", experience.getExperiencePercent(Constants.SKILLS_MINING), "%", ctx.skills.realLevel(Constants.SKILLS_MINING) + 1, formatTime(experience.getTimeToLevel(Constants.SKILLS_MINING))), 5, 75);

    }

    public enum State {
        UP_STAIRS, DOWN_STAIRS, BANK, MINE, SHIFT_DROP, RUN_TO_BANK, RUN_TO_MINE
    }
}
