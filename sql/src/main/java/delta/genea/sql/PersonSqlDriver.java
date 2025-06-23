package delta.genea.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
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
import delta.common.utils.jdbc.PreparedStatementWrapper;
import delta.genea.data.HomeForPerson;
import delta.genea.data.OccupationForPerson;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.Sex;
import delta.genea.data.TitleForPerson;

/**
 * SQL driver for persons.
 * @author DAM
 */
public class PersonSqlDriver extends ObjectSqlDriver<Person>
{
  private static final Logger LOGGER=LoggerFactory.getLogger(PersonSqlDriver.class);

  private static final String SELECT="SELECT ";

  private PreparedStatement _psGetByPrimaryKey;
  private PreparedStatement _psGetAll;
  private PreparedStatement _psInsert;
  private PreparedStatement _psCount;
  private PreparedStatement _psChildren;
  private PreparedStatement _psGodChildren;
  private PreparedStatement _psPatronyme;
  private PreparedStatement _psGetOccupations;
  private PreparedStatement _psGetHomes;
  private PreparedStatement _psGetTitles;
  private PreparedStatement _psInsertOccupation;
  private PreparedStatement _psInsertHome;
  private PreparedStatement _psPartialGetByPrimaryKey;
  private ObjectsSource _mainDataSource;

  private static final String[] DELETE_REQUESTS={
    "DELETE FROM profession WHERE cle_personne = ?",
    "DELETE FROM residence WHERE cle_personne = ?",
    "UPDATE mariage SET cle_homme=null WHERE cle_homme = ?",
    "UPDATE mariage SET cle_femme=null WHERE cle_femme = ?",
    "DELETE FROM personne_acte WHERE cle_personne = ?",
    "UPDATE personne_acte SET ref_lien=null WHERE ref_lien = ?",
    "UPDATE acte SET cle_p1=null WHERE cle_p1 = ?",
    "UPDATE acte SET cle_p2=null WHERE cle_p2 = ?",
    "UPDATE personne SET cle_pere=null WHERE cle_pere = ?",
    "UPDATE personne SET cle_mere=null WHERE cle_mere = ?",
    "UPDATE personne SET cle_parrain=null WHERE cle_parrain = ?",
    "UPDATE personne SET cle_marraine=null WHERE cle_marraine = ?",
    "DELETE FROM personne WHERE cle = ?",
  };

  /**
   * Constructor.
   * @param mainDataSource Main data source.
   */
  public PersonSqlDriver(ObjectsSource mainDataSource)
  {
    _mainDataSource=mainDataSource;
  }

  @Override
  protected void buildPreparedStatements(Connection newConnection)
  {
    try
    {
      String fields="cle,nom,prenoms,sexe,signature,date_naissance,infos_n,cle_ln,date_deces,infos_d,cle_ld,cle_pere,cle_mere,cle_parrain,cle_marraine,commentaire,sans_descendance";
      String sql=SELECT+fields+" FROM personne WHERE cle = ?";
      _psGetByPrimaryKey=newConnection.prepareStatement(sql);
      _psGetByPrimaryKey=PreparedStatementWrapper.buildProxy(_psGetByPrimaryKey);
      sql=SELECT+fields+" FROM personne";
      _psGetAll=newConnection.prepareStatement(sql);
      sql="INSERT INTO personne ("+fields
          +") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      if (usesHSQLDB())
      {
        _psInsert=newConnection.prepareStatement(sql);
      }
      else
      {
        _psInsert=newConnection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
      }
      sql="SELECT COUNT(*) FROM personne WHERE cle = ?";
      _psCount=newConnection.prepareStatement(sql);
      // Added date_naissance to field list for HSQLDB compatibility
      sql="SELECT DISTINCT cle,date_naissance FROM personne WHERE cle_pere = ? OR cle_mere = ? order by date_naissance";
      _psChildren=newConnection.prepareStatement(sql);
      // Added nom,prenoms to field list for HSQLDB compatibility
      sql="SELECT DISTINCT cle,nom,prenoms FROM personne WHERE nom like ? order by nom,prenoms";
      _psPatronyme=newConnection.prepareStatement(sql);
      // Added date_naissance to field list for HSQLDB compatibility
      sql="SELECT DISTINCT cle,date_naissance FROM personne WHERE cle_parrain = ? OR cle_marraine = ? order by date_naissance";
      _psGodChildren=newConnection.prepareStatement(sql);
      sql="SELECT cle_personne,annee,profession,lieu FROM profession WHERE cle_personne = ? ORDER BY annee,profession";
      _psGetOccupations=newConnection.prepareStatement(sql);
      sql="SELECT cle_personne,annee,lieu,cle_commune FROM residence WHERE cle_personne = ? ORDER BY annee";
      _psGetHomes=newConnection.prepareStatement(sql);
      sql="SELECT cle_personne,annee,titre FROM titre WHERE cle_personne = ? ORDER BY annee";
      _psGetTitles=newConnection.prepareStatement(sql);
      sql="INSERT INTO profession (cle_personne,annee,profession,lieu) VALUES (?,?,?,?)";
      _psInsertOccupation=newConnection.prepareStatement(sql);
      sql="INSERT INTO residence (cle_personne,annee,lieu,cle_commune) VALUES (?,?,?,?)";
      _psInsertHome=newConnection.prepareStatement(sql);
      String partialFields="cle,nom,prenoms,sexe,date_naissance,infos_n,date_deces,infos_d,sans_descendance";
      sql=SELECT+partialFields+" FROM personne WHERE cle = ?";
      _psPartialGetByPrimaryKey=newConnection.prepareStatement(sql);
    }
    catch (SQLException sqlException)
    {
      LOGGER.error("Exception while building prepared statements for class Person",sqlException);
    }
  }

  @Override
  public Person getByPrimaryKey(Long primaryKey)
  {
    if (primaryKey==null)
    {
      return null;
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      Person ret=null;
      ResultSet rs=null;
      try
      {
        LOGGER.debug("LOAD person {}",primaryKey);
        _psGetByPrimaryKey.setLong(1,primaryKey.longValue());
        rs=_psGetByPrimaryKey.executeQuery();
        if (rs.next())
        {
          ret=new Person(primaryKey);
          fillPerson(ret,rs);
          List<OccupationForPerson> occupations=loadOccupations(primaryKey);
          ret.setOccupations(occupations);
          List<HomeForPerson> homes=loadHomes(primaryKey);
          ret.setHomes(homes);
          List<TitleForPerson> titles=loadTitles(primaryKey);
          ret.setTitles(titles);
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
  public Person getPartialByPrimaryKey(Long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      Person ret=null;
      ResultSet rs=null;
      try
      {
        LOGGER.debug("LOAD PARTIAL person {}",primaryKey);
        _psPartialGetByPrimaryKey.setLong(1,primaryKey.longValue());
        rs=_psPartialGetByPrimaryKey.executeQuery();
        if (rs.next())
        {
          ret=new Person(primaryKey);
          fillPartialPerson(ret,rs);
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psPartialGetByPrimaryKey);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  private void fillPerson(Person person, ResultSet rs) throws SQLException
  {
    int n=2;
    person.setLastName(rs.getString(n));
    n++;
    person.setFirstname(rs.getString(n));
    n++;
    person.setSex(Sex.getFromValue(rs.getString(n).charAt(0)));
    n++;
    person.setSignature(rs.getString(n));
    n++;
    person.setBirthDate(rs.getDate(n++),rs.getString(n++));
    DataProxy<Place> birthPlaceProxy=null;
    long birthPlaceKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      birthPlaceProxy=_mainDataSource.buildProxy(Place.class,Long.valueOf(birthPlaceKey));
    }
    person.setBirthPlaceProxy(birthPlaceProxy);
    n++;
    person.setDeathDate(rs.getDate(n++),rs.getString(n++));
    DataProxy<Place> deathPlaceProxy=null;
    long deathPlaceKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      deathPlaceProxy=_mainDataSource.buildProxy(Place.class,Long.valueOf(deathPlaceKey));
    }
    person.setDeathPlaceProxy(deathPlaceProxy);
    n++;
    DataProxy<Person> fatherProxy=null;
    long fatherKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      fatherProxy=getObjectSource().buildProxy(Person.class,Long.valueOf(fatherKey));
    }
    person.setFatherProxy(fatherProxy);
    n++;
    DataProxy<Person> motherProxy=null;
    long motherKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      motherProxy=getObjectSource().buildProxy(Person.class,Long.valueOf(motherKey));
    }
    person.setMotherProxy(motherProxy);
    n++;
    DataProxy<Person> godFatherProxy=null;
    long godFatherKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      godFatherProxy=getObjectSource().buildProxy(Person.class,Long.valueOf(godFatherKey));
    }
    person.setGodFatherProxy(godFatherProxy);
    n++;
    DataProxy<Person> godMotherProxy=null;
    long godMotherKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      godMotherProxy=getObjectSource().buildProxy(Person.class,Long.valueOf(godMotherKey));
    }
    person.setGodMotherProxy(godMotherProxy);
    n++;
    person.setComments(rs.getString(n));
    n++;
    person.setNoDescendants(rs.getBoolean(n));
  }

  private void fillPartialPerson(Person person, ResultSet rs)
      throws SQLException
  {
    int n=2;
    person.setLastName(rs.getString(n));
    n++;
    person.setFirstname(rs.getString(n));
    n++;
    person.setSex(Sex.getFromValue(rs.getString(n).charAt(0)));
    n++;
    person.setBirthDate(rs.getDate(n++),rs.getString(n++));
    person.setDeathDate(rs.getDate(n++),rs.getString(n++));
    person.setNoDescendants(rs.getBoolean(n));
  }

  private List<OccupationForPerson> loadOccupations(Long primaryKey)
  {
    if (primaryKey==null)
    {
      throw new IllegalArgumentException("loadOccupations: primaryKey is null");
    }
    List<OccupationForPerson> ret=null;
    Connection connection=getConnection();
    synchronized (connection)
    {
      ResultSet rs=null;
      try
      {
        _psGetOccupations.setLong(1,primaryKey.longValue());
        rs=_psGetOccupations.executeQuery();
        while (rs.next())
        {
          OccupationForPerson occupation=new OccupationForPerson();
          int n=2;
          occupation.setYear(rs.getInt(n));
          n++;
          occupation.setOccupation(rs.getString(n));
          n++;
          long placeKey=rs.getLong(n);
          if (!rs.wasNull())
          {
            occupation.setPlaceProxy(_mainDataSource.buildProxy(Place.class,Long.valueOf(placeKey)));
          }
          n++;
          if (ret==null)
          {
            ret=new ArrayList<OccupationForPerson>();
          }
          ret.add(occupation);
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGetOccupations);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  private List<HomeForPerson> loadHomes(Long primaryKey)
  {
    if (primaryKey==null)
    {
      throw new IllegalArgumentException("loadHomes: primaryKey is null");
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      List<HomeForPerson> ret=null;
      ResultSet rs=null;
      try
      {
        _psGetHomes.setLong(1,primaryKey.longValue());
        rs=_psGetHomes.executeQuery();
        while (rs.next())
        {
          HomeForPerson home=new HomeForPerson();
          int n=2;
          home.setYear(rs.getInt(n));
          n++;
          home.setPlaceDetails(rs.getString(n));
          n++;
          long placeKey=rs.getLong(n);
          if (!rs.wasNull())
          {
            home.setPlaceProxy(_mainDataSource.buildProxy(Place.class,Long.valueOf(placeKey)));
          }
          n++;
          if (ret==null)
          {
            ret=new ArrayList<HomeForPerson>();
          }
          ret.add(home);
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGetHomes);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  private List<TitleForPerson> loadTitles(Long primaryKey)
  {
    if (primaryKey==null)
    {
      throw new IllegalArgumentException("loadTitles: primaryKey is null");
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      List<TitleForPerson> ret=null;
      ResultSet rs=null;
      try
      {
        _psGetTitles.setLong(1,primaryKey.longValue());
        rs=_psGetTitles.executeQuery();
        while (rs.next())
        {
          TitleForPerson title=new TitleForPerson();
          int n=2;
          int year=rs.getInt(n);
          if (!rs.wasNull())
          {
            title.setYear(Integer.valueOf(year));
          }
          n++;
          title.setTitle(rs.getString(n));
          if (ret==null)
          {
            ret=new ArrayList<TitleForPerson>();
          }
          ret.add(title);
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGetTitles);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  @Override
  public List<Person> getAll()
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Person> ret=new ArrayList<Person>();
      Person person=null;
      ResultSet rs=null;
      try
      {
        rs=_psGetAll.executeQuery();
        while (rs.next())
        {
          long personKey=rs.getLong(1);
          Long primaryKey=Long.valueOf(personKey);
          person=new Person(primaryKey);
          fillPerson(person,rs);
          List<OccupationForPerson> occupations=loadOccupations(primaryKey);
          person.setOccupations(occupations);
          List<HomeForPerson> homes=loadHomes(primaryKey);
          person.setHomes(homes);
          List<TitleForPerson> titles=loadTitles(primaryKey);
          person.setTitles(titles);
          ret.add(person);
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
   * Indicates if the person identified by <code>primaryKey</code>
   * exists or not.
   * @param primaryKey Identifier for the targeted person.
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
   * Get the identifiers of the children of the person identified
   * by <code>primaryKey</code>.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of person identifiers.
   */
  public List<Long> getChildren(long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Long> ret=new ArrayList<Long>();
      ResultSet rs=null;
      try
      {
        LOGGER.debug("GET childrens FOR {}",Long.valueOf(primaryKey));
        _psChildren.setLong(1,primaryKey);
        _psChildren.setLong(2,primaryKey);
        rs=_psChildren.executeQuery();
        while (rs.next())
        {
          ret.add(Long.valueOf(rs.getLong(1)));
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psChildren);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  /**
   * Get the identifiers of the god children of the person identified
   * by <code>primaryKey</code>.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of person identifiers.
   */
  public List<Long> getGodChildren(long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Long> ret=new ArrayList<Long>();
      ResultSet rs=null;
      try
      {
        LOGGER.debug("GET god childrens FOR {}",Long.valueOf(primaryKey));
        _psGodChildren.setLong(1,primaryKey);
        _psGodChildren.setLong(2,primaryKey);
        rs=_psGodChildren.executeQuery();
        while (rs.next())
        {
          ret.add(Long.valueOf(rs.getLong(1)));
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGodChildren);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  /**
   * Get the identifiers of the persons whose last name is <code>name</code>.
   * @param name Last name of the persons to search.
   * @return A list of person identifiers.
   */
  public List<Long> getByName(String name)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Long> ret=new ArrayList<Long>();
      ResultSet rs=null;
      try
      {
        LOGGER.debug("GET BY NAME {}",name);
        _psPatronyme.setString(1,name);
        rs=_psPatronyme.executeQuery();
        while (rs.next())
        {
          ret.add(Long.valueOf(rs.getLong(1)));
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psPatronyme);
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
    if (primaryKey==null)
    {
      throw new IllegalArgumentException("getRelatedObjectIDs: primaryKey is null");
    }
    List<Long> ret=null;
    if (relationName.equals(Person.CHILDREN_RELATION))
    {
      ret=getChildren(primaryKey.longValue());
    }
    else if (relationName.equals(Person.GOD_CHILDREN_RELATION))
    {
      ret=getGodChildren(primaryKey.longValue());
    }
    return ret;
  }

  @Override
  public List<Long> getObjectIDsSet(String setID, Object[] parameters)
  {
    List<Long> ret=new ArrayList<Long>();
    if (setID.equals(Person.NAME_SET))
    {
      String name=(String)parameters[0];
      ret=getByName(name);
    }
    return ret;
  }

  @Override
  public void create(Person person)
  {
    if (person==null)
    {
      throw new IllegalArgumentException("create: person==null");
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        int n=1;
        Long key=person.getPrimaryKey();
        if (key==null)
        {
          _psInsert.setNull(n,Types.INTEGER);
        }
        else
        {
          _psInsert.setLong(n,key.longValue());
        }
        n++;
        String lastName=person.getLastName();
        if (lastName.length()>32)
        {
          lastName=lastName.substring(0,32);
        }
        _psInsert.setString(n,lastName);
        n++;
        _psInsert.setString(n,person.getFirstname());
        n++;
        _psInsert.setString(n,String.valueOf(person.getSex().getValue()));
        n++;
        _psInsert.setString(n,person.getSignature());
        n++;
        setDate(person.getBirthDate(),n);
        n++;
        _psInsert.setString(n,person.getBirthInfos());
        n++;
        setProxy(person.getBirthPlaceProxy(),n);
        n++;
        setDate(person.getDeathDate(),n);
        n++;
        _psInsert.setString(n,person.getDeathInfos());
        n++;
        setProxy(person.getDeathPlaceProxy(),n);
        n++;
        setProxy(person.getFatherProxy(),n);
        n++;
        setProxy(person.getMotherProxy(),n);
        n++;
        setProxy(person.getGodFatherProxy(),n);
        n++;
        setProxy(person.getGodMotherProxy(),n);
        n++;
        _psInsert.setString(n,person.getComments());
        n++;
        _psInsert.setBoolean(n,person.getNoDescendants());
        _psInsert.executeUpdate();
        if (key==null)
        {
          if (usesHSQLDB())
          {
            Long primaryKey=JDBCTools.getPrimaryKey(connection,1);
            person.setPrimaryKey(primaryKey);
          }
          else
          {
            ResultSet rs=_psInsert.getGeneratedKeys();
            if (rs.next())
            {
              long primaryKey=rs.getLong(1);
              person.setPrimaryKey((primaryKey!=0)?Long.valueOf(primaryKey):null);
            }
          }
        }
        // Occupations
        handleOccupations(person);
        // Homes
        handleHomes(person);
        // Titles
        // TODO
      }
      catch (SQLWarning sqlWarning)
      {
        LOGGER.warn("",sqlWarning);
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

  private void setDate(Long date, int n) throws SQLException
  {
    if (date!=null)
    {
      _psInsert.setDate(n,new java.sql.Date(date.longValue()));
    }
    else
    {
      _psInsert.setNull(n,Types.DATE);
    }
  }

  private void handleOccupations(Person person)
  {
    List<OccupationForPerson> occupations=person.getOccupations();
    if ((occupations!=null)&&(!occupations.isEmpty()))
    {
      int nb=occupations.size();
      for (int i=0; i<nb; i++)
      {
        createOccupation(person,occupations.get(i));
      }
    }
  }

  private void handleHomes(Person person)
  {
    List<HomeForPerson> homes=person.getHomes();
    if ((homes!=null)&&(!homes.isEmpty()))
    {
      int nb=homes.size();
      for (int i=0; i<nb; i++)
      {
        createHome(person,homes.get(i));
      }
    }
  }

  /**
   * Add a new occupation for a person.
   * @param person Targeted person.
   * @param occupation Occupation to add.
   */
  private void createOccupation(Person person, OccupationForPerson occupation)
  {
    if (person==null)
    {
      throw new IllegalArgumentException("createOccupation person==null");
    }
    Long key=person.getPrimaryKey();
    if (key==null)
    {
      throw new IllegalArgumentException("createOccupation key==null");
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        int n=1;
        _psInsertOccupation.setLong(n,key.longValue());
        n++;
        _psInsertOccupation.setInt(n,occupation.getYear());
        n++;
        _psInsertOccupation.setString(n,occupation.getOccupation());
        n++;
        DataProxy<Place> placeProxy=occupation.getPlaceProxy();
        if ((placeProxy!=null) && (placeProxy.getPrimaryKey()!=null))
        {
          _psInsertOccupation.setLong(n,placeProxy.getPrimaryKey().longValue());
        }
        else
        {
          _psInsertOccupation.setNull(n,Types.INTEGER);
        }
        _psInsertOccupation.executeUpdate();
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psInsertOccupation);
      }
    }
  }

  /**
   * Add a new home for a person.
   * @param person Targeted person.
   * @param home Home to add.
   */
  public void createHome(Person person, HomeForPerson home)
  {
    if (person==null)
    {
      throw new IllegalArgumentException("createHome: person==null");
    }
    Long key=person.getPrimaryKey();
    if (key==null)
    {
      throw new IllegalArgumentException("createHome: key==null");
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        int n=1;
        _psInsertHome.setLong(n,key.longValue());
        n++;
        _psInsertHome.setInt(n,home.getYear());
        n++;
        _psInsertHome.setString(n,home.getPlaceDetails());
        n++;
        DataProxy<Place> placeProxy=home.getPlaceProxy();
        if ((placeProxy!=null) && (placeProxy.getPrimaryKey()!=null))
        {
          _psInsertHome.setLong(n,placeProxy.getPrimaryKey().longValue());
        }
        else
        {
          _psInsertHome.setNull(n,Types.INTEGER);
        }
        _psInsertHome.executeUpdate();
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psInsertHome);
      }
    }
  }

  @Override
  public void delete(Long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        connection.setAutoCommit(false);
        String keyStr=String.valueOf(primaryKey);
        for(int i=0;i<DELETE_REQUESTS.length;i++)
        {
          String sql=DELETE_REQUESTS[i];
          sql=sql.replace("?",keyStr);
          Statement statement=null;
          try
          {
            statement=connection.createStatement();
            int nbRows=statement.executeUpdate(sql);
            if (nbRows!=0)
            {
              LOGGER.debug("Deleted {} row(s) with {}",Integer.valueOf(nbRows),sql);
            }
          }
          finally
          {
            CleanupManager.cleanup(statement);
          }
        }
        connection.commit();
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        try
        {
          connection.rollback();
        }
        catch(Exception e)
        {
          LOGGER.error("Error in rollback!",e);
        }
      }
    }
  }
}
