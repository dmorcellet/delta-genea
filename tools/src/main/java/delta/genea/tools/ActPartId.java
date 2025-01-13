package delta.genea.tools;

import delta.genea.data.ActType;

/**
 * Act part identifier.
 * @author DAM
 */
public class ActPartId
{
  private String _name;
  private long _actType;
  private long _sosa1;
  private long _sosa2;
  private int _pageIndex;

  /**
   * Constructor.
   * @param name Filename.
   * @param actType Act type.
   * @param sosa1 Sosa person 1.
   * @param sosa2 Sosa person 2.
   * @param pageIndex Page index.
   */
  public ActPartId(String name, long actType, long sosa1, long sosa2, int pageIndex)
  {
    _name=name;
    _actType=actType;
    _sosa1=sosa1;
    _sosa2=sosa2;
    _pageIndex=pageIndex;
  }

  /**
   * Get the filename.
   * @return a filename.
   */
  public String getName()
  {
    return _name;
  }

  /**
   * Get the act type.
   * @return the act type.
   */
  public long getActType()
  {
    return _actType;
  }

  /**
   * Get the sosa 1.
   * @return the sosa 1.
   */
  public long getSosa1()
  {
    return _sosa1;
  }

  /**
   * Get the sosa 2.
   * @return the sosa 2.
   */
  public long getSosa2()
  {
    return _sosa2;
  }

  /**
   * Get the page index.
   * @return the page index.
   */
  public int getPageIndex()
  {
    return _pageIndex;
  }

  /**
   * Build an act identifier from a filename.
   * @param name Input filename.
   * @return an id or <code>null</code> if not valid.
   */
  public static ActPartId build(String name)
  {
    long actType=getActType(name);
    if (actType==0)
    {
      return null;
    }
    if (name.endsWith(".jpg"))
    {
      name=name.substring(0,name.length()-4);
    }
    String prefix=""; // or "j_" or "ninie/"
    String newName=prefix;
    newName=newName+name.substring(0,2);
    name=name.substring(2);
    long sosa1=0;
    long sosa2=0;
    {
      int index=0;
      int n=name.length();
      while (index<n)
      {
        char c=name.charAt(index);
        if (Character.isDigit(c))
        {
          sosa1=(sosa1*10)+(c-'0');
        }
        else
        {
          break;
        }
        index++;
      }
      name=name.substring(index);
    }
    newName=newName+convertSosa(sosa1);
    if (actType==ActType.UNION)
    {
      if ((name.length()==0) || (name.charAt(0)!='-'))
      {
        return null;
      }
      name=name.substring(1);
      {
        int index=0;
        int n=name.length();
        while (index<n)
        {
          char c=name.charAt(index);
          if (Character.isDigit(c))
          {
            sosa2=(sosa2*10)+(c-'0');
          }
          else
          {
            break;
          }
          index++;
        }
        name=name.substring(index);
      }
      newName=newName+"-"+convertSosa(sosa2);
    }
    // Page index
    int pageIndex=0;
    if ((name.length()>1) && (name.charAt(0)=='-'))
    {
      name=name.substring(1);
      int index=0;
      int n=name.length();
      while (index<n)
      {
        char c=name.charAt(index);
        if (Character.isDigit(c))
        {
          pageIndex=(pageIndex*10)+(c-'0');
        }
        else
        {
          break;
        }
        index++;
      }
    }
    else
    {
      pageIndex=1;
    }
    return new ActPartId(newName,actType,sosa1,sosa2,pageIndex);
  }

  private static long getActType(String name)
  {
    if (name.startsWith("an"))
    {
      return ActType.BIRTH;
    }
    if (name.startsWith("ad"))
    {
      return ActType.DEATH;
    }
    if (name.startsWith("am"))
    {
      return ActType.UNION;
    }
    return 0;
  }

  private static long convertSosa(long sosa)
  {
    return sosa;
  }

  @Override
  public String toString()
  {
    return "Act "+_actType+", sosa1="+_sosa1+",sosa2="+_sosa2+",pageIndex="+_pageIndex;
  }
}
