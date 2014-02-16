import json
from models.question import Question


def get_questions(data=False):
    if data:
        with open('data/questions.json') as f:
            t = json.load(f)

            return dict((q['id'], Question(q['id'], q['label'], q['type'], q['offers'], q['answers'])) for q in t)
            return {}
    else:
        with open('config/questions.json', 'r') as f:
            return dict((q['id'], Question(q['id'], q['label'], q['type'], q['offers'])) for q in json.load(f))
