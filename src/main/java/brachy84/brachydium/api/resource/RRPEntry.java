package brachy84.brachydium.api.resource;

import brachy84.brachydium.Brachydium;
import brachy84.brachydium.api.material.Material;
import brachy84.brachydium.common.Materials;
import net.devtech.arrp.api.RRPPreGenEntrypoint;
import net.minecraft.util.Identifier;

public class RRPEntry implements RRPPreGenEntrypoint {

    private static Identifier mtId(String string) {
        return Brachydium.id(string);
    }

    @Override
    public void pregen() {



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
