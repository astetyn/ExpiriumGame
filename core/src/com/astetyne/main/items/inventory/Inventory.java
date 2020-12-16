package com.astetyne.main.items.inventory;

import com.astetyne.main.Resources;
import com.astetyne.main.gui.HotBarSlot;
import com.astetyne.main.gui.SwitchArrow;
import com.astetyne.main.stages.RunningGameStage;

public class Inventory {

    private HotBarSlot toolSlot, buildSlot, useSlot;
    private final SwitchArrow switchArrowUp, switchArrowDown;
    private RunningGameStage gameStage;

    public Inventory(RunningGameStage gameStage) {

        this.gameStage = gameStage;

        toolSlot = new HotBarSlot(Resources.HOT_BAR_SLOT_STYLE_TOOL, onFocusTool);
        buildSlot = new HotBarSlot(Resources.HOT_BAR_SLOT_STYLE_TOOL, onFocusBuild);
        useSlot = new HotBarSlot(Resources.HOT_BAR_SLOT_STYLE_TOOL, onFocusUse);

        toolSlot.setFocus(true);

        switchArrowUp = new SwitchArrow(Resources.SWITCH_ARROW_STYLE, onSwitch);
        switchArrowDown = new SwitchArrow(Resources.SWITCH_ARROW_STYLE, onSwitch);

    }

    private final Runnable onFocusTool = () -> {
        System.out.println("Clicked on tool slot.");
        buildSlot.setFocus(false);
        useSlot.setFocus(false);
        gameStage.getGameGUI().buildTableTool();
    };

    private final Runnable onFocusBuild = () -> {
        System.out.println("Clicked on build slot.");
        toolSlot.setFocus(false);
        useSlot.setFocus(false);
        gameStage.getGameGUI().buildTableBuild();
    };

    private final Runnable onFocusUse = () -> {
        System.out.println("Clicked on use slot.");
        toolSlot.setFocus(false);
        buildSlot.setFocus(false);
        gameStage.getGameGUI().buildTableUse();
    };

    private final Runnable onSwitch = () -> {
        System.out.println("Clicked on switch.");
    };

    public HotBarSlot getToolSlot() {
        return toolSlot;
    }

    public HotBarSlot getBuildSlot() {
        return buildSlot;
    }

    public HotBarSlot getUseSlot() {
        return useSlot;
    }

    public SwitchArrow getSwitchArrowUp() {
        return switchArrowUp;
    }

    public SwitchArrow getSwitchArrowDown() {
        return switchArrowDown;
    }
}
