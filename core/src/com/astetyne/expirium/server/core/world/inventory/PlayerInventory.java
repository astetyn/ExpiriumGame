package com.astetyne.expirium.server.core.world.inventory;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemCat;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.core.WorldSaveable;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.net.PacketInputStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerInventory extends Inventory implements WorldSaveable {

    private final Player owner;
    private ItemStack itemInHand;
    private int indexTools, indexMats, indexCons;
    private ItemStack isTool, isMat, isCon;
    private ChosenSlot chosenSlot;

    public PlayerInventory(Player owner, int columns, int rows, float maxWeight) {
        super(rows, columns, maxWeight);
        this.owner = owner;
        indexTools = 0;
        indexMats = 0;
        indexCons = 0;
        isTool = new ItemStack(Item.EMPTY);
        isMat = new ItemStack(Item.EMPTY);
        isCon = new ItemStack(Item.EMPTY);
        chosenSlot = ChosenSlot.TOOL_SLOT;
        itemInHand = new ItemStack(Item.EMPTY);
        label = "Note that game is still in alpha!";
    }

    public PlayerInventory(Player owner, int columns, int rows, float maxWeight, DataInputStream in) throws IOException {
        super(rows, columns, maxWeight, in);
        this.owner = owner;
        indexTools = in.readInt();
        indexMats = in.readInt();
        indexCons = in.readInt();
        isTool = new ItemStack(Item.get(in.readInt()));
        isMat = new ItemStack(Item.get(in.readInt()));
        isCon = new ItemStack(Item.get(in.readInt()));
        chosenSlot = ChosenSlot.get(in.readInt());
        itemInHand = new ItemStack(Item.get(in.readInt()));
        label = "Note that game is still in alpha!";
    }

    @Override
    protected boolean canInsert(Item item, int x, int y) {
        if(y == 0 && x + item.getGridWidth() >= 3) return false;
        return super.canInsert(item, x, y);
    }

    @Override
    public void move(IntVector2 pos1, IntVector2 pos2) {
        GridItemStack gis = grid[pos1.x][pos1.y];
        if(gis == null) return;
        if(pos2.y == 0) {
            if(pos2.x == 2) {
                // split half
                IntVector2 appendPos = null;
                outer:
                for(int y = rows - 1; y >= 0; y--) {
                    for(int x = 0; x < columns; x++) {
                        if(canInsert(gis.getItem(), x, y)) {
                            appendPos = new IntVector2(x, y);
                            break outer;
                        }
                    }
                }
                if(gis.getAmount() == 1 || appendPos == null) return;
                int newAmount = gis.getAmount() / 2;
                gis.decreaseAmount(newAmount);
                decreaseWeight(gis.getItem().getWeight() * newAmount);
                insert(new ItemStack(gis.getItem(), newAmount), appendPos);
                willNeedUpdate();
                return;
            }else if(pos2.x == 3) {
                // split one
                IntVector2 appendPos = null;
                outer:
                for(int y = rows - 1; y >= 0; y--) {
                    for(int x = 0; x < columns; x++) {
                        if(canInsert(gis.getItem(), x, y)) {
                            appendPos = new IntVector2(x, y);
                            break outer;
                        }
                    }
                }
                if(gis.getAmount() == 1 || appendPos == null) return;
                gis.decreaseAmount(1);
                decreaseWeight(gis.getItem().getWeight());
                insert(new ItemStack(gis.getItem()), appendPos);
                willNeedUpdate();
                return;
            }else if(pos2.x == 4) {
                // throw
                remove(pos1);
                for(int i = 0; i < gis.getAmount(); i++) {
                    owner.getServer().getWorld().spawnEntity(EntityType.DROPPED_ITEM, owner.getCenter(), gis.getItem() , Consts.ITEM_COOLDOWN_DROP);
                }
                willNeedUpdate();
                return;
            }
        }
        super.move(pos1, pos2);
    }

    public void onMove(Inventory second, PacketInputStream in) {

        Inventory from = in.getBoolean() ? this : second; // fromMain
        IntVector2 pos1 = in.getIntVector();
        Inventory to = in.getBoolean() ? this : second; // toMain
        IntVector2 pos2 = in.getIntVector();

        if(from == to && pos1.y == pos2.y && pos1.x == pos2.x) return;

        GridItemStack gis = from.getItemOn(pos1);

        if(pos2.x == -1 || pos2.y == -1) {
            from.remove(pos1);
            for(int i = 0; i < gis.getAmount(); i++) {
                owner.getServer().getWorld().spawnEntity(EntityType.DROPPED_ITEM, owner.getCenter(), gis.getItem() , Consts.ITEM_COOLDOWN_DROP);
            }
            return;
        }

        if(from == to) {
            from.move(pos1, pos2);
            return;
        }

        if(gis == null) return;
        GridItemStack gis2 = to.getItemOn(pos2);
        if(gis.getItem().isMergeable() && gis2 != null && gis2.getItem() == gis.getItem()) {
            gis2.increaseAmount(gis.getAmount());
            to.increaseWeight(gis.getWeight());
            from.remove(pos1);
            to.refresh();
            return;
        }

        if(!to.canInsert(gis, pos2)) return;
        to.insert(gis, pos2);
        from.remove(pos1);
    }

    public void onInteract(UIInteractType type) {

        switch(type) {

            case SLOT_TOOLS: chosenSlot = ChosenSlot.TOOL_SLOT; break;
            case SLOT_MATERIALS: chosenSlot = ChosenSlot.MATERIAL_SLOT; break;
            case SLOT_CONSUMABLE: chosenSlot = ChosenSlot.CONSUMABLE_SLOT; break;
            case SWITCH_UP:
                if(chosenSlot == ChosenSlot.TOOL_SLOT) {
                    indexTools++;
                }else if(chosenSlot == ChosenSlot.MATERIAL_SLOT){
                    indexMats++;
                }else if(chosenSlot == ChosenSlot.CONSUMABLE_SLOT){
                    indexCons++;
                }
                break;
            case SWITCH_DOWN:
                if(chosenSlot == ChosenSlot.TOOL_SLOT) {
                    indexTools--;
                }else if(chosenSlot == ChosenSlot.MATERIAL_SLOT){
                    indexMats--;
                }else if(chosenSlot == ChosenSlot.CONSUMABLE_SLOT){
                    indexCons--;
                }
                break;
            case OPEN_INV:
                // toto je zbytocne?
                break;
            case CONSUME_BUTTON:
                if(itemInHand.getItem().getCategory() != ItemCat.CONSUMABLE) break;
                remove(itemInHand.getItem(), 1);
                owner.increaseFoodLevel(itemInHand.getItem().getFood());

                switch(itemInHand.getItem()) {

                    case FRUIT_BOWL:
                        append(Item.WOODEN_BOWL, 1);
                        break;

                    case JAR_BLUEBERRY_JAM:
                    case JAR_RASPBERRY_JAM:
                        append(Item.JAR, 1);
                        break;
                }
                break;
        }
        updateHotSlots();
    }

    public void updateHotSlots() {

        List<ItemStack> is1 = new ArrayList<>();
        List<ItemStack> is2 = new ArrayList<>();
        List<ItemStack> is3 = new ArrayList<>();

        is1.add(new ItemStack(Item.EMPTY));
        is2.add(new ItemStack(Item.EMPTY));
        is3.add(new ItemStack(Item.EMPTY));

        for(ItemStack is : items) {
            if(is.getItem().getCategory() == ItemCat.TOOL) {
                is1.add(is);
            }else if(is.getItem().getCategory() == ItemCat.MATERIAL) {
                is2.add(is);
            }else if(is.getItem().getCategory() == ItemCat.CONSUMABLE) {
                is3.add(is);
            }
        }

        if(indexTools >= is1.size()) {
            indexTools = 0;
        }else if(indexTools <= -1) {
            indexTools = is1.size() - 1;
        }
        if(indexMats >= is2.size()) {
            indexMats = 0;
        }else if(indexMats <= -1) {
            indexMats = is2.size() - 1;
        }
        if(indexCons >= is3.size()) {
            indexCons = 0;
        }else if(indexCons <= -1) {
            indexCons = is3.size() - 1;
        }

        isTool = is1.get(indexTools);
        isMat = is2.get(indexMats);
        isCon = is3.get(indexCons);

        if(chosenSlot == ChosenSlot.TOOL_SLOT) {
            itemInHand = isTool;
        }else if(chosenSlot == ChosenSlot.MATERIAL_SLOT) {
            itemInHand = isMat;
        }else if(chosenSlot == ChosenSlot.CONSUMABLE_SLOT) {
            itemInHand = isCon;
        }

        owner.getNetManager().putHotSlotsFeedPacket(chosenSlot, isTool, isMat, isCon);

        for(Player ep : owner.getServer().getPlayers()) {
            ep.getNetManager().putHandItemPacket(owner.getId(), itemInHand.getItem());
        }
    }

    public ItemStack getItemInHand() {
        if(itemInHand.getItem() != Item.EMPTY && itemInHand.getAmount() <= 0) itemInHand.setItem(Item.EMPTY);
        return itemInHand;
    }

    @Override
    public void writeData(WorldBuffer out) {
        super.writeData(out);
        out.writeInt(indexTools);
        out.writeInt(indexMats);
        out.writeInt(indexCons);
        out.writeInt(isTool.getItem().ordinal());
        out.writeInt(isMat.getItem().ordinal());
        out.writeInt(isCon.getItem().ordinal());
        out.writeInt(chosenSlot.ordinal());
        out.writeInt(itemInHand.getItem().ordinal());
    }
}
