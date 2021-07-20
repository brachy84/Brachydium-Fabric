package brachy84.brachydium.api.cover;

import brachy84.brachydium.api.item.ItemBehaviour;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class CoverItemBehaviour implements ItemBehaviour {

    private final Cover cover;

    public CoverItemBehaviour(Cover cover) {
        this.cover = cover;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ICoverable coverable = CoverableApi.LOOKUP.find(context.getWorld(), context.getBlockPos(), context.getSide());
        if(coverable == null) return ActionResult.PASS;
        if(coverable.canPlaceCover(cover, context.getSide()) && cover.canPlaceOn(context.getSide(), coverable)) {
            coverable.placeCover(cover, context.getSide());
            cover.onAttach(coverable, context.getSide());
            context.getPlayer().getStackInHand(context.getHand()).decrement(1);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }
}
