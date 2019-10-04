package oof.oofengine.data;

import java.io.File;

public class OofConfig {

    private boolean debug = false;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    public OofConfig withDebugMode() {
        this.debug = true;
        return this;
    }

    private int widthPx;
    private int heightPx;
    private File oofModel;
    private File oofSkin;
    private File oofFace;

    public OofConfig() {}

    public int getWidthPx() {
        return widthPx;
    }

    private void setWidthPx(int widthPx) {
        this.widthPx = widthPx;
    }

    public OofConfig withWidthPx(int widthPx) {
        setWidthPx(widthPx);
        return this;
    }

    public int getHeightPx() {
        return heightPx;
    }

    private void setHeightPx(int heightPx) {
        this.heightPx = heightPx;
    }

    public OofConfig withHeightPx(int heightPx) {
        setHeightPx(heightPx);
        return this;
    }

    public OofConfig validate() {
        // TODO
        return this;
    }

    public boolean debug() {
        return debug;
    }
}
