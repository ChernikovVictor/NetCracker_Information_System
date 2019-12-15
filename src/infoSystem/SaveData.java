package infoSystem;

import infoSystem.model.*;

import javax.xml.bind.*;
import java.io.*;
import java.util.List;

public class SaveData {

    /* Сериализовать список траспортов в файл */
    public static void serializeTransports(List<Transport> transports, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(transports);
            System.out.println("Данные успешно сохранены в " + filename);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /* Десериализовать список траспортов из файла */
    public static List<Transport> deserializeTransports(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            List<Transport> list = (List<Transport>) in.readObject();
            System.out.println("Данные успешно загружены из " + filename);
            return list;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /* Получить список траспортов из XML файла */
    public static List<Transport> getFromXML(String filename) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(TransportListWrapper.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            TransportListWrapper wrapper = (TransportListWrapper) unmarshaller.unmarshal(new File(filename));
            System.out.println("Данные успешно загружены из " + filename);
            return wrapper.getTransports();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Записать список траспортов в XML файл */
    public static void saveAsXML(List<Transport> transports, String filename) {
        try {
            TransportListWrapper wrapper = new TransportListWrapper(transports);
            JAXBContext context = JAXBContext.newInstance(TransportListWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // флаг для читабельного вывода XML в JAXB
            marshaller.marshal(wrapper, new File(filename));
            System.out.println("Данные успешно сохранены в " + filename);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
