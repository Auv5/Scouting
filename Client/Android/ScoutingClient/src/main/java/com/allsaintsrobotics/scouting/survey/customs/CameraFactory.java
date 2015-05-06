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
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.allsaintsrobotics.scouting.survey.Form;
import com.allsaintsrobotics.scouting.survey.QCustomFactory;
import com.allsaintsrobotics.scouting.survey.Question;

import java.io.File;

/**
* Created by Jack on 28/01/14.
*/
public class CameraFactory<T> extends QCustomFactory<T> {
    private static final String TAG = "CameraFormFactory";

    public CameraFactory() {}

    @Override
    public Form getForm(Question<T> q, T t) {
        String dir = "scouting_pics";

        File sdcard = Environment.getExternalStorageDirectory();

        //TODO: Handle no SD card case

        File dirFile = new File(sdcard, dir);

        // Ensure directory exists.
        //noinspection ResultOfMethodCallIgnored
        dirFile.mkdir();

        String filename = String.valueOf(q.getId()) + "_" + t.hashCode() + ".jpg";

        Log.d(TAG, "Camera filename: " + filename);

        File newFile = new File(dirFile, filename);

        return new CameraForm<>(q, t, newFile);
    }

    @Override
    public View getValueView(Question<T> q, T m, Context c) {
        return new CameraViewer<>(m, q).getView(c);
    }
}
