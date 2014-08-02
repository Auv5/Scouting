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

import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.ScoutingDBHelper;

public class MatchQuestion extends Question<Match> {
    public MatchQuestion(String label, QCustomFactory<Match> factory, int id, String[] offers) {
        super(label, factory, id, offers);
    }

    @Override
    public String dbRead(Match match) {
        return ScoutingDBHelper.getInstance().getAnswer(this, match);
    }

    @Override
    public void dbWrite(Match match, String value) {
        ScoutingDBHelper.getInstance().setAnswer(this, match, value);
    }
}
