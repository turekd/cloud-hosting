package it.dturek.cloudhosting.util;

import javax.persistence.NoResultException;
import javax.persistence.Query;

public class DaoUtil {

    public static Object getSingleResultOrNull(Query query) {
        try {
            return query.getSingleResult();
        } catch (NoResultException exception) {
            return null;
        }
    }

}
