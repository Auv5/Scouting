package com.allsaintsrobotics.scouting;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on: 8/13/13.
 */
public abstract class AbstractStatistic implements Statistic {
    private static Map<String, Class<? extends Statistic>> types = null;

    protected static void registerStat(Class<? extends Statistic> clazz, String id)
    {
        if (types == null)
        {
            types = new HashMap<String, Class<? extends Statistic>>();
        }
        types.put(id, clazz);
    }

    @Override
    public String getTypeString()
    {
        for (Map.Entry<String, Class<? extends Statistic>> id : types.entrySet())
        {
            if (id.getValue().equals(this.getClass()))
            {
                return id.getKey();
            }
        }

        return null;
    }

    public static Class<? extends Statistic> getStatisticType(String qType) {
        return types.get(qType);
    }
}
