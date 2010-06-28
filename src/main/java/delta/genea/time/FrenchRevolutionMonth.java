package delta.genea.time;

import java.util.HashMap;

/**
 * Represents a month in the French revolution calendar.
 * @author DAM
 */
public class FrenchRevolutionMonth
{
  private static final HashMap<Integer,FrenchRevolutionMonth> _map=new HashMap<Integer,FrenchRevolutionMonth>();

  /**
   * 'Vendémiaire'.
   */
  public static final FrenchRevolutionMonth VENDEMIAIRE=new FrenchRevolutionMonth(1);
  /**
   * 'Brumaire'.
   */
  public static final FrenchRevolutionMonth BRUMAIRE=new FrenchRevolutionMonth(2);
  /**
   * 'Frimaire'.
   */
  public static final FrenchRevolutionMonth FRIMAIRE=new FrenchRevolutionMonth(3);
  /**
   * 'Nivôse'.
   */
  public static final FrenchRevolutionMonth NIVOSE=new FrenchRevolutionMonth(4);
  /**
   * 'Pluviôse'.
   */
  public static final FrenchRevolutionMonth PLUVIOSE=new FrenchRevolutionMonth(5);
  /**
   * 'Ventôse'.
   */
  public static final FrenchRevolutionMonth VENTOSE=new FrenchRevolutionMonth(6);
  /**
   * 'Germinal'.
   */
  public static final FrenchRevolutionMonth GERMINAL=new FrenchRevolutionMonth(7);
  /**
   * 'Floréal'.
   */
  public static final FrenchRevolutionMonth FLOREAL=new FrenchRevolutionMonth(8);
  /**
   * 'Prairial'.
   */
  public static final FrenchRevolutionMonth PRAIRIAL=new FrenchRevolutionMonth(9);
  /**
   * 'Messidor'.
   */
  public static final FrenchRevolutionMonth MESSIDOR=new FrenchRevolutionMonth(10);
  /**
   * 'Thermidor'.
   */
  public static final FrenchRevolutionMonth THERMIDOR=new FrenchRevolutionMonth(11);
  /**
   * 'Fructidor'.
   */
  public static final FrenchRevolutionMonth FRUCTIDOR=new FrenchRevolutionMonth(12);
  /**
   * 'Sans cullotides' (extra days).
   */
  public static final FrenchRevolutionMonth SANS_CULLOTIDES=new FrenchRevolutionMonth(13);

  private static final String[] LABELS=
      {"Vendémiaire", "Brumaire", "Frimaire", "Nivôse", "Pluviose", "Ventôse", "Germinal", "Floréal", "Prairial", "Messidor", "Thermidor", "Fructidor", "Sans-Culottides"};

  private int _monthNumber;

  private FrenchRevolutionMonth(int index)
  {
    _monthNumber=index;
    _map.put(Integer.valueOf(index),this);
  }

  /**
   * Get the month index (starting at 1 for Vendémiaire).
   * @return the month index.
   */
  public int getIndex()
  {
    return _monthNumber;
  }

  /**
   * Get a label for this month.
   * @return A label.
   */
  public String getLabelForMonth()
  {
    return LABELS[_monthNumber-1];
  }

  /**
   * Get the month identified by the specified index.
   * @param index Targeted index.
   * @return A month or <code>null</code> if not found.
   */
  public static FrenchRevolutionMonth fromIndex(int index)
  {
    return _map.get(Integer.valueOf(index));
  }
}
