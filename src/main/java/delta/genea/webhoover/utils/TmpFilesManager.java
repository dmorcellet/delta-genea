package delta.genea.webhoover.utils;

import java.io.File;

import delta.common.utils.environment.FileSystem;
import delta.common.utils.files.FilesDeleter;

/**
 * @author DAM
 */
public class TmpFilesManager
{
  private File _rootDir;
  private int _counter;

  public TmpFilesManager()
  {
    this("default");
  }

  public TmpFilesManager(String name)
  {
    this(new File(FileSystem.getTmpDir(),name));
  }

  public TmpFilesManager(File rootDir)
  {
    _rootDir=rootDir;
    _counter=1;
  }

  public File newTmpFile(String name)
  {
    File newFile=new File(_rootDir,name);
    _rootDir.mkdirs();
    return newFile;
  }

  public File newTmpFile()
  {
    String name=String.valueOf(_counter);
    _counter++;
    return newTmpFile(name);
  }

  public void cleanup()
  {
    FilesDeleter deleter=new FilesDeleter(_rootDir,null,true);
    deleter.doIt();
  }
}
