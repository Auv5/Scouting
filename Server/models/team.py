def get_team(number):
    return Team._teams[number]


class Team:
    _teams = {}

    def __init__(self, number, nickname):
        self.number = number
        self.id = number
        self.nickname = nickname
        Team._teams[number] = self

    def __str__(self):
        print('Team #' + self.number)

    def to_json(self):
        return self.number, self.nickname
