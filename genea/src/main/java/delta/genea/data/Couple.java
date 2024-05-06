package delta.genea.data;

import delta.common.framework.objects.data.DataObject;

/**
 * A couple, made of a man and a woman.
 * @author DAM
 */
public class Couple
{
  private Person _man;
  private Person _woman;

  /**
   * Constrcutor.
   * @param man Man.
   * @param woman Woman.
   */
  public Couple(Person man, Person woman)
  {
    _man=man;
    _woman=woman;
  }

  /**
   * Get the referenced man.
   * @return a man or <code>null</code>.
   */
  public Person getMan()
  {
    return _man;
  }

  /**
   * Get the primary key for the referenced man.
   * @return A primary key or <code>null</code>.
   */
  public Long getManPrimaryKey()
  {
    Long pk=null;
    if (_man!=null)
    {
      pk=_man.getPrimaryKey();
    }
    return pk;
  }

  /**
   * Get the referenced woman.
   * @return a woman or <code>null</code>.
   */
  public Person getWoman()
  {
    return _woman;
  }

  /**
   * Get the primary key for the referenced woman.
   * @return A primary key or <code>null</code>.
   */
  public Long getWomanPrimaryKey()
  {
    Long pk=null;
    if (_woman!=null)
    {
      pk=_woman.getPrimaryKey();
    }
    return pk;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof Couple))
    {
      return false;
    }
    Couple c=(Couple)obj;
    if (_man==null)
    {
      if (c._man!=null)
      {
        return false;
      }
    }
    else
    {
      if (c._man==null)
      {
        return false;
      }
      if (!DataObject.keysAreEqual(_man.getPrimaryKey(),c._man.getPrimaryKey()))
      {
        return false;
      }
    }
    if (_woman==null)
    {
      if (c._woman!=null)
      {
        return false;
      }
    }
    else
    {
      if (c._woman==null)
      {
        return false;
      }
      if (!DataObject.keysAreEqual(_woman.getPrimaryKey(),c._woman.getPrimaryKey()))
      {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode()
  {
    Long manKey=null;
    if (_man!=null)
    {
      manKey=_man.getPrimaryKey();
    }
    Long womanKey=null;
    if (_woman!=null)
    {
      womanKey=_woman.getPrimaryKey();
    }
    long value=((manKey!=null)?manKey.longValue():0)+((womanKey!=null)?womanKey.longValue():0);
    return (int)(value^(value>>>32));
  }

  @Override
  public String toString()
  {
    StringBuilder sb=new StringBuilder();
    if (_man!=null)
    {
      sb.append(_man.getFullName());
    }
    if (_woman!=null)
    {
      if (sb.length()>0)
      {
        sb.append('/');
      }
      sb.append(_woman.getFullName());
    }
    return sb.toString();
  }
}
