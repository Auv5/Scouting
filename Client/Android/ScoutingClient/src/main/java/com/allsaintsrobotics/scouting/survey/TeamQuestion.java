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

import com.allsaintsrobotics.scouting.models.Team;
import com.allsaintsrobotics.scouting.ScoutingDBHelper;

public class TeamQuestion extends Question<Team> {
    public TeamQuestion(String label, QCustomFactory<Team> factory, int id, String[] offers) {
        super(label, factory, id, offers);
    }

    @Override
    public String dbRead(Team team) {
        return ScoutingDBHelper.getInstance().getAnswer(this, team);
    }

    @Override
    public void dbWrite(Team team, String value) {
        ScoutingDBHelper.getInstance().setAnswer(this, team, value);
    }
}
