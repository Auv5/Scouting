package com.allsaintsrobotics.scouting.survey;

import android.util.Log;

import com.allsaintsrobotics.scouting.models.Team;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jack on 11/24/13.
 */
public abstract class FormFactory {
    private static Map<String, Class<? extends FormFactory>> factories;

    protected String[] offers;

    private static String TAG = "FormFactory";

    static {
        factories = new HashMap<String, Class<? extends FormFactory>>();

        factories.put("fr", FreeResponseForm.FreeResponseFormFactory.class);
    }

    public abstract Form getForm(Question q, Team t);

    public static FormFactory forId(String id) {
        // No runtime checks - Dangerous!
        try {
            Class<? extends FormFactory> factoryClazz = factories.get(id);

            Log.e(TAG, "Class: " + factoryClazz.getName());

            int i = 0;

            for (Constructor<?> c : factoryClazz.getConstructors()) {
                StringBuilder sb = new StringBuilder("Constructor " + i + ": ");

                for (Class<?> type : c.getParameterTypes()) {
                    sb.append(type.getName() + ", ");
                }

                Log.e(TAG, sb.toString());
            }

            return (FormFactory)factories.get(id).getConstructors()[0].newInstance();
        } catch (InstantiationException e) {
            //TODO: Handle with loggers
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            //TODO: Handle with loggers
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            //TODO: Handle with loggers
            e.printStackTrace();
        }

        // Unreachable. To keep compiler happy.
        return null;
    }

    public FormFactory setOffers(String[] offers) {
        this.offers = offers;
        return this;
    }
}
