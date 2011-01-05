package delta.genea.gedcom;

import delta.genea.misc.GenealogySoftware;

/**
 * GEDCOM source line decoder.
 * @author DAM
 */
public class SourceDecoder
{
  /**
   * Find out the name of the software that produced a GEDCOM file
   * by looking at its SOURce line.
   * @param sourceLine Source line.
   * @return A genealogy software identifier.
   */
  public static int getSourceSoftware(String sourceLine)
  {
    if (sourceLine==null) return GenealogySoftware.UNKNOWN;
    if (sourceLine.length()==0) return GenealogySoftware.UNKNOWN;

    if (sourceLine.contains("1 SOUR PAF")) return GenealogySoftware.PERSONAL_ANCESTRAL_FILE;
    if (sourceLine.contains("1 SOUR BASGEN98")) return GenealogySoftware.BASGEN_98;
    if (sourceLine.contains("1 SOUR HEREDIS")) return GenealogySoftware.HEREDIS;
    if (sourceLine.contains("1 SOUR GENEATIQUE")) return GenealogySoftware.GENEATIQUE;
    if (sourceLine.contains("1 SOUR GENEA")) return GenealogySoftware.GENEA;
    return GenealogySoftware.UNKNOWN;
  }
}
