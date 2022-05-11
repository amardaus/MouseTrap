from flask import Flask, jsonify, Response
from flask_sqlalchemy import SQLAlchemy
import pytz
from datetime import datetime, timezone
from flask import send_file
import cv2
import sys
sys.path.append("/home/pi/Documents/MouseTrap")
from Core.python_serwo import open_gate

app = Flask(__name__)
app.config ['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///mouse.sqlite3'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db =  SQLAlchemy(app)


class Detection(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	datetime = db.Column(db.DateTime, default=datetime.now(pytz.timezone('Europe/Warsaw')))
	img = db.Column(db.String)
	label = db.Column(db.String)
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
	d = Detection.query.order_by(Detection.id.desc())
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
		
@app.route('/verify/<int:id>')
def verify(id):
	detection = Detection.query.get(id)
	detection.verified = True
	db.session.commit()
	return "success"
	
@app.route('/change_token/<string:token>')
def change_token(token):
	username = "User"
	user = User.query.filter_by(username=username).first()
	if user is None:
		user = User(username=username,token=token)
		db.session.add(user)
		db.session.commit()
	else:
		user.token = token
		db.session.commit()
	return "success"

@app.route('/get_token')
def get_token():
	username = "User"
	user = User.query.filter_by(username=username).first()
	return user.token
	
@app.route('/add_detection/<string:img_name>')
def add_detection(img_name):
	label = "zwierze"
	username = "User"
	user = User.query.filter_by(username=username).first()
	detection = Detection(user_id=user.id,img=img_name,label=label)
	db.session.add(detection)
	db.session.commit()
	return "<p>new detection added</p>"
	
@app.route('/get_image/<int:id>')
def get_image(id):
	detection = Detection.query.get(id)
	file = "/home/pi/cam/" + detection.img
	return send_file(file, mimetype='image/jpg')

@app.route('/show_image/<string:img_name>')
def show_image(img_name):
	file = "/home/pi/cam/" + img_name
	try:
		return send_file(file, mimetype='image/jpg')
	except Exception as e:
		return "Image does not exist :((("

@app.route('/capture_image')
def capture_image():
	cam = cv2.VideoCapture(0)
	ret, frame = cam.read()
	if not ret:
		return("error")
	img_name = "frame_capture_" + datetime.now().strftime("%Y%m%d-%H%M%S") +".jpg"
	img_path = "/home/pi/cam/"
	cv2.imwrite(img_path + img_name, frame)
	cam.release()
	cv2.destroyAllWindows()
	return img_name

@app.route('/open_trap')
def open_trap():
	open_gate()
	return "success"

@app.route('/close_trap')
def close_trap():
        close_gate()
        return "success"
    
if __name__ == "__main__":
	db.create_all()
	app.run(host="0.0.0.0")
