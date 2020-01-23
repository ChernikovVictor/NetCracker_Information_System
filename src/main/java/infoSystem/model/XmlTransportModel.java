package infoSystem.model;

import infoSystem.util.XmlLoader;

import java.util.List;

public class XmlTransportModel extends TransportModel
{
    public XmlTransportModel() {
        super();
    }

    public XmlTransportModel(List<Transport> transports) {
        super(transports);
    }

    @Override
    public void downloadTransports(String filename) {
        transports = XmlLoader.getFromXML(filename);
    }

    @Override
    public void saveTransports(String filename) {
        XmlLoader.saveAsXML(transports, filename);
    }
}
