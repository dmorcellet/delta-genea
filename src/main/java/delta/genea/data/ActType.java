package delta.genea.data;

import delta.common.framework.objects.data.DataObject;

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
  /**
   * Act type for death acts.
   */
  public static final long DEATH=4;
  /**
   * Act type for burial acts.
   */
  public static final long BURIAL=2;
  /**
   * Act type for union acts.
   */
  public static final long UNION=5;
  /**
   * Act type for consainguinity tree acts.
   */
  public static final long CONSANGUINITY_TREE=6;
  /**
   * Act type for selling acts.
   */
  public static final long SELLING=7;
  /**
   * Act type for transaction acts.
   */
  public static final long TRANSACTION=8;
  /**
   * Act type for other acts.
   */
  public static final long OTHER=0;
  /**
   * Act type for wedding contracts.
   */
  public static final long WEDDING_CONTRACT=12;
  /**
   * Act type for lease acts.
   */
  public static final long LEASE=9;
  /**
   * Act type for death acts.
   */
  public static final long SHARE=10;
  /**
   * Act type for testimonial acts.
   */
  public static final long TESTIMONIAL=11;

  // Relations
  // ...
  // Sets
  // ...

  /**
   * Class name.
   */
  public static final String CLASS_NAME="ACT_TYPE";

  private String _type;

  @Override
  public String getClassName() { return CLASS_NAME; }

  /**
   * Constructor.
   * @param primaryKey Primary key.
   */
  public ActType(Long primaryKey)
  {
    super();
    setPrimaryKey(primaryKey);
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

  /**
   * Set act type.
   * @param type Type to set.
   */
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
  @Override
  public String getLabel()
  {
    return _type;
  }

  /**
   * Indicates if this act type is related to a birth.
   * @return <code>true</code> if it is, <code>null</code>.
   */
  public boolean isBirthAct()
  {
    long key=getPrimaryKey().longValue();
    return ((key==BIRTH) || (key==BAPTEM));
  }

  /**
   * Indicates if this act type is related to a death.
   * @return <code>true</code> if it is, <code>null</code>.
   */
  public boolean isDeathAct()
  {
    long key=getPrimaryKey().longValue();
    return ((key==DEATH) || (key==BURIAL));
  }

  @Override
  public String toString()
  {
    return _type;
  }
}
