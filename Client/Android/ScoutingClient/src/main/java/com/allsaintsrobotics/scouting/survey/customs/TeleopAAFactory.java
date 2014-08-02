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

import android.content.Context;
import android.view.View;

import com.allsaintsrobotics.scouting.survey.Form;
import com.allsaintsrobotics.scouting.survey.QCustomFactory;
import com.allsaintsrobotics.scouting.survey.Question;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jack on 30/01/14.
 * This file is a part of the ASTECHZ Scouting Client.
 */
public class TeleopAAFactory<T> extends QCustomFactory<T> {
    @Override
    public Form getForm(Question<T> q, T t) {
        return new TeleopAAForm<T>(q, t);
    }

    @Override
    public View getValueView(Question<T> q, T t, Context c) {
        return new TeleopAAViewer<T>(t, q).getView(c);
    }

    @Override
    public JSONObject getJSON(Question<T> q, T t) {
        try {
            String answer = q.getAnswer(t);
            if (answer == null) return new JSONObject();
            else return new JSONObject(answer);
        } catch (JSONException ignored) { }

        return null;
    }
}
