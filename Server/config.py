import json
from models.question import MatchQuestion, TeamQuestion


def get_questions(data=False):
    if data:
        with open('data/questions.json') as f:
            t = json.load(f)

            match_questions = {}
            team_questions = {}

            for q in t:
                if q['__type'] == 'team':
                    team_questions[q['id']] = TeamQuestion(q['id'], q['label'], q['type'], q['offers'], q['answers'])
                else:
                    match_questions[q['id']] = MatchQuestion(q['id'], q['label'], q['type'], q['offers'], q['answers'])

            return [team_questions, match_questions]
    else:
        with open('config/questions.json', 'r') as f:
            t = json.load(f)

            match_questions = {}
            team_questions = {}

            for q in t:
                if 'm_' in q['type']:
                    match_questions[q['id']] = MatchQuestion(q['id'], q['label'], q['type'], q['offers'])
                else:
                    team_questions[q['id']] = TeamQuestion(q['id'], q['label'], q['type'], q['offers'])

            return [team_questions, match_questions]