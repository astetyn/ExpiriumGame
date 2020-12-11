package com.astetyne.main.stages;

import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.entity.Entity;
import com.astetyne.main.entity.Player;
import com.astetyne.main.gui.elements.TextElement;
import com.astetyne.main.gui.elements.ThumbStick;
import com.astetyne.main.net.client.actions.PlayerMoveActionC;
import com.astetyne.main.net.netobjects.SVector;
import com.astetyne.main.net.netobjects.SPlayer;
import com.astetyne.main.net.server.actions.*;
import com.astetyne.main.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.util.List;

public class RunningGameStage extends Stage {

    public static float PPM = 32;

    private final GameWorld gameWorld;
    private final OrthographicCamera cameraWorld;
    private final ThumbStick thumbStick;
    private final TextElement fpsTextE;
    private Player player;

    private final Box2DDebugRenderer b2dr;

    public RunningGameStage() {

        cameraWorld = new OrthographicCamera();

        b2dr = new Box2DDebugRenderer();

        thumbStick = new ThumbStick(10,10, 200);
        fpsTextE = new TextElement(85, 90, 100, "fps:", 0.5f);
        ExpiriumGame.getGame().getGui().addElement(fpsTextE);
        ExpiriumGame.getGame().getGui().addElement(thumbStick);

        gameWorld = new GameWorld(batch, this);

        resize();

    }

    @Override
    public void update() {

        gameWorld.update();
        fpsTextE.setText("fps: "+Gdx.graphics.getFramesPerSecond());

        float xr = thumbStick.getXR();
        float yr = thumbStick.getYR();
        Vector2 center = player.getBody().getWorldCenter();
        player.getBody().applyLinearImpulse(0.2f*xr, 0.2f*yr, center.x, center.y, true);

        cameraCenter();

        //System.out.println(player.getLocation());

    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        b2dr.render(gameWorld.getWorld(), cameraWorld.combined.cpy().scl(PPM));

        batch.begin();
        gameWorld.render();
        ExpiriumGame.getGame().getGui().render(batch);
        batch.end();

    }

    @Override
    public void resize() {

        cameraWorld.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraWorld.update();

    }

    @Override
    public void dispose() {
        super.dispose();
        gameWorld.dispose();
    }

    @Override
    public void onServerUpdate(List<ServerAction> actions) {

        for(ServerAction serverAction : actions) {

            if(serverAction instanceof InitDataActionS) {
                InitDataActionS initData = (InitDataActionS) serverAction;
                player = (Player) gameWorld.createEntity(initData.getPlayerID(), initData.getPlayerLocation().toVector(), Player.class);

                SVector l = initData.getPlayerLocation();
                player.getBody().setTransform(l.getX(), l.getY(), 0);
                for(SPlayer p : initData.getPlayersEntities()) {
                    if(p.getID() == player.getID()) continue;
                    Player p2 = (Player) gameWorld.createEntity(p.getID(), p.getLocation().toVector(), Player.class);
                    gameWorld.getOtherPlayers().add(p2);
                }

            }else if(serverAction instanceof ChunkFeedActionS) {
                ChunkFeedActionS action = (ChunkFeedActionS) serverAction;
                gameWorld.feedChunk(action.getChunk());
                //System.out.println("FEEDING ON CLIENT: "+action.getChunk().getId());

            }else if(serverAction instanceof PlayerJoinActionS) {
                PlayerJoinActionS psa = (PlayerJoinActionS) serverAction;
                Player p = (Player) gameWorld.createEntity(psa.getPlayerID(), psa.getLocation().toVector(), Player.class);
                gameWorld.getOtherPlayers().add(p);

            }else if(serverAction instanceof EntityMoveActionS) {
                EntityMoveActionS ema = (EntityMoveActionS) serverAction;
                if(ema.getEntityID() == player.getID()) continue;
                Entity e = gameWorld.getEntitiesID().get(ema.getEntityID());
                e.getTargetPosition().set(ema.getNewLocation().toVector());
                e.setInterpolateDelta(0);
                e.getBody().setLinearVelocity(ema.getVelocity().toVector());
            }
        }

        gameWorld.checkChunks();

        ExpiriumGame.getGame().getClientGateway().addAction(new PlayerMoveActionC(new SVector(player.getLocation()), new SVector(player.getVelocity())));

    }

    public void cameraCenter() {
        Vector3 position = cameraWorld.position;
        position.x = player.getLocation().x * PPM;
        position.y = player.getLocation().y * PPM;
        cameraWorld.position.set(position);
        cameraWorld.update();
    }

    public OrthographicCamera getCamera() {
        return cameraWorld;
    }

    public Player getPlayer() {
        return player;
    }
}
