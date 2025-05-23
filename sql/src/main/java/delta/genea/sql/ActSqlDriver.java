package delta.genea.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.Identifiable;
import delta.common.framework.objects.data.ObjectsSource;
import delta.common.framework.objects.sql.ObjectSqlDriver;
import delta.common.utils.jdbc.CleanupManager;
import delta.common.utils.jdbc.JDBCTools;
import delta.genea.data.Act;
import delta.genea.data.ActText;
import delta.genea.data.ActType;
import delta.genea.data.Person;
import delta.genea.data.PersonInAct;
import delta.genea.data.Place;

/**
 * SQL driver for acts.
 * @author DAM
 */
public class ActSqlDriver extends ObjectSqlDriver<Act>
{
  private static final Logger LOGGER=LoggerFactory.getLogger(ActSqlDriver.class);

  private PreparedStatement _psGetByPrimaryKey;
  private PreparedStatement _psGetAll;
  private PreparedStatement _psInsert;
  private PreparedStatement _psUpdate;
  private PreparedStatement _psCount;
  private PreparedStatement _psGetMainFromPerson;
  private PreparedStatement _psGetOtherFromPerson;
  private PreparedStatement _psGetPersonInAct;
  private PreparedStatement _psGetFromPlace;
  private ObjectsSource _mainDataSource;

  /**
   * Constructor.
   * @param mainDataSource Main data source.
   */
  public ActSqlDriver(ObjectsSource mainDataSource)
  {
    _mainDataSource=mainDataSource;
  }

  @Override
  protected void buildPreparedStatements(Connection newConnection)
  {
    try
    {
      String fields="cle_acte,cle_type_acte,date_acte,cle_lieu,cle_p1,cle_p2,cle_texte,chemin,nb_parties,traite,commentaire";
      // Select
      String sql="SELECT "+fields+" FROM acte WHERE cle_acte = ?";
      _psGetByPrimaryKey=newConnection.prepareStatement(sql);
      // Select all
      sql="SELECT "+fields+" FROM acte";
      _psGetAll=newConnection.prepareStatement(sql);
      // Insert
      sql="INSERT INTO acte ("+fields+") VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      if (usesHSQLDB())
      {
        _psInsert=newConnection.prepareStatement(sql);
      }
      else
      {
        _psInsert=newConnection.prepareStatement(sql,
            Statement.RETURN_GENERATED_KEYS);
      }
      // Update
      sql="UPDATE acte SET cle_type_acte=?,date_acte=?,cle_lieu=?,cle_p1=?,cle_p2=?,cle_texte=?,chemin=?,nb_parties=?,traite=?,commentaire=? WHERE cle_acte=?";
      _psUpdate=newConnection.prepareStatement(sql);
      // Select count
      sql="SELECT COUNT(*) FROM acte WHERE cle_acte = ?";
      _psCount=newConnection.prepareStatement(sql);
      sql="SELECT cle_acte FROM acte WHERE cle_p1 = ? OR cle_p2 = ?";
      _psGetMainFromPerson=newConnection.prepareStatement(sql);
      sql="SELECT DISTINCT cle_acte FROM personne_acte WHERE cle_personne = ?";
      _psGetOtherFromPerson=newConnection.prepareStatement(sql);
      sql="SELECT cle_personne,presence,signature,lien,ref_lien FROM personne_acte WHERE cle_acte = ?";
      _psGetPersonInAct=newConnection.prepareStatement(sql);
      sql="SELECT cle_acte FROM acte,type_acte WHERE cle_type_acte=cle and cle_lieu = ? order by type, date_acte";
      _psGetFromPlace=newConnection.prepareStatement(sql);
    }
    catch (SQLException sqlException)
    {
      LOGGER.error("Exception while building prepared statements for class Act",sqlException);
    }
  }

  @Override
  public Act getByPrimaryKey(Long primaryKey)
  {
    if (primaryKey==null)
    {
      return null;
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      Act ret=null;
      ResultSet rs=null;
      try
      {
        _psGetByPrimaryKey.setLong(1,primaryKey.longValue());
        rs=_psGetByPrimaryKey.executeQuery();
        if (rs.next())
        {
          ret=new Act(primaryKey);
          fillAct(ret,rs);
          List<PersonInAct> persons=loadPersonsInAct(primaryKey);
          ret.setPersonsInAct(persons);
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGetByPrimaryKey);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  @Override
  public List<Act> getAll()
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Act> ret=new ArrayList<Act>();
      Act act=null;
      ResultSet rs=null;
      try
      {
        rs=_psGetAll.executeQuery();
        while (rs.next())
        {
          long personKey=rs.getLong(1);
          Long primaryKey=Long.valueOf(personKey);
          act=new Act(primaryKey);
          fillAct(act,rs);
          List<PersonInAct> persons=loadPersonsInAct(primaryKey);
          act.setPersonsInAct(persons);
          ret.add(act);
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGetAll);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  private void fillAct(Act act, ResultSet rs) throws SQLException
  {
    int n=2;
    long actTypeKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      act.setActTypeProxy(_mainDataSource.buildProxy(ActType.class,Long.valueOf(actTypeKey)));
    }
    n++;
    act.setDate(rs.getDate(n));
    n++;
    long placeKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      act.setPlaceProxy(_mainDataSource.buildProxy(Place.class,Long.valueOf(placeKey)));
    }
    n++;
    long p1Key=rs.getLong(n);
    if (!rs.wasNull())
    {
      act.setP1Proxy(_mainDataSource.buildProxy(Person.class,Long.valueOf(p1Key)));
    }
    n++;
    long p2Key=rs.getLong(n);
    if (!rs.wasNull())
    {
      act.setP2Proxy(_mainDataSource.buildProxy(Person.class,Long.valueOf(p2Key)));
    }
    n++;
    long actTextKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      act.setTextProxy(_mainDataSource.buildProxy(ActText.class,Long.valueOf(actTextKey)));
    }
    n++;
    act.setPath(rs.getString(n));
    n++;
    act.setNbFiles(rs.getInt(n));
    n++;
    act.setTraite(rs.getBoolean(n));
    n++;
    act.setComment(rs.getString(n));
  }

  private List<PersonInAct> loadPersonsInAct(Long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      List<PersonInAct> ret=null;
      PersonInAct tmp=null;
      ResultSet rs=null;
      try
      {
        _psGetPersonInAct.setLong(1,primaryKey.longValue());
        rs=_psGetPersonInAct.executeQuery();
        while (rs.next())
        {
          tmp=new PersonInAct();
          int n=1;
          long pKey=rs.getLong(n);
          if (!rs.wasNull())
          {
            tmp.setPersonProxy(_mainDataSource.buildProxy(Person.class,Long.valueOf(pKey)));
          }
          n++;
          tmp.setPresence(rs.getString(n));
          n++;
          tmp.setSignature(rs.getString(n));
          n++;
          tmp.setLink(rs.getString(n));
          n++;
          long refLink=rs.getLong(n);
          if (!rs.wasNull())
          {
            tmp.setLinkRefProxy(_mainDataSource.buildProxy(Person.class,Long.valueOf(refLink)));
          }
          n++;
          if (ret==null)
          {
            ret=new ArrayList<PersonInAct>();
          }
          ret.add(tmp);
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGetByPrimaryKey);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  /**
   * Indicates if the act identified by <code>primaryKey</code>
   * exists or not.
   * @param primaryKey Identifier for the targeted act.
   * @return <code>true</code> if it does, <code>false</code> otherwise.
   */
  public boolean exists(long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      boolean ret=false;
      ResultSet rs=null;
      try
      {
        _psCount.setLong(1,primaryKey);
        rs=_psGetByPrimaryKey.executeQuery();
        if (rs.next())
        {
          long count=rs.getLong(1);
          if (count>0)
          {
            ret=true;
          }
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGetByPrimaryKey);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  /**
   * Get the identifiers of acts where one of the main person is
   * the person identified by <code>primaryKey</code>.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of act identifiers.
   */
  public List<Long> getMainFromPerson(Long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Long> ret=new ArrayList<Long>();
      Long act=null;
      ResultSet rs=null;
      try
      {
        _psGetMainFromPerson.setLong(1,primaryKey.longValue());
        _psGetMainFromPerson.setLong(2,primaryKey.longValue());
        rs=_psGetMainFromPerson.executeQuery();
        while (rs.next())
        {
          act=Long.valueOf(rs.getLong(1));
          ret.add(act);
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGetMainFromPerson);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  /**
   * Get the identifiers of acts where the person identified
   * by <code>primaryKey</code> do appear.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of act identifiers.
   */
  public List<Long> getOtherFromPerson(Long primaryKey)
  {
    if (primaryKey==null)
    {
      throw new IllegalArgumentException("primaryKey is null");
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Long> ret=new ArrayList<Long>();
      Long act=null;
      ResultSet rs=null;
      try
      {
        _psGetOtherFromPerson.setLong(1,primaryKey.longValue());
        rs=_psGetOtherFromPerson.executeQuery();
        while (rs.next())
        {
          act=Long.valueOf(rs.getLong(1));
          ret.add(act);
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGetOtherFromPerson);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  /**
   * Get the identifiers of acts whose place is identified
   * by <code>primaryKey</code>.
   * @param primaryKey Identifier of the targeted place.
   * @return A list of act identifiers.
   */
  public List<Long> getActsFromPlace(Long primaryKey)
  {
    if (primaryKey==null)
    {
      throw new IllegalArgumentException("primaryKey is null");
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Long> ret=new ArrayList<Long>();
      Long act=null;
      ResultSet rs=null;
      try
      {
        _psGetFromPlace.setLong(1,primaryKey.longValue());
        rs=_psGetFromPlace.executeQuery();
        while (rs.next())
        {
          act=Long.valueOf(rs.getLong(1));
          ret.add(act);
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGetFromPlace);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  @Override
  public List<Long> getRelatedObjectIDs(String relationName, Long primaryKey)
  {
    List<Long> ret=null;
    if (relationName.equals(Act.MAIN_ACTS_RELATION))
    {
      ret=getMainFromPerson(primaryKey);
    }
    else if (relationName.equals(Act.OTHER_ACTS_RELATION))
    {
      ret=getOtherFromPerson(primaryKey);
    }
    else if (relationName.equals(Act.ACTS_FROM_PLACE))
    {
      ret=getActsFromPlace(primaryKey);
    }
    return ret;
  }

  @Override
  public void create(Act act)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        int n=1;
        Long key=act.getPrimaryKey();
        if (key==null)
        {
          _psInsert.setNull(n,Types.INTEGER);
        }
        else
        {
          _psInsert.setLong(n,key.longValue());
        }
        n++;
        setProxy(act.getActTypeProxy(),n);
        n++;
        Long actDate=act.getDate();
        if (actDate!=null)
        {
          _psInsert.setDate(n,new java.sql.Date(actDate.longValue()));
        }
        else
        {
          _psInsert.setNull(n,Types.DATE);
        }
        n++;
        setProxy(act.getPlaceProxy(),n);
        n++;
        setProxy(act.getP1Proxy(),n);
        n++;
        setProxy(act.getP2Proxy(),n);
        n++;
        setProxy(act.getTextProxy(),n);
        n++;
        _psInsert.setString(n,act.getPath());
        n++;
        _psInsert.setInt(n,act.getNbFiles());
        n++;
        _psInsert.setBoolean(n,act.getTraite());
        n++;
        _psInsert.setString(n,act.getComment());
        _psInsert.executeUpdate();
        if (key==null)
        {
          if (usesHSQLDB())
          {
            Long primaryKey=JDBCTools.getPrimaryKey(connection,1);
            act.setPrimaryKey(primaryKey);
          }
          else
          {
            ResultSet rs=_psInsert.getGeneratedKeys();
            if (rs.next())
            {
              long primaryKey=rs.getLong(1);
              act.setPrimaryKey(Long.valueOf(primaryKey));
            }
          }
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psInsert);
      }
    }
  }

  private void setProxy(DataProxy<? extends Identifiable<Long>> proxy, int n) throws SQLException
  {
    if ((proxy!=null) && (proxy.getPrimaryKey()!=null))
    {
      _psInsert.setLong(n,proxy.getPrimaryKey().longValue());
    }
    else
    {
      _psInsert.setNull(n,Types.INTEGER);
    }
  }

  @Override
  public void update(Act act)
  {
    if (act==null)
    {
      throw new IllegalArgumentException("act==null");
    }
    Long key=act.getPrimaryKey();
    if (key==null)
    {
      throw new IllegalArgumentException("key==null");
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        int n=1;
        DataProxy<ActType> actType=act.getActTypeProxy();
        if ((actType!=null) && (actType.getPrimaryKey()!=null))
        {
          _psUpdate.setLong(n,actType.getPrimaryKey().longValue());
        }
        else
        {
          _psUpdate.setNull(n,Types.INTEGER);
        }
        n++;
        Long actDate=act.getDate();
        if (actDate!=null) _psUpdate.setDate(n,new java.sql.Date(actDate.longValue()));
        else _psUpdate.setNull(n,Types.DATE);
        n++;
        DataProxy<Place> place=act.getPlaceProxy();
        if ((place!=null) && (place.getPrimaryKey()!=null))
        {
          _psUpdate.setLong(n,place.getPrimaryKey().longValue());
        }
        else
        {
          _psUpdate.setNull(n,Types.INTEGER);
        }
        n++;
        DataProxy<Person> p1Proxy=act.getP1Proxy();
        if ((p1Proxy!=null) && (p1Proxy.getPrimaryKey()!=null))
        {
          _psUpdate.setLong(n,p1Proxy.getPrimaryKey().longValue());
        }
        else
        {
          _psUpdate.setNull(n,Types.INTEGER);
        }
        n++;
        DataProxy<Person> p2Proxy=act.getP2Proxy();
        if ((p2Proxy!=null) && (p2Proxy.getPrimaryKey()!=null))
        {
          _psUpdate.setLong(n,p2Proxy.getPrimaryKey().longValue());
        }
        else
        {
          _psUpdate.setNull(n,Types.INTEGER);
        }
        n++;
        DataProxy<ActText> textProxy=act.getTextProxy();
        if ((textProxy!=null) && (textProxy.getPrimaryKey()!=null))
        {
          _psUpdate.setLong(n,textProxy.getPrimaryKey().longValue());
        }
        else
        {
          _psUpdate.setNull(n,Types.INTEGER);
        }
        n++;
        _psUpdate.setString(n,act.getPath());
        n++;
        _psUpdate.setInt(n,act.getNbFiles());
        n++;
        _psUpdate.setBoolean(n,act.getTraite());
        n++;
        _psUpdate.setString(n,act.getComment());
        n++;
        _psUpdate.setLong(n,key.longValue());
        _psUpdate.executeUpdate();
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psInsert);
      }
    }
  }
}
