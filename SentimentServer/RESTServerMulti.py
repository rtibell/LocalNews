from flask import Flask, jsonify, request

from transformers import AutoTokenizer, AutoModelForSequenceClassification, pipeline

class SentimentRequest():
    workload: list[str] = []

class SentimentResponse():
    label: str
    score: float

    def __init__(self, label, score):
        self.label = label
        self.score = score

tokenizer = AutoTokenizer.from_pretrained("KBLab/megatron-bert-large-swedish-cased-165k")
model = AutoModelForSequenceClassification.from_pretrained("KBLab/robust-swedish-sentiment-multiclass")

print("start classifier...")
classifier = pipeline("sentiment-analysis", model=model, tokenizer=tokenizer)

print("start REST-server")
app = Flask(__name__)

#@app.post('/sentiment') #, response_model=list[SentimentResponse], status_code=201)
#async def compute_sentiment(req: SentimentRequest) -> list[SentimentResponse]:
@app.route('/sentiment', methods=['POST'])
def compute_sentiment():
    request_data = request.get_json()
    results = classifier(request_data['workload'])
    ret_val = [SentimentResponse(result['label'], result['score']) for result in results]
    return jsonify(ret_val), 201

if __name__ == '__main__':
    app.run(debug=True)
