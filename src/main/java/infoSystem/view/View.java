package infoSystem.view;

import infoSystem.model.Model;
import infoSystem.model.Transport;

public interface View
{
    void showTransport(Transport transport);
    void showAllTransports(Model model);
    String getTransportInfo(Transport transport);
    String getAllTransportsInfo(Model model);
}
