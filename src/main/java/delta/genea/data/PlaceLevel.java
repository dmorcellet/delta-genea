package delta.genea.data;

/**
 * Place level/meaning.
 * @author DAM
 */
public final class PlaceLevel
{
  /**
   * Country.
   */
  public static final PlaceLevel COUNTRY=new PlaceLevel(10,"Pays");
  /**
   * Department.
   */
  public static final PlaceLevel DEPARTMENT=new PlaceLevel(5,"Département");
  /**
   * Town.
   */
  public static final PlaceLevel TOWN=new PlaceLevel(3,"Commune");
  /**
   * Part of town.
   */
  public static final PlaceLevel TOWN_PART=new PlaceLevel(2,"Quartier");
  /**
   * Home.
   */
  public static final PlaceLevel HOME=new PlaceLevel(1,"Domicile");
  /**
   * Unknown.
   */
  public static final PlaceLevel UNKNOWN=new PlaceLevel(0,"Inconnu");

  private static final PlaceLevel[] VALUES={COUNTRY, DEPARTMENT, TOWN, TOWN_PART, HOME, UNKNOWN};

  private int _value;
  private String _label;

  private PlaceLevel(int value, String label)
  {
    _value=value;
    _label=label;
  }

  /**
   * Get a place level from its integer value.
   * @param value Value to use.
   * @return A place level instance or <code>null</code> if not found.
   */
  public static PlaceLevel getFromValue(int value)
  {
    PlaceLevel ret=UNKNOWN;
    for(int i=0;i<VALUES.length;i++)
    {
      if (VALUES[i]._value==value)
      {
        ret=VALUES[i];
        break;
      }
    }
    return ret;
  }

  /**
   * Get the associated integer value.
   * @return an integer value.
   */
  public int getValue()
  {
    return _value;
  }

  @Override
  public String toString()
  {
    return _label;
  }
}
