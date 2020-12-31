package net.marvk.fs.vatsim.map.view.painter;

import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.text.TextAlignment;
import net.marvk.fs.vatsim.map.data.FlightInformationRegionBoundary;
import net.marvk.fs.vatsim.map.data.Polygon;
import net.marvk.fs.vatsim.map.view.map.MapVariables;

import java.util.HashSet;
import java.util.Set;

public class FirbPainter extends MapPainter<FlightInformationRegionBoundary> {
    @Parameter("Fill Color")
    private final Color fillColor;
    @Parameter("Color")
    private final Color color;
    @Parameter(value = "Line Width", min = 0, max = 10)
    private final double lineWidth;
    @Parameter("Fill")
    private final boolean fill;
    @Parameter("Stroke")
    private final boolean stroke;
    @Parameter("Label")
    private final boolean label;

    private final Set<FlightInformationRegionBoundary> paintedFirbs = new HashSet<>();

    public FirbPainter(final MapVariables mapVariables, final Color color, final double lineWidth, final boolean fill, final boolean stroke, final boolean label) {
        super(mapVariables);
        this.color = color;
        this.lineWidth = lineWidth;
        this.fill = fill;
        this.label = label;
        this.stroke = stroke;
        this.fillColor = color.deriveColor(0, 1, 1, 0.05);
    }

    public FirbPainter(final MapVariables mapVariables, final Color color, final double lineWidth) {
        this(mapVariables, color, lineWidth, false, true, false);
    }

    @Override
    public void afterAllRender() {
        paintedFirbs.clear();
    }

    @Override
    public void paint(final GraphicsContext c, final FlightInformationRegionBoundary firb) {
        if (firb.isExtension()) {
            return;
        }

        if (!paintedFirbs.add(firb)) {
            return;
        }

        final Polygon polygon = firb.getPolygon();

        if (fill) {
            c.setFill(fillColor);
            painterHelper.fillPolygons(c, polygon);
        }

        if (stroke) {
            c.setStroke(color);
            c.setLineWidth(lineWidth);
            c.setLineDashes(null);
            painterHelper.strokePolygons(c, polygon);
        }

        if (label) {
            c.setFill(color);
            final Point2D polyLabel = polygon.getExteriorRing().getPolyLabel();

            if (polyLabel != null) {
                c.setTextAlign(TextAlignment.CENTER);
                c.setTextBaseline(VPos.CENTER);
                c.fillText(
                        "%s%s".formatted(firb.getIcao(), firb.isOceanic() ? " Oceanic" : ""),
                        mapVariables.toCanvasX(polyLabel.getX()),
                        mapVariables.toCanvasY(polyLabel.getY())
                );
            }
        }
    }

    private Paint hatched(final FlightInformationRegionBoundary firb) {
        final double cx = mapVariables.toCanvasX(firb.getPolygon().boundary().getMinX());
        final double cy = mapVariables.toCanvasY(firb.getPolygon().boundary().getMinY());
        return new LinearGradient(
                cx,
                cy,
                cx + 10,
                cy + 10,
                false,
                CycleMethod.REPEAT,
                new Stop(0, fillColor),
                new Stop(0.5, fillColor),
                new Stop(0.5, fillColor.darker()),
                new Stop(1, fillColor.darker())
        );
    }
}
