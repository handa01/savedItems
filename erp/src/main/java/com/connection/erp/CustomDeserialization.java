package com.connection.erp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class CustomDeserialization {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomDeserialization.class);

    static ClassLoader customClassLoader = null;

    public static void setCustomClassLoader(ClassLoader customClassLoader1) {
        customClassLoader = customClassLoader1;
    }

    public Object deserialize(@Nullable byte[] bytes) {

        if (bytes == null) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes)){
            @Override
            public Class resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
                return Class.forName(desc.getName(), false, customClassLoader);
            }
        }) {
            return ois.readObject();
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Failed to deserialize object", ex);
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Failed to deserialize object type", ex);
        }
    }
}
