package delta.genea.web.pages;

import java.io.PrintWriter;
import java.util.Set;

import delta.common.framework.web.WebPageTools;
import delta.common.utils.ParameterFinder;
import delta.genea.data.Couple;
import delta.genea.data.Person;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.data.trees.CommonAncestorsComputer;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.pages.tools.PersonTools;

/**
 * Builder for the 'common ancestors' HTML page.
 * @author DAM
 */
public class CommonAncestorsPage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private Long _key1;
  private Long _key2;
  private Person _p1;
  private Person _p2;
  private Set<Couple> _couples;

  @Override
  public void parseParameters() throws Exception
  {
    _key1=ParameterFinder.getLongParameter(_request,"KEY1",Long.valueOf(76));
    _key2=ParameterFinder.getLongParameter(_request,"KEY2",Long.valueOf(76));
  }

  @Override
  public void fetchData() throws Exception
  {
    _p1=getDataSource().load(Person.class,_key1);
    AncestorsTree tree1=new AncestorsTree(_p1,1000);
    tree1.build();
    _p2=getDataSource().load(Person.class,_key2);
    AncestorsTree tree2=new AncestorsTree(_p2,1000);
    tree2.build();
    _couples=new CommonAncestorsComputer().compute(tree1,tree2);
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
    StringBuilder sb=new StringBuilder();
    sb.append("Ancêtres communs de ");
    sb.append(_p1.getFullName());
    sb.append(" et de ");
    sb.append(_p2.getFullName());

    String title=sb.toString();
    WebPageTools.generatePageHeader(title,pw);
    WebPageTools.generateHorizontalRuler(pw);
    pw.println("<div>");
    pw.print("<b>");
    pw.print("Ancêtres communs de ");
    pTools.generatePersonName(_p1);
    pw.print(" et de ");
    pTools.generatePersonName(_p2);
    pw.print("</b>");
    pw.println("</div>");
    WebPageTools.generateHorizontalRuler(pw);

    pw.println("<div>");
    if ((_couples!=null) && (!_couples.isEmpty()))
    {
      pw.println("<ul>");
      for(Couple c : _couples)
      {
        Person man=c.getMan();
        Person woman=c.getWoman();
        pw.println("<li>");
        boolean outputDone=false;
        if (man!=null)
        {
          pTools.generatePersonName(man);
          outputDone=true;
        }
        if (woman!=null)
        {
          if (outputDone) pw.print(" - ");
          pTools.generatePersonName(woman);
        }
        pw.println("</li>");
      }
      pw.println("</ul>");
    }
    pw.println("</div>");
    WebPageTools.generatePageFooter(pw);
  }
}
