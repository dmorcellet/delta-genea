package delta.genea.webhoover.gennpdc;

import java.io.File;

import delta.common.utils.environment.FileSystem;
import delta.common.utils.files.FilesDeleter;

/**
 * @author DAM
 */
public class TmpFilesManager
{
  private File _rootDir;

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
  }

  public File newTmpFile(String name)
  {
    File newFile=new File(_rootDir,name);
    _rootDir.mkdirs();
    return newFile;
  }

  public void cleanup()
  {
    FilesDeleter deleter=new FilesDeleter(_rootDir,null,true);
    deleter.doIt();
  }
}
