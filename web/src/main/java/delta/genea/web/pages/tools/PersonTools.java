package delta.genea.web.pages.tools;

import java.io.PrintWriter;
import java.util.List;

import delta.genea.data.Person;
import delta.genea.data.Sex;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.data.trees.AncestorsTreesRegistry;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.pages.PersonPageParameters;

/**
 * Tool methods to display person data.
 * @author DAM
 */
public class PersonTools
{
  private PrintWriter _pw;
  private GeneaUserContext _context;
  private Long _deCujus;
  private boolean _useSexIcon;
  private boolean _useNoDescendants;
  private boolean _asLink;
  private boolean _useIsAncestorIcon;
  private boolean _useLifeTime;

  /**
   * Constructor.
   * @param context User context.
   * @param pw Output writer.
   */
  public PersonTools(GeneaUserContext context, PrintWriter pw)
  {
    _context=context;
    _deCujus=context.getDeCujus();
    _pw=pw;
    _useSexIcon=false;
    _useNoDescendants=false;
    _asLink=true;
    _useIsAncestorIcon=true;
    _useLifeTime=true;
  }

  /**
   * Set the 'use sex icon' flag.
   * @param value Value to set.
   */
  public void setUseSexIcon(boolean value) { _useSexIcon=value; }
  /**
   * Set the 'use no descendants icon' flag.
   * @param value Value to set.
   */
  public void setUseNoDescendants(boolean value) { _useNoDescendants=value; }
  /**
   * Set the 'display as a link' flag.
   * @param value Value to set.
   */
  public void setAsLink(boolean value) { _asLink=value; }
  /**
   * Set the 'use is ancestor icon' flag.
   * @param value Value to set.
   */
  public void setUseIsAncestorIcon(boolean value) { _useIsAncestorIcon=value; }
  /**
   * Set the 'use life time' flag.
   * @param value Value to set.
   */
  public void setUseLifeTime(boolean value) { _useLifeTime=value; }

  /**
   * Generate a person name to the output write.
   * @param person Person to use.
   */
  public void generatePersonName(Person person)
  {
    generatePersonName(person,null,null);
  }

  /**
   * Generate a person name to the output write.
   * @param person Person to use.
   * @param alternativeText Alternative text for link.
   * Uses person name if <code>null</code>.
   * @param dbName Database name (<code>null</code> to use default current database).
   */
  public void generatePersonName(Person person, String alternativeText, String dbName)
  {
    PrintWriter pw=_pw;
    if (person!=null)
    {
      if (_useIsAncestorIcon)
      {
        Long key=person.getPrimaryKey();
        if (isAncestor(key))
        {
          pw.print("<IMG SRC=\"ressources/ancetre.gif\" ALT=\"Ancêtre\">");
        }
      }
      if (_useSexIcon)
      {
        pw.print("<IMG SRC=\"ressources/");
        String sexIcon=getIcon(person.getSex());
        pw.print(sexIcon);
        pw.print("\" ALT=\"Sexe\">");
      }

      if (_useNoDescendants)
      {
        if (person.getNoDescendants())
        {
          pw.print("<IMG SRC=\"ressources/sansDescendance.gif\" ALT=\"Sans descendance\">");
        }
      }

      pw.print("<B>");
      if (_asLink)
      {
        PersonPageParameters ppp=new PersonPageParameters(person.getPrimaryKey());
        String dbNameToUse=(dbName==null)?_context.getDbName():dbName;
        ppp.setParameter(GeneaUserContext.DB_NAME,dbNameToUse);
        pw.print("<A HREF=\"");
        pw.print(ppp.build());
        pw.print("\">");
      }
      if (alternativeText!=null)
      {
        pw.print(alternativeText);
      }
      else
      {
        pw.print(person.getFirstname());
        pw.print(' ');
        pw.print(person.getLastName());
      }
      if (_asLink)
      {
        pw.print("</A>");
      }
      pw.print("</B>");
      if (_useLifeTime)
      {
        pw.print(" (");
        pw.print(person.getBirthGeneaDate().getYearString());
        pw.print(" - ");
        pw.print(person.getDeathGeneaDate().getYearString());
        pw.print(")");
      }
    }
    else
    {
      pw.print("???");
    }
  }

  private String getIcon(Sex sex)
  {
    if (sex==Sex.MALE)
    {
      return "homme.gif";
    }
    if (sex==Sex.FEMALE)
    {
      return "femme.gif";
    }
    return "inconnu.gif";
  }

  private boolean isAncestor(Long key)
  {
    GeneaDataSource dataSource=_context.getDataSource();
    AncestorsTreesRegistry registry=dataSource.getAncestorsTreesRegistry();
    AncestorsTree tree=registry.getTree(_deCujus);
    if (tree!=null)
    {
      List<Long> sosas=tree.getSosas(key);
      return ((sosas!=null)&&(!sosas.isEmpty()));
    }
    return false;
  }
}
