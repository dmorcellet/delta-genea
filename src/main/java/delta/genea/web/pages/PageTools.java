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
import delta.genea.time.GregorianDate;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.pages.tools.PersonTools;

public class PageTools extends WebPageTools
{
  private GeneaUserContext _context;

  public PageTools(GeneaUserContext context, PrintWriter pw)
  {
    super(pw);
    _context=context;
  }

  public void generateActLink(Act act, String label)
  {
    if (act!=null)
    {
      _pw.print("<A HREF=\"");
      ActPageParameters actPage=new ActPageParameters(act.getPrimaryKey());
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

  public void generatePictureLink(Picture picture)
  {
    if (picture!=null)
    {
      _pw.print("<A HREF=\"");
      PicturePageParameters picturePage=new PicturePageParameters(picture.getPrimaryKey());
      _pw.print(picturePage.build());
      _pw.print("\">");
      _pw.print(picture.getTitle());
      _pw.print("</A>");
    }
  }

  public static String generateDate(GeneaDate gdate)
  {
    GeneaDateFormatter formatter=new GeneaDateFormatter();
    StringBuilder sb=new StringBuilder();
    formatter.format(gdate,sb);
    String ret=sb.toString();
    return ret;
  }

  public static String generateDate(Long date, String infos)
  {
    GeneaDate gdate=new GeneaDate(date,infos);
    return generateDate(gdate);
  }

  public static String generateDate(Date date, String infos)
  {
    GeneaDate gdate=new GeneaDate(date,infos);
    return generateDate(gdate);
  }

  public static void generateYear(Date date, String infos, PrintWriter pw)
  {
    if (date!=null)
    {
      GregorianDate gd=new GregorianDate(date);
      pw.print(gd.getYear());
    }
    else if ((infos==null) || (infos.length()==0))
    {
      pw.print("???");
    }
    else
    {
      pw.print(infos);
    }
  }

  public static void generatePlace(Place place, PrintWriter pw, String before, String after)
  {
    if (place!=null)
    {
      if (before!=null)
      {
        pw.print(before);
      }
      pw.print(place.getFullName());
      if (after!=null)
      {
        pw.print(after);
      }
    }
  }
}
