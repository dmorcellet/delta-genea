package delta.genea.web.formatters;

import java.util.List;

import delta.common.utils.text.TextFormatter;
import delta.genea.data.Person;
import delta.genea.data.Sex;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.data.trees.AncestorsTreesRegistry;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.pages.PersonPageParameters;

/**
 * A text formatter used for persons in HTML mode.
 * @author DAM
 */
public class PersonHtmlFormatter extends TextFormatter
{
  private GeneaUserContext _context;
  private Long _deCujus;
  private boolean _useSexIcon;
  private boolean _useNoDescendants;
  private boolean _useLifeTime;
  private boolean _asLink;
  private boolean _useIsAncestorIcon;

  public PersonHtmlFormatter(GeneaUserContext context)
  {
    _context=context;
    _useSexIcon=false;
    _useNoDescendants=false;
    _asLink=true;
    _useLifeTime=true;
    _useIsAncestorIcon=true;
  }

  public void setDeCujus(Long deCujus) { _deCujus=deCujus; }
  public void setUseSexIcon(boolean value) { _useSexIcon=value; }
  public void setUseLifeTime(boolean value) { _useLifeTime=value; }
  public void setUseNoDescendants(boolean value) { _useNoDescendants=value; }
  public void setAsLink(boolean value) { _asLink=value; }
  public void setUseIsAncestorIcon(boolean value) { _useIsAncestorIcon=value; }

  /**
   * Format the specified object into the given <tt>StringBuilder</tt>.
   * @param o Object to format.
   * @param sb Output string builder.
   */
  @Override
  public void format(Object o, StringBuilder sb)
  {
    Person person=null;
    if (o instanceof Person) person=(Person)o;
    if (person==null) return;
    if (_useIsAncestorIcon)
    {
      Long key=person.getPrimaryKey();
      if (isAncestor(key))
      {
        sb.append("<IMG SRC=\"ressources/ancetre.gif\" ALT=\"Ancêtre\">");
      }
    }
    if (_useSexIcon)
    {
      sb.append("<IMG SRC=\"ressources/");
      if (person.getSex()==Sex.MALE) sb.append("homme.gif");
      else if (person.getSex()==Sex.FEMALE) sb.append("femme.gif");
      else sb.append("inconnu.gif");
      sb.append("\" ALT=\"Sexe\">");
    }
    if (_useNoDescendants)
    {
      if (person.getNoDescendants())
      {
        sb.append("<IMG SRC=\"ressources/sansDescendance.gif\" ALT=\"Sans descendance\">");
      }
    }

    sb.append("<B>");
    if (_asLink)
    {
      PersonPageParameters ppp=new PersonPageParameters(person.getPrimaryKey());
      ppp.setParameter(GeneaUserContext.DB_NAME,_context.getDbName());
      sb.append("<A HREF=\"");
      sb.append(ppp.build());
      sb.append("\">");
    }
    sb.append(person.getFullName());
    if (_asLink)
    {
      sb.append("</A>");
    }
    sb.append("</B>");
    if (_useLifeTime)
    {
      sb.append(" (");
      sb.append(person.getBirthGeneaDate().getYearString());
      sb.append(" - ");
      sb.append(person.getDeathGeneaDate().getYearString());
      sb.append(")");
    }
  }

  private boolean isAncestor(long key)
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
