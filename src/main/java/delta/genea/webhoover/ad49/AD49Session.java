package delta.genea.webhoover.ad49;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import delta.common.utils.files.FilesDeleter;
import delta.common.utils.files.TextFileReader;
import delta.genea.webhoover.Downloader;

/**
 * @author DAM
 */
public class AD49Session
{
  private static final String PHPSID_SEED="PHPSID=";
  private static final String PHPSID_TERMINATOR="'";
  private static final String REGISTRY_PAGE=Constants.ROOT_SITE+"/cg49work/registre.php";

  private static int _counter=0;
  private static final File ROOT_DIR=new File("/tmp");
  private Downloader _downloader;
  private File _tmpDir;
  private String _phpSID;

  public AD49Session()
  {
    // Nothing to do !!
  }

  private static int getCounterValue()
  {
    int ret=_counter;
    _counter++;
    return ret;
  }

  public void start()
  {
    _downloader=new Downloader();
    int counter=getCounterValue();
    _tmpDir=new File(ROOT_DIR,String.valueOf(counter));
    _tmpDir.mkdirs();
    _phpSID="";
    fetchPHPSessionID();
  }

  public void stop()
  {
    _downloader.stop();
    _downloader=null;
    FilesDeleter deleter=new FilesDeleter(_tmpDir,null,true);
    deleter.doIt();
  }

  public Downloader getDownloader()
  {
    return _downloader;
  }

  public String getPHPSessionID()
  {
    return _phpSID;
  }

  public File getTmpDir()
  {
    return _tmpDir;
  }

  private void fetchPHPSessionID()
  {
    File registryPageFile=new File(_tmpDir,"registryPage.html");
    _downloader.downloadPage(REGISTRY_PAGE, registryPageFile);
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
