package delta.genea.xml;

/**
 * Constants for tags and attribute names used in the
 * genea XML documents.
 * @author DAM
 */
public class GeneaXMLConstants
{
  /**
   * Act type, place: attribute 'name'.
   */
  public static final String NAME_ATTR="name";
  /**
   * Act, union: attribute 'date'.
   */
  public static final String DATE_ATTR="date";
  /**
   * Act, union: attribute 'dateInfos'.
   */
  public static final String DATE_INFOS_ATTR="dateInfos";
  /**
   * Act, union: attribute 'placeId'.
   */
  public static final String PLACE_ATTR="placeId";
  /**
   * Act, union: attribute 'comments'.
   */
  public static final String COMMENTS_ATTR="comments";

  /**
   * Act: attribute 'typeId'.
   */
  public static final String TYPE_ATTR="typeId";
  /**
   * Act: attribute 'personId1'.
   */
  public static final String PERSON1_ATTR="personId1";
  /**
   * Act: attribute 'personId2'.
   */
  public static final String PERSON2_ATTR="personId2";
  /**
   * Act: attribute 'path'.
   */
  public static final String ACT_FILE_PATH_ATTR="path";
  /**
   * Act: attribute 'nbParts'.
   */
  public static final String ACT_NB_PARTS_ATTR="nbParts";
  /**
   * Act: attribute 'traite'.
   */
  public static final String ACT_TRAITE_ATTR="traite";
  /**
   * Act: attribute 'textId'.
   */
  public static final String ACT_TEXT_ATTR="textId";

  /**
   * Person in act tag.
   */
  public static final String PERSON_IN_ACT_TAG="person";
  /**
   * Person in Act: attribute 'personId'.
   */
  public static final String PERSON_IN_ACT_ID_ATTR="personId";
  /**
   * Person in Act: attribute 'presence'.
   */
  public static final String PERSON_IN_ACT_PRESENCE_ATTR="presence";
  /**
   * Person in Act: attribute 'signature'.
   */
  public static final String PERSON_IN_ACT_SIGNATURE_ATTR="signature";
  /**
   * Person in Act: attribute 'link'.
   */
  public static final String PERSON_IN_ACT_LINK_ATTR="link";
  /**
   * Person in Act: attribute 'otherPersonId'.
   */
  public static final String PERSON_IN_ACT_OTHER_ID_ATTR="otherPersonId";

  /**
   * Place: attribute 'shortName'.
   */
  public static final String PLACE_SHORT_NAME_ATTR="shortName";
  /**
   * Place: attribute 'level'.
   */
  public static final String PLACE_LEVEL_ATTR="level";
  /**
   * Place: attribute 'parentPlaceId'.
   */
  public static final String PLACE_PARENT_PLACE_ATTR="parentPlaceId";

  /**
   * Union : man key.
   */
  public static final String UNION_MAN_ATTR="manId";
  /**
   * Union : man order.
   */
  public static final String UNION_MAN_ORDER_ATTR="manOrder";
  /**
   * Union : woman key.
   */
  public static final String UNION_WOMAN_ATTR="womanId";
  /**
   * Union : woman order.
   */
  public static final String UNION_WOMAN_ORDER_ATTR="womanOrder";
  /**
   * Union : wedding contract ID.
   */
  public static final String UNION_WEDDING_CONTRACT_ID_ATTR="weddingContractId";

  /**
   * Picture : title.
   */
  public static final String PICTURE_TITLE_ATTR="title";
  /**
   * Picture : path.
   */
  public static final String PICTURE_PATH_ATTR="path";
  /**
   * Picture : person in picture tag.
   */
  public static final String PERSON_IN_PICTURE_TAG="person";
  /**
   * Person in picture : attribute 'id'.
   */
  public static final String PERSON_IN_PICTURE_ID_ATTR="id";

  /**
   * Person: attribute 'lastName'.
   */
  public static final String PERSON_LASTNAME_ATTR="lastName";
  /**
   * Person: attribute 'firstName'.
   */
  public static final String PERSON_FIRSTNAME_ATTR="firstName";
  /**
   * Person: attribute 'sex'.
   */
  public static final String PERSON_SEX_ATTR="sex";
  /**
   * Person: attribute 'signature'.
   */
  public static final String PERSON_SIGNATURE_ATTR="signature";
  /**
   * Person: attribute 'birthDate'.
   */
  public static final String BIRTH_DATE_ATTR="birthDate";
  /**
   * Person: attribute 'birthDateInfos'.
   */
  public static final String BIRTH_DATE_INFOS_ATTR="birthDateInfos";
  /**
   * Person: attribute 'birthPlaceId'.
   */
  public static final String BIRTH_PLACE_ATTR="birthPlaceId";
  /**
   * Person: attribute 'deathDate'.
   */
  public static final String DEATH_DATE_ATTR="deathDate";
  /**
   * Person: attribute 'deathDateInfos'.
   */
  public static final String DEATH_DATE_INFOS_ATTR="deathDateInfos";
  /**
   * Person: attribute 'deathPlaceId'.
   */
  public static final String DEATH_PLACE_ATTR="deathPlaceId";
  /**
   * Person: attribute 'noDescendants'.
   */
  public static final String NO_DESCENDANTS_ATTR="noDescendants";
  /**
   * Person: attribute 'fatherId'.
   */
  public static final String FATHER_ATTR="fatherId";
  /**
   * Person: attribute 'motherId'.
   */
  public static final String MOTHER_ATTR="motherId";
  /**
   * Person: attribute 'godFatherId'.
   */
  public static final String GODFATHER_ATTR="godFatherId";
  /**
   * Person: attribute 'godMotherId'.
   */
  public static final String GODMOTHER_ATTR="godMotherId";
  /**
   * Person occupation tag.
   */
  public static final String PERSON_OCCUPATION_TAG="occupation";
  /**
   * Occupation: attribute 'occupation'.
   */
  public static final String OCCUPATION_ATTR="occupation";
  /**
   * Occupation: attribute 'year'.
   */
  public static final String OCCUPATION_YEAR_ATTR="year";
  /**
   * Occupation: attribute 'placeId'.
   */
  public static final String OCCUPATION_PLACE_ATTR="placeId";
  /**
   * Person home tag.
   */
  public static final String PERSON_HOME_TAG="home";
  /**
   * Home: attribute 'details'.
   */
  public static final String HOME_DETAILS_ATTR="details";
  /**
   * Home: attribute 'year'.
   */
  public static final String HOME_YEAR_ATTR="year";
  /**
   * Home: attribute 'placeId'.
   */
  public static final String HOME_PLACE_ATTR="placeId";
  /**
   * Person title tag.
   */
  public static final String PERSON_TITLE_TAG="title";
  /**
   * Title: attribute 'text'.
   */
  public static final String TITLE_TEXT_ATTR="text";
  /**
   * Title: attribute 'year'.
   */
  public static final String TITLE_YEAR_ATTR="year";

  /**
   * Cousinage: attribute 'cousin1'.
   */
  public static final String COUSIN1_ATTR="cousin1";
  /**
   * Cousinage: attribute 'cousin2'.
   */
  public static final String COUSIN2_ATTR="cousin2";
}
