import requests
import json

serverToken = 'AAAAncoJdu4:APA91bFaWWdQzg5vGi-vPuRGJ2iwArMBBQrqPrJ7Mhnyw9ZD_0elLNWwUBF3ZuhSOzpUz9p6CBj4Uo78uqluN3arh30DjQwtCtIZNmh3t_zx0Hl_hFlnW-oeZp_Zh6lZ9rNPB_BEQnAS'

headers = {
	'Content-Type': 'application/json',
	'Authorization': 'key=' + serverToken,
}

res1 = requests.get('http://127.0.0.1:5000/get_token/Kurczak')
deviceToken = res1.content.decode('utf-8')
print(deviceToken)

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

res2 = requests.post('https://fcm.googleapis.com/fcm/send', headers = headers, data = json.dumps(body))
print(res2.status_code)
print(res2.json())