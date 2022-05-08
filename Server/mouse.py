from flask import Flask, jsonify, Response
from flask_sqlalchemy import SQLAlchemy
import pytz
from datetime import datetime, timezone
from flask import send_file 
import cv2

app = Flask(__name__)
app.config ['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///mouse.sqlite3'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db =  SQLAlchemy(app)


class Detection(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	datetime = db.Column(db.DateTime, default=datetime.now(pytz.timezone('Europe/Warsaw')))
	img = db.Column(db.String)
	verified = db.Column(db.Boolean, default=False)
	user_id = db.Column(db.String, db.ForeignKey('user.id'))

	def __repr__(self):
		return self.id
	
	def as_dict(self):
		return {c.name: str(getattr(self, c.name)) for c in self.__table__.columns}

class User(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	username = db.Column(db.String, nullable=False)
	token = db.Column(db.String)
	detections = db.relationship('Detection', cascade='all, delete', lazy=True)

@app.route('/')
def hello():
	return "<p>Hello worm</p>"

@app.route('/get_all')
def get_all():
	d = Detection.query.order_by(Detection.datetime.desc())
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


@app.route('/get_last')
def get_last():
	d = Detection.query.filter_by(verified=False).order_by(Detection.datetime.desc()).first()
	if d is not None:
		detection = d.as_dict()
		return jsonify(detection)
	else:
		return jsonify({})
		
@app.route('/verify/<int:id>/')
def verify(id):
	detection = Detection.query.get(id)
	detection.verified = True
	db.session.commit()

@app.route('/change_token/<string:username>/<string:token>')
def change_token(username,token):
	user = User.query.filter_by(username=username).first()
	
	msg = "";
	if user is not None:
		user.token = token
		db.session.commit()
		return "success"
	else:
		return "User not found"

@app.route('/get_token/<string:username>')
def get_token(username):
	user = User.query.filter_by(username=username).first()
	return user.token

@app.route('/create_user/<string:username>/<string:token>')
def create_user(username,token):
	user = User(username=username,token=token)
	db.session.add(user)
	db.session.commit()
	
@app.route('/add_detection/<string:img_name>')
def add_detection(img_name):
	img_name = "_DSC0328.JPG"
	detection = Detection(user_id=1,img=img_name)
	db.session.add(detection)
	db.session.commit()
	return "<p>new detection added</p>"
	
@app.route('/get_image/<int:id>')
def get_image(id):
	detection = Detection.query.get(id)
	file = "C:\\Users\\amard\\Pictures\\" + detection.img
	return send_file(file, mimetype='image/jpg')

if __name__ == "__main__":
	db.create_all()
	app.run(host="0.0.0.0")
