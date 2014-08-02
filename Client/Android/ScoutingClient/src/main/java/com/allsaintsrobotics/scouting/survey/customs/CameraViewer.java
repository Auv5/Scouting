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

import com.allsaintsrobotics.scouting.survey.Question;
import com.allsaintsrobotics.scouting.survey.Viewer;

/**
 * Created by Jack on 28/01/14.
 * This file is a part of the ASTECHZ Scouting app.
 */
public class CameraViewer<T> extends Viewer<T> {
    public CameraViewer(T t, Question<T> q) {
        super(t, q);
    }

    @Override
    public View getView(Context c) {
        //TODO: Show image
        return super.getView(c);
    }
}
