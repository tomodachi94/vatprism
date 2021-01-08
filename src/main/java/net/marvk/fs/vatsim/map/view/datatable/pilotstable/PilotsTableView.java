package net.marvk.fs.vatsim.map.view.datatable.pilotstable;

import com.google.inject.Inject;
import net.marvk.fs.vatsim.map.data.Airport;
import net.marvk.fs.vatsim.map.data.FlightRule;
import net.marvk.fs.vatsim.map.data.Pilot;
import net.marvk.fs.vatsim.map.view.EtaToStringMapper;
import net.marvk.fs.vatsim.map.view.TextFlowHighlighter;
import net.marvk.fs.vatsim.map.view.datatable.clientstable.AbstractClientsTableView;

import java.util.Comparator;

public class PilotsTableView extends AbstractClientsTableView<PilotsTableViewModel, Pilot> {
    private static final EtaToStringMapper ETA_MAPPER = new EtaToStringMapper();

    @Inject
    public PilotsTableView(final TextFlowHighlighter textFlowHighlighter) {
        super(textFlowHighlighter);
    }

    @Override
    protected void initializeColumns() {
        super.initializeColumns();

        this.<Airport>newColumnBuilder()
                .title("Departure")
                .objectObservableValueFactory(e -> e.getFlightPlan().departureAirportProperty())
                .toStringMapper(Airport::getIcao)
                .sortable(Comparator.nullsFirst(Comparator.comparing(Airport::getIcao)))
                .mono(true)
                .widthFactor(0.7)
                .build();

        this.<Airport>newColumnBuilder()
                .title("Arrival")
                .objectObservableValueFactory(e -> e.getFlightPlan().arrivalAirportProperty())
                .toStringMapper(Airport::getIcao)
                .sortable(Comparator.nullsFirst(Comparator.comparing(Airport::getIcao)))
                .mono(true)
                .widthFactor(0.7)
                .build();

        this.<Airport>newColumnBuilder()
                .title("Alternate")
                .objectObservableValueFactory(e -> e.getFlightPlan().alternativeAirportProperty())
                .toStringMapper(Airport::getIcao)
                .sortable(Comparator.nullsFirst(Comparator.comparing(Airport::getIcao)))
                .mono(true)
                .widthFactor(0.7)
                .build();

        this.<String>newColumnBuilder()
                .title("Aircraft Type")
                .stringObservableValueFactory(e -> e.getFlightPlan().aircraftProperty())
                .sortable()
                .build();

        this.<FlightRule>newColumnBuilder()
                .title("Flight Rules")
                .objectObservableValueFactory(e -> e.getFlightPlan().flightRuleProperty())
                .toStringMapper(Enum::toString)
                .sortable()
                .widthFactor(0.85)
                .build();

        this.<Number>newColumnBuilder()
                .title("Gnd. Speed")
                .objectObservableValueFactory(Pilot::groundSpeedProperty)
                .toStringMapper(e -> "%dkts".formatted(e.intValue()))
                .sortable()
                .mono(true)
                .widthFactor(0.7)
                .build();

        this.<Number>newColumnBuilder()
                .title("Altitude")
                .objectObservableValueFactory(Pilot::altitudeProperty)
                .toStringMapper(e -> "%dft".formatted(e.intValue()))
                .sortable()
                .mono(true)
                .widthFactor(0.7)
                .build();

        this.<Pilot.Eta>newColumnBuilder()
                .title("ETA")
                .objectObservableValueFactory(Pilot::etaProperty)
                .toStringMapper(ETA_MAPPER::map)
                .sortable(Comparator.comparing(Pilot.Eta::getDuration))
                .mono(true)
                .widthFactor(0.6)
                .build();
    }
}
