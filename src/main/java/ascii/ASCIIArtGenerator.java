package ascii;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * ASCII Art Generator in Java.
 * Prints a given text as an ASCII text art on the console.
 * This code is licensed under - CC Attribution CC BY 4.0.
 *
 * @author www.quickprogrammingtips.com
 */
public class ASCIIArtGenerator {

    public static final int ART_SIZE_SMALL = 12;
    public static final int ART_SIZE_MEDIUM = 18;
    public static final int ART_SIZE_LARGE = 24;
    public static final int ART_SIZE_HUGE = 32;
    public static final String alphanum = "ABCDEFGHJKLMNPRSTUVWXYZabcdefghjkmnoprstuvwxyz";
    private static final String pixels = "|!;[]{},.°"; // have same size in the mindustry chat

    private static final String DEFAULT_ART_SYMBOL = "!";

    public static void main(String[] args) throws Exception {
        ASCIIArtGenerator artGen = new ASCIIArtGenerator();
        String captchaTestString = generateRandomString(7); // generate the captchaText
        String captcha = artGen.noise(artGen.getTextArt(captchaTestString, ASCIIArtGenerator.ART_SIZE_LARGE, ASCIIArtFont.ART_FONT_MONO, "!"), 0.03f, 5); // call noise function to give it some artifacts
        System.out.println(captcha);
        System.out.println();

        System.out.println(generateCommand(captcha)); // [debug] generate the command to send it in chat
        System.out.println(captchaTestString); // print the actual text
    }

    static String generateCommand(String text) {
        StringBuilder command = new StringBuilder("js Call.sendMessage(\"");
        for (char i : text.toCharArray()) {
            if (i == '\n') {
                command.append("\\n\" + \"");
            } else {
                command.append(i);
            }
        }
        command.append("\")");
        return command.toString();
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(alphanum.charAt((int) (Math.random() * alphanum.length())));
        }
        return sb.toString();
    }

    /**
     * Prints ASCII art for the specified text. For size, you can use predefined sizes or a custom size.
     * Usage - getTextArt("Hi",30,ASCIIArtFont.ART_FONT_SERIF,"@");
     *
     * @param artText
     * @param textHeight - Use a predefined size or a custom type
     * @param fontType   - Use one of the available fonts
     * @param artSymbol  - Specify the character for printing the ascii art
     */
    private String getTextArt(String artText, int textHeight, ASCIIArtFont fontType, String artSymbol) {
        String fontName = fontType.getValue();
        int imageWidth = findImageWidth(textHeight, artText, fontName);

        BufferedImage image = new BufferedImage(imageWidth, textHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Font font = new Font(fontName, Font.BOLD, textHeight);
        g.setFont(font);

        Graphics2D graphics = (Graphics2D) g;
        graphics.drawString(artText, 0, getBaselinePosition(g, font));

        StringBuilder mainBuilder = new StringBuilder();

        for (int y = 0; y < textHeight; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < imageWidth; x++)
                sb.append(image.getRGB(x, y) == Color.WHITE.getRGB() ? artSymbol : " ");
            if (sb.toString().trim().isEmpty())
                continue;
//            System.out.println(sb);
            mainBuilder.append(sb).append("\n");
        }
        return mainBuilder.toString();
    }

    /**
     * Convenience method for printing ascii text art.
     * Font default - Dialog,  Art symbol default - *
     *
     * @param artText
     * @param textHeight
     * @throws Exception
     */
    private void printTextArt(String artText, int textHeight) throws Exception {
        getTextArt(artText, textHeight, ASCIIArtFont.ART_FONT_DIALOG, DEFAULT_ART_SYMBOL);
    }

    /**
     * Using the Current font and current art text find the width of the full image
     *
     * @param textHeight
     * @param artText
     * @param fontName
     * @return
     */
    private int findImageWidth(int textHeight, String artText, String fontName) {
        BufferedImage im = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics g = im.getGraphics();
        g.setFont(new Font(fontName, Font.BOLD, textHeight));
        return g.getFontMetrics().stringWidth(artText);
    }

    /**
     * Find where the text baseline should be drawn so that the characters are within image
     *
     * @param g
     * @param font
     * @return
     */
    private int getBaselinePosition(Graphics g, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int y = metrics.getAscent() - metrics.getDescent();
        return y;
    }

    /**
     * insert some noise
     *
     * @param s             string to noisify
     * @param noise         how many iterations
     * @param noiseStrength
     */
    public String noise(String s, float noise, float noiseStrength) {
        ArrayList<String> string = new ArrayList<>(Arrays.asList(s.split("")));
        if (noise < 1) {
            noise = s.length() * noise;
        }
        noise = (int) noise;
        for (int i = 0; i < noise; i++) {
            int index = (int) (Math.random() * string.size());
            String pix = string.get(index);
            if (Objects.equals(pix, "\n")) continue;
            int pixId = pixels.indexOf(pix);
            string.set(index,
                    String.valueOf(
                            pixels.charAt(
                                    getRandomNumber(
                                            (int) Math.max(pixId - noiseStrength, 0),
                                            (int) Math.min(pixId + noiseStrength, pixels.length())
                                    )
                            )
                    )
            );
        }
        return String.join("", string);
    }

    // some helper function
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    /**
     * fonts
     */
    public enum ASCIIArtFont {
        ART_FONT_DIALOG("Dialog"), ART_FONT_DIALOG_INPUT("DialogInput"),
        ART_FONT_MONO("Monospaced"), ART_FONT_SERIF("Serif"), ART_FONT_SANS_SERIF("SansSerif");

        private String value;

        private ASCIIArtFont(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
