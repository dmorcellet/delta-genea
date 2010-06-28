package delta.genea.data;

import java.io.File;
import java.util.Date;
import java.util.List;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectSource;
import delta.genea.misc.GeneaCfg;

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

  private ActType _actType;
  private Date _date;
  private DataProxy<Place> _place;
  private DataProxy<Person> _p1;
  private DataProxy<Person> _p2;
  private String _path;
  private int _nbFiles;
  private boolean _traite;
  private DataProxy<ActText> _text;
  private String _commentaire;
  private List<PersonInAct> _personsInAct;

  @Override
  public String getClassName()
  {
    return CLASS_NAME;
  }

  /**
   * Constructor.
   * @param primaryKey Primary key.
   * @param source Attached objects source.
   */
  public Act(long primaryKey, ObjectSource<Act> source)
  {
    super(primaryKey,source);
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

  public void setActType(ActType actType)
  {
    _actType=actType;
  }

  public ActType getActType()
  {
    return _actType;
  }

  public DataProxy<Place> getPlaceProxy()
  {
    return _place;
  }

  public void setPlaceProxy(DataProxy<Place> place)
  {
    _place=place;
  }

  public long getPlaceKey()
  {
    long ret=0;
    if (_place!=null)
    {
      ret=_place.getPrimaryKey();
    }
    return ret;
  }

  public Place getPlace()
  {
    if(_place!=null)
    {
      return _place.getDataObject();
    }
    return null;
  }

  public DataProxy<Person> getP1Proxy()
  {
    return _p1;
  }

  public void setP1Proxy(DataProxy<Person> p1)
  {
    _p1=p1;
  }

  public long getP1Key()
  {
    long ret=0;
    if (_p1!=null)
    {
      ret=_p1.getPrimaryKey();
    }
    return ret;
  }

  public Person getP1()
  {
    if(_p1!=null)
    {
      return _p1.getDataObject();
    }
    return null;
  }

  public DataProxy<Person> getP2Proxy()
  {
    return _p2;
  }

  public void setP2Proxy(DataProxy<Person> p2)
  {
    _p2=p2;
  }

  public long getP2Key()
  {
    long ret=0;
    if (_p2!=null)
    {
      ret=_p2.getPrimaryKey();
    }
    return ret;
  }

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
    return _actType.getLabel();
  }

  public String getFullLabel()
  {
    StringBuffer label=new StringBuffer();
    label.append("Acte de ");
    label.append(getLabel());
    Person p1=getP1();
    Person p2=getP2();
    if ((p1!=null) || (p2!=null))
    {
      label.append(" de ");
      if (p1!=null)
      {
        label.append(p1.getSurname());
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
        label.append(p2.getSurname());
        label.append(' ');
        label.append(p2.getFirstname());
      }
    }
    return label.toString();
  }

  public String getPath()
  {
    return _path;
  }

  public void setPath(String path)
  {
    _path=path;
  }

  public int getNbFiles()
  {
    return _nbFiles;
  }

  public void setNbFiles(int nbFiles)
  {
    _nbFiles=nbFiles;
  }

  public boolean getTraite()
  {
    return _traite;
  }

  public void setTraite(boolean traite)
  {
    _traite=traite;
  }

  public String getComment()
  {
    return _commentaire;
  }

  public void setComment(String comment)
  {
    _commentaire=comment;
  }

  public DataProxy<ActText> getTextProxy()
  {
    return _text;
  }

  public void setTextProxy(DataProxy<ActText> text)
  {
    _text=text;
  }

  public long getTextKey()
  {
    long ret=0;
    if (_text!=null)
    {
      ret=_text.getPrimaryKey();
    }
    return ret;
  }

  public ActText getText()
  {
    if(_text!=null)
    {
      return _text.getDataObject();
    }
    return null;
  }

  public List<PersonInAct> getPersonsInAct()
  {
    return _personsInAct;
  }

  public void setPersonsInAct(List<PersonInAct> list)
  {
   _personsInAct=list;
  }

  public String getActFilename(int i)
  {
    if (_path==null)
    {
      return null;
    }
    StringBuffer sb=new StringBuffer(_path);
    if (i>0)
    {
      sb.append('-');
      sb.append(i+1);
    }
    sb.append(".jpg");
    String imgName=sb.toString();
    return imgName;
  }

  public boolean checkFiles()
  {
    File rootPath=GeneaCfg.getInstance().getActsRootPath();
    File file;
    for(int i=0;i<_nbFiles;i++)
    {
      file=new File(rootPath,getActFilename(i));
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
