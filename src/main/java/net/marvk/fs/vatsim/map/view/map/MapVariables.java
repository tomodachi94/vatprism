package net.marvk.fs.vatsim.map.view.map;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

public class MapVariables {
    public static final double WORLD_WIDTH = 360;
    public static final double WORLD_HALF_WIDTH = 180;
    public static final double WORLD_HEIGHT = 180;
    public static final double WORLD_HALF_HEIGHT = 90;

    public static final double WORLD_ASPECT_RATIO = WORLD_WIDTH / WORLD_HEIGHT;
    private static final int EXPANDED_WIDTH = 5;

    private double scale;

    private double viewWidth = 0;
    private double viewHeight = 0;

    private double viewHalfWidth = 0;
    private double viewHalfHeight = 0;

    private double worldCenterX = 0;
    private double worldCenterY = 0;

    private final double[] xBuf;
    private final double[] yBuf;

    private double aspectScaleY;

    private Rectangle2D worldView = new Rectangle2D(0, 0, 0, 0);
    private Rectangle2D worldViewExpanded = new Rectangle2D(0, 0, 0, 0);

    public MapVariables() {
        this(320000);
    }

    public MapVariables(final int bufferSize) {
        this.xBuf = new double[bufferSize];
        this.yBuf = new double[bufferSize];
    }

    public Point2D toWorld(final Point2D canvas) {
        return new Point2D(
                toWorldX(canvas.getX()),
                toWorldY(canvas.getY())
        );
    }

    public Point2D toCanvas(final Point2D world) {
        return new Point2D(
                toCanvasX(world.getX()),
                toCanvasY(world.getY())
        );
    }

    public double toCanvasX(final double modelX) {
        final double zeroStart = (modelX + worldCenterX) * scale + WORLD_HALF_WIDTH;
        final double normalized = zeroStart / WORLD_WIDTH;

        return normalized * viewWidth + 0.5;
    }

    public double toCanvasY(final double modelY) {
        final double zeroStart = ((modelY + worldCenterY) * scale * aspectScaleY() + WORLD_HALF_HEIGHT);
        final double normalized = zeroStart / WORLD_HEIGHT;

        return viewHeight - normalized * viewHeight + 0.5;
    }

    public double toWorldX(final double canvasX) {
        return ((((canvasX - 0.5) / viewWidth) * WORLD_WIDTH - WORLD_HALF_WIDTH) / scale) - worldCenterX;
    }

    public double toWorldY(final double canvasY) {
        return ((((1 - (canvasY - 0.5) / viewHeight) * WORLD_HEIGHT - WORLD_HALF_HEIGHT) / scale) / aspectScaleY()) - worldCenterY;
    }

    public boolean isContainedInWorldView(final Point2D worldPosition) {
        return worldView.contains(worldPosition);
    }

    public boolean isContainedInExpandedWorldView(final Point2D worldPosition) {
        return worldViewExpanded.contains(worldPosition);
    }

    public boolean isIntersectingWorldView(final Rectangle2D worldRectangle) {
        return worldView.intersects(worldRectangle);
    }

    public double aspectScaleY() {
        return aspectScaleY;
    }

    void setScale(final double scale) {
        this.scale = scale;
        updateWorldView();
    }

    void setViewSize(final double width, final double height) {
        this.viewWidth = width;
        this.viewHeight = height;

        this.viewHalfHeight = width / 2.0;
        this.viewHalfWidth = height / 2.0;

        this.aspectScaleY = viewWidth / viewHeight / WORLD_ASPECT_RATIO;
        updateWorldView();
    }

    void updateWorldView() {
        final double minX = toWorldX(0);
        final double maxX = toWorldX(viewWidth);
        final double minY = toWorldY(viewHeight);
        final double maxY = toWorldY(0);

        this.worldView = new Rectangle2D(
                minX,
                minY,
                maxX - minX,
                maxY - minY
        );

        final double expandedWidth = EXPANDED_WIDTH / scale;
        this.worldViewExpanded = new Rectangle2D(
                minX - expandedWidth,
                minY - expandedWidth,
                maxX - minX + expandedWidth + expandedWidth,
                maxY - minY + expandedWidth + expandedWidth
        );
    }

    void setViewWidth(final double width) {
        setViewSize(width, viewHeight);
    }

    void setViewHeight(final double height) {
        setViewSize(viewWidth, height);
    }

    void setWorldCenter(final double x, final double y) {
        this.worldCenterX = x;
        this.worldCenterY = y;
        updateWorldView();
    }

    double scaleForRectFit(final double worldWidth, final double worldHeight) {
        return Math.min(scaleForWorldHeight(worldHeight), scaleForWorldWidth(worldWidth));
    }

    double scaleForWorldHeight(final double worldHeight) {
        final double ratio = worldHeight * aspectScaleY / WORLD_HEIGHT;

        return 1.0 / ratio;
    }

    double scaleForWorldWidth(final double worldWidth) {
        final double ratio = worldWidth / WORLD_WIDTH;

        return 1.0 / ratio;
    }

    public double worldWidthToViewWidth(final double worldWidth) {
        return (worldWidth / (WORLD_WIDTH / scale)) * viewWidth;
    }

    void setWorldCenter(final Point2D worldCenter) {
        setWorldCenter(worldCenter.getX(), worldCenter.getY());
    }

    public double getScale() {
        return scale;
    }

    public double getViewWidth() {
        return viewWidth;
    }

    public double getViewHeight() {
        return viewHeight;
    }

    public double getViewHalfWidth() {
        return viewHalfWidth;
    }

    public double getViewHalfHeight() {
        return viewHalfHeight;
    }

    public double getWorldCenterX() {
        return worldCenterX;
    }

    public double getWorldCenterY() {
        return worldCenterY;
    }

    public double[] getXBuf() {
        return xBuf;
    }

    public double[] getYBuf() {
        return yBuf;
    }
}
