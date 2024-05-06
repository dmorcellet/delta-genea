package delta.genea.web.pages;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.web.WebPageTools;
import delta.common.utils.ParameterFinder;
import delta.common.utils.html.HtmlConstants;
import delta.genea.data.Person;
import delta.genea.data.Union;
import delta.genea.data.trees.AncestorsList;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.pages.tools.PersonTools;

/**
 * Builder for the 'dates to search' HTML page.
 * @author DAM
 */
public class DatesToSearchPage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private Person _root;
  private Long _key;
  private int _depth;
  private Object[][] _data;
  private int _nbPersons;

  @Override
  public void parseParameters() throws Exception
  {
    _key=ParameterFinder.getLongParameter(_request,"KEY",Long.valueOf(76));
    _depth=ParameterFinder.getIntParameter(_request,"DEPTH",100);
  }

  @Override
  public void fetchData() throws Exception
  {
    _root=getDataSource().load(Person.class,_key);
    AncestorsTree tree=new AncestorsTree(_root,_depth);
    tree.build();
    AncestorsList data=new AncestorsList(tree);
    buildAncestorsList(data);
  }

  private void buildAncestorsList(AncestorsList aList)
  {
    List<Union> unions=null;
    int nb=aList.getNbPersons();
    _nbPersons=nb;
    Object[][] data=new Object[nb][5];
    int nbGenerations=aList.getNbGenerations();
    List<Person> persons;
    List<Long> sosas;
    Long sosa;
    Person person;
    Long previousKey=null;
    int index=0;
    String tmp;
    SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
    for(int i=0;i<nbGenerations;i++)
    {
      persons=aList.getPersons(i);
      sosas=aList.getSosas(i);
      int nbPersons=persons.size();
      for(int j=0;j<nbPersons;j++)
      {
        person=persons.get(j);
        sosa=sosas.get(j);
        data[index][0]=sosa;
        data[index][1]=person;
        data[index][2]=person.getBirthGeneaDate().format(sdf);
        if ((sosa.longValue()%2==1) && (previousKey!=null))
        {
          unions=getDataSource().loadRelation(Union.class,Union.UNIONS_RELATION,person.getPrimaryKey());
          int nbUnions=unions.size();
          Union u;
          for(int k=0;k<nbUnions;k++)
          {
            u=unions.get(k);
            if (DataObject.keysAreEqual(u.getManKey(),previousKey))
            {
              tmp=u.getGeneaDate().format(sdf);
              data[index][3]=tmp;
              data[index-1][3]=tmp;
            }
          }
        }
        data[index][4]=person.getDeathGeneaDate().format(sdf);
        index++;
        previousKey=person.getPrimaryKey();
      }
    }
    _data=data;
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
    String title="Ma page";
    WebPageTools.generatePageHeader(title,pw);
    WebPageTools.generateHorizontalRuler(pw);
    pw.println(HtmlConstants.DIV);
    pw.print("<b>");
    pTools.generatePersonName(_root);
    pw.println("</b>");
    pw.println(HtmlConstants.END_DIV);
    WebPageTools.generateHorizontalRuler(pw);

    int nb=_nbPersons;
    pw.println("<table width=\"75%\" border=\"1\">");
    Person person;
    for(int i=0;i<nb;i++)
    {
      pw.print("<tr>");
      pw.print("<td align=\"center\"> <div style=\"text-align:center;\"><b>");
      pw.print(_data[i][0]);
      pw.print("</b></div></td>");
      pw.print("<td>");
      person=(Person)_data[i][1];
      pw.print(person.getFullName());
      pw.print("</td>");
      for(int j=2;j<5;j++)
      {
        pw.print("<td><div style=\"text-align:center;\">");
        pw.print(_data[i][j]);
        pw.print("</div></td>");
      }
      pw.println("</tr>");
    }
    pw.println("</table>");
    WebPageTools.generatePageFooter(pw);
  }
}
