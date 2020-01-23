package infoSystem.util;

import infoSystem.model.Transport;

import javax.xml.bind.*;
import java.io.File;
import java.util.List;

public class XmlLoader {

    /* Получить список траспортов из XML файла */
    public static List<Transport> getFromXML(String filename) {
        return getFromXML(new File(filename));
    }

    /* Получить список траспортов из XML файла */
    public static List<Transport> getFromXML(File file) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(TransportListWrapper.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            TransportListWrapper wrapper = (TransportListWrapper) unmarshaller.unmarshal(file);
            System.out.println("Данные успешно загружены из " + file.getName());
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
