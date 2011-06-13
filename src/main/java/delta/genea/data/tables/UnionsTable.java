package delta.genea.data.tables;

import delta.common.framework.objects.data.DataObjectComparator;
import delta.common.utils.tables.DataTable;
import delta.common.utils.tables.DataTableSort;
import delta.genea.data.GeneaDate;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.comparators.GeneaDateComparator;

/**
 * Table of unions.
 * @author DAM
 */
public class UnionsTable extends DataTable
{
  /**
   * 'date' column identifier.
   */
  public static final String DATE_COLUMN="DATE";

  /**
   * 'place' column identifier.
   */
  public static final String PLACE_COLUMN="LIEU";

  /**
   * 'man' column identifier.
   */
  public static final String MAN_COLUMN="HOMME";

  /**
   * 'woman' column identifier.
   */
  public static final String WOMAN_COLUMN="FEMME";

  /**
   * Constructor.
   */
  public UnionsTable()
  {
    init();
  }

  private void init()
  {
    // Date
    addColumn(DATE_COLUMN,GeneaDate.class,new GeneaDateComparator());
    // Place
    addColumn(PLACE_COLUMN,Place.class,new DataObjectComparator<Place>());
    // Man
    addColumn(MAN_COLUMN,Person.class,new DataObjectComparator<Person>());
    // Woman
    addColumn(WOMAN_COLUMN,Person.class,new DataObjectComparator<Person>());
  }

  /**
   * Get the default sort for this table.
   * @return a data table sort.
   */
  public DataTableSort getDefaultSort()
  {
    DataTableSort sort=new DataTableSort();
    sort.addSort(DATE_COLUMN,Boolean.TRUE);
    sort.addSort(PLACE_COLUMN,Boolean.TRUE);
    sort.addSort(MAN_COLUMN,Boolean.TRUE);
    sort.addSort(WOMAN_COLUMN,Boolean.TRUE);
    return sort;
  }
}
