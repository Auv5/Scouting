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

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;

/**
 * Created by jack on 11/24/13.
 * This file is a part of the ASTECHZ Scouting app.
 */
public abstract class Form<T> {
    protected final Question<T> question;
    protected final T t;
    private Validator validator;

    protected Form(Question<T> q, T t) {
        this.question = q;
        this.t = t;
    }

    public abstract View getAnswerView(Activity c, ViewGroup parent);

    protected abstract String getAnswer();

    protected abstract void setError(String error);

    public void write() {
        String answer = this.getAnswer();
        if (answer != null && !answer.isEmpty()) {
           question.write(t, answer);
        }
    }

    public void setValidator(Validator v) {
        this.validator = v;
    }

    public boolean validate() {
        if (validator == null) {
            return true;
        }
        else if (validator.validate(getAnswer())) {
            return true;
        }
        else {
            this.setError(validator.getError());
            return false;
        }
    }

    public boolean result(Activity c, int request, int response, Intent data) {
        return false;
    }

    public interface Validator {
        public boolean validate(String text);
        public String getError();
    }
}
