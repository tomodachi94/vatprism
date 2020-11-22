package net.marvk.fs.vatsim.map.view.clients;

import de.saxsys.mvvmfx.InjectScope;
import net.marvk.fs.vatsim.map.data.ClientViewModel;
import net.marvk.fs.vatsim.map.repository.ClientRepository;
import net.marvk.fs.vatsim.map.view.GlobalScope;
import net.marvk.fs.vatsim.map.view.table.SimpleTableViewModel;

public class ClientsViewModel extends SimpleTableViewModel<ClientViewModel> {
    @InjectScope
    private GlobalScope globalScope;

    public ClientsViewModel(final ClientRepository clientRepository) {
        super(clientRepository);
    }
}
