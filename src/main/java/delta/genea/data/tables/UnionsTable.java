package delta.genea.data.tables;

import delta.common.framework.objects.data.DataObjectComparator;
import delta.common.utils.tables.DataTable;
import delta.common.utils.tables.DataTableColumn;
import delta.common.utils.tables.DataTableSort;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.comparators.GeneaDateComparator;

/**
 * @author DAM
 */
public class UnionsTable extends DataTable
{
  public static final String DATE_COLUMN="DATE";
  public static final String PLACE_COLUMN="LIEU";
  public static final String MAN_COLUMN="HOMME";
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
    addColumn(DATE_COLUMN);
    DataTableColumn dateColumn=getColumnByName(DATE_COLUMN);
    GeneaDateComparator dateComparator=new GeneaDateComparator();
    dateColumn.setComparator(dateComparator);
    // Place
    addColumn(PLACE_COLUMN);
    DataTableColumn placeColumn=getColumnByName(PLACE_COLUMN);
    DataObjectComparator<Place> placeComparator=new DataObjectComparator<Place>();
    placeColumn.setComparator(placeComparator);
    // Man
    addColumn(MAN_COLUMN);
    DataTableColumn manColumn=getColumnByName(MAN_COLUMN);
    DataObjectComparator<Person> manComparator=new DataObjectComparator<Person>();
    manColumn.setComparator(manComparator);
    // Woman
    addColumn(WOMAN_COLUMN);
    DataTableColumn womanColumn=getColumnByName(WOMAN_COLUMN);
    DataObjectComparator<Person> womanComparator=new DataObjectComparator<Person>();
    womanColumn.setComparator(womanComparator);
  }

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
