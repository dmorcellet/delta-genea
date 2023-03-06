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
   * Union : woman key.
   */
  public static final String UNION_WOMAN_ATTR="womanId";
  /**
   * Union : woman key.
   */
  public static final String UNION_WEDDING_CONTRACT_ID_ATTR="weddingContractId";
}
