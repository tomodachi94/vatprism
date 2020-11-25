package net.marvk.fs.vatsim.map.data;

import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import net.marvk.fs.vatsim.api.data.VatsimAirspace;
import net.marvk.fs.vatsim.map.repository.FlightInformationRegionRepository;

public class FlightInformationRegionBoundaryViewModel extends SimpleDataViewModel<VatsimAirspace, FlightInformationRegionBoundaryViewModel> implements ViewModel {
    private final FlightInformationRegionRepository flightInformationRegionRepository;
    private final ObjectProperty<Polygon> polygon = new SimpleObjectProperty<>();

    @Inject
    public FlightInformationRegionBoundaryViewModel(final FlightInformationRegionRepository flightInformationRegionRepository) {
        super();
        this.flightInformationRegionRepository = flightInformationRegionRepository;
        setupBindings();
    }

    private void setupBindings() {
        modelProperty().addListener((observable, oldValue, newValue) -> update(newValue));
    }

    private void update(final VatsimAirspace airspace) {
        updatePoints(airspace);
        updateFirs(airspace);
    }

    private void updateFirs(final VatsimAirspace airspace) {

    }

    private void updatePoints(final VatsimAirspace airspace) {
        if ("BIRD-S".equals(icaoProperty().get())) {
            System.out.println();
        }

        if (airspace == null) {
            polygon.set(null);
        } else {
            polygon.set(new Polygon(airspace.getAirspacePoints()));
        }
    }

    public StringProperty icaoProperty() {
        return stringProperty("icao", c -> c.getGeneral().getIcao());
    }

    public BooleanProperty oceanicProperty() {
        return booleanProperty("oceanic", c -> c.getGeneral().getOceanic());
    }

    public BooleanProperty extensionProperty() {
        return booleanProperty("extension", c -> c.getGeneral().getExtension());
    }

    public ObjectProperty<Point2D> minPositionProperty() {
        return pointProperty("minPosition", c -> c.getGeneral().getMinPosition());
    }

    public ObjectProperty<Point2D> maxPositionProperty() {
        return pointProperty("maxPosition", c -> c.getGeneral().getMaxPosition());
    }

    public ObjectProperty<Point2D> centerPositionProperty() {
        return pointProperty("centerPosition", c -> c.getGeneral().getCenterPosition());
    }

    public Polygon getPolygon() {
        return polygon.get();
    }

    public ObjectProperty<Polygon> polygonProperty() {
        return polygon;
    }
}
