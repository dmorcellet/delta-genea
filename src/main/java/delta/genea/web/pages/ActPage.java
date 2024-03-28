package delta.genea.web.pages;

import java.io.PrintWriter;
import java.util.List;

import org.apache.log4j.Logger;

import delta.common.framework.web.WebPageTools;
import delta.common.utils.NumericTools;
import delta.common.utils.ParameterFinder;
import delta.genea.data.Act;
import delta.genea.data.ActText;
import delta.genea.data.Person;
import delta.genea.data.PersonInAct;
import delta.genea.data.Sex;
import delta.genea.misc.GeneaConstants;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.formatters.PersonHtmlFormatter;
import delta.genea.web.pages.tools.PersonTools;

/**
 * Builder for the 'act' HTML page.
 * @author DAM
 */
public class ActPage extends GeneaWebPage
{
  private static final Logger LOGGER=Logger.getLogger(ActPage.class);
  private static final String LINK_SEED="<a href=\"";
  private static final String LINK_SEED_END="\">";
  private static final String LINK_END="</a>";
  private static final String PERSON_LINK="person:";
  private static final String ID_ATTR="id=";
  private static final String DB_NAME_ATTR="dbName=";

  // HTML 4.01 strict validated
  private Long _key;
  private Act _act;

  @Override
  public void parseParameters() throws Exception
  {
    _key=ParameterFinder.getLongParameter(_request,ActPageParameters.KEY,null);
  }

  @Override
  public void fetchData() throws Exception
  {
    _act=getDataSource().load(Act.class,_key);
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
      for(int i=0;i<nb;i++)
      {
        PersonInAct personInAct=persons.get(i);
        Person person=personInAct.getPerson();
        Person linkReference=personInAct.getLinkReference();
        pw.println("<li>");
        String personLink=pTools.format(person);
        pw.print(personLink);
        // Link
        String link=personInAct.getLink();
        if ((link!=null)&&(link.length()>0))
        {
          pw.print(", ");
          pw.print(link);
          if (linkReference!=null)
          {
            pw.print(" de ");
            String refLink=pTools.format(linkReference);
            pw.print(refLink);
          }
        }
        pw.print(" - ");

        // Présence
        boolean useSignature=true;
        String presence=personInAct.getPresence();
        if ("O".equals(presence))
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
        else if ("+".equals(presence))
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
        else if ("N".equals(presence))
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
        else if ((presence==null) || (presence.length()==0))
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
          String signature=personInAct.getSignature();
          if (signature==null)
          {
            signature="";
          }
          if ("S".equals(signature))
          {
            pw.print("(Signature)");
          }
          else if ("M".equals(signature))
          {
            pw.print("(Marque)");
          }
          else if (("N".equals(signature))||("-".equals(signature)))
          {
            pw.print("(Rien)");
          }
          else if ((signature==null) || (signature.length()==0))
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
        imagePage.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
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
    generateActText(pw,text);

    WebPageTools.generatePageFooter(pw);
  }

  private void generateActText(PrintWriter pw, ActText text)
  {
    if (text!=null)
    {
      String textStr=text.getText();
      textStr=textStr.replace("\r\n","\n");
      textStr=textStr.replace("\n","<br>\n");
      pw.println("<div>");
      int textIndex=0;
      int length=textStr.length();
      while (textIndex<length)
      {
        boolean foundLink=false;
        String link=null;
        String linkContents=null;
        int linkStartIndex=textStr.indexOf(LINK_SEED,textIndex);
        if (linkStartIndex!=-1)
        {
          pw.print(textStr.substring(textIndex,linkStartIndex));
          int linkStartEndIndex=textStr.indexOf(LINK_SEED_END,linkStartIndex+LINK_SEED.length());
          if (linkStartEndIndex!=-1)
          {
            int linkTerminationIndex=textStr.indexOf(LINK_END,linkStartEndIndex+LINK_SEED_END.length());
            if (linkTerminationIndex!=-1)
            {
              link=textStr.substring(linkStartIndex+LINK_SEED.length(),linkStartEndIndex);
              linkContents=textStr.substring(linkStartEndIndex+LINK_SEED_END.length(),linkTerminationIndex);
              foundLink=true;
              textIndex=linkTerminationIndex+LINK_END.length();
            }
          }
        }
        if (foundLink)
        {
          handleLink(pw,link,linkContents);
        }
        else
        {
          pw.println(textStr.substring(textIndex));
          textIndex=length;
        }
      }
      pw.println("</div>");
    }
  }

  private void handleLink(PrintWriter pw, String linkDef, String linkContents)
  {
    // person : <a href="person:id=1234,dbName=genea">Nom personne</a>
    if (linkDef.startsWith(PERSON_LINK))
    {
      linkDef=linkDef.substring(PERSON_LINK.length());
      String[] attributes=linkDef.split(",");
      long key=-1;
      String dbName=null;
      if (attributes!=null)
      {
        for(int i=0;i<attributes.length;i++)
        {
          if (attributes[i].startsWith(ID_ATTR)) key=NumericTools.parseLong(attributes[i].substring(ID_ATTR.length()),key);
          else if (attributes[i].startsWith(DB_NAME_ATTR)) dbName=attributes[i].substring(DB_NAME_ATTR.length());
          else
          {
            LOGGER.warn("Unmanaged attribute: ["+attributes[i]+"]");
          }
        }
      }
      boolean ok=false;
      if (key!=-1)
      {
        Person p=getDataSource().load(Person.class,Long.valueOf(key));
        if (p!=null)
        {
          GeneaUserContext context=(GeneaUserContext)getUserContext();
          PersonTools pTools=new PersonTools(context,pw);
          pTools.setUseIsAncestorIcon(true);
          pTools.setUseSexIcon(false);
          pTools.setUseNoDescendants(false);
          pTools.setAsLink(true);
          pTools.generatePersonName(p,linkContents,dbName);
          ok=true;
        }
      }
      if (!ok)
      {
        pw.print(linkContents);
      }
    }
  }
}
