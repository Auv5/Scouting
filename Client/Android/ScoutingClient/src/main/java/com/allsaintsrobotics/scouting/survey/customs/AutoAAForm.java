/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.allsaintsrobotics.scouting.survey.customs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allsaintsrobotics.scouting.R;
import com.allsaintsrobotics.scouting.survey.Form;
import com.allsaintsrobotics.scouting.survey.Question;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jack on 29/01/14.
 * This file is a part of the ASTECHZ Scouting app.
 */
public class AutoAAForm<T> extends Form<T> {
    private LinearLayout view = null;

    private CheckBox hotBox, bottomBox;

    private TextView numBalls;

    private int score;
    private int balls;
    private int bottom;
    private int top;
    private int hot;



    public AutoAAForm(Question q, T t) {
        super(q, t);
    }

    @Override
    public View getAnswerView(Activity c, ViewGroup parent) {
        if (view == null)
        {
            LayoutInflater li = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.view = (LinearLayout)li.inflate(R.layout.question_autoaa, null);
            this.numBalls = (TextView) view.findViewById(R.id.aaauto_score);
            Button minus = (Button) view.findViewById(R.id.remove_score);

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (balls > 0) {
                        balls--;
                    }
                    if (hotBox.isChecked() && hot > 0) {
                        hot--;
                    }
                    if (bottomBox.isChecked() && bottom > 0) {
                        bottom--;
                    }
                    if (!bottomBox.isChecked() && top > 0) {
                        top--;
                    }
                    if (score > 0) {
                        score -= (hotBox.isChecked() ? 5 : 0) + (bottomBox.isChecked() ? 0 : 9) + (6);
                        // User error.
                        if (score < 0) {
                            score = 0;
                        }
                    }

                    numBalls.setText(Integer.toString(score));
                }
            });

            Button plus = (Button) view.findViewById(R.id.add_score);

            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int balls = Integer.valueOf(AutoAAForm.this.numBalls.getText().toString());

                    balls++;

                    if (hotBox.isChecked()) {
                        hot++;
                    }
                    if (bottomBox.isChecked()) {
                        bottom++;
                    } else {
                        top++;
                    }

                    score += (hotBox.isChecked() ? 5 : 0) + (bottomBox.isChecked() ? 0 : 9) + (6);

                    numBalls.setText(Integer.toString(score));
                }
            });

            this.bottomBox = (CheckBox) view.findViewById(R.id.aaauto_bottom);
            this.hotBox = (CheckBox) view.findViewById(R.id.aaauto_hot);
        }

        String text = question.getAnswer(t);

        if (text != null) {
            try {
                JSONObject data = new JSONObject(text);

                score = data.getInt("score");
                top = data.getInt("top");
                bottom = data.getInt("bottom");
                hot = data.getInt("hot");
                balls = data.getInt("balls");

                numBalls.setText(Integer.toString(score));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    @Override
    public String getAnswer() {
        try {
            JSONObject json = new JSONObject();

            json.put("score", score);
            json.put("value", score);
            json.put("top", top);
            json.put("bottom", bottom);
            json.put("hot", hot);
            json.put("balls", balls);

            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setError(String error) {
        // Nothing!
    }
}
