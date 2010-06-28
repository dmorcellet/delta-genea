package delta.genea.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectSource;
import delta.common.framework.objects.sql.ObjectSqlDriver;
import delta.common.utils.jdbc.CleanupManager;
import delta.common.utils.jdbc.JDBCTools;
import delta.genea.data.Act;
import delta.genea.data.ActText;
import delta.genea.data.ActType;
import delta.genea.data.Person;
import delta.genea.data.PersonInAct;
import delta.genea.data.Place;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.utils.GeneaLoggers;

/**
 * SQL driver for acts.
 * @author DAM
 */
public class ActSqlDriver extends ObjectSqlDriver<Act>
{
  private static final Logger _logger=GeneaLoggers.getGeneaSqlLogger();

  private PreparedStatement _psGetByPrimaryKey;
  private PreparedStatement _psInsert;
  private PreparedStatement _psUpdate;
  private PreparedStatement _psCount;
  private PreparedStatement _psGetMainFromPerson;
  private PreparedStatement _psGetOtherFromPerson;
  private PreparedStatement _psGetPersonInAct;
  private PreparedStatement _psGetFromPlace;
  private GeneaDataSource _mainDataSource;

  ActSqlDriver(GeneaDataSource mainDataSource)
  {
    _mainDataSource=mainDataSource;
  }

  @Override
  protected void buildPreparedStatements(Connection newConnection)
  {
    try
    {
      String fields="cle_acte,type_acte,date_acte,cle_lieu,cle_p1,cle_p2,cle_texte,chemin,nb_parties,traite,commentaire";
      // Select
      String sql="SELECT "+fields+" FROM acte WHERE cle_acte = ?";
      _psGetByPrimaryKey=newConnection.prepareStatement(sql);
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
      sql="UPDATE acte SET type_acte=?,date_acte=?,cle_lieu=?,cle_p1=?,cle_p2=?,cle_texte=?,chemin=?,nb_parties=?,traite=?,commentaire=? WHERE cle_acte=?";
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
      sql="SELECT cle_acte FROM acte WHERE cle_lieu = ? order by type_acte, date_acte";
      _psGetFromPlace=newConnection.prepareStatement(sql);
    }
    catch (SQLException sqlException)
    {
      _logger.error("Exception while building prepared statements for class Act",sqlException);
    }
  }

  @Override
  public Act getByPrimaryKey(long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      Act ret=null;
      ResultSet rs=null;
      try
      {
        _psGetByPrimaryKey.setLong(1,primaryKey);
        rs=_psGetByPrimaryKey.executeQuery();
        if (rs.next())
        {
          ret=new Act(primaryKey,_mainDataSource.getActDataSource());
          fillAct(ret,rs);
          List<PersonInAct> persons=loadPersonsInAct(primaryKey);
          ret.setPersonsInAct(persons);
        }
      }
      catch (SQLException sqlException)
      {
        _logger.error("",sqlException);
        CleanupManager.cleanup(_psGetByPrimaryKey);
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
    act.setActType(ActType.getFromValue(rs.getString(n).charAt(0)));
    n++;
    act.setDate(rs.getDate(n));
    n++;
    act.setPlaceProxy(new DataProxy<Place>(rs.getLong(n),_mainDataSource.getPlaceDataSource()));
    n++;
    ObjectSource<Person> personDS=_mainDataSource.getPersonDataSource();
    act.setP1Proxy(new DataProxy<Person>(rs.getLong(n),personDS));
    n++;
    act.setP2Proxy(new DataProxy<Person>(rs.getLong(n),personDS));
    n++;
    ObjectSource<ActText> textDS=_mainDataSource.getTextDataSource();
    act.setTextProxy(new DataProxy<ActText>(rs.getLong(n),textDS));
    n++;
    act.setPath(rs.getString(n));
    n++;
    act.setNbFiles(rs.getInt(n));
    n++;
    act.setTraite(rs.getBoolean(n));
    n++;
    act.setComment(rs.getString(n));
    n++;
  }

  private List<PersonInAct> loadPersonsInAct(long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      List<PersonInAct> ret=null;
      PersonInAct tmp=null;
      ResultSet rs=null;
      try
      {
        _psGetPersonInAct.setLong(1,primaryKey);
        rs=_psGetPersonInAct.executeQuery();
        while (rs.next())
        {
          tmp=new PersonInAct();
          int n=1;
          tmp.setPersonProxy(new DataProxy<Person>(rs.getLong(n),
              _mainDataSource.getPersonDataSource()));
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
            tmp.setLinkRefProxy(new DataProxy<Person>(refLink,_mainDataSource.getPersonDataSource()));
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
        _logger.error("",sqlException);
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
        _logger.error("",sqlException);
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
  public List<Long> getMainFromPerson(long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Long> ret=new ArrayList<Long>();
      Long act=null;
      ResultSet rs=null;
      try
      {
        _psGetMainFromPerson.setLong(1,primaryKey);
        _psGetMainFromPerson.setLong(2,primaryKey);
        rs=_psGetMainFromPerson.executeQuery();
        while (rs.next())
        {
          act=Long.valueOf(rs.getLong(1));
          ret.add(act);
        }
      }
      catch (SQLException sqlException)
      {
        _logger.error("",sqlException);
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
  public List<Long> getOtherFromPerson(long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Long> ret=new ArrayList<Long>();
      Long act=null;
      ResultSet rs=null;
      try
      {
        _psGetOtherFromPerson.setLong(1,primaryKey);
        rs=_psGetOtherFromPerson.executeQuery();
        while (rs.next())
        {
          act=Long.valueOf(rs.getLong(1));
          ret.add(act);
        }
      }
      catch (SQLException sqlException)
      {
        _logger.error("",sqlException);
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
  public List<Long> getActsFromPlace(long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Long> ret=new ArrayList<Long>();
      Long act=null;
      ResultSet rs=null;
      try
      {
        _psGetFromPlace.setLong(1,primaryKey);
        rs=_psGetFromPlace.executeQuery();
        while (rs.next())
        {
          act=Long.valueOf(rs.getLong(1));
          ret.add(act);
        }
      }
      catch (SQLException sqlException)
      {
        _logger.error("",sqlException);
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
  public List<Long> getRelatedObjectIDs(String relationName, long primaryKey)
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
        long key=act.getPrimaryKey();
        if (key==0) _psInsert.setNull(n,Types.INTEGER);
        else _psInsert.setLong(n,key);
        n++;
        _psInsert.setString(n,String.valueOf(act.getActType().getValue()));
        n++;
        Long actDate=act.getDate();
        if (actDate!=null) _psInsert.setDate(n,new java.sql.Date(actDate.longValue()));
        else _psInsert.setNull(n,Types.DATE);
        n++;
        DataProxy<Place> place=act.getPlaceProxy();
        if (place!=null) _psInsert.setLong(n,place.getPrimaryKey());
        else _psInsert.setNull(n,Types.INTEGER);
        n++;
        DataProxy<Person> p1Proxy=act.getP1Proxy();
        if (p1Proxy!=null) _psInsert.setLong(n,p1Proxy.getPrimaryKey());
        else _psInsert.setNull(n,Types.INTEGER);
        n++;
        DataProxy<Person> p2Proxy=act.getP2Proxy();
        if (p2Proxy!=null) _psInsert.setLong(n,p2Proxy.getPrimaryKey());
        else _psInsert.setNull(n,Types.INTEGER);
        n++;
        DataProxy<ActText> textProxy=act.getTextProxy();
        if (textProxy!=null) _psInsert.setLong(n,textProxy.getPrimaryKey());
        else _psInsert.setNull(n,Types.INTEGER);
        n++;
        _psInsert.setString(n,act.getPath());
        n++;
        _psInsert.setInt(n,act.getNbFiles());
        n++;
        _psInsert.setBoolean(n,act.getTraite());
        n++;
        _psInsert.setString(n,act.getComment());
        n++;
        _psInsert.executeUpdate();
        if (usesHSQLDB())
        {
          if (key==0)
          {
            long primaryKey=JDBCTools.getPrimaryKey(connection,1);
            act.setPrimaryKey(primaryKey);
          }
        }
        else
        {
          ResultSet rs=_psInsert.getGeneratedKeys();
          if (rs.next())
          {
            long primaryKey=rs.getLong(1);
            act.setPrimaryKey(primaryKey);
          }
        }
      }
      catch (SQLException sqlException)
      {
        _logger.error("",sqlException);
        CleanupManager.cleanup(_psInsert);
      }
    }
  }

  @Override
  public void update(Act act)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        int n=1;
        _psUpdate.setString(n,String.valueOf(act.getActType().getValue()));
        n++;
        Long actDate=act.getDate();
        if (actDate!=null) _psUpdate.setDate(n,new java.sql.Date(actDate.longValue()));
        else _psUpdate.setNull(n,Types.DATE);
        n++;
        DataProxy<Place> place=act.getPlaceProxy();
        if (place!=null) _psUpdate.setLong(n,place.getPrimaryKey());
        else _psUpdate.setNull(n,Types.INTEGER);
        n++;
        DataProxy<Person> p1Proxy=act.getP1Proxy();
        if (p1Proxy!=null) _psUpdate.setLong(n,p1Proxy.getPrimaryKey());
        else _psUpdate.setNull(n,Types.INTEGER);
        n++;
        DataProxy<Person> p2Proxy=act.getP2Proxy();
        if (p2Proxy!=null) _psUpdate.setLong(n,p2Proxy.getPrimaryKey());
        else _psUpdate.setNull(n,Types.INTEGER);
        n++;
        DataProxy<ActText> textProxy=act.getTextProxy();
        if (textProxy!=null) _psUpdate.setLong(n,textProxy.getPrimaryKey());
        else _psUpdate.setNull(n,Types.INTEGER);
        n++;
        _psUpdate.setString(n,act.getPath());
        n++;
        _psUpdate.setInt(n,act.getNbFiles());
        n++;
        _psUpdate.setBoolean(n,act.getTraite());
        n++;
        _psUpdate.setString(n,act.getComment());
        n++;
        long key=act.getPrimaryKey();
        _psUpdate.setLong(n,key);
        n++;
        _psUpdate.executeUpdate();
      }
      catch (SQLException sqlException)
      {
        _logger.error("",sqlException);
        CleanupManager.cleanup(_psInsert);
      }
    }
  }
}
