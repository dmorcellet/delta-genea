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
  public static final Long BIRTH=Long.valueOf(3);
  /**
   * Act type for baptem acts.
   */
  public static final Long BAPTEM=Long.valueOf(1);
  /**
   * Act type for death acts.
   */
  public static final Long DEATH=Long.valueOf(4);
  /**
   * Act type for burial acts.
   */
  public static final Long BURIAL=Long.valueOf(2);
  /**
   * Act type for union acts.
   */
  public static final Long UNION=Long.valueOf(5);
  /**
   * Act type for consainguinity tree acts.
   */
  public static final Long CONSANGUINITY_TREE=Long.valueOf(6);
  /**
   * Act type for selling acts.
   */
  public static final Long SELLING=Long.valueOf(7);
  /**
   * Act type for transaction acts.
   */
  public static final Long TRANSACTION=Long.valueOf(8);
  /**
   * Act type for other acts.
   */
  public static final Long OTHER=Long.valueOf(0);
  /**
   * Act type for wedding contracts.
   */
  public static final Long WEDDING_CONTRACT=Long.valueOf(12);
  /**
   * Act type for lease acts.
   */
  public static final Long LEASE=Long.valueOf(9);
  /**
   * Act type for death acts.
   */
  public static final Long SHARE=Long.valueOf(10);
  /**
   * Act type for testimonial acts.
   */
  public static final Long TESTIMONIAL=Long.valueOf(11);

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
  public ActType(Long primaryKey, ObjectSource<ActType> source)
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
    Long key=getPrimaryKey();
    return ((key==BIRTH) || (key==BAPTEM));
  }

  public boolean isDeathAct()
  {
    Long key=getPrimaryKey();
    return ((key==DEATH) || (key==BURIAL));
  }

  @Override
  public String toString()
  {
    return _type;
  }
}
