import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class BubbleTexturePacker {
    //private static final String INPUT_DIR = Gdx.files.internal("assets/data/animations").toString();
    private static final String INPUT_DIR = "../android/assets/images/animations/";
    private static final String OUTPUT_DIR = "../android/assets/animations/";
    private static final String PACK_FILE = "animations";

    public static void main(String[] args){
        // create the packing's settings
        Settings settings = new Settings();

        // adjust the padding settings
        settings.paddingX = 2;
        settings.paddingY = 2;
        settings.edgePadding = false;

        // set the maximum dimension of each image atlas
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;
        settings.combineSubdirectories = true;

        // pack the images
        TexturePacker.process(settings, INPUT_DIR, OUTPUT_DIR, PACK_FILE);
    }
}

