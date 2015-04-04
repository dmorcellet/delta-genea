package delta.genea.web.pages;

import java.io.PrintWriter;
import java.util.Date;

import delta.common.framework.web.WebPageTools;
import delta.genea.data.Act;
import delta.genea.data.GeneaDate;
import delta.genea.data.Person;
import delta.genea.data.Picture;
import delta.genea.data.Place;
import delta.genea.data.formatters.GeneaDateFormatter;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.pages.tools.PersonTools;

/**
 * Some HTML pages generation tools.
 * @author DAM
 */
public class PageTools extends WebPageTools
{
  private GeneaUserContext _context;

  /**
   * Constructor.
   * @param context User context.
   * @param pw Output writer.
   */
  public PageTools(GeneaUserContext context, PrintWriter pw)
  {
    super(pw);
    _context=context;
  }

  /**
   * Generate a link for an act.
   * @param act Act to link.
   * @param label Label for the link.
   */
  public void generateActLink(Act act, String label)
  {
    if (act!=null)
    {
      _pw.print("<A HREF=\"");
      ActPageParameters actPage=new ActPageParameters(act.getPrimaryKey());
      actPage.setParameter(GeneaUserContext.DB_NAME,_context.getDbName());
      _pw.print(actPage.build());
      _pw.print("\">");
      _pw.print(label);
      _pw.print("</A>");
    }
    else
    {
      _pw.print(label);
    }
  }

  /**
   * Generate a link for an act involving 1 or 2 persons.
   * @param act Act to link.
   * @param p1 First person.
   * @param p2 Second person (may be <code>null</code>).
   */
  public void generateActLink(Act act, Person p1, Person p2)
  {
    if (act==null) return;

    if (act.getTraite())
    {
      _pw.print("<img src=\"ressources/cocheVerte.gif\" alt=\"Coche verte\">");
    }
    if (act.getNbFiles()>0)
    {
      _pw.print("<img src=\"ressources/acte.gif\" alt=\"Acte\">");
    }
    PersonTools pTools=new PersonTools(_context,_pw);
    pTools.setUseIsAncestorIcon(true);
    pTools.setUseSexIcon(false);
    pTools.setUseNoDescendants(true);
    pTools.setAsLink(true);
    _pw.print("<B>");
    generateActLink(act,act.getLabel());
    _pw.print("</B> de ");
    pTools.generatePersonName(p1);
    if (p2!=null)
    {
      _pw.print(" et de ");
      pTools.generatePersonName(p2);
    }
    _pw.print(" (");
    _pw.print(generateDate(act.getDate(),""));
    _pw.print(')');
  }

  /**
   * Generate a link for a picture.
   * @param picture Picture to link.
   */
  public void generatePictureLink(Picture picture)
  {
    if (picture!=null)
    {
      _pw.print("<A HREF=\"");
      PicturePageParameters picturePage=new PicturePageParameters(picture.getPrimaryKey());
      picturePage.setParameter(GeneaUserContext.DB_NAME,_context.getDbName());
      _pw.print(picturePage.build());
      _pw.print("\">");
      _pw.print(picture.getTitle());
      _pw.print("</A>");
    }
  }

  /**
   * Build a string representation of a date.
   * @param gdate Date.
   * @return A displayable string.
   */
  public static String generateDate(GeneaDate gdate)
  {
    GeneaDateFormatter formatter=new GeneaDateFormatter();
    StringBuilder sb=new StringBuilder();
    formatter.format(gdate,sb);
    String ret=sb.toString();
    return ret;
  }

  /**
   * Build a string representation of a date.
   * @param date Date.
   * @param infos Date information.
   * @return A displayable string.
   */
  public static String generateDate(Long date, String infos)
  {
    GeneaDate gdate=new GeneaDate(date,infos);
    return generateDate(gdate);
  }

  /**
   * Build a string representation of a date.
   * @param date Date.
   * @param infos Date information.
   * @return A displayable string.
   */
  public static String generateDate(Date date, String infos)
  {
    GeneaDate gdate=new GeneaDate(date,infos);
    return generateDate(gdate);
  }

  /**
   * Generate a place.
   * @param place Place to use.
   * @param before String to write before the place (may be <code>null</code>).
   * @param after String to write after the place (may be <code>null</code>).
   */
  public void generatePlace(Place place, String before, String after)
  {
    if (place!=null)
    {
      if (before!=null)
      {
        _pw.print(before);
      }
      _pw.print(place.getFullName());
      if (after!=null)
      {
        _pw.print(after);
      }
    }
  }
}
