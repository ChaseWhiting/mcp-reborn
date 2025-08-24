package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.network.PacketBuffer;

import java.util.Arrays;
import java.util.Collection;

public class NumberArgumentType<T extends Number> implements ArgumentType<T> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "1.2", ".5", "-1", "-.5", "-1234.56");
    private final T minimum;
    private final T maximum;
    private final Class<T> type;

    public NumberArgumentType(T minimum, T maximum, Class<T> type) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.type = type;
    }
    private static final DynamicCommandExceptionType READER_INVALID_NUMBER = new DynamicCommandExceptionType((value) -> {
        return new LiteralMessage("Invalid number '" + value + "'");
    });

    public static <T extends Number> NumberArgumentType<T> numberArg(Class<T> type) {
        T defaultValue = getDefaultValue(type);
        return numberArg(type, defaultValue);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T getDefaultValue(Class<T> type) {
        if (type == Integer.class) {
            return (T) Integer.valueOf(Integer.MIN_VALUE);
        } else if (type == Double.class) {
            return (T) Double.valueOf(Double.NEGATIVE_INFINITY);
        } else if (type == Float.class) {
            return (T) Float.valueOf(Float.NEGATIVE_INFINITY);
        } else if (type == Long.class) {
            return (T) Long.valueOf(Long.MIN_VALUE);
        } else if (type == Number.class) {
            return (T) Double.valueOf(Double.NEGATIVE_INFINITY);
        }
        // Add more types as needed
        throw new IllegalArgumentException("Unsupported number type: " + type);
    }

    public static <T extends Number> NumberArgumentType<T> numberArg(Class<T> type, T min) {
        T max = getMaxValue(type);
        return numberArg(type, min, max);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T getMaxValue(Class<T> type) {
        if (type == Integer.class) {
            return (T) Integer.valueOf(Integer.MAX_VALUE);
        } else if (type == Double.class) {
            return (T) Double.valueOf(Double.POSITIVE_INFINITY);
        } else if (type == Float.class) {
            return (T) Float.valueOf(Float.POSITIVE_INFINITY);
        } else if (type == Long.class) {
            return (T) Long.valueOf(Long.MAX_VALUE);
        }
        // Add more types as needed
        throw new IllegalArgumentException("Unsupported number type: " + type);
    }

    public static <T extends Number> NumberArgumentType<T> numberArg(Class<T> type, T min, T max) {
        return new NumberArgumentType<>(min, max, type);
    }
    @SuppressWarnings("unchecked")
    public static <T extends Number> T getNumber(CommandContext<?> context, String name) {
        return (T) context.getArgument(name, Number.class);
    }

    public static int getInteger(CommandContext<?> context, String name) {
        return IntegerArgumentType.getInteger(context, name);
    }

    public static double getDouble(CommandContext<?> context, String name) {
        return DoubleArgumentType.getDouble(context, name);
    }

    public static float getFloat(CommandContext<?> context, String name) {
        return FloatArgumentType.getFloat(context, name);
    }

    public static long getLong(CommandContext<?> context, String name) {
        return LongArgumentType.getLong(context, name);
    }

    public T getMinimum() {
        return this.minimum;
    }

    public T getMaximum() {
        return this.maximum;
    }

    public T parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        String numberStr = reader.readUnquotedString();
        T result;

        try {
            if (type == Integer.class) {
                result = type.cast(Integer.parseInt(numberStr));
            } else if (type == Long.class) {
                result = type.cast(Long.parseLong(numberStr));
            } else if (type == Double.class) {
                result = type.cast(Double.parseDouble(numberStr));
            } else if (type == Float.class) {
                result = type.cast(Float.parseFloat(numberStr));
            } else {
                // Default to Double if no specific type is provided
                result = type.cast(Double.parseDouble(numberStr));
            }
        } catch (NumberFormatException e) {
            reader.setCursor(start);
            throw READER_INVALID_NUMBER.createWithContext(reader, numberStr);
        }

        if (result.doubleValue() < this.minimum.doubleValue()) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooLow().createWithContext(reader, result, this.minimum);
        } else if (result.doubleValue() > this.maximum.doubleValue()) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooHigh().createWithContext(reader, result, this.maximum);
        } else {
            return result;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof NumberArgumentType)) {
            return false;
        } else {
            NumberArgumentType<?> that = (NumberArgumentType<?>) o;
            return this.maximum.equals(that.maximum) && this.minimum.equals(that.minimum) && this.type.equals(that.type);
        }
    }

    public int hashCode() {
        return 31 * this.minimum.hashCode() + this.maximum.hashCode() + this.type.hashCode();
    }

    public String toString() {
        return this.maximum.doubleValue() == Double.POSITIVE_INFINITY ?
                "number(" + this.minimum + ")" :
                "number(" + this.minimum + ", " + this.maximum + ")";
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static class Serializer<T extends Number> implements IArgumentSerializer<NumberArgumentType<T>> {

        @Override
        public void serializeToNetwork(NumberArgumentType<T> argument, PacketBuffer buffer) {
            buffer.writeDouble(argument.minimum.doubleValue());
            buffer.writeDouble(argument.maximum.doubleValue());
            buffer.writeUtf(argument.type.getName());
        }

        @Override
        public NumberArgumentType<T> deserializeFromNetwork(PacketBuffer buffer) {
            double min = buffer.readDouble();
            double max = buffer.readDouble();
            String className = buffer.readUtf(32767);

            try {
                Class<T> type = (Class<T>) Class.forName(className);
                return new NumberArgumentType<>(type.cast(min), type.cast(max), type);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to deserialize NumberArgumentType", e);
            }
        }

        @Override
        public void serializeToJson(NumberArgumentType<T> argument, JsonObject json) {
            json.addProperty("min", argument.minimum);
            json.addProperty("max", argument.maximum);
            json.addProperty("type", argument.type.getName());
        }
    }



    // Integer Argument Types
    public static NumberArgumentType<Integer> integer() {
        int def = getDefaultValue(Integer.class);
        return numberArg(Integer.class, def);
    }

    public static NumberArgumentType<Integer> integer(int min) {
        return numberArg(Integer.class, min);
    }

    public static NumberArgumentType<Integer> integer(int min, int max) {
        return numberArg(Integer.class, min, max);
    }

    // Double Argument Types
    public static NumberArgumentType<Double> doubleArg() {
        double def = getDefaultValue(Double.class);
        return numberArg(Double.class, def);
    }

    public static NumberArgumentType<Double> doubleArg(double min) {
        return numberArg(Double.class, min);
    }

    public static NumberArgumentType<Double> doubleArg(double min, double max) {
        return numberArg(Double.class, min, max);
    }

    // Float Argument Types
    public static NumberArgumentType<Float> floatArg() {
        float def = getDefaultValue(Float.class);
        return numberArg(Float.class, def);
    }

    public static NumberArgumentType<Float> floatArg(float min) {
        return numberArg(Float.class, min);
    }

    public static NumberArgumentType<Float> floatArg(float min, float max) {
        return numberArg(Float.class, min, max);
    }

    // Long Argument Types
    public static NumberArgumentType<Long> longArg() {
        long def = getDefaultValue(Long.class);
        return numberArg(Long.class, def);
    }

    public static NumberArgumentType<Long> longArg(long min) {
        return numberArg(Long.class, min);
    }

    public static NumberArgumentType<Long> longArg(long min, long max) {
        return numberArg(Long.class, min, max);
    }
}