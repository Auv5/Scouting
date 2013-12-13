import json
from models.question import Question


def get_questions():
    with open('config/questions.json', 'r') as f:
        return [Question(q['id'], q['label'], q['type'], q['offers']) for q in json.load(f)]