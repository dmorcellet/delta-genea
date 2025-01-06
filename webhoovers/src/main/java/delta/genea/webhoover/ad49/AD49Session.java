package delta.genea.webhoover.ad49;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.text.TextUtils;
import delta.downloads.Downloader;
import delta.genea.webhoover.ADSession;

/**
 * AD49 download session.
 * @author DAM
 */
public class AD49Session extends ADSession
{
  private static final Logger LOGGER=LoggerFactory.getLogger(AD49Session.class);

  private static final String PHPSID_SEED="PHPSID=";
  private static final String PHPSID_TERMINATOR="'";
  private static final String REGISTRY_PAGE=Constants.ROOT_SITE+"/cg49work/registre.php";

  private String _phpSID;

  /**
   * Constructor.
   */
  public AD49Session()
  {
    super();
    _phpSID="";
  }

  /**
   * Get the PHP session ID for this session.
   * @return A PHP session ID.
   */
  public String getPHPSessionID()
  {
    return _phpSID;
  }

  @Override
  protected void additionalInitializations()
  {
    try
    {
      fetchPHPSessionID();
    }
    catch(Exception e)
    {
      LOGGER.warn("Could not fetch PHP session ID!",e);
    }
  }
  
  private void fetchPHPSessionID() throws Exception
  {
    File tmpDir=getTmpDir();
    Downloader downloader=getDownloader();
    File registryPageFile=new File(tmpDir,"registryPage.html");
    downloader.downloadToFile(REGISTRY_PAGE, registryPageFile);
    // Parse registry page to get PHP session ID
    {
      List<String> lines=TextUtils.readAsLines(registryPageFile);
      String line;
      int index;
      for(Iterator<String> it=lines.iterator();it.hasNext();)
      {
        line=it.next();
        index=line.indexOf(PHPSID_SEED);
        if (index!=-1)
        {
          _phpSID=line.substring(index+PHPSID_SEED.length());
          int endIndex=_phpSID.indexOf(PHPSID_TERMINATOR);
          if (endIndex!=-1)
          {
            _phpSID=_phpSID.substring(0,endIndex);
            break;
          }
        }
      }
    }
  }
}
