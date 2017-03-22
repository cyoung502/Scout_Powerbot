package scripts;

import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.TilePath;

import java.util.concurrent.Callable;

/**
 * Created by noemailgmail on 3/21/2017.
 */
public class Location extends ClientAccessor<ClientContext>{
    BoundingBox bankBox;
    BoundingBox miningBox;
    Tile[] path;

    public Location(ClientContext ctx, BoundingBox bankBox, BoundingBox miningBox, Tile[] path) {
        super(ctx);
        this.bankBox = bankBox;
        this.miningBox = miningBox;
        this.path = path;
    }

    public BoundingBox getBankBox() {
        return bankBox;
    }

    public void setBankBox(BoundingBox bankBox) {
        this.bankBox = bankBox;
    }

    public BoundingBox getMiningBox() {
        return miningBox;
    }

    public void setMiningBox(BoundingBox miningBox) {
        this.miningBox = miningBox;
    }

    public Tile[] getPath() {
        return path;
    }

    public void setPath(Tile[] path) {
        this.path = path;
    }

    public void runToBank(){
        TilePath tilePath = ctx.movement.newTilePath(path);
        tilePath.traverse();
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !ctx.players.local().inMotion();
            }
        },150,10);
    }

    public void runToMine(){
        TilePath tilePath = ctx.movement.newTilePath(path).reverse();
        tilePath.traverse();
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !ctx.players.local().inMotion();
            }
        },150,10);
    }

}
