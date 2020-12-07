package net.marvk.fs.vatsim.map.view.painter;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.marvk.fs.vatsim.map.data.Polygon;
import net.marvk.fs.vatsim.map.view.map.MapVariables;

public class PainterHelper {
    private final MapVariables mapVariables;

    public PainterHelper(final MapVariables mapVariables) {
        this.mapVariables = mapVariables;
    }

    public void strokePolygons(final GraphicsContext c, final Polygon polygon) {
        drawPolygons(c, polygon, false, false);
    }

    public void strokePolylines(final GraphicsContext c, final Polygon polygon) {
        drawPolygons(c, polygon, true, false);
    }

    public void fillPolygons(final GraphicsContext c, final Polygon polygon) {
        drawPolygons(c, polygon, false, true);
    }

    private void drawPolygons(final GraphicsContext c, final Polygon polygon, final boolean polyline, final boolean fill) {
        if (mapVariables.toCanvasX(polygon.boundary().getMinX()) < 0) {
            drawPolygon(c, polygon, 360, polyline, fill);
        }

        if (mapVariables.toCanvasX(polygon.boundary().getMaxX()) > mapVariables.getViewWidth()) {
            drawPolygon(c, polygon, -360, polyline, fill);
        }

        drawPolygon(c, polygon, 0, polyline, fill);
    }

    private void drawPolygon(final GraphicsContext c, final Polygon polygon, final double offsetX, final boolean polyline, final boolean fill) {
        if (!mapVariables.isIntersectingWorldView(shiftedBounds(polygon, offsetX))) {
            return;
        }

        for (int i = 0; i < polygon.size(); i++) {
            mapVariables.getXBuf()[i] = mapVariables.toCanvasX(polygon.getPointsX()[i] + offsetX);
            mapVariables.getYBuf()[i] = mapVariables.toCanvasY(polygon.getPointsY()[i]);
        }

        if (polyline) {
            if (!fill) {
                c.strokePolyline(mapVariables.getXBuf(), mapVariables.getYBuf(), polygon.size());
            }
        } else {
            if (fill) {
                c.fillPolygon(mapVariables.getXBuf(), mapVariables.getYBuf(), polygon.size());
            } else {
                c.strokePolygon(mapVariables.getXBuf(), mapVariables.getYBuf(), polygon.size());
            }
        }
    }

    private static Rectangle2D shiftedBounds(final Polygon polygon, final double offsetX) {
        final Rectangle2D boundary = polygon.boundary();
        return new Rectangle2D(
                boundary.getMinX() + offsetX,
                boundary.getMinY(),
                boundary.getWidth(),
                boundary.getHeight()
        );
    }

    public void setPixel(final GraphicsContext c, final Color color, final int x, final int y) {
        final int actualX;

        if (x < 0) {
            actualX = (int) (x + mapVariables.getViewWidth() * mapVariables.getScale());
        } else if (x >= mapVariables.getViewWidth()) {
            actualX = (int) (x - mapVariables.getViewWidth() * mapVariables.getScale());
        } else {
            actualX = x;
        }

        c.getPixelWriter().setColor(
                actualX,
                y,
                color
        );
    }

}
