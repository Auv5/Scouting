/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.allsaintsrobotics.scouting.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.allsaintsrobotics.scouting.R;
import com.allsaintsrobotics.scouting.survey.Question;

import java.util.List;

/**
 * Created by jack on 11/25/13.
 * This file is a part of the ASTECHZ Scouting app.
 */
public class QuestionAdapter<T> extends ArrayAdapter {
    private final Context context;
    private final List<? extends Question<T>> questions;

    private final T team;

    public QuestionAdapter(Context context, T team, List<? extends Question<T>> questions) {
        super(context, R.layout.listitem_pitstats, (List)questions);

        this.context = context;
        this.questions = questions;
        this.team = team;
    }

    

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        QuestionFieldHelper qfh;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listitem_pitstats, parent, false);

            qfh = new QuestionFieldHelper();

            qfh.label = (TextView) row.findViewById(R.id.pit_label);
            qfh.value = (FrameLayout) row.findViewById(R.id.pit_value);

            row.setTag(qfh);
        }
        else {
            qfh = (QuestionFieldHelper)row.getTag();
        }

        Question q = questions.get(position);

        qfh.label.setText(q.getLabel());
        qfh.value.removeAllViews();
        qfh.value.addView(q.getValueView(team, context));

        return row;
    }

    private class QuestionFieldHelper {
        TextView label;
        FrameLayout value;
    }
}
