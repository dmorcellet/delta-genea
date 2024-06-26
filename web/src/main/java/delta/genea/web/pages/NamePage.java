package delta.genea.web.pages;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import delta.common.framework.web.WebPageTools;
import delta.common.utils.ParameterFinder;
import delta.common.utils.collections.TreeNode;
import delta.common.utils.html.HtmlConstants;
import delta.genea.data.Person;
import delta.genea.data.comparators.PersonComparator;
import delta.genea.data.comparators.PersonComparator.COMPARISON_CRITERIA;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.pages.tools.PersonTools;

/**
 * Builder for the 'name' HTML page.
 * @author DAM
 */
public class NamePage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private String _name;
  private boolean _noDescendants;
  private TreeNode<Person> _data;
  private int _nbBeforeFilter;
  private PersonTools _pTools;

  @Override
  public void parseParameters() throws Exception
  {
    _name=ParameterFinder.getStringParameter(_request,NamePageParameters.NAME,"MORCELLET");
    _noDescendants=ParameterFinder.getBooleanParameter(_request,NamePageParameters.NO_DESCENDANTS,true);
  }

  @Override
  public void fetchData() throws Exception
  {
    // Load data
    List<Person> list=getDataSource().loadObjectSet(Person.class,Person.NAME_SET,_name);
    // Build tree
    _data=buildTree(list);
  }

  private TreeNode<Person> buildTree(List<Person> persons)
  {
    TreeNode<Person> ret=new TreeNode<Person>(null);

    int nb=persons.size();
    _nbBeforeFilter=nb;

    TreeMap<Long,TreeNode<Person>> map=new TreeMap<Long,TreeNode<Person>>();
    List<TreeNode<Person>> list=new ArrayList<TreeNode<Person>>();
    for(int i=0;i<nb;i++)
    {
      Person p=persons.get(i);
      if ((_noDescendants) || (!p.getNoDescendants()))
      {
        TreeNode<Person> node=ret.addChild(p);
        map.put(p.getPrimaryKey(),node);
        list.add(node);
      }
    }
    nb=list.size();
    for(int i=0;i<nb;i++)
    {
      TreeNode<Person> node=list.get(i);
      Person p=node.getData();

      // Find father
      Person father=p.getFather();
      boolean fatherFound=false;
      if (father!=null)
      {
        TreeNode<Person> found=map.get(father.getPrimaryKey());
        if (found!=null)
        {
          node.changeSuperNode(found);
          fatherFound=true;
        }
      }
      if (!fatherFound)
      {
        // If father not found, find mother
        Person mother=p.getMother();
        if (mother!=null)
        {
          TreeNode<Person> found=map.get(mother.getPrimaryKey());
          if (found!=null)
          {
            node.changeSuperNode(found);
          }
        }
      }
    }
    ret.sortChildren(new PersonComparator(COMPARISON_CRITERIA.BIRTH_DATE),true);
    return ret;
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
    String title="Patronyme "+_name;
    WebPageTools.generatePageHeader(title,pw);
    int nbPersonnes=_data.getNumberOfDescendants();

    WebPageTools.generateHorizontalRuler(pw);
    pw.println(HtmlConstants.DIV);
    pw.print("<b>");
    pw.print("Patronyme ");
    pw.print(_name);
    pw.print(" (");
    pw.print(nbPersonnes);
    if (nbPersonnes!=_nbBeforeFilter)
    {
      pw.print('/');
      pw.print(_nbBeforeFilter);
    }
    pw.print(')');
    NamePageParameters params=new NamePageParameters(_name);
    params.setNoDescendants(!_noDescendants);
    params.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
    pw.print(" <a href=\"");
    pw.print(params.build());
    pw.print("\">");
    if (_noDescendants) pw.print("Masquer les personnes sans descendance");
    else pw.print("Afficher les personnes sans descendance");
    pw.println("</a></b>");
    pw.println(HtmlConstants.END_DIV);
    WebPageTools.generateHorizontalRuler(pw);

    pw.println(HtmlConstants.DIV);
    if (nbPersonnes>0)
    {
      TreeNode<Person> node=_data.getFirstChild();
      while (node!=null)
      {
        generateTree(node,pw);
        node=node.getNextBrother();
      }
    }
    else
    {
      pw.println("Aucune");
    }
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
