package net.minecraft.world;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Nonnull
@TypeQualifierDefault(value={ElementType.FIELD})
@Retention(value= RetentionPolicy.RUNTIME)
public @interface FieldsAreNonnullByDefault {
}