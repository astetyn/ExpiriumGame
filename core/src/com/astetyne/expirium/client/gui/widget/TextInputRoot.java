package com.astetyne.expirium.client.gui.widget;

import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.gui.roots.menu.MenuRootable;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class TextInputRoot extends WidgetGroup implements MenuRootable {

    public TextInputRoot(Runnable listener, TextField textField) {

        TextField tf = new TextField("", Res.TEXT_FIELD_STYLE);
        tf.setAlignment(Align.center);
        tf.setText(textField.getText());
        tf.setTextFieldFilter(textField.getTextFieldFilter());
        tf.setMaxLength(textField.getMaxLength());
        tf.setMessageText(textField.getMessageText());

        TextButton doneButton = new TextButton("Done", Res.TEXT_BUTTON_STYLE);
        doneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                textField.setText(tf.getText());
                listener.run();
            }
        });

        tf.setBounds(20,850, 1500, 120);
        addActor(tf);

        doneButton.setBounds(1560, 850, 420, 120);
        addActor(doneButton);

    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void onEnd() {

    }
}
