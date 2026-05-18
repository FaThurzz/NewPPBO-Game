package game.engine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {

    public static BufferedImage load(String path) {
        try {
            InputStream is = ImageLoader.class
                    .getResourceAsStream(path);
            if (is == null) {
                System.err.println("Gambar tidak ditemukan: " + path);
                return null;
            }
            System.out.println("Berhasil load: " + path);
            return ImageIO.read(is);
        } catch (IOException e) {
            System.err.println("Gagal load: " + path);
            return null;
        }
    }
}