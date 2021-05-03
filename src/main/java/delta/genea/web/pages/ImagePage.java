package delta.genea.web.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import delta.common.utils.ParameterFinder;
import delta.common.utils.io.StreamTools;
import delta.genea.utils.GeneaLoggers;
import delta.genea.web.GeneaApplicationContext;

/**
 * Builder for the 'image' HTML page.
 * @author DAM
 */
public class ImagePage extends GeneaWebPage
{
  private static final Logger _logger=GeneaLoggers.getGeneaLogger();

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
      _logger.error("",e);
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
    if (_logger.isDebugEnabled())
    {
      _logger.debug("Image file ["+ret+"]");
    }
    if (!ret.exists())
    {
      String filename=ret.getName();
      if (filename.endsWith(".png.jpg"))
      {
        filename=filename.substring(0,filename.length()-8)+".png";
      }
      ret=new File(ret.getParentFile(),filename);
    }
    return ret;
  }
}
