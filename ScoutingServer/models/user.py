class User:
    def __init__(self, userid):
        self.id = userid
        self.matches = []
        self.teams = []


def divide_users(teams, matches, num_users):
    users = []
    teams_per_user = len(teams) // num_users

    for i in range(num_users):
        user = User(i)
        user.teams = teams[teams_per_user*i:teams_per_user*(i+1)]
        users.append(user)

    # (match, team) tuples for matches for which the user can't attend.
    conflict_matches = []

    for m in matches:
        m.free_users = users[:]
        for t in m.blue + m.red:
            for u in users:
                if t in u.teams:
                    if u in m.free_users:
                        m.free_users.remove(u)
                        u.matches.append(m)
                    else:
                        # This match must be resolved...
                        conflict_matches.append((m, t))

    for m, t in conflict_matches:
        if len(m.free_users) == 0:
            print('Not enough users. Impossible case.')
            return []
        else:
            m.free_users[0].matches.append(m)
            m.free_users[0].teams.append(t)

    return users