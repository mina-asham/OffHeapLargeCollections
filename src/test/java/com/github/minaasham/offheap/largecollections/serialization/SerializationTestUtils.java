package com.github.minaasham.offheap.largecollections.serialization;

import lombok.experimental.UtilityClass;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UtilityClass
public final class SerializationTestUtils {

    private static final Random RANDOM = new Random();

    public static <T> void testRoundtrip(T value, Supplier<ObjectSerializer<T>> serializerSupplier) {
        ObjectSerializer<T> serializer = serializerSupplier.get();
        ByteBuffer buffer = ByteBuffer.allocate(serializer.sizeInBytes(value));
        MemoryReader reader = new BufferMemoryReader(buffer);
        MemoryWriter writer = new BufferMemoryWriter(buffer);

        serializer.serialize(writer, value);
        buffer.flip();

        T actualValue = serializer.deserialize(reader);

        if (value.getClass().isArray()) {
            assertArrayEquals((Object[]) value, (Object[]) actualValue);
        } else {
            assertEquals(value, actualValue);
        }
    }

    public static String randomString() {
        int size = RANDOM.nextInt(32);
        byte[] bytes = new byte[size];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (33 + RANDOM.nextInt(128 - 33));
        }
        return new String(bytes, UTF_8);
    }

    public static int randomInt() {
        return RANDOM.nextInt();
    }
}
