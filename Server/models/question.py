import models.match
import models.team

lookup = {'match': models.match.get_match, 'team': models.team.get_team}

class Question:
    def __init__(self, qid, label, qtype, offers, answers=None):
        self.qid = qid
        self.label = label
        self.qtype = qtype
        self.offers = offers
        self.atype = 'match' if 'm_' in qtype else 'team'
        if answers is None:
            self.answers = {}
        else:
            if self.atype == 'team':
                self.answers = {lookup[self.atype](int(key)): value for key, value in answers.items()}
            else:
                self.answers = {lookup[self.atype](int(key)): {lookup['team'](int(k)): v for k, v in value.items()}
                                for key, value in answers.items()}

    def add_answer(self, obj, answer, assoc=None):
        if assoc:
            if obj not in self.answers:
                self.answers[obj] = {}
            self.answers[obj][assoc] = answer
        else:
            self.answers[obj] = answer

    def to_json(self, compact=True):
        if compact:
            return {'id': self.qid, 'label': self.label, 'type': self.qtype, 'offers': self.offers}
        else:
            if self.atype == 'team':
                return {'id': self.qid, 'label': self.label, 'type': self.qtype, 'offers': self.offers,
                        'answers': {key.id: value for key, value in self.answers.items()}}
            else:
                return {'id': self.qid, 'label': self.label, 'type': self.qtype, 'offers': self.offers,
                        'answers': {key.id: {k.id: v for k, v in value.items()} for key, value in
                                    self.answers.items()}}