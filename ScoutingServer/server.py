from wsgiref.simple_server import make_server
from pyramid.config import Configurator
from pyramid.response import Response

from models.user import divide_users

import json
import scrape


id_counter = 0
users = []


def register(request):
    global users, id_counter
    if id_counter < len(users):
        user = users[id_counter]
        to_send = {'id': user.id, 'teams': [t.to_json() for t in user.teams],
                   'matches': [m.to_json(t) for t, m in user.matches]}
        id_counter += 1
    else:
        return Response(json.dumps({}))

    return Response(json.dumps(to_send))


def main():
    global users

    reg_id = '2013onto2'

    matches = scrape.usfirst_scrape_matches(reg_id)
    teams = scrape.download_teams(scrape.download_regional(reg_id))

    users = divide_users(teams, matches, 6)

    config = Configurator()
    config.add_route('register', '/api/register')
    config.add_view(register, route_name='register')

    app = config.make_wsgi_app()

    server = make_server('0.0.0.0', 8000, app)

    print('Serving!')

    server.serve_forever()

if __name__ == '__main__':
    main()