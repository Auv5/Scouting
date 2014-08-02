# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

import json
from models.question import MatchQuestion, TeamQuestion


def get_questions(data=False):
    if data:
        with open('data/questions.json') as f:
            t = json.load(f)

            match_questions = {}
            team_questions = {}

            for q in t:
                if q['__type'] == 'team':
                    team_questions[q['id']] = TeamQuestion(q['id'], q['label'], q['type'], q['offers'], q['answers'])
                else:
                    match_questions[q['id']] = MatchQuestion(q['id'], q['label'], q['type'], q['offers'], q['answers'])

            return [team_questions, match_questions]
    else:
        with open('config/questions.json', 'r') as f:
            t = json.load(f)

            match_questions = {}
            team_questions = {}

            for q in t:
                if 'm_' in q['type']:
                    match_questions[q['id']] = MatchQuestion(q['id'], q['label'], q['type'], q['offers'])
                else:
                    team_questions[q['id']] = TeamQuestion(q['id'], q['label'], q['type'], q['offers'])

            return [team_questions, match_questions]