package scripts;

import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;

import java.awt.*;


@Script.Manifest(
        name = "Scout's Nightmare Zone AFK Trainer",
        description = "Will AFK the nightmare zone for dank xp drops.",
        properties = "client = 4;"
)

public class ScoutsNMZ extends PollingScript<ClientContext> implements PaintListener {

    public static final BoundingBox BB_BANK = new BoundingBox(new Tile(2609,3097),
            new Tile(2613,3088));
    public static final BoundingBox BB_LOBBY = new BoundingBox(new Tile(2601,3118),
            new Tile(2609,3112));
    public static final Tile[] PATH = {
            new Tile(2612,3092),
            new Tile(2618, 3106),
            new Tile(2605, 3115)};
    public static final Tile BARREL_TILE = new Tile(2601,3116);
    public static final int DOMINIC_ONION = 1120;
    public static final int POTION = 26269;
    public static final int DOMINICS_COFFER = 26272;
    public static final int REWARDS_CHEST = 26273;
    public static final int OVERLOAD_BARREL = 26279;
    public static final int ABSORPTION_BARREL = 26280;
    public static final int ROCK_CAKE = 7510;
    public static final int[] ABSORPTION_POTION = {11734, 11735, 11736, 11737};
    public static final int[] OVERLOAD_POTION = {11730, 11731, 11732, 11733};
    public static final Font FONT_BODY1 = new Font("Helvetica", Font.BOLD, 14);
    public static final Font FONT_BODY2 = new Font("Helvetica", Font.PLAIN, 14);
    public static final Font FONT_HEADING = new Font("Helvetica", Font.BOLD, 18);
    private Tile finalTile;
    private long prayerTimer = 0;
    private int xpStartHitPoints = 0;
    private int xpStartAttack = 0;
    private int xpStartStrength = 0;
    private int xpStartDefense = 0;
    private int xpStartRange = 0;
    private int xpStartMagic = 0;

    @Override
    public void start() {

//        System.out.println("Hit Points:" + getXpPercentage(Experience.HIT_POINTS));
//        System.out.println("Attack:" + getXpPercentage(Experience.ATTACK));
//        System.out.println("Defense:" + getXpPercentage(Experience.DEFENSE));
//        System.out.println("Strength:" + getXpPercentage(Experience.STRENGTH));
//        System.out.println("Range:" + getXpPercentage(Experience.RANGE));
//        System.out.println("Magic:" + getXpPercentage(Experience.MAGIC));
//
//        System.out.println("Current XP:" + ctx.skills.experience(Constants.SKILLS_RANGE));
//        System.out.println("XP at level: " + ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_RANGE)));
//        System.out.println("XP at next level:" + ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_RANGE) + 1));
//        System.out.println("XP at level: " + ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_RANGE)));
//        double numerator = ctx.skills.experience(Constants.SKILLS_RANGE) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_RANGE));
//        double denominator = ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_RANGE) + 1) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_RANGE));
//        System.out.println((int)((numerator / denominator) * 100.0));
//        System.out.println(getCombatStyle());
//        System.out.println(ctx.combat.health())

//        replenishSupplies();
//        Condition.sleep(Random.nextInt(3000,6000));
//        startDream();
//        ctx.camera.turnTo(BARREL_TILE);
//        ctx.movement.step(BARREL_TILE);
//        Condition.sleep(Random.nextInt(6000,9000));
//        withdrawPotions();
//        Condition.sleep(Random.nextInt(3000,6000));
//        eatToHealth(51);
//        Condition.sleep(Random.nextInt(1000,2000));
//        startDream();
//        Condition.sleep(Random.nextInt(5000, 7500));
        //ctx.controller.stop();

        ctx.game.tab(Game.Tab.ATTACK);
        prayerTimer = getRuntime();
        xpStartHitPoints = getXpCombat(Experience.HIT_POINTS);
        xpStartAttack = getXpCombat(Experience.ATTACK);
        xpStartStrength = getXpCombat(Experience.STRENGTH);
        xpStartDefense = getXpCombat(Experience.DEFENSE);
        xpStartRange = getXpCombat(Experience.RANGE);
        xpStartMagic = getXpCombat(Experience.MAGIC);
        Tile startTile = ctx.players.local().tile();
        finalTile = new Tile(startTile.x() + 2, startTile.y() + 31, 3);

        if(ctx.combat.health() > 51){
            eatToHealth(51);
        }

        if(ctx.combat.health() == 51){
            ctx.game.tab(Game.Tab.INVENTORY);
            Item overloadPotion = ctx.inventory.select().id(OVERLOAD_POTION).first().poll();
            overloadPotion.interact("Drink", "Overload");
            Condition.sleep(Random.nextInt(600, 1200));
            Item absorptionPotion = ctx.inventory.select().id(ABSORPTION_POTION).first().poll();
            absorptionPotion.interact("Drink", "Absorption");
            Condition.sleep(Random.nextInt(600, 1200));
            ctx.game.tab(Game.Tab.PRAYER);
            if (ctx.widgets.widget(541).valid()) {
                ctx.widgets.widget(541).component(11).component(0).click();
            }
            Condition.sleep(Random.nextInt(600, 1200));
            ctx.widgets.widget(541).component(11).component(1).click();
        }
    }

    @Override
    public void poll() {

        if (ctx.inventory.select().id(OVERLOAD_POTION).isEmpty() ||
                (ctx.inventory.select().id(ABSORPTION_POTION).isEmpty() &&
                        getAbsorptionPoints() <= 0)){
            ctx.controller.stop();
        }

        final State state = getState();
//        final State state = null;
        if (state == null) {
            return;
        }

        switch (state) {
            case DRINK_ABSORPTION:
                ctx.game.tab(Game.Tab.INVENTORY);
                for (int i = 0; i < 16; i++) {
                    Item absorptionPotion = ctx.inventory.select().id(ABSORPTION_POTION).first().poll();
                    if(ctx.inventory.select().id(ABSORPTION_POTION).isEmpty()){
                        break;
                    }
                    absorptionPotion.interact("Drink", "Absorption");
                    Condition.sleep(Random.nextInt(600, 700));
                }
                break;
            case DRINK_OVERLOAD:
                ctx.game.tab(Game.Tab.INVENTORY);
                if(!ctx.inventory.select().id(OVERLOAD_POTION).isEmpty()) {
                    Item overloadPotion = ctx.inventory.select().id(OVERLOAD_POTION).first().poll();
                    overloadPotion.interact("Drink", "Overload");
                    Condition.sleep(Random.nextInt(600, 1200));
                }
                break;
            case EAT_CAKE:
                ctx.game.tab(Game.Tab.INVENTORY);
                Item rockCake = ctx.inventory.select().id(ROCK_CAKE).first().poll();
                rockCake.interact("Guzzle", "Dwarven");
                Condition.sleep(Random.nextInt(600, 625));
                break;
            case FLICK_PRAYER:
                ctx.game.tab(Game.Tab.PRAYER);
                if (ctx.widgets.widget(541).valid()) {
                    ctx.widgets.widget(541).component(11).component(0).click();
                }
                Condition.sleep(Random.nextInt(600, 1200));
                ctx.widgets.widget(541).component(11).component(1).click();
                prayerTimer = getRuntime();
                break;
            case RUN_TO_TILE:
                ctx.camera.turnTo(finalTile);
                ctx.movement.step(finalTile);
                Condition.sleep(Random.nextInt(600, 1200));
                break;
        }
    }

    private State getState() {
        if (getRuntime() - prayerTimer > Random.nextInt(45000, 52500)) {
            return State.FLICK_PRAYER;
        }
        if (!(ctx.skills.level(Constants.SKILLS_ATTACK) > ctx.skills.realLevel(Constants.SKILLS_ATTACK)) &&
                ctx.combat.health() >= 51){
            return State.DRINK_OVERLOAD;
        }
        if (getAbsorptionPoints() < 200) {
            return State.DRINK_ABSORPTION;
        }
        if (!ctx.players.local().tile().equals(finalTile)) {
            return State.RUN_TO_TILE;
        }
        if(ctx.combat.health() < 10 &&
                ctx.combat.health() >= 2){
            return State.EAT_CAKE;
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

    private int getAbsorptionPoints(){
        if(ctx.widgets.widget(202).valid()){
            String s =  ctx.widgets.widget(202).component(1).component(9).text();
            if(s.equals("")){
                return -1;
            }
            return Integer.parseInt(s);
        }
        return -1;
    }

    private String getRewardPoints() {
        if (ctx.widgets.widget(202).valid()) {
            String s = ctx.widgets.widget(202).component(1).component(3).text();
            return s.substring(s.indexOf('>') + 1, s.length());
        }
        return null;
    }

    private int getXpCombat(Experience exp){
        switch(exp){
            case HIT_POINTS:
                return ctx.skills.experience(Constants.SKILLS_HITPOINTS);
            case ATTACK:
                return ctx.skills.experience(Constants.SKILLS_ATTACK);
            case STRENGTH:
                return ctx.skills.experience(Constants.SKILLS_STRENGTH);
            case DEFENSE:
                return ctx.skills.experience(Constants.SKILLS_DEFENSE);
            case RANGE:
                return ctx.skills.experience(Constants.SKILLS_RANGE);
            case MAGIC:
                return ctx.skills.experience(Constants.SKILLS_MAGIC);
        }
        return -1;
    }

    private int getXpGained(int startXp, Experience exp){
        switch(exp){
            case HIT_POINTS:
                return getXpCombat(Experience.HIT_POINTS) - startXp;
            case ATTACK:
                return getXpCombat(Experience.ATTACK) - startXp;
            case STRENGTH:
                return getXpCombat(Experience.STRENGTH) - startXp;
            case DEFENSE:
                return getXpCombat(Experience.DEFENSE) - startXp;
            case RANGE:
                return getXpCombat(Experience.RANGE) - startXp;
            case MAGIC:
                return getXpCombat(Experience.MAGIC) - startXp;
        }
        return -1;
    }

    private int getXpHour(int startXp, Experience exp){
        switch(exp){
            case HIT_POINTS:
                return (int)((getXpGained(startXp, Experience.HIT_POINTS) * 3600000D) / getRuntime());
            case ATTACK:
                return (int)((getXpGained(startXp, Experience.ATTACK) * 3600000D) / getRuntime());
            case STRENGTH:
                return (int)((getXpGained(startXp, Experience.STRENGTH) * 3600000D) / getRuntime());
            case DEFENSE:
                return (int)((getXpGained(startXp, Experience.DEFENSE) * 3600000D) / getRuntime());
            case RANGE:
                return (int)((getXpGained(startXp, Experience.RANGE) * 3600000D) / getRuntime());
            case MAGIC:
                return (int)((getXpGained(startXp, Experience.MAGIC) * 3600000D) / getRuntime());
        }
        return -1;
    }

    private int getXpPercentage(Experience exp){
        double numerator = 0.0;
        double denominator = 0.0;
        switch(exp){
            case HIT_POINTS:
                numerator = ctx.skills.experience(Constants.SKILLS_HITPOINTS) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_HITPOINTS));
                denominator = ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_HITPOINTS) + 1) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_HITPOINTS));
                return (int)((numerator / denominator) * 100);
            case ATTACK:
                numerator = ctx.skills.experience(Constants.SKILLS_ATTACK) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_ATTACK));
                denominator = ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_ATTACK) + 1) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_ATTACK));
                return (int)((numerator / denominator) * 100);
            case STRENGTH:
                numerator = ctx.skills.experience(Constants.SKILLS_STRENGTH) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_STRENGTH));
                denominator = ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_STRENGTH) + 1) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_STRENGTH));
                return (int)((numerator / denominator) * 100);
            case DEFENSE:
                numerator = ctx.skills.experience(Constants.SKILLS_DEFENSE) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_DEFENSE));
                denominator = ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_DEFENSE) + 1) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_DEFENSE));
                return (int)((numerator / denominator) * 100);
            case RANGE:
                numerator = ctx.skills.experience(Constants.SKILLS_RANGE) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_RANGE));
                denominator = ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_RANGE) + 1) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_RANGE));
                return (int)((numerator / denominator) * 100);
            case MAGIC:
                numerator = ctx.skills.experience(Constants.SKILLS_MAGIC) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_MAGIC));
                denominator = ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_MAGIC) + 1) - ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_MAGIC));
                return (int)((numerator / denominator) * 100);
        }
        return -1;
    }

    private int getTimeToLevel(int xpStart, Experience exp){
        double xpToLevel;
        double xpHour;
        switch(exp){
            case HIT_POINTS:
                xpToLevel = ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_HITPOINTS) + 1) - ctx.skills.experience(Constants.SKILLS_HITPOINTS);
                xpHour = getXpHour(xpStart, exp);
                if(xpHour == 0){
                    return 0;
                }
                return (int)((xpToLevel / xpHour) * 3600000D);
            case ATTACK:
                xpToLevel = ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_ATTACK) + 1) - ctx.skills.experience(Constants.SKILLS_ATTACK);
                xpHour = getXpHour(xpStart, exp);
                if(xpHour == 0){
                    return 0;
                }
                return (int)((xpToLevel / xpHour) * 3600000D);
            case STRENGTH:
                xpToLevel = ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_STRENGTH) + 1) - ctx.skills.experience(Constants.SKILLS_STRENGTH);
                xpHour = getXpHour(xpStart, exp);
                if(xpHour == 0){
                    return 0;
                }
                return (int)((xpToLevel / xpHour) * 3600000D);
            case DEFENSE:
                xpToLevel = ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_DEFENSE) + 1) - ctx.skills.experience(Constants.SKILLS_DEFENSE);
                xpHour = getXpHour(xpStart, exp);
                if(xpHour == 0){
                    return 0;
                }
                return (int)((xpToLevel / xpHour) * 3600000D);
            case RANGE:
                xpToLevel = ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_RANGE) + 1) - ctx.skills.experience(Constants.SKILLS_RANGE);
                xpHour = getXpHour(xpStart, exp);
                if(xpHour == 0){
                    return 0;
                }
                return (int)((xpToLevel / xpHour) * 3600000D);
            case MAGIC:
                xpToLevel = ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_MAGIC) + 1) - ctx.skills.experience(Constants.SKILLS_MAGIC);
                xpHour = getXpHour(xpStart, exp);
                if(xpHour == 0){
                    return 0;
                }
                return (int)((xpToLevel / xpHour) * 3600000D);
        }
        return -1;
    }

    private String getCombatStyle(){
        Varpbits vb = ctx.varpbits;
        switch(vb.varpbit(43)){
            case 0:
                return ctx.widgets.widget(593).component(6).text();
            case 1:
                return ctx.widgets.widget(593).component(10).text();
            case 2:
                return ctx.widgets.widget(593).component(14).text();
            case 3:
                return ctx.widgets.widget(593).component(18).text();
        }
        return null;
    }

    private String getCombatLevel(){
        String s = ctx.widgets.widget(593).component(2).text();
        return s.substring(s.indexOf(':') + 2, s.length());
    }

    private boolean inBank(){
        return BB_BANK.getCollision(ctx.players.local().tile());
    }

    private boolean inLobby(){
        return BB_LOBBY.getCollision(ctx.players.local().tile());
    }

    private void replenishSupplies(){
        GameObject rewardsChest = ctx.objects.select().id(REWARDS_CHEST).poll();
        rewardsChest.interact("Search", "Rewards chest");
        Condition.sleep(Random.nextInt(600,700));
        if(ctx.widgets.widget(206).valid()){
            ctx.widgets.widget(206).component(2).component(5).click();
            Condition.sleep(Random.nextInt(600, 700));
            ctx.widgets.widget(206).component(6).component(9).interact("Buy-X", "Absorption (1)");
            Condition.sleep(Random.nextInt(600, 700));
            ctx.input.sendln(String.valueOf(Random.nextInt(256, 999)));
            Condition.sleep(Random.nextInt(600, 700));
            ctx.widgets.widget(206).component(6).component(6).interact("Buy-X", "Overload (1)");
            Condition.sleep(Random.nextInt(600, 700));
            ctx.input.sendln(String.valueOf(Random.nextInt(256, 999)));
            Condition.sleep(Random.nextInt(600, 700));
            ctx.widgets.widget(206).component(1).component(11).click();
        }
    }

    private void startDream() {
        Npc dominicOnion = ctx.npcs.select().id(DOMINIC_ONION).poll();
        dominicOnion.interact("Dream", "Dominic Onion");
        Condition.sleep(Random.nextInt(600, 700));
        if (ctx.widgets.widget(219).valid()) {
            ctx.widgets.widget(219).component(0).component(4).click();
            Condition.sleep(Random.nextInt(600, 700));
            if (ctx.widgets.widget(231).valid()) {
                ctx.widgets.widget(231).component(2).click();
                Condition.sleep(Random.nextInt(600, 700));
                if (ctx.widgets.widget(219).valid()) {
                    ctx.widgets.widget(219).component(0).component(1).click();
                    Condition.sleep(Random.nextInt(600, 700));
                    if (ctx.widgets.widget(231).valid()) {
                        ctx.widgets.widget(231).component(2).click();
                    }
                }
            }
        }
    }

    private void drinkPotion(){
        GameObject potion = ctx.objects.select().id(POTION).poll();
        potion.interact("Drink", "Potion");
        Condition.sleep(Random.nextInt(600, 700));
        if(ctx.widgets.widget(129).valid()){
            ctx.widgets.widget(129).component(6).click();
            Condition.sleep(Random.nextInt(600, 700));
        }
    }

    private void withdrawOverload(){
        GameObject overloadBarrel = ctx.objects.select().id(OVERLOAD_BARREL).poll();
        overloadBarrel.interact("Take", "Overload potion");
        Condition.sleep(Random.nextInt(600,700));
        if(ctx.widgets.widget(162).component(32).visible()) {
            ctx.input.sendln("24");
        }
    }

    private void withdrawAbsorption(){
        GameObject absorptionBarrel = ctx.objects.select().id(ABSORPTION_BARREL).poll();
        absorptionBarrel.interact("Take", "Absorption potion");
        Condition.sleep(Random.nextInt(600, 700));
        if(ctx.widgets.widget(162).component(32).visible()){
            ctx.input.sendln(String.valueOf(Random.nextInt(84, 999)));
        }
    }

    private void withdrawPotions(){
        withdrawOverload();
        Condition.sleep(Random.nextInt(1800, 2400));
        withdrawAbsorption();
    }

    private void eatToHealth(int health){
        ctx.game.tab(Game.Tab.INVENTORY);
        int tempHealth = ctx.skills.realLevel(Constants.SKILLS_HITPOINTS);
        int damage = tempHealth / 10;
        tempHealth = ctx.combat.health();
        Item rockCake = ctx.inventory.select().id(ROCK_CAKE).poll();
        if (rockCake.valid()){
            for(int i = 1; i < (tempHealth - health) / damage; i ++){
                rockCake.interact("Guzzle", "Dwarven rock cake");
                Condition.sleep(Random.nextInt(1200, 1300));
            }
            tempHealth = ctx.combat.health();
            for(int j = 0; j < (tempHealth - health); j++){
                rockCake.interact("Eat", "Dwarven rock cake");
                Condition.sleep(Random.nextInt(600, 700));
            }
        }
    }

    @Override
    public void repaint(Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(FONT_BODY2);
        g.setColor(new Color(68,68,68));
        g.fillRoundRect(0, 0, 275,110,20,20);
        g.fillRoundRect(0, 115,275,265,20,20);
        g.setColor(new Color(194,56,235));
        g.fillRect(0, 10,275,20);
        g.setColor(new Color(96, 96, 96));
        g.fillRect(10, 145, 250, 15);
        g.fillRect(10, 185, 250, 15);
        g.fillRect(10, 225, 250, 15);
        g.fillRect(10, 265, 250, 15);
        g.fillRect(10, 305, 250, 15);
        g.fillRect(10, 345, 250, 15);
        g.setColor(new Color(247, 246, 246));
        g.setFont(FONT_HEADING);
        g.drawString("Scout's AFK NMZ",60, 26);
        g.setFont(FONT_BODY1);
        g.drawString(String.format("Time Running: %s", formatTime(getTotalRuntime())), 10, 45);
        g.drawString(String.format("Points Gained: %s", getRewardPoints()),10, 65);
        g.drawString(String.format("Absorption: %d", getAbsorptionPoints()), 10, 85);
        g.drawString(String.format("Combat Style: %s (Level %s)", getCombatStyle(), getCombatLevel()), 10, 105);
        g.drawString(String.format("%-14s% ,d%n XP (% ,d%n/HR)",
                "Hit Points:",
                getXpGained(xpStartHitPoints, Experience.HIT_POINTS),
                getXpHour(xpStartHitPoints, Experience.HIT_POINTS)),
                10, 140);
        g.drawString(String.format("%-16s% ,d%n XP (% ,d%n/HR)",
                "Attack:",
                getXpGained(xpStartAttack, Experience.ATTACK),
                getXpHour(xpStartAttack, Experience.ATTACK)),
                10, 180);
        g.drawString(String.format("%-14s% ,d%n XP (% ,d%n/HR)",
                "Strength:",
                getXpGained(xpStartStrength, Experience.STRENGTH),
                getXpHour(xpStartStrength, Experience.STRENGTH)),
                10, 220);
        g.drawString(String.format("%-14s% ,d%n XP (% ,d%n/HR)",
                "Defense:",
                getXpGained(xpStartDefense, Experience.DEFENSE),
                getXpHour(xpStartDefense, Experience.DEFENSE)),
                10, 260);
        g.drawString(String.format("%-15s% ,d%n XP (% ,d%n/HR)",
                "Range:",
                getXpGained(xpStartRange, Experience.RANGE),
                getXpHour(xpStartRange, Experience.RANGE)),
                10, 300);
        g.drawString(String.format("%-16s% ,d%n XP (% ,d%n/HR)",
                "Magic:",
                getXpGained(xpStartMagic, Experience.MAGIC),
                getXpHour(xpStartMagic, Experience.MAGIC)),
                10, 340);
        g.setColor(new Color(146, 211, 110));
        g.fillRect(10, 145, (int)(250 *  (getXpPercentage(Experience.HIT_POINTS) / 100.0)), 15);
        g.fillRect(10, 185, (int)(250 *  (getXpPercentage(Experience.ATTACK) / 100.0)), 15);
        g.fillRect(10, 225, (int)(250 *  (getXpPercentage(Experience.STRENGTH) / 100.0)), 15);
        g.fillRect(10, 265, (int)(250 *  (getXpPercentage(Experience.DEFENSE) / 100.0)), 15);
        g.fillRect(10, 305, (int)(250 *  (getXpPercentage(Experience.RANGE) / 100.0)), 15);
        g.fillRect(10, 345, (int)(250 *  (getXpPercentage(Experience.MAGIC) / 100.0)), 15);
        g.setFont(FONT_BODY1);
        g.setColor(new Color(3, 48, 118));
        g.drawString(String.format("%d%s (%s to %d)", getXpPercentage(Experience.HIT_POINTS),
                "%",
                formatTime(getTimeToLevel(xpStartHitPoints, Experience.HIT_POINTS)),
                ctx.skills.realLevel(Constants.SKILLS_HITPOINTS) + 1),65, 157);
        g.drawString(String.format("%d%s (%s to %d)", getXpPercentage(Experience.ATTACK),
                "%",
                formatTime(getTimeToLevel(xpStartAttack, Experience.ATTACK)),
                ctx.skills.realLevel(Constants.SKILLS_ATTACK) + 1), 65, 197);
        g.drawString(String.format("%d%s (%s to %d)", getXpPercentage(Experience.STRENGTH),
                "%",
                formatTime(getTimeToLevel(xpStartStrength, Experience.STRENGTH)),
                ctx.skills.realLevel(Constants.SKILLS_STRENGTH) + 1), 65, 237);
        g.drawString(String.format("%d%s (%s to %d)", getXpPercentage(Experience.DEFENSE),
                "%",
                formatTime(getTimeToLevel(xpStartDefense, Experience.DEFENSE)),
                ctx.skills.realLevel(Constants.SKILLS_DEFENSE) + 1), 65, 277);
        g.drawString(String.format("%d%s (%s to %d)", getXpPercentage(Experience.RANGE),
                "%",
                formatTime(getTimeToLevel(xpStartRange, Experience.RANGE)),
                ctx.skills.realLevel(Constants.SKILLS_RANGE) + 1), 65, 317);
        g.drawString(String.format("%d%s (%s to %d)", getXpPercentage(Experience.MAGIC),
                "%",
                formatTime(getTimeToLevel(xpStartMagic, Experience.MAGIC)),
                ctx.skills.realLevel(Constants.SKILLS_MAGIC) + 1), 65, 357);
    }

    private enum State {
        DRINK_ABSORPTION, DRINK_OVERLOAD, EAT_CAKE, FLICK_PRAYER, RUN_TO_TILE
    }

    private enum Experience {
        HIT_POINTS, ATTACK, STRENGTH, DEFENSE, RANGE, MAGIC
    }
}