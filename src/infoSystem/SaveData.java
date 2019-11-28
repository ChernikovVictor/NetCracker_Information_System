package infoSystem;

import infoSystem.model.Transport;

import java.io.*;
import java.util.List;

public class SaveData {

    // сериализовать модель в файл
    public static void serializeTransports(List<Transport> transports, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(transports);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // десериализовать модель из файла
    public static List<Transport> deserializeTransports(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<Transport>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
