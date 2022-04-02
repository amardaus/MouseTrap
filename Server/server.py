import requests
import json

serverToken = 'AAAAncoJdu4:APA91bFaWWdQzg5vGi-vPuRGJ2iwArMBBQrqPrJ7Mhnyw9ZD_0elLNWwUBF3ZuhSOzpUz9p6CBj4Uo78uqluN3arh30DjQwtCtIZNmh3t_zx0Hl_hFlnW-oeZp_Zh6lZ9rNPB_BEQnAS'
deviceToken = 'fDmkUqHduFQ:APA91bHkKspufsDvNn8PBYuUvTLE4y5QQkPPo2f_LlkTkmwNVxodHorYW_RyJ6SnTWR3Uw6LrNncyWNt7jgdZAOjGJJqXiewTBk_RY6dD4dDMevtFJPXOmzJ1sIovbnyhllj6WIFlEb8'

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