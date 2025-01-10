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
  private String _id;
  /**
   * Place name.
   */
  private String _placeName;
  /**
   * Church.
   */
  private String _church;
  /**
   * Act type.
   */
  private String _actType;
  /**
   * Period of time.
   */
  private String _period;
  /**
   * Source.
   */
  private String _source;
  /**
   * Comments.
   */
  private String _comments;
  /**
   * URL of acts.
   */
  private String _link;

  /**
   * @return the id
   */
  public String getId()
  {
    return _id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id)
  {
    _id=id;
  }

  /**
   * @return the placeName
   */
  public String getPlaceName()
  {
    return _placeName;
  }

  /**
   * @param placeName the placeName to set
   */
  public void setPlaceName(String placeName)
  {
    _placeName=placeName;
  }

  /**
   * @return the church
   */
  public String getChurch()
  {
    return _church;
  }

  /**
   * @param church the church to set
   */
  public void setChurch(String church)
  {
    _church=church;
  }

  /**
   * @return the actType
   */
  public String getActType()
  {
    return _actType;
  }

  /**
   * @param actType the actType to set
   */
  public void setActType(String actType)
  {
    _actType=actType;
  }

  /**
   * @return the period
   */
  public String getPeriod()
  {
    return _period;
  }

  /**
   * @param period the period to set
   */
  public void setPeriod(String period)
  {
    _period=period;
  }

  /**
   * @return the source
   */
  public String getSource()
  {
    return _source;
  }

  /**
   * @param source the source to set
   */
  public void setSource(String source)
  {
    _source=source;
  }

  /**
   * @return the comments
   */
  public String getComments()
  {
    return _comments;
  }

  /**
   * @param comments the comments to set
   */
  public void setComments(String comments)
  {
    _comments=comments;
  }

  /**
   * @return the link
   */
  public String getLink()
  {
    return _link;
  }

  /**
   * @param link the link to set
   */
  public void setLink(String link)
  {
    _link=link;
  }

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
