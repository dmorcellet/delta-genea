package delta.genea.webhoover.expoactes;

import java.io.File;

import delta.downloads.Downloader;
import delta.genea.webhoover.utils.TmpFilesManager;

/**
 * @author DAM
 */
public class ExpoActeSession
{
  private String _name;
  private String _siteRoot;
  private File _outputDir;
  private int _actsCounter;
  private TmpFilesManager _tmpFilesManager;
  private Downloader _downloader;
  
  public ExpoActeSession(String name, String siteRoot, File outputDir)
  {
    _name=name;
    _siteRoot=siteRoot;
    _outputDir=outputDir;
    _actsCounter=0;
    _tmpFilesManager=new TmpFilesManager(name);
    _downloader=new Downloader();
  }

  public String getName()
  {
    return _name;
  }

  public String getSiteRoot()
  {
    return _siteRoot;
  }

  public File getOutputDir()
  {
    return _outputDir;
  }

  public void incrementActsCounter()
  {
    _actsCounter++;
  }

  public int getActsCounter()
  {
    return _actsCounter;
  }

  public Downloader getDownloader()
  {
    return _downloader;
  }

  public TmpFilesManager getTmpFilesManager()
  {
    return _tmpFilesManager;
  }
  
  public void terminate()
  {
    _tmpFilesManager.cleanup();
  }
}
