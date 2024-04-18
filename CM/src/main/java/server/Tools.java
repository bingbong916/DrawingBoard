package server;

import client.shapes.GShape;

import java.io.*;
import java.util.Base64;

public class Tools {
    public static GShape deserializeString(String encodedShape) {
        try {
            byte[] data = Base64.getDecoder().decode(encodedShape);
            ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(data);
            ObjectInputStream in = new ObjectInputStream(byteArrayIn);
            return (GShape) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String serializeShape(GShape shape) {
        try {
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteArrayOut);
            out.writeObject(shape);
            out.close();
            return Base64.getEncoder().encodeToString(byteArrayOut.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
