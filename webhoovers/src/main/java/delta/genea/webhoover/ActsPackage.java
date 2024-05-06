package delta.genea.webhoover;

import java.io.File;

/**
 * Storage for a package of acts.
 * @author DAM
 */
public class ActsPackage
{
  /**
   * Identifier.
   */
  public String _id;
  /**
   * Place name.
   */
  public String _placeName;
  /**
   * Church.
   */
  public String _church;
  /**
   * Act type.
   */
  public String _actType;
  /**
   * Period of time.
   */
  public String _period;
  /**
   * Source.
   */
  public String _source;
  /**
   * Comments.
   */
  public String _comments;
  /**
   * URL of acts.
   */
  public String _link;

  /**
   * Get the directory for this package.
   * @param rootDir Root directory to use.
   * @return A directory.
   */
  public File getDirFile(File rootDir)
  {
    File rootPlaceDir=new File(rootDir,_placeName);
    String dirName=_period;
    String what=_actType;
    if ((what!=null) && (what.length()>0))
    {
      dirName=dirName+" - "+what;
      dirName=dirName.replace("Baptêmes, mariages, sépultures","BMS");
      dirName=dirName.replace("Naissances, mariages, décès","NMD");
      dirName=dirName.replace("Tables décennales","TD");
      dirName=dirName.replace("Mariages, sépultures","MS");
      dirName=dirName.replace("Baptêmes, sépultures","BS");
    }
    File ret=new File(rootPlaceDir,dirName);
    return ret;
  }

  @Override
  public String toString()
  {
    StringBuilder sb=new StringBuilder();
    sb.append(_placeName);
    sb.append(" - ").append(_actType);
    sb.append(" - ").append(_period);
    return sb.toString();
  }
}
