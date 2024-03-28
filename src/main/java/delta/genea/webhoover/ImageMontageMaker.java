package delta.genea.webhoover;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.FileLoadDescriptor;
import javax.media.jai.operator.LookupDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;

/**
 * Build a single big image from a mosaic of small images.
 * @author DAM
 */
public class ImageMontageMaker
{
  /**
   * Build image.
   * @param files Files to use.
   * @param out Output image.
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void doIt(File[][] files, File out) throws FileNotFoundException, IOException
  {
    int columnTotal=files.length;
    int rowTotal=files[0].length;
    int col=0;
    Vector<RenderedOp> renderedOps=new Vector<RenderedOp>();
    RenderedOp[][] ops=new RenderedOp[columnTotal][rowTotal];
    RenderedOp op=null;

    String fileName;
    int x=0;
    while (col<columnTotal)
    {
      int y=0;
      int row=0;
      while (row<rowTotal)
      {
        // System.out.println("c:" + col + "r:" + row);
        // Load image
        fileName=files[col][row].getAbsolutePath();
        ops[col][row]=FileLoadDescriptor.create(fileName,null,null,null);
        op=ops[col][row];
        if ((col!=0)||(row!=0))
        {
          // Translate source images to correct places in the mosaic.
          op=TranslateDescriptor.create(op,Float.valueOf(x),Float.valueOf(y),null,null);
        }
        y+=op.getHeight();
        renderedOps.add(convert(op));
        row++;
      }
      x+=op.getWidth();
      col++;
    }
    RenderedOp finalImage=MosaicDescriptor.create(renderedOps.toArray(new RenderedOp[renderedOps
        .size()]),MosaicDescriptor.MOSAIC_TYPE_OVERLAY,null,null,null,null,null);
    // ImageWriteParam param=new ImageWriteParam();
    // param.setCompressionMode(ImageWriteParam.MODE_DEFAULT);
    Iterator<ImageWriter> iter=ImageIO.getImageWritersByFormatName("jpg");
    ImageWriter jiioWriter=iter.next();
    // System.out.println(jiioWriter.getClass().getName());

    MemoryCacheImageOutputStream mos=new MemoryCacheImageOutputStream(new FileOutputStream(out));
    IIOImage iio_image=new IIOImage(finalImage,null,null);
    jiioWriter.setOutput(mos);
    ImageWriteParam param=jiioWriter.getDefaultWriteParam();
    // param.setCompressionMode(ImageWriteParam.MODE_DEFAULT);
    jiioWriter.write(null,iio_image,param);
    mos.close();
    for(int i=0;i<columnTotal;i++)
    {
      for(int j=0;j<rowTotal;j++)
      {
        ops[i][j].dispose();
      }
    }
    // ImageIO.write(finalImage, "png", out);
  }

  private static RenderedOp convert(RenderedOp image)
  {
    // If the source image is colormapped, convert it to 3-band RGB.
    ColorModel colorModel=image.getColorModel();
    if (colorModel instanceof IndexColorModel)
    {
      // Retrieve the IndexColorModel
      IndexColorModel icm=(IndexColorModel)image.getColorModel();
      // Cache the number of elements in each band of the colormap.
      int mapSize=icm.getMapSize();
      // Allocate an array for the lookup table data.
      byte[][] lutData=new byte[3][mapSize];
      // Load the lookup table data from the IndexColorModel.
      icm.getReds(lutData[0]);
      icm.getGreens(lutData[1]);
      icm.getBlues(lutData[2]);
      // Create the lookup table object.
      LookupTableJAI lut=new LookupTableJAI(lutData);
      // Replace the original image with the 3-band RGB image.
      image=LookupDescriptor.create(image,lut,null);
    }
    return image;
  }
}
