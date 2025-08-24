package net.minecraft.entity;

import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class EntityAttachments {
    private final Map<EntityAttachment, List<Vector3d>> attachments;

    EntityAttachments(Map<EntityAttachment, List<Vector3d>> map) {
        this.attachments = map;
    }

    public static EntityAttachments createDefault(float f, float f2) {
        return EntityAttachments.builder().build(f, f2);
    }

    public static Builder builder() {
        return new Builder();
    }

    public EntityAttachments scale(float f, float f2, float f3) {
        return new EntityAttachments(Util.makeEnumMap(EntityAttachment.class, entityAttachment -> {
            ArrayList<Vector3d> arrayList = new ArrayList<Vector3d>();
            for (Vector3d vec3 : this.attachments.get(entityAttachment)) {
                arrayList.add(vec3.multiply(f, f2, f3));
            }
            return arrayList;
        }));
    }

    @Nullable
    public Vector3d getNullable(EntityAttachment entityAttachment, int n, float f) {
        List<Vector3d> list = this.attachments.get((Object)entityAttachment);
        if (n < 0 || n >= list.size()) {
            return null;
        }
        return EntityAttachments.transformPoint(list.get(n), f);
    }

    public Vector3d get(EntityAttachment entityAttachment, int n, float f) {
        Vector3d vec3 = this.getNullable(entityAttachment, n, f);
        if (vec3 == null) {
            throw new IllegalStateException("Had no attachment point of type: " + String.valueOf((Object)entityAttachment) + " for index: " + n);
        }
        return vec3;
    }

    public Vector3d getAverage(EntityAttachment entityAttachment) {
        List<Vector3d> list = this.attachments.get((Object)entityAttachment);
        if (list == null || list.isEmpty()) {
            throw new IllegalStateException("No attachment points of type: PASSENGER");
        }
        Vector3d vec3 = Vector3d.ZERO;
        for (Vector3d vec32 : list) {
            vec3 = vec3.add(vec32);
        }
        return vec3.scale(1.0f / (float)list.size());
    }

    public Vector3d getClamped(EntityAttachment entityAttachment, int n, float f) {
        List<Vector3d> list = this.attachments.get((Object)entityAttachment);
        if (list.isEmpty()) {
            throw new IllegalStateException("Had no attachment points of type: " + String.valueOf((Object)entityAttachment));
        }
        Vector3d vec3 = list.get(MathHelper.clamp(n, 0, list.size() - 1));
        return EntityAttachments.transformPoint(vec3, f);
    }

    private static Vector3d transformPoint(Vector3d vec3, float f) {
        return vec3.yRot(-f * ((float)Math.PI / 180));
    }

    public static class Builder {
        private final Map<EntityAttachment, List<Vector3d>> attachments = new EnumMap<EntityAttachment, List<Vector3d>>(EntityAttachment.class);

        Builder() {
        }

        public Builder attach(EntityAttachment entityAttachment, float f, float f2, float f3) {
            return this.attach(entityAttachment, new Vector3d(f, f2, f3));
        }

        public Builder attach(EntityAttachment entityAttachment2, Vector3d vec3) {
            this.attachments.computeIfAbsent(entityAttachment2, entityAttachment -> new ArrayList<>(1)).add(vec3);
            return this;
        }

        public EntityAttachments build(float f, float f2) {
            Map<EntityAttachment, List<Vector3d>> map = Util.makeEnumMap(EntityAttachment.class, entityAttachment -> {
                List<Vector3d> list = this.attachments.get(entityAttachment);
                if (list == null) {
                    //System.out.println("FALLBACK for: " + entityAttachment);
                } else {
                    //System.out.println("Using provided for: " + entityAttachment + ": " + list);
                }
                return list == null ? entityAttachment.createFallbackPoints(f, f2) : List.copyOf(list);
            });

            //System.out.println("Map built in build():");
            for (Map.Entry<EntityAttachment, List<Vector3d>> entry : map.entrySet()) {
                //System.out.println(entry.getKey() + " -> " + entry.getValue());
            }
            //System.out.println("Map contains VEHICLE: " + map.containsKey(EntityAttachment.VEHICLE));
            //System.out.println("Map value for VEHICLE: " + map.get(EntityAttachment.VEHICLE));



            return new EntityAttachments(map);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            for (Map.Entry<EntityAttachment, List<Vector3d>> entry : attachments.entrySet()) {
                builder.append("[").append(entry.getKey().name()).append(", ").append(entry.getValue().stream().map(Vector3d::toString).collect(Collectors.joining(","))).append("]");
            }
            return builder.toString();
        }
    }
}
