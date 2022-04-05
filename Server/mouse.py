from flask import Flask, jsonify
from flask_sqlalchemy import SQLAlchemy
import pytz
from datetime import datetime, timezone

app = Flask(__name__)
app.config ['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///mouse.sqlite3'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db =  SQLAlchemy(app)



class Detection(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	user = db.Column(db.String, nullable=False)
	date = db.Column(db.Date, default=datetime.now(pytz.timezone('Europe/Warsaw')).date())
	time = db.Column(db.Time, default=datetime.now(pytz.timezone('Europe/Warsaw')).time())

	def __repr__(self):
		return self.id
	
	def as_dict(self):
		return {c.name: str(getattr(self, c.name)) for c in self.__table__.columns}


@app.route('/')
def hello():
	return "<p>Hello worm</p>"

@app.route('/get_all')
def get_all():
	d = Detection.query.all()
	if d is not None:
		detections = [detection.as_dict() for detection in d]
		return jsonify(detections)
	else:
		return jsonify([])
    
@app.route('/get/<int:id>/')
def get(id):
	d = Detection.query.get(id)
	if d is not None:
		detection = d.as_dict()
		return jsonify(detection)
	else:
		return jsonify({})


@app.route('/add')
def add():
	detection = Detection(user="Kurczak")
	db.session.add(detection)
	db.session.commit()
	return "<p>added</p>"

if __name__ == "__main__":
	db.create_all()
	app.run()
