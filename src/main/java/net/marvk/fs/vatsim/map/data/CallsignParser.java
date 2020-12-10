package net.marvk.fs.vatsim.map.data;

import com.google.inject.Inject;
import javafx.geometry.Point2D;
import lombok.extern.slf4j.Slf4j;
import net.marvk.fs.vatsim.api.data.VatsimClient;
import net.marvk.fs.vatsim.api.data.VatsimClientType;
import net.marvk.fs.vatsim.api.data.VatsimController;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class CallsignParser {
    private final AirportRepository airportRepository;
    private final FlightInformationRegionRepository flightInformationRegionRepository;
    private final UpperInformationRegionRepository upperInformationRegionRepository;

    @Inject
    public CallsignParser(
            final AirportRepository airportRepository,
            final FlightInformationRegionRepository flightInformationRegionRepository,
            final UpperInformationRegionRepository upperInformationRegionRepository
    ) {
        this.airportRepository = airportRepository;
        this.flightInformationRegionRepository = flightInformationRegionRepository;
        this.upperInformationRegionRepository = upperInformationRegionRepository;
    }

    public Result parse(final VatsimController controller) {
        if (controller == null ||
                controller.getCallsign() == null ||
                controller.getClientType() == null ||
                controller.getClientType() == VatsimClientType.PILOT
        ) {
            return Result.EMPTY;
        }

        final String callsign = controller.getCallsign();
        final String cid = controller.getCid();

        final String[] sections = callsign.split("_");
        final int n = sections.length;

        final ControllerType defaultControllerType = ControllerType.OBS;

        final String identifier;
        final String infix;
        final ControllerType controllerType;

        if (n <= 1) {
            identifier = null;
            infix = null;
            controllerType = defaultControllerType;
        } else if (n == 2) {
            identifier = sections[0];
            infix = null;
            final String s1 = sections[1];
            controllerType = ControllerType.fromString(s1, defaultControllerType);
        } else if (n == 3) {
            identifier = sections[0];
            infix = sections[1];
            controllerType = ControllerType.fromString(sections[2], defaultControllerType);
        } else {
            log.warn("Unexpected callsign " + callsign);
            return Result.EMPTY;
        }

        final Airport airport;
        final UpperInformationRegion uir;
        final FlightInformationRegion fir;

        if (controllerType == ControllerType.OBS) {
            airport = null;
            fir = null;
            uir = null;
        } else {
            if (controllerType != ControllerType.CTR && controllerType != ControllerType.FSS) {
                airport = getAirport(controller, identifier);

                if (airport == null) {
                    log.warn("UNKNOWN AIRPORT: " + identifier + " Full callsign: " + callsign + ", cid " + cid);
                }

                uir = null;
                fir = null;
            } else {
                airport = null;
                uir = getUir(controller, identifier);

                if (uir == null) {
                    fir = getFir(controller, identifier, infix);

                    if (fir == null) {
                        log.warn("UNKNOWN UIR or FIR: " + identifier + " Full callsign: " + callsign + ", cid " + cid);
                    }
                } else {
                    fir = null;
                }
            }
        }

        return new Result(
                airport,
                fir,
                uir,
                controllerType
        );
    }

    private FlightInformationRegion getFir(final VatsimClient vatsimClient, final String identifier, final String infix) {
        final List<FlightInformationRegion> firs = flightInformationRegionRepository.getByIdentifierAndInfix(identifier, infix);

        if (firs.isEmpty()) {
            return null;
        }

        if (firs.size() > 1) {
            log.warn("Could not determine exact FIR for " + identifier + ", full callsign " + vatsimClient.getCallsign() + ", cid " + vatsimClient
                    .getCid());
        }

        return firs.get(0);
    }

    private UpperInformationRegion getUir(final VatsimClient vatsimClient, final String identifier) {
        return returnFirst(
                () -> extractViewModel(
                        upperInformationRegionRepository.getByIcao(identifier),
                        e -> new Point2D(0., 0.),
                        vatsimClient
                )
        );
    }

    private Airport getAirport(final VatsimClient vatsimClient, final String identifier) {
        return returnFirst(
                () -> extractViewModel(
                        airportRepository.getByIcao(identifier),
                        e -> e.positionProperty().get(),
                        vatsimClient
                ),
                () -> extractViewModel(
                        airportRepository.getByIata(identifier),
                        e -> e.positionProperty().get(),
                        vatsimClient
                )
        );
    }

    @SafeVarargs
    private static <ViewModel> ViewModel returnFirst(final Supplier<ViewModel>... supplier) {
        return Arrays.stream(supplier).map(Supplier::get).filter(Objects::nonNull).findFirst().orElse(null);
    }

    private static <ViewModel> ViewModel extractViewModel(final List<ViewModel> viewModels, final Function<ViewModel, Point2D> positionExtractor, final VatsimClient vatsimClient) {
        if (viewModels.isEmpty()) {
            return null;
        }

        log.warn("Multiple models");
        return viewModels.get(0);
    }

    public static class Result {
        public static final Result EMPTY = new Result(null, null, null, ControllerType.NONE);

        private final Airport airport;
        private final FlightInformationRegion flightInformationRegion;
        private final UpperInformationRegion upperInformationRegion;
        private final ControllerType controllerType;

        public Result(
                final Airport airport,
                final FlightInformationRegion flightInformationRegion,
                final UpperInformationRegion upperInformationRegion,
                final ControllerType controllerType
        ) {
            this.airport = airport;
            this.flightInformationRegion = flightInformationRegion;
            this.upperInformationRegion = upperInformationRegion;
            this.controllerType = Objects.requireNonNullElse(controllerType, ControllerType.NONE);
        }

        public Airport getAirport() {
            return airport;
        }

        public FlightInformationRegion getFlightInformationRegion() {
            return flightInformationRegion;
        }

        public UpperInformationRegion getUpperInformationRegion() {
            return upperInformationRegion;
        }

        public ControllerType getControllerType() {
            return controllerType;
        }

        public boolean isEmpty() {
            return EMPTY.equals(this);
        }
    }
}


