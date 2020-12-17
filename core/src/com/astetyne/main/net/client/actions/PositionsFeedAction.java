package com.astetyne.main.net.client.actions;

import com.astetyne.main.net.netobjects.SAdvPosition;

import java.util.List;

public class PositionsFeedAction extends ClientAction {

    private final List<SAdvPosition> positions;

    public PositionsFeedAction(List<SAdvPosition> positions) {
        this.positions = positions;
    }

    public List<SAdvPosition> getPositions() {
        return positions;
    }
}
