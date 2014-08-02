# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

from wsgiref.simple_server import make_server
from pyramid.config import Configurator
from pyramid.response import Response
from pyramid.renderers import render_to_response

from models.user import divide_users

import json
import os
import scrape
import config
import models.team
import models.match


class Server:
    def __init__(self, teams, matches):
        # TODO: Save users and id_counter
        self.id_counter = 0
        self.users = []
        self.teams = teams
        self.matches = matches
        self.team_questions, self.match_questions = config.get_questions(os.path.exists('data/questions.json'))

    def register(self, request):
        if self.id_counter < len(self.users):
            user = self.users[self.id_counter]
            #user.name = request.params['name']
            combine = {}
            combine.update(self.team_questions)
            combine.update(self.match_questions)
            to_send = {'id': user.id, 'teams': [t.to_json() for t in user.teams],
                       'matches': [m.to_json(t) for t, m in user.matches], 'questions': [q.to_small_json() for q in
                                                                                            combine.values()],
                       'conflicts': [t.id for t in user.teams if t in user.conflicts]}
            self.id_counter += 1
            return Response(json.dumps(to_send))
        else:
            return Response(json.dumps({}))

    def upload(self, request):
        if request.method == 'POST' and 'data' in request.params:
            data = json.loads(request.params['data'])

            for (mid, assoc) in dict(data['match_ans']).items():
                m = models.match.get_match(int(mid))
                for ans in assoc:
                    if 'value' in ans:
                        q_to = self.match_questions[ans['id']]
                        del ans['id']
                        q_to.add_answer(models.team.get_team(ans['scout']), m, ans)

            for (tid, assoc) in dict(data['team_ans']).items():
                t = models.team.get_team(int(tid))
                for ans in assoc:
                    if 'value' in ans:
                        q_to = self.team_questions[ans['id']]
                        del ans['id']
                        q_to.add_answer(t, ans)

            self.write_questions()
            return Response('SUCCESS!!')
        else:
            return Response('ERROR: USE THE RIGHT METHOD YOU INCOMPETENT FOOL')

    def make_spreadsheet(self):
        team_labels = ["Team"] + [q.label for q in self.team_questions.values()]
        outf = open('data/export_teams.csv', 'w')
        outf.write('\"' + '\",'.join(team_labels) + '\",\n')

        for t in self.teams:
            outf.write("\"" + str(t.id) + "\",")
            for tq in self.team_questions.values():
                if tq.has(t):
                    outf.write("\"" + tq.get(t) + "\",")
                else:
                    outf.write(",")
            outf.write("\n")

        outf = open('data/export_teams.csv', 'w')

        outf.close()

        outf = open('data/export_match.csv', 'w')

        outf.write("\"Teams\",")

        for m in self.matches:
            for mq in self.match_questions.values():
                outf.write("\"" + str(m.id) + " " + mq.label + "\",")

        outf.write('\n')

        for t in self.teams:
            outf.write('\"' + str(t.id) + '\",')
            tmatches = [m for m in self.matches if t in m.red or t in m.blue]
            for mq in self.match_questions.values():
                for m in self.matches:
                    if m in tmatches and mq.has(m, t):
                        outf.write('\"' + mq.get(m, t) + '\",')
                    else:
                        outf.write('\"\",')
            outf.write('\n')

        outf.close()

        return Response("")

    def write_questions(self):
        with open('data/questions.json', 'w') as f:
            combine = {}
            combine.update(self.team_questions)
            combine.update(self.match_questions)
            json.dump([q.to_json() for q in combine.values()], f)

def register(request):
    global server
    return server.register(request)


def _render(request, template, params={}):
    return render_to_response('server:templates/' + template + '.template.pt', params, request=request)

def configure(request):
    return _render(request, 'configure')


def upload(request):
    global server
    return server.upload(request)


def make_spreadsheet(request):
    global server
    return server.make_spreadsheet()

def main():
    global server

    reg_id = '2013onto2'

    teams = scrape.download_teams(scrape.download_regional(reg_id))

    matches = scrape.usfirst_scrape_matches(reg_id)

    server = Server(teams, matches)

    server.users = divide_users(teams, matches, 6)

    configuration = Configurator()

    configuration.include('pyramid_chameleon')

    configuration.add_static_view(name='assets', path='server:static/')

    configuration.add_route('register', '/api/register')
    configuration.add_view(register, route_name='register')

    configuration.add_route('upload', '/api/upload')
    configuration.add_view(upload, route_name='upload')

    configuration.add_route('make_spreadsheet', '/make_spreadsheet')
    configuration.add_view(make_spreadsheet, route_name='make_spreadsheet')

    configuration.add_route('configure', '/configure')
    configuration.add_view(configure, route_name='configure')

    app = configuration.make_wsgi_app()

    server_app = make_server('0.0.0.0', 80, app)

    print('Serving!')

    server_app.serve_forever()

if __name__ == '__main__':
    main()
