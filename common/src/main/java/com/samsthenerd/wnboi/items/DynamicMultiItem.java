package com.samsthenerd.wnboi.items;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import com.samsthenerd.wnboi.interfaces.KeyboundItem;
import com.samsthenerd.wnboi.mixin.MixinItemUsageContext;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.ActionResult;

// this is currently not the most polished code ever. use with caution
public abstract class DynamicMultiItem extends Item implements KeyboundItem{
    public int numItems;
    public static String ITEM_COUNT_KEY = "multi_item_count";
    public static String ITEM_STACK_KEY = "multi_item_stack";
    public static String SELECTED_SLOT_KEY = "multi_item_selected";

    // meant so that stuff works like a bundle
    protected boolean actAsBag = false;
    protected int bagStackSize = -1;

    public DynamicMultiItem(Settings settings, int numItems) {
        super(settings);
        this.numItems = numItems;
    }

    // can override if you want more dynamic behavior
    public int getNumItems(ItemStack stack){
        return this.numItems;
    }

    // baseStack is the multiItem and innerStack is the thing that is getting stacked
    public int getMaxStackSize(ItemStack baseStack, ItemStack innerStack){
        int maxStackSize = innerStack.getMaxCount();
        if(actAsBag && bagStackSize > 0)
            maxStackSize = bagStackSize;
        return maxStackSize;
    }

    @Override
    public ItemStack getDefaultStack(){
        ItemStack stack = super.getDefaultStack();
        NbtCompound stackNBT = stack.getOrCreateNbt();
        stackNBT.putInt(ITEM_COUNT_KEY, numItems);
        NbtList itemList = new NbtList();
        for(int i = 0; i < numItems; i++){
            itemList.add(ItemStack.EMPTY.writeNbt(new NbtCompound()));
        }
        stackNBT.put(ITEM_STACK_KEY, itemList);
        stackNBT.putInt(SELECTED_SLOT_KEY, 0);
        stack.setNbt(stackNBT);
        return stack;
    }

    // puts the addStack into the slot in the baseStack  - doesn't touch the addStack
    public ItemStack putStack(ItemStack baseStack, ItemStack addStack, int slotIndex){
        if(!(baseStack.getItem() instanceof DynamicMultiItem) || slotIndex >= numItems || slotIndex < 0)
            return baseStack;
        NbtList itemList = baseStack.getNbt().getList(ITEM_STACK_KEY, NbtElement.COMPOUND_TYPE);
        itemList.set(slotIndex, addStack.writeNbt(new NbtCompound()));
        baseStack.setSubNbt(ITEM_STACK_KEY, itemList);
        return baseStack;
    }

    // used to check if an itemstack can be added with the given slot stack of the basestack
    // returns <if you can put it, final count for slotStack, remaining count for addStack>
    public Triple<Boolean, Integer, Integer> canPutStack(ItemStack baseStack, ItemStack addStack, ItemStack slotStack){
        int maxStackSize = getMaxStackSize(baseStack, addStack);
        if(slotStack.isEmpty()){
            return new ImmutableTriple<Boolean, Integer, Integer>(true, addStack.getCount(), 0);
        } else if(ItemStack.canCombine(slotStack, addStack) && slotStack.getCount() + addStack.getCount() <= maxStackSize){
            int amountLeftOver = Math.max(slotStack.getCount() + addStack.getCount() - maxStackSize, 0);
            int slotAmount = Math.min(maxStackSize, slotStack.getCount() + addStack.getCount());
            return new ImmutableTriple<Boolean, Integer, Integer>(true, slotAmount, amountLeftOver);
        }
        return new ImmutableTriple<Boolean, Integer, Integer>(false, slotStack.getCount(), addStack.getCount());
    }

    // puts this stack into the first non empty slot or where it can fit - sets the addStack count to proper leftover amount
    public ItemStack tryPutStack(ItemStack baseStack, ItemStack addStack){
        if(!(baseStack.getItem() instanceof DynamicMultiItem))
            return baseStack;
        NbtList itemList = baseStack.getNbt().getList(ITEM_STACK_KEY, NbtElement.COMPOUND_TYPE);
        
        for(int i = 0; i < numItems; i++){
            ItemStack slotStack = ItemStack.fromNbt(itemList.getCompound(i));
            Triple<Boolean, Integer, Integer> canPutResult = canPutStack(baseStack, addStack, slotStack);
            if(!canPutResult.getLeft())
                continue;
            slotStack.setCount(canPutResult.getMiddle());
            itemList.set(i, addStack.writeNbt(new NbtCompound()));
            addStack.setCount(canPutResult.getRight());
        }
        baseStack.setSubNbt(ITEM_STACK_KEY, itemList);
        return baseStack;
    }

    public static ItemStack getCurrentSlotStack(ItemStack baseStack){
        NbtList itemList = baseStack.getNbt().getList(ITEM_STACK_KEY, NbtElement.COMPOUND_TYPE);
        int slot = baseStack.getNbt().getInt(SELECTED_SLOT_KEY);
        return ItemStack.fromNbt(itemList.getCompound(slot));
    }

    // only overriding useOnBlock for now, since other uses likely require mixins with more fine tuning
    // see #Item for others that you can add

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return context.getStack().getItem().useOnBlock(new MultiItemUsageContext(context));
    }

    // helper class so we can gaslight minecraft about what itemstack we're holding
    public class MultiItemUsageContext extends ItemUsageContext{
        public MultiItemUsageContext(ItemUsageContext ctx){
            super(ctx.getPlayer(), ctx.getHand(), ((MixinItemUsageContext)ctx).invokeGetHitResult());
        }

        @Override
        public ItemStack getStack(){
            if(super.getStack().getItem() instanceof DynamicMultiItem )
                return getCurrentSlotStack(super.getStack());
            return super.getStack();
        }
    }

}
