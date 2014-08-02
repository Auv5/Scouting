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

import com.allsaintsrobotics.scouting.survey.Question;
import com.allsaintsrobotics.scouting.survey.Viewer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jack on 30/01/14.
 * This file is a part of the ASTECHZ Scouting app.
 */
public class AutoAAViewer<T> extends Viewer<T> {
    public AutoAAViewer(T t, Question<T> q) {
        super(t, q);
    }

    @Override
    protected String textFilter(String text) {
        if (text == null) {
            return "";
        }

        try {
            JSONObject json = new JSONObject(text);

            return Integer.toString(json.getInt("score"));
        } catch (JSONException e) {
            return "";
        }
    }
}
