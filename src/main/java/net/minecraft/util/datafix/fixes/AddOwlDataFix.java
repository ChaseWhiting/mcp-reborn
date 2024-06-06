package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class AddOwlDataFix extends DataFix {
    public AddOwlDataFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return fixTypeEverywhereTyped("Add Owl Data Fix", getInputSchema().getType(TypeReferences.ENTITY), typed -> {
            return typed.update(DSL.remainderFinder(), dynamic -> {
                return dynamic;
            });
        });
    }
}
