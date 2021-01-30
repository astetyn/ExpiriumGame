package com.astetyne.expirium.client.gui.roots.game;

import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.screens.GameScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class DeathRoot extends WidgetGroup implements GameRootable {

    private static final int waitTime = 10;

    private float timeWaiting;
    private final TextButton resurrectButton;
    private final Label timeRemainingLabel;
    private final boolean firstDeath;

    public DeathRoot(boolean firstDeath, int daysSurvived) {

        this.firstDeath = firstDeath;

        timeWaiting = 0;

        Label deathLabel = new Label("You died", Res.TITLE_LABEL_STYLE);
        deathLabel.setAlignment(Align.center);
        deathLabel.setColor(0.8f, 0, 0, 1);

        timeRemainingLabel = new Label("Resurrect in: ", Res.LABEL_STYLE);
        timeRemainingLabel.setAlignment(Align.center);

        resurrectButton = new TextButton("Resurrect", Res.TEXT_BUTTON_STYLE);
        resurrectButton.setDisabled(true);
        resurrectButton.setColor(Color.DARK_GRAY);
        resurrectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameScreen.get().setRoot(new GameRoot());
            }
        });

        String s = "Days survived: "+daysSurvived;
        Label achievementLabel = new Label(s, Res.LABEL_STYLE);
        achievementLabel.setAlignment(Align.center);
        achievementLabel.setColor(1, 0.8f, 0, 1);

        String s2;
        if(firstDeath) {
            s2 = "This is your first death. In real life there is no other chance. But I am nice. " +
                    "I allow you to continue. Take screenshot if you want to share this achievement. " +
                    "(this will show only once)";
        }else {
            s2 = "So you died again? You must try harder. This game is not that hard...";
        }
        Label noteLabel = new Label(s2, Res.LABEL_STYLE);
        noteLabel.setWrap(true);
        noteLabel.setAlignment(Align.center);

        deathLabel.setBounds(500, 700, 1000, 200);
        addActor(deathLabel);

        timeRemainingLabel.setBounds(500, 600, 1000, 100);
        addActor(timeRemainingLabel);

        resurrectButton.setBounds(700, 500, 600, 80);
        addActor(resurrectButton);

        achievementLabel.setBounds(500, 400, 1000, 100);
        addActor(achievementLabel);

        noteLabel.setBounds(500, 50, 1000, 300);
        addActor(noteLabel);

    }

    @Override
    public void act(float delta) {
        if(timeWaiting >= waitTime) {
            resurrectButton.setDisabled(false);
            resurrectButton.setColor(Color.GREEN);
            timeRemainingLabel.setText("You can now resurrect.");
        }else {
            timeWaiting += delta;
            timeRemainingLabel.setText("Resurrect in: " + (waitTime - (int)timeWaiting));
        }
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public boolean isDimmed() {
        return true;
    }

    @Override
    public void refresh() {

    }

    @Override
    public boolean canInteractWithWorld() {
        return false;
    }
}
