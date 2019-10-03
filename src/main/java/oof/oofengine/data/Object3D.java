package oof.oofengine.data;

import java.io.File;

public class Object3D {
    private final File oofModel;

    public File getOofModel() {
        return oofModel;
    }

    public File getOofMTL() {
        return oofMTL;
    }

    public File getOofTexture() {
        return oofTexture;
    }

    private final File oofMTL;
    private final File oofTexture;

    public Object3D(File oofModel, File oofMTL, File oofTexture) {
        this.oofModel = oofModel;
        this.oofMTL = oofMTL;
        this.oofTexture = oofTexture;
    }


}
