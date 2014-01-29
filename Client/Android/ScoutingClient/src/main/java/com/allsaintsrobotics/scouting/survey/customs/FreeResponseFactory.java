package com.allsaintsrobotics.scouting.survey.customs;

import com.allsaintsrobotics.scouting.survey.Form;
import com.allsaintsrobotics.scouting.survey.QCustomFactory;
import com.allsaintsrobotics.scouting.survey.Question;

/**
* Created by Jack on 28/01/14.
*/
public class FreeResponseFactory<M> extends QCustomFactory<M> {
    // Ensure this is the only constructor so QCustomFactory doesn't mess up.
    public FreeResponseFactory() {}

    @Override
    public Form getForm(Question<M> q, M t) {
        return new FreeResponseForm<M>(q, t);
    }
}
