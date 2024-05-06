package delta.genea.webhoover.utils;

import java.io.File;

import delta.common.utils.environment.FileSystem;
import delta.common.utils.files.FilesDeleter;

/**
 * A manager for temporary files.
 * @author DAM
 */
public class TmpFilesManager
{
  private File _rootDir;
  private int _counter;

  /**
   * Constructor.
   */
  public TmpFilesManager()
  {
    this("default");
  }

  /**
   * Constructor.
   * @param name Base directory name for temporary files.
   */
  public TmpFilesManager(String name)
  {
    this(new File(FileSystem.getTmpDir(),name));
  }

  /**
   * Constructor.
   * @param rootDir Base directory for temporary files.
   */
  public TmpFilesManager(File rootDir)
  {
    _rootDir=rootDir;
    _counter=1;
  }

  /**
   * Build a new temporary file path.
   * @param name Name of file.
   * @return A file.
   */
  public File newTmpFile(String name)
  {
    File newFile=new File(_rootDir,name);
    _rootDir.mkdirs();
    return newFile;
  }

  /**
   * Build a new temporary file path.
   * @return A file.
   */
  public File newTmpFile()
  {
    String name=String.valueOf(_counter);
    _counter++;
    return newTmpFile(name);
  }

  /**
   * Remove all generated temprary files.
   */
  public void cleanup()
  {
    FilesDeleter deleter=new FilesDeleter(_rootDir,null,true);
    deleter.doIt();
  }
}
