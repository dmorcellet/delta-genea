package delta.genea.web.pages;

import java.io.PrintWriter;
import java.util.List;

import delta.common.framework.web.WebPageTools;
import delta.common.utils.html.HtmlConstants;
import delta.genea.data.Person;
import delta.genea.data.PersonInPicture;
import delta.genea.data.Picture;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.pages.tools.PersonTools;

/**
 * Builder for the 'pictures' HTML page.
 * @author DAM
 */
public class PicturesPage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private List<Picture> _pictures;

  @Override
  public void fetchData() throws Exception
  {
    _pictures=getDataSource().loadAll(Picture.class);
  }

  @Override
  public void generate(PrintWriter pw)
  {
    String title="Photos";
    WebPageTools.generatePageHeader(title,pw);
    WebPageTools.generateHorizontalRuler(pw);
    pw.print("<h1>");
    pw.print(title);
    pw.print("</h1>");
    WebPageTools.generateHorizontalRuler(pw);
    pw.println(HtmlConstants.DIV);
    if ((_pictures!=null) && (!_pictures.isEmpty()))
    {
      GeneaUserContext context=(GeneaUserContext)getUserContext();
      PersonTools pTools=new PersonTools(context,pw);
      pTools.setUseIsAncestorIcon(true);
      pTools.setUseSexIcon(false);
      pTools.setUseNoDescendants(false);
      pTools.setAsLink(true);
      pw.println("<ul>");
      for(Picture picture : _pictures)
      {
        PicturePageParameters params=new PicturePageParameters(picture.getPrimaryKey());
        params.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
        pw.print(HtmlConstants.LI);
        pw.print("<a href=\"");
        pw.print(params.build());
        pw.print("\">");
        pw.print(picture.getTitle());
        pw.print("</a>");
        List<PersonInPicture> persons=picture.getPersonsInPicture();
        if ((persons!=null) && (!persons.isEmpty()))
        {
          pw.print(':');
          for(PersonInPicture pip : persons)
          {
            pw.print(' ');
            Person p=pip.getPerson();
            pTools.generatePersonName(p);
          }
        }
        pw.println(HtmlConstants.END_LI);
      }
      pw.println("</ul>");
    }
    pw.println(HtmlConstants.END_DIV);
    WebPageTools.generatePageFooter(pw);
  }
}
