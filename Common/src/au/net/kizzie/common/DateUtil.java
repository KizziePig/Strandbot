package au.net.kizzie.common;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author steve
 */
public class DateUtil {
    private static final Logger LOGGER = Logger.getLogger(DateUtil.class.getName());
    public static Timestamp convertStringToTimestamp(String dateString) throws NumberFormatException, ParseException {
       try {
           Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
           return new Timestamp(date.getTime());
       }
       catch(ParseException ex) {
           LOGGER.log(Level.SEVERE,"DateUtil.convertStringToTimestamp: ERROR: could not parse date in string '{0}' - returning null - ParseException: {1}",new Object[] {dateString, ex});
           throw ex;
       } catch(NumberFormatException ex) {
           LOGGER.log(Level.SEVERE,"DateUtil.convertStringToTimestamp: ERROR: could not parse date in string {0} - returning null - NumberFormatException: {1}",new Object[] {dateString, ex});
           throw ex;
       }
    }
}
