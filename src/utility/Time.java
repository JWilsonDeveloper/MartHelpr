package utility;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Time {
    public static ZonedDateTime localToUTC(ZonedDateTime localZDT) {
        return ZonedDateTime.ofInstant(localZDT.toInstant(), ZoneId.of("UTC"));
    }
}
