package delta.genea.webhoover;

import java.io.File;

import delta.common.utils.files.FilesDeleter;
import delta.genea.webhoover.Downloader;

/**
 * Base class for webhoover sessions.
 * It manages:
 * <ul>
 * <li>a directory for temporary files.
 * <li>a downloader
 * </ul>
 * @author DAM
 */
public class ADSession
{
  private static int _counter=0;
  private static final File ROOT_DIR=new File("/tmp");
  private Downloader _downloader;
  private File _tmpDir;

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

  public void start()
  {
    _downloader=new Downloader();
    int counter=getCounterValue();
    _tmpDir=new File(ROOT_DIR,String.valueOf(counter));
    _tmpDir.mkdirs();
    additionalInitializations();
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

  public File getTmpDir()
  {
    return _tmpDir;
  }

  protected void additionalInitializations()
  {
    // Nothing to do!
  }
}
