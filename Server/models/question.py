class Question:
    def __init__(self, qid, label, qtype, offers):
        self.qid = qid
        self.label = label
        self.qtype = qtype
        self.offers = offers

    def to_json(self):
        return {'id': self.qid, 'label': self.label, 'type': self.qtype, 'offers': self.offers}