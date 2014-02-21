from models.team import get_team


class User:
    def __init__(self, userid):
        self.id = userid
        self.matches = []
        self.teams = []
        self.conflicts = []


def _assign_to_teams(users, teams):
    # Up-rounding integer division so we don't cut off the end of the list by accident
    # if there isn't a clean division.
    teams_per_user = (len(teams) + len(users) // 2) // len(users)

    for u in users:
        u.teams = teams[teams_per_user*u.id:teams_per_user*(u.id+1)]


def _get_conflicts(matches, users):
    # (match, team) tuples for matches for which the user can't attend.
    conflict_matches = []

    for m in matches:
        m.free_users = users[:]
        for t in m.blue + m.red:
            for u in users:
                if t in u.teams:
                    if u in m.free_users:
                        m.free_users.remove(u)
                        u.matches.append((t, m))
                    else:
                        # This match must be resolved...
                        conflict_matches.append((m, t))
                    break

    return conflict_matches


def divide_users(teams, matches, num_users):
    users = [User(i) for i in range(num_users)]

    _assign_to_teams(users, teams)

    conflict_matches = _get_conflicts(matches, users)

    for m, t in conflict_matches:
        if len(m.free_users) == 0:
            print('Not enough users. Impossible case.')
            return []
        else:
            user_to_assign = min(m.free_users, key=lambda self: len(self.matches))
            user_to_assign.matches.append((t, m))

            user_to_assign.conflicts.append(t)

            # Remove user_to_assign from the list of free users.
            m.free_users = [u for u in m.free_users if u != user_to_assign]

            if t not in user_to_assign.teams:
                user_to_assign.teams.append(t)

    return users