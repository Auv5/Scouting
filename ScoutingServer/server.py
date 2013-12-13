from wsgiref.simple_server import make_server
from pyramid.config import Configurator
from pyramid.response import Response

from models.user import divide_users

import json
import scrape
import config


class Server:
    def __init__(self, questions):
        self.id_counter = 0
        self.users = []
        self.questions = questions

    def register(self, request):
        if self.id_counter < len(self.users):
            user = self.users[self.id_counter]
            to_send = {'id': user.id, 'teams': [t.to_json() for t in user.teams],
                       'matches': [m.to_json(t) for t, m in user.matches], 'questions': [q.to_json() for q in
                                                                                         self.questions]}
            self.id_counter += 1
            return Response(json.dumps(to_send))
        else:
            return Response(json.dumps({}))


def register(request):
    global server
    return server.register(request)


def main():
    global server
    server = Server(config.get_questions())

    reg_id = '2013onto2'

    teams = scrape.download_teams(scrape.download_regional(reg_id))

    matches = scrape.usfirst_scrape_matches(reg_id)

    server.users = divide_users(teams, matches, 6)

    configuration = Configurator()
    configuration.add_route('register', '/api/register')
    configuration.add_view(register, route_name='register')

    app = configuration.make_wsgi_app()

    server_app = make_server('0.0.0.0', 8000, app)

    print('Serving!')

    server_app.serve_forever()

if __name__ == '__main__':
    main()