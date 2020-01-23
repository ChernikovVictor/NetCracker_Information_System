package infoSystem.view;

import infoSystem.model.Model;
import infoSystem.model.Transport;

public interface View
{
    void showTransport(Transport transport);
    void showTransport(int index, Model model);
    void showAllTransports(Model model);
}
