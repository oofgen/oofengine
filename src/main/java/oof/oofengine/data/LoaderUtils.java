package oof.oofengine.data;

import oof.oofengine.OofEngineApplication;
import oof.oofengine.model.Model;

import static org.lwjgl.assimp.Assimp.*;

import org.lwjgl.assimp.*;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


public class LoaderUtils {
    public static Model load(String resource, int flags) {
        File file = loadResource(resource);
        AIScene aiScene = aiImportFile(file.getPath(), flags);

        if(aiScene == null) {
            throw new RuntimeException("Unable to import file!");
        }
        return new Model(aiScene);
    }


    private static File loadResource(String filePath) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            return new File(Objects.requireNonNull(loader.getResource(filePath)).getFile());
        } catch (NullPointerException e) {
            throw new RuntimeException("Couldn't find file {}", e);
        }
    }
}
