package delta.genea.webhoover.ad49;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import delta.common.utils.files.TextFileReader;
import delta.genea.webhoover.ADSession;
import delta.genea.webhoover.Downloader;

/**
 * @author DAM
 */
public class AD49Session extends ADSession
{
  private static final String PHPSID_SEED="PHPSID=";
  private static final String PHPSID_TERMINATOR="'";
  private static final String REGISTRY_PAGE=Constants.ROOT_SITE+"/cg49work/registre.php";

  private String _phpSID;

  public AD49Session()
  {
    super();
    _phpSID="";
  }

  public String getPHPSessionID()
  {
    return _phpSID;
  }

  protected void additionalInitializations()
  {
    fetchPHPSessionID();
  }
  
  private void fetchPHPSessionID()
  {
    File tmpDir=getTmpDir();
    Downloader downloader=getDownloader();
    File registryPageFile=new File(tmpDir,"registryPage.html");
    downloader.downloadPage(REGISTRY_PAGE, registryPageFile);
    // Parse registry page to get PHP session ID
    {
      List<String> lines=TextFileReader.readAsLines(registryPageFile);
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
