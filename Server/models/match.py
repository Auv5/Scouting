def get_match(number):
    return Match._matches[number]

class Match:
    _matches = {}
    def __init__(self, id, start_at, red, blue):
        self.red = red
        self.blue = blue
        self.id = id
        self.start_at = start_at
        self._matches[id] = self

    def to_json(self, t):
        return {'id': self.id, 'red': [r.number for r in self.red], 'blue':
            [b.number for b in self.blue], 'scout': t.number}
