package delta.genea.data;

public final class PlaceLevel
{
  public static final PlaceLevel COUNTRY=new PlaceLevel(10,"Pays");
  public static final PlaceLevel DEPARTMENT=new PlaceLevel(5,"Département");
  public static final PlaceLevel TOWN=new PlaceLevel(3,"Commune");
  public static final PlaceLevel TOWN_PART=new PlaceLevel(2,"Quartier");
  public static final PlaceLevel HOME=new PlaceLevel(1,"Domicile");
  public static final PlaceLevel UNKNOWN=new PlaceLevel(0,"Inconnu");
  private static final PlaceLevel[] VALUES={COUNTRY, DEPARTMENT, TOWN, TOWN_PART, HOME, UNKNOWN};

  private int _value;
  private String _label;

  private PlaceLevel(int value, String label)
  {
    _value=value;
    _label=label;
  }

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
