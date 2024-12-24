package delta.genea.web.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.ParameterFinder;
import delta.common.utils.io.StreamTools;
import delta.genea.web.GeneaApplicationContext;

/**
 * Builder for the 'image' HTML page.
 * @author DAM
 */
public class ImagePage extends GeneaWebPage
{
  private static final Logger LOGGER=LoggerFactory.getLogger(ImagePage.class);

  private String _dir;
  private String _image;

  @Override
  public void parseParameters() throws Exception
  {
    _dir=ParameterFinder.getStringParameter(_request,"DIR",null);
    _image=ParameterFinder.getStringParameter(_request,"NAME","myImage");
  }

  @Override
  public String getMIMEType()
  {
    return "image/jpeg";
  }

  @Override
  public boolean isBinary()
  {
    return true;
  }

  @Override
  public void generate(OutputStream os)
  {
    FileInputStream fis=null;
    byte[] buffer=new byte[10000];
    try
    {
      File file=getFile();
      fis=new FileInputStream(file);
      while (true)
      {
        int bytesRead=fis.read(buffer);
        if (bytesRead<=0) break;
        os.write(buffer,0,bytesRead);
      }
    }
    catch(IOException e)
    {
      LOGGER.error("",e);
    }
    finally
    {
      StreamTools.close(fis);
    }
  }

  private File getFile()
  {
    GeneaApplicationContext context=(GeneaApplicationContext)getAppContext();
    File ret=context.getImagePath(_dir,_image);
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("Image file ["+ret+"]");
    }
    if (!ret.exists())
    {
      String filename=ret.getName();
      if (filename.contains(".png"))
      {
        filename=filename.replace(".png","");
        filename=filename.replace(".jpg",".png");
      }
      ret=new File(ret.getParentFile(),filename);
    }
    return ret;
  }
}
