class Match:
    def __init__(self, id, start_at, red, blue):
        self.red = red
        self.blue = blue
        self.id = id
        self.start_at = start_at

    def to_json(self):
        return {'id': self.id, 'red': [t.number for t in self.red], 'blue': [t.number for t in self.blue]}