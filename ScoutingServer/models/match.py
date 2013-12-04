class Match:
    def __init__(self, id, start_at, red, blue):
        self.red = red
        self.blue = blue
        self.id = id
        self.start_at = start_at

    def to_json(self, t):
        return {'id': self.id, 'red': self.red, 'blue': self.blue, 'scout': t}