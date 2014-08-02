/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.allsaintsrobotics.scouting.survey;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Jack on 28/01/14.
 * This file is a part of the ASTECHZ Scouting Client.
 */
public class Viewer<T> {
    private final T t;
    private final Question<T> question;

    private TextView tv;

    public Viewer(T t, Question<T> q) {
        this.t = t;
        this.question = q;
    }

    protected String textFilter(String text) {
        return text;
    }

    public View getView(Context c) {
        String val = textFilter(question.getAnswer(t));

        if (tv == null) {
            tv = new TextView(c);
        }

        tv.setText(val);

        if (tv.getParent() != null) {
            ((ViewGroup)tv.getParent()).removeView(tv);
        }

        return tv;
    }
}
