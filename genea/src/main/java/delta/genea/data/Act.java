package delta.genea.data;

import java.io.File;
import java.util.Date;
import java.util.List;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.DataProxy;
import delta.genea.ActFileResolver;
import delta.genea.misc.GeneaCfg;

/**
 * Act.
 * @author DAM
 */
public class Act extends DataObject<Act>
{
  /**
   * Class name.
   */
  public static final String CLASS_NAME="ACT";
  /**
   * Relation 'main acts' (from a person to some acts).
   */
  public static final String MAIN_ACTS_RELATION="MAIN_ACTS";
  /**
   * Relation 'other acts' (from a person to some acts).
   */
  public static final String OTHER_ACTS_RELATION="OTHER_ACTS";
  /**
   * Relation 'acts from place' (from a place to some acts).
   */
  public static final String ACTS_FROM_PLACE="ACTS_FROM_PLACE";

  /**
   * Type of act.
   */
  private DataProxy<ActType> _actType;
  /**
   * Date of act.
   */
  private Date _date;
  /**
   * Place of act.
   */
  private DataProxy<Place> _place;
  /**
   * Main person #1.
   */
  private DataProxy<Person> _p1;
  /**
   * Main person #2.
   */
  private DataProxy<Person> _p2;
  /**
   * Base path for act's files.
   */
  private String _path;
  /**
   * Number of files for this act. 
   */
  private int _nbFiles;
  private boolean _traite;
  /**
   * Text (transcription) of this act.
   */
  private DataProxy<ActText> _text;
  /**
   * Comment.
   */
  private String _comment;
  /**
   * Persons referenced in this act.
   */
  private List<PersonInAct> _personsInAct;

  @Override
  public String getClassName()
  {
    return CLASS_NAME;
  }

  /**
   * Constructor.
   * @param primaryKey Primary key.
   */
  public Act(Long primaryKey)
  {
    super();
    setPrimaryKey(primaryKey);
    _traite=false;
  }

  /**
   * Get the date of this act.
   * @return the date of this act (or <code>null</code>).
   */
  public Long getDate()
  {
    if (_date==null)
    {
      return null;
    }
    return Long.valueOf(_date.getTime());
  }

  /**
   * Set the date of this act.
   * @param date A date as a long (milliseconds since Epoch), or <code>null</code>.
   */
  public void setDate(Long date)
  {
    if (date!=null)
    {
      _date=new Date(date.longValue());
    }
    else
    {
      _date=null;
    }
  }

  /**
   * Set the date of this act.
   * @param date A date or <code>null</code>.
   */
  public void setDate(Date date)
  {
    if (date!=null)
    {
      _date=new Date(date.getTime());
    }
    else
    {
      _date=null;
    }
  }

  /**
   * Get a proxy for the act type.
   * @return an act type proxy or <code>null</code>.
   */
  public DataProxy<ActType> getActTypeProxy()
  {
    return _actType;
  }

  /**
   * Set proxy for the act type.
   * @param actType Act type proxy.
   */
  public void setActTypeProxy(DataProxy<ActType> actType)
  {
    _actType=actType;
  }

  /**
   * Get the primary key of the act type.
   * @return a primary key or <code>null</code>.
   */
  public Long getActTypeKey()
  {
    Long ret=null;
    if (_actType!=null)
    {
      ret=_actType.getPrimaryKey();
    }
    return ret;
  }

  /**
   * Get the associated act type.
   * @return an act type or <code>null</code>.
   */
  public ActType getActType()
  {
    if(_actType!=null)
    {
      return _actType.getDataObject();
    }
    return null;
  }

  /**
   * Get a proxy for the place.
   * @return a place proxy or <code>null</code>.
   */
  public DataProxy<Place> getPlaceProxy()
  {
    return _place;
  }

  /**
   * Set proxy for the place.
   * @param place Place proxy.
   */
  public void setPlaceProxy(DataProxy<Place> place)
  {
    _place=place;
  }

  /**
   * Get the primary key of the place.
   * @return a primary key or <code>null</code>.
   */
  public Long getPlaceKey()
  {
    Long ret=null;
    if (_place!=null)
    {
      ret=_place.getPrimaryKey();
    }
    return ret;
  }

  /**
   * Get the associated place.
   * @return a place or <code>null</code>.
   */
  public Place getPlace()
  {
    if(_place!=null)
    {
      return _place.getDataObject();
    }
    return null;
  }

  /**
   * Get a proxy for the person 1.
   * @return a person proxy or <code>null</code>.
   */
  public DataProxy<Person> getP1Proxy()
  {
    return _p1;
  }

  /**
   * Set proxy for the person 1.
   * @param p1 Person proxy.
   */
  public void setP1Proxy(DataProxy<Person> p1)
  {
    _p1=p1;
  }

  /**
   * Get the primary key of the person 1.
   * @return a primary key or <code>null</code>.
   */
  public Long getP1Key()
  {
    Long ret=null;
    if (_p1!=null)
    {
      ret=_p1.getPrimaryKey();
    }
    return ret;
  }

  /**
   * Get the associated person 1.
   * @return a person or <code>null</code>.
   */
  public Person getP1()
  {
    if(_p1!=null)
    {
      return _p1.getDataObject();
    }
    return null;
  }

  /**
   * Get a proxy for the person 2.
   * @return a person proxy or <code>null</code>.
   */
  public DataProxy<Person> getP2Proxy()
  {
    return _p2;
  }

  /**
   * Set proxy for the person 2.
   * @param p2 Person proxy.
   */
  public void setP2Proxy(DataProxy<Person> p2)
  {
    _p2=p2;
  }

  /**
   * Get the primary key of the person 2.
   * @return a primary key or <code>null</code>.
   */
  public Long getP2Key()
  {
    Long ret=null;
    if (_p2!=null)
    {
      ret=_p2.getPrimaryKey();
    }
    return ret;
  }

  /**
   * Get the associated person 2.
   * @return a person or <code>null</code>.
   */
  public Person getP2()
  {
    if(_p2!=null)
    {
      return _p2.getDataObject();
    }
    return null;
  }

  @Override
  public String getLabel()
  {
    String ret="???";
    ActType type=getActType();
    if (type!=null)
    {
      ret=type.getLabel();
    }
    return ret;
  }

  /**
   * Get a label that describes this act.
   * @return a long label.
   */
  public String getFullLabel()
  {
    StringBuilder label=new StringBuilder();
    label.append("Acte de ");
    label.append(getLabel());
    Person p1=getP1();
    Person p2=getP2();
    if ((p1!=null) || (p2!=null))
    {
      label.append(" de ");
      if (p1!=null)
      {
        label.append(p1.getLastName());
        label.append(' ');
        label.append(p1.getFirstname());
      }
      else
      {
        label.append("???");
      }
      if (p2!=null)
      {
        label.append(" et de ");
        label.append(p2.getLastName());
        label.append(' ');
        label.append(p2.getFirstname());
      }
    }
    return label.toString();
  }

  /**
   * Get the base path for the files of this act.
   * @return a base path or <code>null</code>.
   */
  public String getPath()
  {
    return _path;
  }

  /**
   * Set the base path for the files of this act.
   * @param path a path or <code>null</code>.
   */
  public void setPath(String path)
  {
    _path=path;
  }

  /**
   * Get the number of files for this act.
   * @return a number of files (possibly 0).
   */
  public int getNbFiles()
  {
    return _nbFiles;
  }

  /**
   * Set the number of files for this act.
   * @param nbFiles Number of files.
   */
  public void setNbFiles(int nbFiles)
  {
    _nbFiles=nbFiles;
  }

  /**
   * Indicates if this act has been fully exploited.
   * @return <code>true</code> if it is, <code>false</code> otherwise.
   */
  public boolean getTraite()
  {
    return _traite;
  }

  /**
   * Set the 'handled' flag.
   * @param traite Value to set.
   */
  public void setTraite(boolean traite)
  {
    _traite=traite;
  }

  /**
   * Get the comment for this act.
   * @return a comment or <code>null/code>.
   */
  public String getComment()
  {
    return _comment;
  }

  /**
   * Set the comment for this act.
   * @param comment Comment to set.
   */
  public void setComment(String comment)
  {
    _comment=comment;
  }

  /**
   * Get a proxy for the text of this act.
   * @return a text proxy or <code>null</code>.
   */
  public DataProxy<ActText> getTextProxy()
  {
    return _text;
  }

  /**
   * Set proxy for the act text.
   * @param text Act text proxy.
   */
  public void setTextProxy(DataProxy<ActText> text)
  {
    _text=text;
  }

  /**
   * Get the primary key of the act text.
   * @return a primary key or <code>null</code>.
   */
  public Long getTextKey()
  {
    Long ret=null;
    if (_text!=null)
    {
      ret=_text.getPrimaryKey();
    }
    return ret;
  }

  /**
   * Get the associated act text.
   * @return an act text or <code>null</code>.
   */
  public ActText getText()
  {
    if(_text!=null)
    {
      return _text.getDataObject();
    }
    return null;
  }

  /**
   * Get the list of persons referenced in this act.
   * @return a list of 'person in act' objects.
   */
  public List<PersonInAct> getPersonsInAct()
  {
    return _personsInAct;
  }

  /**
   * Set the list of persons referenced in this act.
   * @param list List to set.
   */
  public void setPersonsInAct(List<PersonInAct> list)
  {
   _personsInAct=list;
  }

  /**
   * Get the file for a page of screenshot for this act.
   * @param index Index of screenshot (starting at 0).
   * @return A file.
   */
  public File getActFile(int index)
  {
    if (_path==null)
    {
      return null;
    }
    File rootDir=GeneaCfg.getInstance().getActsRootPath();
    String filename=ActFileResolver.resolveFilename(rootDir,_path,index+1);
    return new File(rootDir,filename);
  }

  /**
   * Get the file for a page of screenshot for this act.
   * @param index Index of screenshot (starting at 0).
   * @return A file.
   */
  public String getActFilename(int index)
  {
    if (_path==null)
    {
      return null;
    }
    File rootDir=GeneaCfg.getInstance().getActsRootPath();
    String filename=ActFileResolver.resolveFilename(rootDir,_path,index+1);
    return filename;
  }

  /**
   * Check for the existence of the files of this act.
   * @return <code>true</code> if all the required files exist,
   * <code>false</code> otherwise.
   */
  public boolean checkFiles()
  {
    File file;
    for(int i=0;i<_nbFiles;i++)
    {
      file=getActFile(i);
      if (!file.exists())
      {
        return false;
      }
      if (file.length()==0)
      {
        return false;
      }
    }
    return true;
  }
}
