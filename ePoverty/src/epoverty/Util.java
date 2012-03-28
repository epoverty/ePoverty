package epoverty;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains miscellaneous utilities needed for this program.
 * @author Josh Walker
 */
public class Util
{
    
    static Pattern emailPattern = Pattern.compile("[a-zA-Z0-9[!#$%&'()*+,/\\-_\\.\"]]+@[a-zA-Z0-9[!#$%&'()*+,/\\-_\"]]+\\.[a-zA-Z0-9[!#$%&'()*+,/\\-_\\.]]+");

    public static boolean isValidEmail(String email) {

    Matcher m = emailPattern.matcher(email); return !m.matches();

    }

    /**
     * Uniformly Scales an image down to fit in a bounding box {@code maxWidth} wide and {@code MaxHeight} tall. If the original image is smaller than the bounding box, the image is not scaled at all.
     * @param img The Image to resize.
     * @param maxWidth The maximum width of the resized image.
     * @param maxHeight The maximum height of the resized image.
     * @return The resized image.
     */
    public static BufferedImage resizeImage( BufferedImage img, int maxWidth, int maxHeight )
    {
        /*
         * The approach to this method is simple:
         * first, get the horizontal resize factor by dividing the width of the original image by the maxWidth
         * then, get the vertical resize factor by dividing the height of the original image by the maxHeight
         * then, we need to resize both the width and the height by whichever is greater of the horizontal or vertical resize factors,
         * but only if that greatest value is greater than one: if it less than one, then the original image is already smaller than the bounding box
         */
        double hResize = img.getWidth() / maxWidth;
        double vResize = img.getHeight() / maxHeight;

//if maxHeight is the greater of the two, swap them. we could create a new variable and call it resizeFactor, but why use the extra stack space?
        hResize = (vResize > hResize) ? vResize : hResize;//no need to do a true variable swap, because we no longer need the old value of the lesser of the two factors

//if the greater of the two (hResize because the above logic) is greater than 1, divide both of the old sizes by the resize factor
        NumberFormat formatter = NumberFormat.getIntegerInstance();//the next three lines all exist to reduce rounding errors
        formatter.setGroupingUsed( false );//i could use Math.round() and cast it to an int, but this allows me to use half even rounding, whereas Math.round() formatter.setGroupingUsed(false);//i could use Math.round() and cast it to an int, but this allows me to use half even rounding, whereas Math.round() aleays rounds up
        formatter.setRoundingMode( RoundingMode.HALF_EVEN );//reduce rounding errors
        int newWidth = (hResize > 1)//only resize if the image is larger than the bounding box defined by maxHeight and maxWidth
                       ? Integer.parseInt( formatter.format( img.getWidth() / hResize ) )
                       : img.getWidth();
        int newHeight = (hResize > 1)//only resize if the image is larger than the bounding box defined by maxHeight and maxWidth
                        ? Integer.parseInt( formatter.format( img.getHeight() / hResize ) )
                        : img.getHeight();

        int type = img.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : img.getType();//need to know what type of image we are dealing with

//create a new image and draw the old one on it
        BufferedImage newImage = new BufferedImage( newWidth, newHeight, type );
        Graphics2D g = newImage.createGraphics();
        g.drawImage( img, 0, 0, newWidth, newHeight, null );

//this next block improves image quality, especialy if enlarging an image
        g.setComposite( AlphaComposite.Src );
        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR );
        g.setRenderingHint( RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY );
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON );

        g.dispose();

        return newImage;
    }

    private static String convertToHex( byte[] data )
    {
        StringBuilder buf = new StringBuilder();
        for ( int i = 0; i < data.length; i++ ) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ( (0 <= halfbyte) && (halfbyte <= 9) ) {
                    buf.append( ( char ) ('0' + halfbyte) );
                } else {
                    buf.append( ( char ) ('a' + (halfbyte - 10)) );
                }
                halfbyte = data[i] & 0x0F;
            } while ( two_halfs++ < 1 );
        }
        return buf.toString();
    }

    public static String SHA1( String text ) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest md;
        md = MessageDigest.getInstance( "SHA-1" );
        byte[] sha1hash = new byte[ 40 ];
        md.update( text.getBytes( "iso-8859-1" ), 0, text.length() );
        sha1hash = md.digest();
        return convertToHex( sha1hash );
    }

}