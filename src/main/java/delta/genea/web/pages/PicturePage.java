package delta.genea.web.pages;

import java.io.PrintWriter;
import java.util.List;

import delta.common.framework.web.WebPageTools;
import delta.common.utils.ParameterFinder;
import delta.genea.data.Person;
import delta.genea.data.PersonInPicture;
import delta.genea.data.Picture;
import delta.genea.misc.GeneaConstants;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.formatters.PersonHtmlFormatter;

/**
 * Builder for the 'picture' HTML page.
 * @author DAM
 */
public class PicturePage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private Long _key;
  private Picture _picture;

  @Override
  public void parseParameters() throws Exception
  {
    _key=ParameterFinder.getLongParameter(_request,"KEY",Long.valueOf(76));
  }

  @Override
  public void fetchData() throws Exception
  {
    _picture=getDataSource().load(Picture.class,_key);
    if (_picture==null) return;
    List<PersonInPicture> persons=_picture.getPersonsInPicture();
    if ((persons!=null)&&(persons.size()>0))
    {
      int nb=persons.size();
      PersonInPicture person;
      for(int i=0;i<nb;i++)
      {
        person=persons.get(i);
        person.getPerson();
      }
    }
  }

  @Override
  public void generate(PrintWriter pw)
  {
    if (_picture==null)
    {
      String title="Photo non trouvée";
      WebPageTools.generatePageHeader(title,pw);
      pw.println("<div>");
      pw.println("<B>Photo non trouvée !</B>");
      pw.println("</div>");
      WebPageTools.generatePageFooter(pw);
    }

    GeneaUserContext context=(GeneaUserContext)getUserContext();
    PersonHtmlFormatter pTools=new PersonHtmlFormatter(context);
    pTools.setDeCujus(context.getDeCujus());
    pTools.setUseIsAncestorIcon(true);
    pTools.setUseSexIcon(false);
    pTools.setUseNoDescendants(true);
    pTools.setAsLink(true);

    String title=_picture.getTitle();
    WebPageTools.generatePageHeader(title,pw);
    WebPageTools.generateHorizontalRuler(pw);

    pw.println("<div>");
    pw.print(_picture.getTitle());
    pw.println("</div>");

    WebPageTools.generateHorizontalRuler(pw);

    pw.println("<div>");
    String comment=_picture.getComment();
    if ((comment!=null) && (comment.length()>0))
    {
      pw.print(comment);
      pw.println("<br>");
      pw.println("<br>");
    }
    List<PersonInPicture> persons=_picture.getPersonsInPicture();
    if ((persons!=null)&&(persons.size()>0))
    {
      pw.println("<div style=\"text-decoration:underline;\">");
      pw.println("Personnes identifiées");
      pw.println("</div>");
      pw.println("<ul>");

      int nb=persons.size();
      PersonInPicture personInPicture;
      Person person;
      String personLink;
      for(int i=0;i<nb;i++)
      {
        personInPicture=persons.get(i);
        person=personInPicture.getPerson();
        pw.println("<li>");
        personLink=pTools.format(person);
        pw.print(personLink);
        pw.println("</li>");
      }
      pw.println("</ul>");
    }
    pw.println("</div>");

    String path=_picture.getPath();
    if ((path!=null)&&(path.length()>0))
    {
      WebPageTools.generateHorizontalRuler(pw);
      pw.println("<div style=\"text-align:center;\">");

      String imageName;
      ImagePageParameters imagePage;
      imageName=_picture.getPictureFilename();
      imagePage=new ImagePageParameters(GeneaConstants.PICTURES_DIR,imageName);
      imagePage.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
      pw.print("<img src=\"");
      pw.print(imagePage.build());
      pw.print("\"");
      pw.print(" alt=\"");pw.print(imageName);pw.print("\"");
      pw.println(">");
      pw.println("</div>");
    }
    WebPageTools.generatePageFooter(pw);
  }
}
