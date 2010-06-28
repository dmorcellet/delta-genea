package delta.genea.data;

public class Couple
{
  private Person _man;
  private Person _woman;

  public Couple(Person man, Person woman)
  {
    _man=man;
    _woman=woman;
  }

  public Person getMan()
  {
    return _man;
  }

  public long getManPrimaryKey()
  {
    long pk=0;
    if (_man!=null)
    {
      pk=_man.getPrimaryKey();
    }
    return pk;
  }

  public Person getWoman()
  {
    return _woman;
  }

  public long getWomanPrimaryKey()
  {
    long pk=0;
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
      if (_man.getPrimaryKey()!=c._man.getPrimaryKey())
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
      if (_woman.getPrimaryKey()!=c._woman.getPrimaryKey())
      {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode()
  {
    long manKey=0;
    if (_man!=null)
    {
      manKey=_man.getPrimaryKey();
    }
    long womanKey=0;
    if (_woman!=null)
    {
      womanKey=_woman.getPrimaryKey();
    }
    long value=manKey+womanKey;
    return (int)(value^(value>>>32));
  }

  @Override
  public String toString()
  {
    StringBuffer sb=new StringBuffer();
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
