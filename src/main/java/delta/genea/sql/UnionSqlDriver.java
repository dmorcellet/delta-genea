package delta.genea.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.sql.ObjectSqlDriver;
import delta.common.utils.jdbc.CleanupManager;
import delta.common.utils.jdbc.JDBCTools;
import delta.genea.data.Act;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.Union;
import delta.genea.data.sources.GeneaDataSource;

/**
 * SQL driver for unions.
 * @author DAM
 */
public class UnionSqlDriver extends ObjectSqlDriver<Union>
{
  private static final Logger LOGGER=Logger.getLogger(UnionSqlDriver.class);

  private PreparedStatement _psGetByPrimaryKey;
  private PreparedStatement _psGetAll;
  private PreparedStatement _psInsert;
  private PreparedStatement _psGetFromMan;
  private PreparedStatement _psGetFromWoman;
  private PreparedStatement _psGetByNameAndPlaceH;
  private PreparedStatement _psGetByNameAndPlaceF;

  private GeneaDataSource _mainDataSource;

  /**
   * Constructor.
   * @param mainDataSource Main data source.
   */
  public UnionSqlDriver(GeneaDataSource mainDataSource)
  {
    _mainDataSource=mainDataSource;
  }

  @Override
  protected void buildPreparedStatements(Connection newConnection)
  {
    try
    {
      String fields="cle,cle_homme,cle_femme,date_debut,infos_m,cle_lm,cle_contrat,commentaire";
      // Select
      String sql="SELECT "+fields+" FROM mariage WHERE cle = ?";
      _psGetByPrimaryKey=newConnection.prepareStatement(sql);
      // Select ALL
      sql="SELECT "+fields+" FROM mariage";
      _psGetAll=newConnection.prepareStatement(sql);
      // Insert
      sql="INSERT INTO mariage ("+fields+") VALUES (?,?,?,?,?,?,?,?)";
      if (usesHSQLDB())
      {
        _psInsert=newConnection.prepareStatement(sql);
      }
      else
      {
        _psInsert=newConnection.prepareStatement(sql,
            Statement.RETURN_GENERATED_KEYS);
      }
      // Get from person
      sql="SELECT cle FROM mariage WHERE cle_homme = ? ORDER BY ordre_homme";
      _psGetFromMan=newConnection.prepareStatement(sql);
      sql="SELECT cle FROM mariage WHERE cle_femme = ? ORDER BY ordre_femme";
      _psGetFromWoman=newConnection.prepareStatement(sql);
      // Get unions with involved name and place (men)
      sql="SELECT m.cle,m.date_debut,h.nom from mariage m,personne h where m.cle_homme=h.cle and m.cle_lm=? and h.nom like ?";
      _psGetByNameAndPlaceH=newConnection.prepareStatement(sql);
      // Get unions with involved name and place (women)
      sql="SELECT m.cle,m.date_debut,f.nom from mariage m,personne f where m.cle_femme=f.cle and m.cle_lm=? and f.nom like ?";
      _psGetByNameAndPlaceF=newConnection.prepareStatement(sql);
    }
    catch (SQLException sqlException)
    {
      LOGGER.error("Exception while building prepared statements for class Union",sqlException);
    }
  }

  @Override
  public Union getByPrimaryKey(Long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      Union ret=null;
      ResultSet rs=null;
      try
      {
        _psGetByPrimaryKey.setLong(1,primaryKey.longValue());
        rs=_psGetByPrimaryKey.executeQuery();
        if (rs.next())
        {
          ret=new Union(primaryKey);
          fillUnion(ret,rs);
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

  private void fillUnion(Union union, ResultSet rs) throws SQLException
  {
    int n=2;
    DataProxy<Person> manProxy=null;
    long manKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      manProxy=_mainDataSource.buildProxy(Person.class,Long.valueOf(manKey));
    }
    union.setManProxy(manProxy);
    n++;
    DataProxy<Person> womanProxy=null;
    long womanKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      womanProxy=_mainDataSource.buildProxy(Person.class,Long.valueOf(womanKey));
    }
    union.setWomanProxy(womanProxy);
    n++;
    union.setDate(rs.getDate(n++),rs.getString(n++));
    DataProxy<Place> placeProxy=null;
    long placeKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      placeProxy=_mainDataSource.buildProxy(Place.class,Long.valueOf(placeKey));
    }
    union.setPlaceProxy(placeProxy);
    n++;
    DataProxy<Act> contractProxy=null;
    long contractKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      contractProxy=_mainDataSource.buildProxy(Act.class,Long.valueOf(contractKey));
    }
    union.setWeddingContractProxy(contractProxy);
    n++;
    union.setComments(rs.getString(n));
    n++;
  }

  @Override
  public List<Union> getAll()
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Union> ret=new ArrayList<Union>();
      Union union=null;
      ResultSet rs=null;
      try
      {
        rs=_psGetAll.executeQuery();
        while (rs.next())
        {
          Long primaryKey=null;
          long key=rs.getLong(1);
          if (!rs.wasNull())
          {
            primaryKey=Long.valueOf(key);
          }
          union=new Union(primaryKey);
          fillUnion(union,rs);
          ret.add(union);
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

  /**
   * Get the identifiers of unions where the person identified
   * by <code>primaryKey</code> do appear.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of union identifiers.
   */
  public List<Long> getFromPerson(Long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Long> ret=new ArrayList<Long>();
      List<Long> retMan=getFromManOrWoman(_psGetFromMan,primaryKey);
      ret.addAll(retMan);
      if (ret.size()==0)
      {
        List<Long> retWoman=getFromManOrWoman(_psGetFromWoman,primaryKey);
        ret.addAll(retWoman);
      }
      return ret;
    }
  }

  private List<Long> getFromManOrWoman(PreparedStatement ps, Long primaryKey)
  {
    ArrayList<Long> ret=new ArrayList<Long>();
    Long union=null;
    ResultSet rs=null;
    try
    {
      ps.setLong(1,primaryKey.longValue());
      rs=ps.executeQuery();
      while (rs.next())
      {
        union=Long.valueOf(rs.getLong(1));
        ret.add(union);
      }
    }
    catch (SQLException sqlException)
    {
      LOGGER.error("",sqlException);
      CleanupManager.cleanup(ps);
    }
    finally
    {
      CleanupManager.cleanup(rs);
    }
    return ret;
  }

  @Override
  public List<Long> getRelatedObjectIDs(String relationName, Long primaryKey)
  {
    List<Long> ret=new ArrayList<Long>();
    if (relationName.equals(Union.UNIONS_RELATION))
    {
      ret=getFromPerson(primaryKey);
    }
    return ret;
  }

  @Override
  public List<Long> getObjectIDsSet(String setID, Object[] parameters)
  {
    List<Long> ret=new ArrayList<Long>();
    if (setID.equals(Union.NAME_AND_PLACE_SET))
    {
      String name=(String)parameters[0];
      Long placeKey=(Long)parameters[1];
      ret=getByNameAndPlace(name,placeKey);
    }
    return ret;
  }

  /**
   * Get the identifiers of unions where the last name of the man or the woman
   * is <code>name</code> and the place is <code>placeKey</code>.
   * @param name Last name of man or woman (<code>null</code> for no such filter).
   * @param placeKey Identifier of the targeted place (<code>null</code> for no such filter).
   * @return A list of union identifiers.
   */
  public ArrayList<Long> getByNameAndPlace(String name, Long placeKey)
  {
    ArrayList<Long> ret=new ArrayList<Long>();
    if ((name==null) || (name.length()==0))
    {
      name="%";
    }
    Set<Long> set=new HashSet<Long>();
    Set<Long> set1=getByNameAndPlace(name,placeKey,_psGetByNameAndPlaceH);
    set.addAll(set1);
    Set<Long> set2=getByNameAndPlace(name,placeKey,_psGetByNameAndPlaceF);
    set.addAll(set2);
    ret.addAll(set);
    return ret;
  }

  private Set<Long> getByNameAndPlace(String name, Long placeKey, PreparedStatement ps)
  {
    Set<Long> ret=new HashSet<Long>();
    Connection connection=getConnection();
    synchronized (connection)
    {
      ResultSet rs=null;
      try
      {
        ps.setLong(1,placeKey.longValue());
        ps.setString(2,name);
        rs=ps.executeQuery();
        while (rs.next())
        {
          ret.add(Long.valueOf(rs.getLong(1)));
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(ps);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  @Override
  public void create(Union union)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        int n=1;
        Long key=union.getPrimaryKey();
        if (key==null)
        {
          _psInsert.setNull(n,Types.INTEGER);
        }
        else
        {
          _psInsert.setLong(n,key.longValue());
        }
        n++;
        DataProxy<Person> manPerson=union.getManProxy();
        if (manPerson!=null)
        {
          _psInsert.setLong(n,manPerson.getPrimaryKey().longValue());
        }
        else
        {
          _psInsert.setNull(n,Types.INTEGER);
        }
        n++;
        DataProxy<Person> womanPerson=union.getWomanProxy();
        if (womanPerson!=null)
        {
          _psInsert.setLong(n,womanPerson.getPrimaryKey().longValue());
        }
        else
        {
          _psInsert.setNull(n,Types.INTEGER);
        }
        n++;
        Long date=union.getDate();
        if (date!=null)
        {
          _psInsert.setDate(n,new java.sql.Date(date.longValue()));
        }
        else
        {
          _psInsert.setNull(n,Types.DATE);
        }
        n++;
        _psInsert.setString(n,union.getInfos());
        n++;
        DataProxy<Place> placeProxy=union.getPlaceProxy();
        if ((placeProxy!=null) && (placeProxy.getPrimaryKey()!=null))
        {
          long placeKey=placeProxy.getPrimaryKey().longValue();
          _psInsert.setLong(n,placeKey);
        }
        else
        {
          _psInsert.setNull(n,Types.INTEGER);
        }
        n++;
        DataProxy<Act> weddingContractProxy=union.getWeddingContractProxy();
        if ((weddingContractProxy!=null) && (weddingContractProxy.getPrimaryKey()!=null))
        {
          long weddingContractKey=weddingContractProxy.getPrimaryKey().longValue();
          _psInsert.setLong(n,weddingContractKey);
        }
        else
        {
          _psInsert.setNull(n,Types.INTEGER);
        }
        n++;
        _psInsert.setString(n,union.getComments());
        n++;
        _psInsert.executeUpdate();
        if (key==null)
        {
          if (usesHSQLDB())
          {
            Long primaryKey=JDBCTools.getPrimaryKey(connection,1);
            union.setPrimaryKey(primaryKey);
          }
          else
          {
            ResultSet rs=_psInsert.getGeneratedKeys();
            if (rs.next())
            {
              long primaryKey=rs.getLong(1);
              union.setPrimaryKey(Long.valueOf(primaryKey));
            }
          }
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGetByPrimaryKey);
      }
    }
  }
}
