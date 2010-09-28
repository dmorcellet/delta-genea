package delta.genea.data;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.ObjectSource;

/**
 * Type of an act.
 * @author DAM
 */
public class ActType extends DataObject<ActType>
{
  /**
   * Act type for birth acts.
   */
  public static final long BIRTH=3;
  /**
   * Act type for baptem acts.
   */
  public static final long BAPTEM=1;
  public static final long DEATH=4;
  public static final long BURIAL=2;
  public static final long UNION=5;
  public static final long CONSANGUINITY_TREE=6;
  public static final long SELLING=7;
  public static final long TRANSACTION=8;
  public static final long OTHER=0;
  public static final long WEDDING_CONTRACT=12;
  public static final long LEASE=9;
  public static final long SHARE=10;
  public static final long TESTIMONIAL=11;

  // Relations
  // ...
  // Sets
  // ...

  public static final String CLASS_NAME="ACT_TYPE";

  private String _type;

  @Override
  public String getClassName() { return CLASS_NAME; }

  /**
   * Constructor.
   * @param primaryKey Primary key.
   * @param source Attached objects source.
   */
  public ActType(long primaryKey, ObjectSource<ActType> source)
  {
    super(primaryKey,source);
    _type="";
  }

  /**
   * Get the type of the act.
   * @return the type of the act.
   */
  public String getType()
  {
    return _type;
  }

  public void setType(String type)
  {
    if (type==null)
    {
      type="";
    }
    _type=type;
  }

  /**
   * Get a readable label for this object.
   * @return a readable string.
   */
  public String getLabel()
  {
    return _type;
  }

  public boolean isBirthAct()
  {
    long key=getPrimaryKey();
    return ((key==BIRTH) || (key==BAPTEM));
  }

  public boolean isDeathAct()
  {
    long key=getPrimaryKey();
    return ((key==DEATH) || (key==BURIAL));
  }

  @Override
  public String toString()
  {
    return _type;
  }
}
