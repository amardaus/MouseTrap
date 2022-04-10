import requests
import json

serverToken = 'AAAAncoJdu4:APA91bFaWWdQzg5vGi-vPuRGJ2iwArMBBQrqPrJ7Mhnyw9ZD_0elLNWwUBF3ZuhSOzpUz9p6CBj4Uo78uqluN3arh30DjQwtCtIZNmh3t_zx0Hl_hFlnW-oeZp_Zh6lZ9rNPB_BEQnAS'
deviceToken = 'fSZRGVzXxHU:APA91bHduyakfVPqOMdBjc7NSFwgAhkZzKSUrZpRasaX6dQin9WhlknRjz9zfPfTJtP2lybMnGrLyp04ZpaloxuZt01-NaTVpDxGct5UPuqm-PS9gyGHnDOreVsSCwhJ7GtceqFEvY5S'

headers = {
	'Content-Type': 'application/json',
	'Authorization': 'key=' + serverToken,
}

body = {
	'notification': {
		'title': 'Mouse detected!',
		'body': 'Check it out!'
	},
	'to':
		deviceToken,
	'priority': 
		'high',
}

response = requests.post('https://fcm.googleapis.com/fcm/send', headers = headers, data = json.dumps(body))
print(response.status_code)
print(response.json())