from wsgiref.simple_server import make_server
from pyramid.config import Configurator
from pyramid.response import Response

from models.match import Match
from models.user import User, divide_users
from models.team import Team, get_team

import json


id_counter = 0
users = []


def register(request):
    global users, id_counter
    if id_counter < len(users):
        user = users[id_counter]
        to_send = {'id': user.id, 'teams': [t.to_json() for t in user.teams],
                   'matches': [m.to_json() for m in user.matches]}
        id_counter += 1
    else:
        return Response(json.dumps({}))

    return Response(json.dumps(to_send))


def main():
    global users

    users = divide_users([Team(2994, "The ASTECHZ"), Team(2056, "OP Robotics"), Team(1114, "Simbotics"),
                          Team(610, "The Coyotes"), Team(2013, "The Cybergnomes"), Team(1241, "THEORY6")],
                         [Match(1, 0, [get_team(2994), get_team(2056), get_team(1114)],
                                [get_team(610), get_team(2013), get_team(1241)])], 6)

    config = Configurator()
    config.add_route('register', '/api/register')
    config.add_view(register, route_name='register')

    app = config.make_wsgi_app()

    server = make_server('0.0.0.0', 8000, app)

    print('Serving!')

    server.serve_forever()

if __name__ == '__main__':
    main()