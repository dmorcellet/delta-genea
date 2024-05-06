package delta.genea.webhoover;

import java.io.File;

import delta.common.utils.files.FilesDeleter;
import delta.downloads.Downloader;

/**
 * Base class for webhoover sessions.
 * It manages:
 * <ul>
 * <li>a directory for temporary files,
 * <li>a downloader.
 * </ul>
 * @author DAM
 */
public class ADSession
{
  private static int _counter=0;
  private static final File ROOT_DIR=new File("/tmp");
  private Downloader _downloader;
  private File _tmpDir;

  /**
   * Constructor.
   */
  public ADSession()
  {
    // Nothing to do !!
  }

  private static int getCounterValue()
  {
    int ret=_counter;
    _counter++;
    return ret;
  }

  /**
   * Start this session.
   */
  public void start()
  {
    _downloader=new Downloader();
    int counter=getCounterValue();
    _tmpDir=new File(ROOT_DIR,String.valueOf(counter));
    _tmpDir.mkdirs();
    additionalInitializations();
  }

  /**
   * Stop this session.
   */
  public void stop()
  {
    _downloader.dispose();
    _downloader=null;
    FilesDeleter deleter=new FilesDeleter(_tmpDir,null,true);
    deleter.doIt();
  }

  /**
   * Get the managed downloader.
   * @return a downloader.
   */
  public Downloader getDownloader()
  {
    return _downloader;
  }

  /**
   * Get the base directory for temporary files.
   * @return A directory.
   */
  public File getTmpDir()
  {
    return _tmpDir;
  }

  /**
   * Hook for additional initializations in sub-classes.
   */
  protected void additionalInitializations()
  {
    // Nothing to do!
  }
}
