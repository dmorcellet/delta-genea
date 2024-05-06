package delta.genea.web.pages;

import java.io.PrintWriter;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.web.WebPageTools;
import delta.common.utils.ParameterFinder;
import delta.common.utils.collections.TreeNode;
import delta.common.utils.html.HtmlConstants;
import delta.genea.data.Person;
import delta.genea.data.trees.DescendantsTree;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.pages.tools.PersonTools;

/**
 * Builder for the 'descendants' HTML page.
 * @author DAM
 */
public class DescendantsPage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private DescendantsTree _data;
  private Long _key;
  private int _depth;
  private boolean _sameName;
  private PersonTools _pTools;

  @Override
  public void parseParameters() throws Exception
  {
    _key=ParameterFinder.getLongParameter(_request,DescendantsPageParameters.MAIN_PERSON_KEY,Long.valueOf(76));
    _depth=ParameterFinder.getIntParameter(_request,DescendantsPageParameters.DEPTH,100);
    _sameName=ParameterFinder.getBooleanParameter(_request,DescendantsPageParameters.SAME_NAME,false);
  }

  @Override
  public void fetchData() throws Exception
  {
    DataProxy<Person> proxy=getDataSource().buildProxy(Person.class,_key);
    _data=new DescendantsTree(proxy,_depth,_sameName);
    _data.build(false);
  }

  @Override
  public void generate(PrintWriter pw)
  {
    GeneaUserContext context=(GeneaUserContext)getUserContext();
    _pTools=new PersonTools(context,pw);
    _pTools.setUseIsAncestorIcon(true);
    _pTools.setUseSexIcon(true);
    _pTools.setUseNoDescendants(true);
    _pTools.setAsLink(true);

    Person root=_data.getRootNode().getData();
    long nbDescendants=_data.getNumberOfDescendants();
    StringBuilder sb=new StringBuilder();
    sb.append("Arbre descendant de ");
    sb.append(root.getLastName());
    sb.append(' ');
    sb.append(root.getFirstname());

    String title=sb.toString();
    WebPageTools.generatePageHeader(title,pw);
    WebPageTools.generateHorizontalRuler(pw);
    pw.println(HtmlConstants.DIV);
    _pTools.generatePersonName(root);
    pw.print(" (");
    pw.print(nbDescendants);
    pw.print(")");

    DescendantsPageParameters params=new DescendantsPageParameters(_key,_depth,!_sameName);
    params.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
    pw.print(" <a href=\"");
    pw.print(params.build());
    pw.print("\">");
    if (_sameName) pw.print("Complète");
    else pw.print("Même nom");
    pw.println("</a>");
    pw.println(HtmlConstants.END_DIV);
    WebPageTools.generateHorizontalRuler(pw);
    pw.println(HtmlConstants.DIV);
    TreeNode<Person> rootNode=_data.getRootNode();
    generateTree(rootNode,pw);
    pw.println(HtmlConstants.END_DIV);
    WebPageTools.generatePageFooter(pw);
  }

  private void generateTree(TreeNode<Person> node, PrintWriter pw)
  {
    Person p=node.getData();
    pw.println("<ul>");
    pw.print(HtmlConstants.LI);
    pw.print(' ');
    _pTools.generatePersonName(p);
    TreeNode<Person> son=node.getFirstChild();
    while (son!=null)
    {
      generateTree(son,pw);
      son=son.getNextBrother();
    }
    pw.println(HtmlConstants.END_LI);
    pw.println("</ul>");
  }
}
