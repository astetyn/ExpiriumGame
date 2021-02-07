package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.tiles.Solidity;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.world.inventory.CookingInventory;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.TileFix;
import com.astetyne.expirium.server.net.SimpleServerPacket;

public class MetaTileCampfire extends MetaTile {

    private final CookingInventory inventory;
    private final long placeTime;
    private boolean invalid;
    private ExpiPlayer lastClicker;

    public MetaTileCampfire(ExpiServer server, ExpiTile owner) {
        super(server, owner);
        this.inventory = new CookingInventory(2, 2, 5);
        placeTime = System.currentTimeMillis();
        invalid = false;
        lastClicker = null;
        server.getWorld().scheduleTask(this::onReduce, Consts.SERVER_TPS*100);
        server.getWorld().scheduleTask(this::onEnd, Consts.SERVER_TPS*120);
        server.getWorld().scheduleTask(this::onInvTick, Consts.SERVER_TPS);
    }

    public void onInvTick() {
        if(invalid) return;
        inventory.onTick();
        server.getWorld().scheduleTask(this::onInvTick, Consts.SERVER_TPS);
    }

    public void onReduce() {
        if(invalid) return;
        server.getWorld().changeMaterial(owner, Material.CAMPFIRE_SMALL, false, Source.NATURAL);
    }

    public void onEnd() {
        if(invalid) return;
        server.getWorld().changeMaterial(owner, Material.AIR, false, Source.NATURAL);
        if(lastClicker != null) {
            lastClicker.getNetManager().putSimpleServerPacket(SimpleServerPacket.CLOSE_DOUBLE_INV);
        }
        for(ItemStack is : inventory.getItems()) {
            for(int i = 0; i < is.getAmount(); i++) {
                dropItem(is.getItem());
            }
        }
    }

    @Override
    public void onInteract(ExpiPlayer p, InteractType type) {
        if(placeTime + 500 > System.currentTimeMillis()) return;
        p.setSecondInv(inventory);
        p.getNetManager().putOpenDoubleInvPacket();
        p.getNetManager().putInvFeedPacket();
        lastClicker = p;
    }

    @Override
    public boolean onMaterialChange(Material to) {
        if(to == Material.CAMPFIRE_SMALL) {
            return true;
        }
        invalid = true;
        return false;
    }

    @Override
    public void dropItems() {
        dropItem(Item.CAMPFIRE);
    }

    @Override
    public Solidity getSolidity() {
        return Solidity.LABILE_VERT;
    }

    @Override
    public TileFix getFix() {
        return TileFix.CAMPFIRE;
    }

    @Override
    public int getMaxStability() {
        return 1;
    }

    @Override
    public float getBreakTime() {
        return 0.5f;
    }

}
