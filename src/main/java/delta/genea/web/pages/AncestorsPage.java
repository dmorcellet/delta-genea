package delta.genea.web.pages;

import java.io.PrintWriter;

import delta.common.framework.web.WebPageTools;
import delta.common.utils.ParameterFinder;
import delta.common.utils.collections.BinaryTreeNode;
import delta.genea.data.Person;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.formatters.PersonHtmlFormatter;

/**
 * Builder for the 'ancestors' HTML page.
 * @author DAM
 */
public class AncestorsPage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private AncestorsTree _data;
  private Person _root;
  private long _key;
  private int _depth;
  private PersonHtmlFormatter _pTools;

  @Override
  public void parseParameters() throws Exception
  {
    _key=ParameterFinder.getLongParameter(_request,"KEY",76);
    _depth=ParameterFinder.getIntParameter(_request,"DEPTH",100);
  }

  @Override
  public void fetchData() throws Exception
  {
    _root=getDataSource().getPersonDataSource().load(_key);
    _data=new AncestorsTree(_root,_depth);
    _data.build();
  }

  @Override
  public void generate(PrintWriter pw)
  {
    long nbAncestors=_data.getNumberOfAncestors();
    GeneaUserContext context=(GeneaUserContext)getUserContext();
    _pTools=new PersonHtmlFormatter(context);
    _pTools.setDeCujus(context.getDeCujus());
    _pTools.setUseIsAncestorIcon(true);
    _pTools.setUseSexIcon(false);
    _pTools.setUseNoDescendants(true);
    _pTools.setAsLink(true);

    StringBuffer sb=new StringBuffer();
    sb.append("Arbre ascendant de ");
    sb.append(_root.getLastName());
    sb.append(' ');
    sb.append(_root.getFirstname());
    String title=sb.toString();
    WebPageTools.generatePageHeader(title,pw);
    WebPageTools.generateHorizontalRuler(pw);
    pw.println("<div>");
    String personLink=_pTools.format(_root);
    pw.print(personLink);
    pw.print(" (");
    pw.print(nbAncestors);
    pw.print(" ancêtres)");
    AncestorsListPageParameters listPage=new AncestorsListPageParameters(_root.getPrimaryKey(),_depth);
    pw.print(" <a href=\"");
    pw.print(listPage.build());
    pw.println("\">Ascendance en liste</a>");
    pw.println("</div>");
    WebPageTools.generateHorizontalRuler(pw);
    pw.println("<div>");
    BinaryTreeNode<Person> rootNode=_data.getRootNode();
    generateTree(rootNode,pw,1);
    pw.println("</div>");
    WebPageTools.generatePageFooter(pw);
  }

  private void generateTree(BinaryTreeNode<Person> node, PrintWriter pw,
      long sosa)
  {
    Person p=node.getData();
    pw.println("<ul>");
    pw.print("<li>");
    pw.print(sosa);
    pw.print(" - ");
    String personLink=_pTools.format(p);
    pw.print(personLink);
    BinaryTreeNode<Person> father=node.getLeftNode();
    if (father!=null)
    {
      generateTree(father,pw,2*sosa);
    }
    BinaryTreeNode<Person> mother=node.getRightNode();
    if (mother!=null)
    {
      generateTree(mother,pw,2*sosa+1);
    }
    pw.println("</li>");
    pw.println("</ul>");
  }
}
