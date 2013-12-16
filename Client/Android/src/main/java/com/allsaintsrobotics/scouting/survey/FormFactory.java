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
        factories.put("mc", MultipleChoiceForm.MultipleChoiceFormFactory.class);
        factories.put("cam", CameraForm.CameraFormFactory.class);
    }

    public abstract Form getForm(Question q, Team t);

    public static FormFactory forId(String id) {
        return forId(id, new String[] {});
    }

    public static FormFactory forId(String id, String[] offers) {
        // No runtime checks - Dangerous!
        try {
            FormFactory factory =
                    (FormFactory)factories.get(id).getConstructors()[0]
                            .newInstance();

            factory.setOffers(offers);
            return factory;
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
