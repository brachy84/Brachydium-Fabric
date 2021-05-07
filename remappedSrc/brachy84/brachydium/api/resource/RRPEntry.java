package brachy84.brachydium.api.resource;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.item.tool.Tools;
import brachy84.brachydium.common.Materials;
import net.devtech.arrp.api.RRPPreGenEntrypoint;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import static net.devtech.arrp.api.RuntimeResourcePack.id;

import java.util.Map;

import static brachy84.brachydium.Brachydium.RESOURCE_PACK;
import static brachy84.brachydium.api.resource.RRPHelper.*;

public class RRPEntry implements RRPPreGenEntrypoint {

    private static Identifier mtId(String string) {
        return Brachydium.id(string);
    }

    @Override
    public void pregen() {

        new Materials().init();

        /*RRPHelper.addPipeModelTemplate(2);
        RRPHelper.addPipeModelTemplate(4);
        RRPHelper.addPipeModelTemplate(8);
        RRPHelper.addPipeModelTemplate(16);
        RRPHelper.addPipeModel(2);
        RRPHelper.addPipeModel(4);
        RRPHelper.addPipeModel(8);
        RRPHelper.addPipeModel(16);*/

        //RRPHelper.addPipeBlockState("aluminium", 4);
    }

}
