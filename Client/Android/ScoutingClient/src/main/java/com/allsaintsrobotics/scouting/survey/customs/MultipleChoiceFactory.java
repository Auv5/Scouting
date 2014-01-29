package com.allsaintsrobotics.scouting.survey.customs;

import com.allsaintsrobotics.scouting.survey.Form;
import com.allsaintsrobotics.scouting.survey.QCustomFactory;
import com.allsaintsrobotics.scouting.survey.Question;

/**
* Created by Jack on 28/01/14.
*/
public class MultipleChoiceFactory<M> extends QCustomFactory<M> {
    public MultipleChoiceFactory() {}

    @Override
    public Form getForm(Question<M> q, M t) {
        return new MultipleChoiceForm<M>(q, t);
    }
}
