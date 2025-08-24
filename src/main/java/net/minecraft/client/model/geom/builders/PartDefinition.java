
package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

public class PartDefinition {
    private final List<CubeDefinition> cubes;
    private final PartPose partPose;
    private final Map<String, PartDefinition> children = Maps.newHashMap();

    PartDefinition(List<CubeDefinition> list, PartPose partPose) {
        this.cubes = list;
        this.partPose = partPose;
    }

    public PartDefinition addOrReplaceChild(String string, CubeListBuilder cubeListBuilder, PartPose partPose) {
        PartDefinition partDefinition = new PartDefinition(cubeListBuilder.getCubes(), partPose);
        PartDefinition partDefinition2 = this.children.put(string, partDefinition);
        if (partDefinition2 != null) {
            partDefinition.children.putAll(partDefinition2.children);
        }
        return partDefinition;
    }

    public PartDefinition addOrReplaceChild(String string, PartDefinition partDefinition) {
        PartDefinition partDefinition2 = this.children.put(string, partDefinition);
        if (partDefinition2 != null) {
            partDefinition.children.putAll(partDefinition2.children);
        }
        return partDefinition;
    }

    public ModelPart bake(int n, int n2) {
        Object2ObjectArrayMap object2ObjectArrayMap = this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> ((PartDefinition)entry.getValue()).bake(n, n2), (modelPart, modelPart2) -> modelPart, Object2ObjectArrayMap::new));
        List list = (List)this.cubes.stream().map(cubeDefinition -> cubeDefinition.bake(n, n2)).collect(ImmutableList.toImmutableList());
        ModelPart modelPart3 = new ModelPart(list, (Map<String, ModelPart>)object2ObjectArrayMap);
        modelPart3.setInitialPose(this.partPose);
        modelPart3.loadPose(this.partPose);
        return modelPart3;
    }

    public PartDefinition getChild(String string) {
        return this.children.get(string);
    }

    public Set<Map.Entry<String, PartDefinition>> getChildren() {
        return this.children.entrySet();
    }

    public PartDefinition transformed(UnaryOperator<PartPose> unaryOperator) {
        PartDefinition partDefinition = new PartDefinition(this.cubes, (PartPose)unaryOperator.apply(this.partPose));
        partDefinition.children.putAll(this.children);
        return partDefinition;
    }
}

