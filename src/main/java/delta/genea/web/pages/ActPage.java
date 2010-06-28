package delta.genea.web.pages;

import java.io.PrintWriter;
import java.util.List;

import delta.common.framework.web.WebPageTools;
import delta.genea.data.Act;
import delta.genea.data.ActText;
import delta.genea.data.Person;
import delta.genea.data.PersonInAct;
import delta.genea.data.Sex;
import delta.genea.misc.GeneaConstants;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.formatters.PersonHtmlFormatter;

/**
 * Builder for the 'act' HTML page.
 * @author DAM
 */
public class ActPage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private long _key;
  private Act _act;

  @Override
  public void parseParameters() throws Exception
  {
    _key=getPersonKey("KEY");
  }

  @Override
  public void fetchData() throws Exception
  {
    _act=getDataSource().getActDataSource().load(_key);
    if (_act==null) return;
    _act.getP1();
    _act.getP2();
    List<PersonInAct> persons=_act.getPersonsInAct();
    if ((persons!=null)&&(persons.size()>0))
    {
      int nb=persons.size();
      PersonInAct person;
      for(int i=0;i<nb;i++)
      {
        person=persons.get(i);
        person.getPerson();
        person.getLinkReference();
      }
    }
    _act.getText();
  }

  @Override
  public void generate(PrintWriter pw)
  {
    if (_act==null)
    {
      String title="Acte non trouvé";
      WebPageTools.generatePageHeader(title,pw);
      pw.println("<div>");
      pw.println("<B>Acte non trouvé !</B>");
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

    String title=_act.getFullLabel();
    Person p1=_act.getP1();
    Person p2=_act.getP2();
    WebPageTools.generatePageHeader(title,pw);
    WebPageTools.generateHorizontalRuler(pw);
    pw.println("<div>");

    if (_act.getTraite())
    {
      pw.print("<img src=\"ressources/cocheVerte.gif\" alt=\"Coche verte\"> ");
    }
    pw.print(_act.getLabel());
    pw.print(" de ");
    String p1Link=pTools.format(p1);
    pw.print(p1Link);
    if (p2!=null)
    {
      pw.print(" et de ");
      String p2Link=pTools.format(p2);
      pw.print(p2Link);
    }
    pw.print(" (");
    pw.print(PageTools.generateDate(_act.getDate(),""));
    pw.println(")");
    pw.println("</div>");

    WebPageTools.generateHorizontalRuler(pw);

    pw.println("<div>");
    String comment=_act.getComment();
    if ((comment!=null) && (comment.length()>0))
    {
      pw.print(comment);
      pw.println("<br>");
      pw.println("<br>");
    }
    List<PersonInAct> persons=_act.getPersonsInAct();
    if ((persons!=null)&&(persons.size()>0))
    {
      pw.println("<div style=\"text-decoration:underline;\">");
      pw.println("Personnes mentionnées");
      pw.println("</div>");
      pw.println("<ul>");

      int nb=persons.size();
      PersonInAct personInAct;
      Person person;
      Person linkReference;
      String link, presence, signature;
      String personLink, refLink;
      for(int i=0;i<nb;i++)
      {
        personInAct=persons.get(i);
        person=personInAct.getPerson();
        linkReference=personInAct.getLinkReference();
        pw.println("<li>");
        personLink=pTools.format(person);
        pw.print(personLink);
        // Link
        link=personInAct.getLink();
        if ((link!=null)&&(link.length()>0))
        {
          pw.print(", ");
          pw.print(link);
          if (linkReference!=null)
          {
            pw.print(" de ");
            refLink=pTools.format(linkReference);
            pw.print(refLink);
          }
        }
        pw.print(" - ");

        // Présence
        boolean useSignature=true;
        presence=personInAct.getPresence();
        if (presence.equals("O"))
        {
          if (person.getSex()==Sex.FEMALE)
          {
            pw.print("Présente");
          }
          else
          {
            pw.print("Présent");
          }
        }
        else if (presence.equals("+"))
        {
          if (person.getSex()==Sex.FEMALE)
          {
            pw.print("Décédée");
          }
          else
          {
            pw.print("Décédé");
          }
          useSignature=false;
        }
        else if (presence.equals("N"))
        {
          if (person.getSex()==Sex.FEMALE)
          {
            pw.print("Absente");
          }
          else
          {
            pw.print("Absent");
          }
          useSignature=false;
        }
        else if (presence.length()==0)
        {
          pw.print("???");
        }
        else
        {
          pw.print(presence);
        }

        // Signature
        if (useSignature)
        {
          pw.print(" - ");
          signature=personInAct.getSignature();
          if (signature==null)
          {
            signature="";
          }
          if (signature.equals("S"))
          {
            pw.print("(Signature)");
          }
          else if (signature.equals("M"))
          {
            pw.print("(Marque)");
          }
          else if ((signature.equals("N"))||(signature.equals("-")))
          {
            pw.print("(Rien)");
          }
          else if (signature.length()==0)
          {
            pw.print("???");
          }
          else
          {
            pw.print(signature);
          }
        }
        pw.println("</li>");
      }
      pw.println("</ul>");
    }
    pw.println("</div>");

    String path=_act.getPath();
    if ((path!=null)&&(path.length()>0))
    {
      WebPageTools.generateHorizontalRuler(pw);
      pw.println("<div style=\"text-align:center;\">");

      int nb=_act.getNbFiles();
      String imageName;
      ImagePageParameters imagePage;
      for(int i=0;i<nb;i++)
      {
        imageName=_act.getActFilename(i);
        imagePage=new ImagePageParameters(GeneaConstants.ACTS_DIR,imageName);
        pw.print("<img src=\"");
        pw.print(imagePage.build());
        pw.print("\"");
        pw.print(" alt=\"");pw.print(imageName);pw.print("\"");
        pw.println(">");
      }
      pw.println("</div>");
    }

    // Handle act text
    ActText text=_act.getText();
    if (text!=null)
    {
      String textStr=text.getText();
      textStr=textStr.replace("\r\n","\n");
      textStr=textStr.replace("\n","<br>\n");
      pw.println("<div>");
      pw.println(textStr);
      pw.println("</div>");
    }

    WebPageTools.generatePageFooter(pw);
  }
}
