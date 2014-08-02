# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

import models.match
import models.team

class Question:
    def __init__(self, qid, label, qtype, offers):
        self.qid = qid
        self.label = label
        self.qtype = qtype
        self.offers = offers

    def to_small_json(self):
        return {'id': self.qid, 'label': self.label, 'type': self.qtype, 'offers': self.offers}


class MatchQuestion(Question):
    def __init__(self, qid, label, qtype, offers, answers=None):
        Question.__init__(self, qid, label, qtype, offers)
        if answers is None:
            self.answers = {}
        else:
            self.answers = {models.team.get_team(int(key)): {models.match.get_match(int(k)):
                                v for k, v in value.items()} for key, value in answers.items()}

    def add_answer(self, team, match, answer):
        if match not in self.answers:
            self.answers[team] = {}
        self.answers[team][match] = answer

    def get(self, match, team):
        return self.answers[match][team]['value']

    def has(self, match, team):
        return match in self.answers and team in self.answers[match]

    def to_json(self):
        return {'id': self.qid, 'label': self.label, 'type': self.qtype, 'offers': self.offers,
                        'answers': {key.id: {k.id: v for k, v in value.items()} for key, value in
                                    self.answers.items()},
                        '__type': 'match'}

class TeamQuestion(Question):
    def __init__(self, qid, label, qtype, offers, answers=None):
        Question.__init__(self, qid, label, qtype, offers)
        if answers is None:
            self.answers = {}
        else:
            self.answers = {models.team.get_team(int(key)): value for key, value in answers.items()}

    def add_answer(self, team, answer):
        self.answers[team] = answer

    def get(self, t):
        return self.answers[t]['value']

    def has(self, t):
        return t in self.answers

    def to_json(self):
        return {'id': self.qid, 'label': self.label, 'type': self.qtype, 'offers': self.offers,
                        'answers': {key.id: value for key, value in self.answers.items()},
                        '__type': 'team'}
