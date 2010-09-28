package delta.genea.web.pages;

import java.io.PrintWriter;

import delta.common.framework.web.WebPageTools;
import delta.common.utils.ParameterFinder;
import delta.common.utils.collections.TreeNode;
import delta.genea.data.Place;
import delta.genea.data.PlaceLevel;
import delta.genea.data.sources.PlacesRegistry;
import delta.genea.web.GeneaUserContext;

/**
 * Builder for the 'places' HTML page.
 * @author DAM
 */
public class PlacesPage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private TreeNode<Place> _places;

  @Override
  public void fetchData() throws Exception
  {
    String dbName=ParameterFinder.getStringParameter(_request,GeneaUserContext.DB_NAME,null);
    _places=PlacesRegistry.getInstance(dbName).getPlacesTree();
  }

  @Override
  public void generate(PrintWriter pw)
  {
    String title="Communes";
    WebPageTools.generatePageHeader(title,pw);
    WebPageTools.generateHorizontalRuler(pw);
    pw.print("<h1>");
    pw.print(title);
    pw.print("</h1>");
    WebPageTools.generateHorizontalRuler(pw);
    pw.println("<div>");
    TreeNode<Place> node=_places.getFirstChild();
    while (node!=null)
    {
      generateTree(node,pw);
      node=node.getNextBrother();
    }
    pw.println("</div>");
    WebPageTools.generatePageFooter(pw);
  }

  private void generateTree(TreeNode<Place> node, PrintWriter pw)
  {
    Place p=node.getData();
    pw.println("<ul>");
    pw.print("<li>");
    pw.print(' ');
    if (p.getLevel()==PlaceLevel.TOWN)
    {
      ActsFromPlaceParameters params=new ActsFromPlaceParameters(p.getPrimaryKey());
      pw.print("<a href=\"");
      pw.print(params.build());
      pw.print("\">");
      pw.print(p.getName());
      pw.println("</a>");
    }
    else
    {
      pw.println(p.getName());
    }
    TreeNode<Place> subPlace=node.getFirstChild();
    while (subPlace!=null)
    {
      generateTree(subPlace,pw);
      subPlace=subPlace.getNextBrother();
    }
    pw.println("</li>");
    pw.println("</ul>");
  }
}
