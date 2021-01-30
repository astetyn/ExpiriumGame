package com.astetyne.expirium.server.api.entity;

import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.api.world.inventory.ExpiInventory;
import com.astetyne.expirium.server.api.world.inventory.ExpiPlayerInventory;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.astetyne.expirium.server.net.PacketOutputStream;
import com.astetyne.expirium.server.net.ServerPacketManager;
import com.astetyne.expirium.server.net.ServerPlayerGateway;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ExpiPlayer extends LivingEntity {

    private final ServerPlayerGateway gateway;
    private final String name;
    private final ExpiPlayerInventory mainInv;
    private ExpiInventory secondInv;
    private final ExpiTileBreaker tileBreaker;
    private final ThumbStickData tsData1, tsData2;
    private long lastJump;
    private final Vector2 resurrectLoc;

    public ExpiPlayer(ExpiServer server, Vector2 location, ServerPlayerGateway gateway, String name) {
        super(server, EntityType.PLAYER, location);
        this.gateway = gateway;
        gateway.setOwner(this);
        this.name = name;
        mainInv = new ExpiPlayerInventory(this, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_MAX_WEIGHT);
        secondInv = new ExpiInventory(1, 1, 1, false);
        tileBreaker = new ExpiTileBreaker(server, this);
        tsData1 = new ThumbStickData();
        tsData2 = new ThumbStickData();
        lastJump = 0;
        resurrectLoc = new Vector2(location);
        createBodyFixtures();
        server.getPlayers().add(this);
        server.getEventManager().getTickListeners().add(this);
    }

    public ExpiPlayer(ExpiServer server, DataInputStream in, ServerPlayerGateway gateway) throws IOException {
        super(server, EntityType.PLAYER, in);
        // order is important - must be same as in writeData()
        server.getWorld().getCL().registerListener(this);
        int nameLength = in.readInt();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < nameLength; i++) {
            sb.append(in.readChar());
        }
        this.gateway = gateway;
        gateway.setOwner(this);
        name = sb.toString();
        mainInv = new ExpiPlayerInventory(this, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_ROWS, Consts.PLAYER_INV_MAX_WEIGHT, in);
        secondInv = new ExpiInventory(1, 1, 1, false);
        tileBreaker = new ExpiTileBreaker(server, this);
        tsData1 = new ThumbStickData();
        tsData2 = new ThumbStickData();
        lastJump = 0;
        resurrectLoc = new Vector2(in.readFloat(), in.readFloat());
        createBodyFixtures();
        server.getPlayers().add(this);
        server.getEventManager().getTickListeners().add(this);
    }

    public void createBodyFixtures() {

        body.setFixedRotation(true);

        PolygonShape polyShape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.density = 30f;
        fixtureDef.restitution = 0f;
        fixtureDef.filter.categoryBits = Consts.PLAYER_BIT;
        fixtureDef.filter.maskBits = Consts.DEFAULT_BIT;

        // upper poly
        polyShape.setAsBox(0.45f, 0.55f, new Vector2(0.45f, 0.7f), 0);
        fixtureDef.shape = polyShape;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef);

        // bottom poly
        float[] verts = new float[8];
        verts[0] = 0.05f;
        verts[1] = 0.15f;
        verts[2] = 0.2f;
        verts[3] = 0;
        verts[4] = 0.7f;
        verts[5] = 0;
        verts[6] = 0.85f;
        verts[7] = 0.15f;

        // ground sensor
        polyShape.set(verts);
        fixtureDef.friction = 1;
        body.createFixture(fixtureDef);

        polyShape.setAsBox(0.4f, 0.3f, new Vector2(0.45f, 0.2f), 0);
        fixtureDef.density = 0f;
        fixtureDef.isSensor = true;
        fixtureDef.friction = 0;

        groundSensor = body.createFixture(fixtureDef);

        polyShape.dispose();
    }

    @Override
    public void die() {
        invincible = true;
        getNetManager().putDeathPacket(true, 10);
        setFoodLevel(20);
        setHealthLevel(20);
        teleport(resurrectLoc.x, resurrectLoc.y);
        tsData1.reset();
        tsData2.reset();
        body.setLinearVelocity(0, 0);
        //todo: dropnut polovicu itemov
    }

    public void updateThumbSticks(PacketInputStream in) {
        tsData1.horz = in.getFloat();
        tsData1.vert = in.getFloat();
        tsData2.horz = in.getFloat();
        tsData2.vert = in.getFloat();
    }

    public void onInvMove(PacketInputStream in) {

        boolean fromMain = in.getBoolean();
        IntVector2 pos1 = in.getIntVector();
        boolean toMain = in.getBoolean();
        IntVector2 pos2 = in.getIntVector();

        int row1 = pos1.y;
        int column1 = pos1.x;
        int row2 = pos2.y;
        int column2 = pos2.x;

        if(row1 == row2 && column1 == column2) return;

        //System.out.println("fromMain: "+fromMain+" pos1: "+pos1+" toMain: "+toMain+" pos2: "+pos2);

        if(fromMain) {
            GridItemStack is = mainInv.getGrid()[row1][column1];
            if(is == null) return;

            if(column2 == -1) {
                mainInv.removeGridItem(is);
                throwAwayItem(is);
                return;
            }

            if(toMain) {
                if(row2 == 0 && column2 == mainInv.getColumns() - 2 && mainInv.isPlaceFor(is.getItem())) { //split
                    if(is.getAmount() == 1) return;
                    is.decreaseAmount(1);
                    mainInv.decreaseWeight(is.getItem().getWeight());
                    mainInv.addItem(new ItemStack(is.getItem()), false);
                    getNetManager().putInvFeedPacket();
                    return;
                }else if(row2 == 0 && column2 == mainInv.getColumns() - 1) { //throw
                    mainInv.removeGridItem(is);
                    throwAwayItem(is);
                    return;
                }
                ItemStack destIS = mainInv.getGrid()[row2][column2];
                if(destIS != null && destIS != is && destIS.getItem() == is.getItem()) {
                    destIS.increaseAmount(is.getAmount());
                    mainInv.increaseWeight(is.getItem().getWeight() * is.getAmount());
                    mainInv.removeGridItem(is);
                    getNetManager().putInvFeedPacket();
                    return;
                }
                if(!mainInv.isPlaceFor(is.getItem(), row2, column2, row1, column1)) return;
                mainInv.cleanGridFrom(is);
                is.getGridPos().set(pos2);
                mainInv.insertToGrid(is);
            }else {
                if(!secondInv.canBeAdded(is.getItem(), is.getAmount())) return;
                secondInv.addItem(is, true);
                mainInv.removeGridItem(is);
            }
        }else {
            GridItemStack is = secondInv.getGrid()[row1][column1];
            if(is == null) return;

            if(column2 == -1) {
                secondInv.removeGridItem(is);
                throwAwayItem(is);
                return;
            }

            if(toMain) {
                if(row2 == 0 && column2 == mainInv.getColumns() - 2 && secondInv.isPlaceFor(is.getItem())) { //split
                    if(is.getAmount() == 1) return;
                    is.decreaseAmount(1);
                    secondInv.decreaseWeight(is.getItem().getWeight());
                    secondInv.addItem(new ItemStack(is.getItem()), false);
                    getNetManager().putInvFeedPacket();
                    return;
                }else if(row2 == 0 && column2 == mainInv.getColumns() - 1) { //throw
                    secondInv.removeGridItem(is);
                    throwAwayItem(is);
                    return;
                }
                if(!mainInv.canBeAdded(is.getItem(), is.getAmount())) return;
                mainInv.addItem(is, true);
                secondInv.removeGridItem(is);
            }else {
                ItemStack destIS = secondInv.getGrid()[row2][column2];
                if(destIS != null && destIS != is && destIS.getItem() == is.getItem()) {
                    destIS.increaseAmount(is.getAmount());
                    secondInv.increaseWeight(is.getItem().getWeight() * is.getAmount());
                    secondInv.removeGridItem(is);
                    getNetManager().putInvFeedPacket();
                    return;
                }
                if(!secondInv.isPlaceFor(is.getItem(), row2, column2, row1, column1)) return;
                secondInv.cleanGridFrom(is);
                is.getGridPos().set(pos2);
                secondInv.insertToGrid(is);
            }
        }
        getNetManager().putInvFeedPacket();
    }

    private void throwAwayItem(ItemStack is) {
        for(int i = 0; i < is.getAmount(); i++) {
            //todo: vytvorit spravnu lokaciu itemu, podla otocenia hraca? podla okolitych blokov?
            ExpiDroppedItem edi = new ExpiDroppedItem(server, getCenter(), is.getItem(), Consts.ITEM_COOLDOWN_DROP);
            for(ExpiPlayer pp : server.getPlayers()) {
                pp.getNetManager().putEntitySpawnPacket(edi);
            }
        }
        getNetManager().putInvFeedPacket();
    }

    public void applyPhysics() {

        if(invincible && tsData1.horz == 0 && tsData1.vert == 0 && tsData2.horz == 0 && tsData2.vert == 0) {
            invincible = false;
        }

        if(onGround) {
            Vector2 center = body.getWorldCenter();
            if(tsData1.vert >= 0.6f && lastJump + Consts.JUMP_DELAY < System.currentTimeMillis()) {
                body.applyLinearImpulse(0, 200, center.x, center.y, true);
                lastJump = System.currentTimeMillis();
            }
        }
        float vY = body.getLinearVelocity().y;
        if((body.getLinearVelocity().x >= 3 && tsData1.horz > 0)) {
            tsData1.horz = 0;
            //body.setLinearVelocity(3, vY);
        }else if(body.getLinearVelocity().x <= -3 && tsData1.horz < 0) {
            tsData1.horz = 0;
            //body.setLinearVelocity(-3, vY);
        }else {
            body.applyForceToCenter((40000.0f/Consts.SERVER_DEFAULT_TPS) * tsData1.horz, 0, true);
        }
    }

    @Override
    public void onTick(float delta) {
        super.onTick(delta);

        getNetManager().putLivingStatsPacket();

        if(mainInv.isInvalid() || secondInv.isInvalid()) {
            getNetManager().putInvFeedPacket();
            mainInv.setInvalid(false);
            secondInv.setInvalid(false);
        }

        tileBreaker.onTick(tsData2);
    }

    public void wantsToMakeItem(ItemRecipe recipe) {
        System.out.println("Wants to make item");
        if(Consts.DEBUG) {
            System.out.println("Overriding item check (debug mode)");
            mainInv.addItem(recipe.getProduct(), true);
            getNetManager().putInvFeedPacket();
            return;
        }
        for(ItemStack is : recipe.getRequiredItems()) {
            if(!mainInv.contains(is)) return;
        }
        System.out.println("Has req items");
        if(!mainInv.canBeAdded(recipe.getProduct().getItem(), recipe.getProduct().getAmount())) return;
        System.out.println("Can be added");

        for(ItemStack is : recipe.getRequiredItems()) {
            mainInv.removeItem(is);
        }
        mainInv.addItem(recipe.getProduct(), true);
        getNetManager().putInvFeedPacket();
        System.out.println("Added");
    }

    public ServerPacketManager getNetManager() {
        return gateway.getManager();
    }

    public ServerPlayerGateway getGateway() {
        return gateway;
    }

    public String getName() {
        return name;
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @Override
    public void destroy() {
        destroySafe();
        server.getPlayers().remove(this);
    }

    public void destroySafe() {
        super.destroy();
        server.getEventManager().getTickListeners().remove(this);
    }

    @Override
    public void writeMeta(PacketOutputStream out) {
        out.putString(name);
    }

    public ExpiPlayerInventory getInv() {
        return mainInv;
    }

    public ExpiInventory getSecondInv() {
        return secondInv;
    }

    public void setSecondInv(ExpiInventory secondInv) {
        this.secondInv = secondInv;
    }

    public Vector2 getResurrectLoc() {
        return resurrectLoc;
    }

    public void setResurrectLoc(float x, float y) {
        resurrectLoc.x = x;
        resurrectLoc.y = y;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        super.writeData(out);
        out.writeInt(name.length());
        out.writeChars(name);
        mainInv.writeData(out);
        out.writeFloat(resurrectLoc.x);
        out.writeFloat(resurrectLoc.y);
    }
}
