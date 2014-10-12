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

public class PersonTools
{
  private PrintWriter _pw;
  private GeneaUserContext _context;
  private Long _deCujus;
  private boolean _useSexIcon;
  private boolean _useNoDescendants;
  private boolean _asLink;
  private boolean _useIsAncestorIcon;
  private boolean _showDates;

  public PersonTools(GeneaUserContext context, PrintWriter pw)
  {
    _context=context;
    _deCujus=context.getDeCujus();
    _pw=pw;
    _useSexIcon=false;
    _useNoDescendants=false;
    _asLink=true;
    _useIsAncestorIcon=true;
    _showDates=true;
  }

  public void setUseSexIcon(boolean value) { _useSexIcon=value; }
  public void setUseNoDescendants(boolean value) { _useNoDescendants=value; }
  public void setAsLink(boolean value) { _asLink=value; }
  public void setUseIsAncestorIcon(boolean value) { _useIsAncestorIcon=value; }
  public void setShowDates(boolean value) { _showDates=value; }

  public void generatePersonName(Person person)
  {
    generatePersonName(person,null,null);
  }

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
        if (person.getSex()==Sex.MALE)
        {
          pw.print("homme.gif");
        }
        else if (person.getSex()==Sex.FEMALE)
        {
          pw.print("femme.gif");
        }
        else
        {
          pw.print("inconnu.gif");
        }
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
      if (_showDates)
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

  private boolean isAncestor(Long key)
  {
    String dbName=_context.getDbName();
    GeneaDataSource dataSource=GeneaDataSource.getInstance(dbName);
    AncestorsTreesRegistry registry=dataSource.getAncestorsTreesRegistry();
    AncestorsTree tree=registry.getTree(_deCujus);
    if (tree!=null)
    {
      List<Long> sosas=tree.getSosas(key);
      return ((sosas!=null)&&(sosas.size()>0));
    }
    return false;
  }
}
