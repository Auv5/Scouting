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
import android.widget.TextView;

import com.allsaintsrobotics.scouting.R;
import com.allsaintsrobotics.scouting.models.Team;

import java.util.List;

/**
* Created by jack on 11/24/13.
*/
public class TeamAdapter extends ArrayAdapter<Team> {
    private final List<Team> teams;
    private final Context context;

    public TeamAdapter(Context context, List<Team> teams) {
        super(context, R.layout.listitem_team, teams);
        this.context = context;
        this.teams = teams;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        TeamFieldHelper tfh;

        if (row == null) {
            LayoutInflater li = LayoutInflater.from(context);

            row = li.inflate(R.layout.listitem_team, parent, false);

            tfh = new TeamFieldHelper();

            tfh.number = (TextView) row.findViewById(R.id.tv_teamnumber);
            tfh.name = (TextView) row.findViewById(R.id.tv_teamname);

            row.setTag(tfh);
        }

        Team t = teams.get(position);

        int number = t.getNumber();
        String name = t.getNickname();

        tfh = (TeamFieldHelper) row.getTag();

        tfh.number.setText(String.format(context.getString(R.string.team_number), number));
        tfh.name.setText(String.format(context.getString(R.string.team_name), name));

        return row;
    }

    private class TeamFieldHelper {
        TextView number;
        TextView name;
    }
}
