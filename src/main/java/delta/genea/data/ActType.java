package delta.genea.data;

public final class ActType
{
  /**
   * Act type for birth acts.
   */
  public static final ActType BIRTH=new ActType('N',"Naissance");
  /**
   * Act type for baptem acts.
   */
  public static final ActType BAPTEM=new ActType('B',"Baptême");
  public static final ActType DEATH=new ActType('D',"Décès");
  public static final ActType BURIAL=new ActType('S',"Sépulture");
  public static final ActType UNION=new ActType('M',"Mariage");
  public static final ActType CONSANGUINITY_TREE=new ActType('A',"Dispense de consanguinité");
  public static final ActType SELLING=new ActType('V',"Vente");
  public static final ActType TRANSACTION=new ActType('T',"Transaction");
  public static final ActType OTHER=new ActType('?',"Inconnu");
  public static final ActType WEDDING_CONTRACT=new ActType('C',"Contrat de mariage");
  public static final ActType LEASE=new ActType('b',"Bail");
  public static final ActType SHARE=new ActType('P',"Partage");
  private static final ActType[] VALUES={BIRTH, BAPTEM, DEATH, BURIAL, UNION, WEDDING_CONTRACT, SELLING, TRANSACTION, LEASE, SHARE, OTHER};

  private char _value;
  private String _label;

  private ActType(char value, String label)
  {
    _value=value;
    _label=label;
  }

  public static ActType getFromValue(char value)
  {
    ActType ret=OTHER;
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

  public char getValue()
  {
  	return _value;
  }

  public boolean isBirthAct()
  {
    return ((this==BIRTH) || (this==BAPTEM));
  }

  public boolean isDeathAct()
  {
    return ((this==DEATH) || (this==BURIAL));
  }

  @Override
  public String toString()
  {
    return _label;
  }

  public String getLabel()
  {
    return _label;
  }
}
