package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.TypeReferences;

public class AddRaccoonDataFix extends DataFix {
    public AddRaccoonDataFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return fixTypeEverywhereTyped("Add Raccoon Data Fix", getInputSchema().getType(TypeReferences.ENTITY), typed -> {
            return typed.update(DSL.remainderFinder(), dynamic -> {
                return dynamic;
            });
        });
    }
}
