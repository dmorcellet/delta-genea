package delta.genea.webhoover.expoactes;

import java.io.File;

import delta.downloads.Downloader;
import delta.genea.webhoover.utils.TmpFilesManager;

/**
 * Session for expoactes.
 * @author DAM
 */
public class ExpoActeSession
{
  private String _siteRoot;
  private File _outputDir;
  private int _actsCounter;
  private TmpFilesManager _tmpFilesManager;
  private Downloader _downloader;

  /**
   * Constructor.
   * @param name Session name.
   * @param siteRoot Root URL of the site.
   * @param outputDir Output directory for downloaded data.
   */
  public ExpoActeSession(String name, String siteRoot, File outputDir)
  {
    _siteRoot=siteRoot;
    _outputDir=outputDir;
    _actsCounter=0;
    _tmpFilesManager=new TmpFilesManager(name);
    _downloader=new Downloader();
  }

  /**
   * Get the root URL of the managed site.
   * @return An URL.
   */
  public String getSiteRoot()
  {
    return _siteRoot;
  }

  /**
   * Get the output directory.
   * @return a directory path. 
   */
  public File getOutputDir()
  {
    return _outputDir;
  }

  /**
   * Increment the acts counter.
   */
  public void incrementActsCounter()
  {
    _actsCounter++;
  }

  /**
   * Get the counter for acts.
   * @return A counter value.
   */
  public int getActsCounter()
  {
    return _actsCounter;
  }

  /**
   * Get the managed downloader. 
   * @return a downloader service.
   */
  public Downloader getDownloader()
  {
    return _downloader;
  }

  /**
   * Get the managed temporary files manager.
   * @return A temporary files manager.
   */
  public TmpFilesManager getTmpFilesManager()
  {
    return _tmpFilesManager;
  }

  /**
   * Release all managed resources.
   */
  public void terminate()
  {
    _tmpFilesManager.cleanup();
  }
}
