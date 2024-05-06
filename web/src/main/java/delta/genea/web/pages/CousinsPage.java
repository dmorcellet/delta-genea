package delta.genea.web.pages;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import delta.common.framework.web.WebPageTools;
import delta.common.utils.ParameterFinder;
import delta.common.utils.html.HtmlConstants;
import delta.genea.data.Person;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.pages.tools.PersonTools;

/**
 * Builder for the 'cousins' HTML page.
 * @author DAM
 */
public class CousinsPage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private CousinsPageData _data;

  @Override
  public void parseParameters() throws Exception
  {
    Long key=ParameterFinder.getLongParameter(_request,"KEY",Long.valueOf(76));
    _data=new CousinsPageData(key);
  }

  @Override
  public void fetchData() throws Exception
  {
    _data.load(getDataSource().getObjectsSource());
  }

  @Override
  public void generate(PrintWriter pw)
  {
    GeneaUserContext context=(GeneaUserContext)getUserContext();
    PersonTools pTools=new PersonTools(context,pw);
    pTools.setUseIsAncestorIcon(true);
    pTools.setUseSexIcon(false);
    pTools.setUseNoDescendants(true);
    pTools.setAsLink(true);
    Person root=_data.getMainPerson();
    StringBuilder sb=new StringBuilder();
    sb.append("Cousins de ");
    sb.append(root.getLastName());
    sb.append(' ');
    sb.append(root.getFirstname());

    String title=sb.toString();
    WebPageTools.generatePageHeader(title,pw);
    WebPageTools.generateHorizontalRuler(pw);
    pw.println(HtmlConstants.DIV);
    pw.print("<B>Cousins de ");
    pTools.generatePersonName(root);
    pw.println("</B>");
    pw.println(HtmlConstants.END_DIV);
    WebPageTools.generateHorizontalRuler(pw);

    pw.println(HtmlConstants.DIV);
    List<Person> persons=_data.getCousins();
    if ((persons!=null) && (!persons.isEmpty()))
    {
      pw.println("<ul>");
      CommonAncestorsPageParameters params;
      Person p;
      pTools.setUseSexIcon(true);
      for(Iterator<Person> it=persons.iterator();it.hasNext();)
      {
        pw.print(HtmlConstants.LI);
        p=it.next();
        pTools.generatePersonName(p);
        params=new CommonAncestorsPageParameters(root.getPrimaryKey(),p.getPrimaryKey());
        params.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
        pw.print(" ");
        pw.print("<a href=\"");
        pw.print(params.build());
        pw.println("\">Ancêtres communs</a>");
        pw.println(HtmlConstants.END_LI);
      }
      pTools.setUseSexIcon(false);
      pw.println("</ul>");
    }
    else
    {
      pw.println("Aucun !");
    }
    pw.println(HtmlConstants.END_DIV);
    WebPageTools.generatePageFooter(pw);
  }
}
