package delta.genea.web.pages;

import java.io.PrintWriter;
import java.util.List;

import delta.common.framework.web.WebPageTools;
import delta.common.utils.ParameterFinder;
import delta.common.utils.html.HtmlConstants;
import delta.common.utils.tables.DataTableRow;
import delta.common.utils.tables.DataTableSort;
import delta.genea.data.Place;
import delta.genea.data.Union;
import delta.genea.data.formatters.GeneaDateFormatter;
import delta.genea.data.tables.UnionsTable;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.formatters.DataTableHtmlFormatter;
import delta.genea.web.formatters.PersonHtmlFormatter;

/**
 * Builder for the 'unions' HTML page.
 * @author DAM
 */
public class UnionsPage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private String _name;
  private Long _key;
  private Place _place;
  private UnionsTable _unions;
  private DataTableSort _sort;

  @Override
  public void parseParameters() throws Exception
  {
    _name=ParameterFinder.getStringParameter(_request,NamePageParameters.NAME,"MORCELLET");
    _key=ParameterFinder.getLongParameter(_request,"KEY",Long.valueOf(76));
    String sort=ParameterFinder.getStringParameter(_request,"SORT","");
    if (sort.length()>0)
    {
      _sort=DataTableSort.buildSortFromString(sort);
    }
  }

  @Override
  public void fetchData() throws Exception
  {
    // Load data
    _place=getDataSource().load(Place.class,_key);

    List<Union> unions=getDataSource().loadObjectSet(Union.class,Union.NAME_AND_PLACE_SET,_name,_key);
    _unions=new UnionsTable();
    int dateColumnIndex=_unions.getColumnByKey(UnionsTable.DATE_COLUMN).getIndex();
    int placeColumnIndex=_unions.getColumnByKey(UnionsTable.PLACE_COLUMN).getIndex();
    int manColumnIndex=_unions.getColumnByKey(UnionsTable.MAN_COLUMN).getIndex();
    int womanColumnIndex=_unions.getColumnByKey(UnionsTable.WOMAN_COLUMN).getIndex();
    for(Union u : unions)
    {
      DataTableRow row=_unions.addRow();
      row.setData(dateColumnIndex,u.getGeneaDate());
      row.setData(placeColumnIndex,u.getPlace());
      row.setData(manColumnIndex,u.getMan());
      row.setData(womanColumnIndex,u.getWoman());
    }

    DataTableSort sort=_sort;
    if (sort==null)
    {
      sort=_unions.getDefaultSort();
    }
    _unions.sort(sort);
  }

  @Override
  public void generate(PrintWriter pw)
  {
    GeneaUserContext context=(GeneaUserContext)getUserContext();
    Long deCujus=context.getDeCujus();
    PersonHtmlFormatter personsFormatter=new PersonHtmlFormatter(context);
    personsFormatter.setAsLink(true);
    personsFormatter.setDeCujus(deCujus);
    personsFormatter.setUseSexIcon(false);
    personsFormatter.setUseIsAncestorIcon(true);
    personsFormatter.setUseNoDescendants(true);
    personsFormatter.setUseLifeTime(false);
    GeneaDateFormatter dateFormatter=new GeneaDateFormatter();
    dateFormatter.setUseRevolutionaryCalendar(false);

    String placeName="";
    if (_place!=null)
    {
      placeName=_place.getFullName();
    }
    String title="Mariages "+_name+" à "+placeName;
    WebPageTools.generatePageHeader(title,pw);
    int nbUnions=_unions.getNbRows();
    WebPageTools.generateHorizontalRuler(pw);
    pw.println(HtmlConstants.DIV);
    pw.print("<b>");
    pw.print(title);
    pw.print(" (");
    pw.print(nbUnions);
    pw.print(')');
    pw.println("</b>");
    WebPageTools.generateHorizontalRuler(pw);
    DataTableHtmlFormatter tableFormatter=new DataTableHtmlFormatter();
    tableFormatter.setFormatter(UnionsTable.DATE_COLUMN,dateFormatter);
    tableFormatter.setFormatter(UnionsTable.MAN_COLUMN,personsFormatter);
    tableFormatter.setFormatter(UnionsTable.WOMAN_COLUMN,personsFormatter);
    StringBuilder sb=new StringBuilder();
    tableFormatter.format(_unions,sb);
    pw.print(sb);
    pw.println(HtmlConstants.END_DIV);
    WebPageTools.generatePageFooter(pw);
  }
}
