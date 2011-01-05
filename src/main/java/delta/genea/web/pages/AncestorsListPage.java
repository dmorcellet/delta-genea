package delta.genea.web.pages;

import java.io.PrintWriter;
import java.util.List;

import delta.common.framework.web.WebPageTools;
import delta.common.utils.ParameterFinder;
import delta.genea.data.Person;
import delta.genea.data.trees.AncestorsList;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.formatters.PersonHtmlFormatter;

/**
 * Builder for the 'ancestors list' HTML page.
 * @author DAM
 */
public class AncestorsListPage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private AncestorsList _data;
  private Person _root;
  private long _key;
  private int _depth;

  @Override
  public void parseParameters() throws Exception
  {
    _key=ParameterFinder.getLongParameter(_request,AncestorsListPageParameters.KEY,76);
    _depth=ParameterFinder.getIntParameter(_request,AncestorsListPageParameters.DEPTH,100);
  }

  @Override
  public void fetchData() throws Exception
  {
    _root=getDataSource().getPersonDataSource().load(_key);
    AncestorsTree tree=new AncestorsTree(_root,_depth);
    tree.build();
    _data=new AncestorsList(tree);
  }

  @Override
  public void generate(PrintWriter pw)
  {
    long nbAncestors=_data.getNbPersons()-1;
    GeneaUserContext context=(GeneaUserContext)getUserContext();
    PersonHtmlFormatter pTools=new PersonHtmlFormatter(context);
    pTools.setDeCujus(context.getDeCujus());
    pTools.setUseIsAncestorIcon(true);
    pTools.setUseSexIcon(false);
    pTools.setUseNoDescendants(true);
    pTools.setAsLink(true);

    StringBuffer sb=new StringBuffer();
    sb.append("Ancêtres de ");
    sb.append(_root.getLastName());
    sb.append(' ');
    sb.append(_root.getFirstname());
    String title=sb.toString();
    WebPageTools.generatePageHeader(title,pw);
    WebPageTools.generateHorizontalRuler(pw);
    pw.println("<div>");
    String personLink=pTools.format(_root);
    pw.print(personLink);
    pw.print(" (");
    pw.print(nbAncestors);
    pw.print(" ancêtres)");
    AncestorsPageParameters ancestorsPage=new AncestorsPageParameters(_root.getPrimaryKey(),_depth);
    ancestorsPage.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
    pw.print(" <a href=\"");
    pw.print(ancestorsPage.build());
    pw.println("\">Ascendance en arbre</a>");
    WebPageTools.generateHorizontalRuler(pw);
    pw.println("</div>");

    int nbGenerations=_data.getNbGenerations();
    long nbMax=1;
    List<Person> persons;
    List<Long> sosas;
    for(int i=0;i<nbGenerations;i++)
    {
      persons=_data.getPersons(i);
      sosas=_data.getSosas(i);
      if (i>0)
      {
        WebPageTools.generateHorizontalRuler(pw);
      }
      pw.println("<div>");
      pw.println("<b>Génération "+(i+1)+" ("+persons.size()+"/"+nbMax+")</b><br>");
      int nbPersons=persons.size();
      for(int j=0;j<nbPersons;j++)
      {
        pw.print(sosas.get(j));
        pw.print(" - ");
        personLink=pTools.format(persons.get(j));
        pw.print(personLink);
        pw.println("<br>");
      }
      pw.println("</div>");
      nbMax*=2;
    }
    WebPageTools.generatePageFooter(pw);
  }
}
