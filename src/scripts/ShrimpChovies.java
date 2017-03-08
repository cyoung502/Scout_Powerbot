package scripts;

import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

@Script.Manifest(
        name = "Scout's ShrimpChovies",
        properties = "author=qp Scout qp; topic=1296203; client=4;",
        description = "Fishes Draynor for Shrimp and Anchovies"
)

public class ShrimpChovies extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    public static final int[] FISH_POOLS = {1525};
    public static final int[] BANKERS = {394, 395};
    public static final int ITEM_SHRIMP = 317;
    public static final int ITEM_ANCHOVIES = 321;
    public static final int ITEM_SMALL_NET = 303;
    public static final Tile[] Path = {
            new Tile(3087, 3228, 0),
            new Tile(3099, 3229, 0),
            new Tile(3095, 3243, 0)
    };

    private boolean useBanking = true;
    private TilePath pathToBank;
    private TilePath pathToFish;
    private int priceShrimp = 0;
    private int priceAnchovies = 0;
    private long timeSinceFish = 0;
    private int shrimpCaught = 0;
    private int anchoviesCaught = 0;

    @Override
    public void start() {
        timeSinceFish = getRuntime();
        pathToBank = ctx.movement.newTilePath(Path);
        pathToFish = ctx.movement.newTilePath(Path).reverse();
    }

    @Override
    public void poll() {
        //System.out.println(ctx.widgets.widget(233).valid());
        //ctx.controller.stop();

        if (ctx.inventory.select().id(ITEM_SMALL_NET).isEmpty()) {
            ctx.controller.stop();
        }

        final State state = getState();
        if (state == null) {
            return;
        }

        switch (state) {
            case FISH:
                Npc fishPool = ctx.npcs.select().id(FISH_POOLS).nearest().poll();
                if (fishPool.inViewport()) {
                    fishPool.interact("Net", "Fishing Spot");
                    timeSinceFish = getRuntime();
                } else {
                    ctx.movement.step(fishPool);
                    ctx.camera.turnTo(fishPool);
                }
                Condition.sleep(Random.nextInt(500, 1000));
                break;
            case DROP_FISH:
                for (Item i : ctx.inventory.select().id(ITEM_SHRIMP)) {
                    i.interact("Drop");
                }
                for (Item j : ctx.inventory.select().id(ITEM_ANCHOVIES)) {
                    j.interact("Drop");
                }
                break;
            case RUN_TO_BANK:
                pathToBank.traverse();
                Condition.sleep(Random.nextInt(6000, 10000));
                break;
            case RUN_TO_FISH:
                pathToFish.traverse();
                Condition.sleep(Random.nextInt(6000, 10000));
                break;
            case CLOSE_LVLUP:
                ctx.widgets.widget(233).component(2).click();
                break;
            case BANK:
                if(!ctx.bank.opened()) {
                    final Npc banker = ctx.npcs.select().id(BANKERS).nearest().poll();
                    if (banker.inViewport()) {
                        banker.interact("Bank", "Banker");
                    } else {
                        ctx.camera.turnTo(banker);
                    }
                } else {
                    final Item shrimp = ctx.inventory.select().id(ITEM_SHRIMP).poll();
                    final Item anchovies = ctx.inventory.select().id(ITEM_ANCHOVIES).poll();
                    shrimp.interact("Deposit-All");
                    anchovies.interact("Deposit-All");
                }
                Condition.sleep(Random.nextInt(500, 1000));
                break;
        }
    }

    private State getState() {
        if (ctx.widgets.widget(233).valid()) {
            return State.CLOSE_LVLUP;
        }
        if(ctx.inventory.select().count() == 28 &&
                isInBoundingBox(ctx.players.local().tile(),
                        new Tile(3092, 3246, 0),
                        new Tile(3097, 3240, 0)) &&
                useBanking){
            return State.BANK;
        }
        if(ctx.inventory.select().count() < 28 &&
                isInBoundingBox(ctx.players.local().tile(),
                        new Tile(3090, 3246, 0),
                        new Tile(3097, 3240, 0)) &&
                useBanking){
            return State.RUN_TO_FISH;
        }
        if (ctx.players.local().animation() == -1 &&
                ctx.inventory.select().count() < 28 &&
                (getRuntime() - timeSinceFish) > 5000) {
            return State.FISH;
        }
        if (ctx.inventory.select().count() == 28 &&
                useBanking) {
            return State.RUN_TO_BANK;
        }
        if (ctx.inventory.select().count() == 28 &&
                !useBanking) {
            return State.DROP_FISH;
        }
        return null;
    }

    private enum State {
        BANK, CLOSE_LVLUP, DROP_FISH, FISH, RUN_TO_BANK, RUN_TO_FISH
    }

    @SuppressWarnings("Duplicates")
    private String convertTime(long time) {
        /*
        String s = (time / 3600000) % 24 + "H ";
        s += (time / 60000) % 60 + "M ";
        s += (time / 1000) % 60 + "S";
        */
        long l;
        String s;
        l = (time / 3600000) % 24;
        if (l < 10) {
            s = "0" + (int) l + ":";
        } else {
            s = l + ":";
        }
        s += (time / 60000) % 60 + ":";
        s += (time / 1000) % 60;
        return s;
    }

    public Boolean isInBoundingBox(Tile position, Tile tl, Tile br) {
        return (position.x() >= tl.x() &&
                position.x() <= br.x() &&
                position.y() <= tl.y() &&
                position.y() >= br.y()
        );
    }

    public static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);

    @Override
    public void messaged(MessageEvent e) {
        final String msg = e.text().toLowerCase();
        if (msg.equals("you catch some shrimps.")) {
            shrimpCaught++;
        } else if (msg.equals("you catch some anchovies.")) {
            anchoviesCaught++;
        }
    }

    @Override
    public void repaint(Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);
        g.setColor(Color.BLACK);
        g.fillRect(5, 5, 160, 180);
        g.setColor(Color.BLUE);
        g.fillRect(7, 7, 156, 16);
        g.setColor(Color.WHITE);
        g.drawString("Draynor Fish", 55, 20);
        g.drawString(String.format("Time Running: %s ", convertTime(getTotalRuntime())), 10, 40);
        g.drawString(String.format("Shrimp Caught: %d", shrimpCaught), 10, 60);
        g.drawString(String.format("Shrimp/HR: %.0f", (shrimpCaught * 3600000D) / getRuntime()), 10, 80);
        g.drawString(String.format("Anchovies Caught: %d", anchoviesCaught), 10, 100);
        g.drawString(String.format("Anchovies/HR: %.0f", (anchoviesCaught * 3600000D) / getRuntime()), 10, 120);
        int xpGained = (shrimpCaught * 10) + (anchoviesCaught * 40);
        g.drawString(String.format("XP Gained: %d", xpGained), 10, 140);
        g.drawString(String.format("XP/HR: %.0f", (xpGained * 3600000D) / getRuntime()), 10, 160);
    }
}