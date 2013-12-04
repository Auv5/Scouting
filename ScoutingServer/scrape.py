import json
import os

from bs4 import BeautifulSoup
from http.client import HTTPConnection

from models.match import Match
from models.team import Team


def usfirst_scrape_matches(reg_id):
    conn = HTTPConnection('www2.usfirst.org')
    # First four letters = year, rest = ID
    url = '/' + reg_id[:4] + 'comp/Events/' + reg_id[4:].upper() + '/ScheduleQual.html'
    conn.request('GET', url)
    r = conn.getresponse()

    answer = r.read().decode('utf8')

    soup = BeautifulSoup(answer)

    matches = soup.find_all(name='td')
    matches = [t.get_text().strip() for t in matches]
    matches = matches[matches.index('Blue 3')+1:
        [i for i, m in enumerate(matches) if m.startswith('Match scheduling')][0]]

    matchinfo = []

    work_info = None

    capture_index = 0

    for t in matches:
        if not t:
            continue
        if capture_index == 0:
            work_info = [''.join(t.split())]
            capture_index += 1
        elif capture_index < 8:
            work_info.append(''.join(t.split()))
            capture_index += 1
        elif capture_index == 8:
            capture_index = 0
            matchinfo.append(work_info)
            work_info = [''.join(t.split())]
            capture_index += 1

    return [Match(int(id), time, [int(red1), int(red2), int(red3)], [int(blue1), int(blue2), int(blue3)])
            for time, id, red1, red2, red3, blue1, blue2, blue3 in matchinfo]


def cache_or_get_json(name, func, *args, **kwargs):
    quiet = False

    if 'quiet' in kwargs:
        quiet = kwargs['quiet']

    if not os.path.isdir('cache'):
        os.mkdir('cache')

    filename = 'cache/' + name + '.json'

    if os.path.exists(filename):
        if not quiet:
            print('Using cached: ' + name)
        return json.loads(open(filename, 'r').read())
    else:
        if not quiet:
            print('Generating: ' + name)
        value = func(*args)
        open(filename, 'w').write(value)
        return json.loads(value)


def download_regional_impl(key, quiet=False):
    if not quiet:
        print('Downloading event details...')

    conn = HTTPConnection('www.thebluealliance.com')

    conn.request('GET', '/api/v1/event/details?event=' + key)

    r = conn.getresponse()

    answer = r.read().decode('utf-8')
    return answer


def download_teams_impl(r, quiet=False):
    reg_teams = r['teams']

    to_api = ','.join(reg_teams)
    if not quiet:
        print('Downloading team details...')

    conn = HTTPConnection('www.thebluealliance.com')
    conn.request('GET', '/api/v1/teams/show?teams=' + to_api)
    r = conn.getresponse()

    answer = r.read().decode('utf-8')

    return answer


def download_regional(key, quiet=False):
    return cache_or_get_json('regional' + key, download_regional_impl, key, quiet)


def download_teams(r, quiet=False):
    return [Team(t['team_number'], t['nickname']) for t in
            cache_or_get_json('teams' + r['key'], download_teams_impl, r, quiet)]