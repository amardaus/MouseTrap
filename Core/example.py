#! /usr/bin/python2
 
import time
import sys
import requests
import numpy as np
import json
# from python_serwo import close_gate, cleanup

EMULATE_HX711=True

#divisor to scale
referenceUnit = 1

#mass of popular animals
waga_myszy = 40
waga_lasicy = 100
waga_szczura = 400
waga_kota = 4000
mass_to_animal = {0: "brak", waga_myszy : "mysz", waga_lasicy : "lasica",  waga_szczura : "szczur",  waga_kota : "kot"}
animal_mass = np.asarray([0, waga_myszy, waga_lasicy, waga_szczura, waga_kota])

#post notification params
API_ENDPOINT = "http://192.168.112.107:5000"
MOTION_ENDPOINT = "http://192.168.112.107:8082/0"

if not EMULATE_HX711:
    import RPi.GPIO as GPIO
    from hx711 import HX711
else:
    from emulated_hx711 import HX711

def find_nearest(value):
    """to classify value from a load cell to one of the animals"""
    idx = (np.abs(animal_mass - value)).argmin()
    return mass_to_animal.get(animal_mass[idx])

def get_image():
    """ function disable a camera stream on Motion linux server and take a picture and then enable stream again"""
    try:
        motion_status = requests.get(f'{MOTION_ENDPOINT}/detection/status')
        if "ACTIVE" in motion_status.text:
            requests.get(f'{MOTION_ENDPOINT}/action/quit')
            time.sleep(1)
            image_name_request = requests.get(f"{API_ENDPOINT}/capture_image")
            requests.get(f'{MOTION_ENDPOINT}/action/restart')
            return image_name_request.text
        elif "NOT RUNNING" in motion_status.text:
            image_name_request = requests.get(f"{API_ENDPOINT}/capture_image")
            requests.get(f'{MOTION_ENDPOINT}/action/restart')
            return image_name_request.text
    except Exception as e:
        print(e)
        print("couldnt disable a stream")
        return "plant.jpg"

def actionAfterDetection():
    """TODO list of actions if something is detected in the cage"""
    # close_gate() #python_serwo close gate function call
    image_name = get_image()
    requests.get(f'{API_ENDPOINT}/add_detection/{image_name}')
    # send send image to ai
    send_notification()

def send_notification():
    serverToken = 'AAAAncoJdu4:APA91bFaWWdQzg5vGi-vPuRGJ2iwArMBBQrqPrJ7Mhnyw9ZD_0elLNWwUBF3ZuhSOzpUz9p6CBj4Uo78uqluN3arh30DjQwtCtIZNmh3t_zx0Hl_hFlnW-oeZp_Zh6lZ9rNPB_BEQnAS'

    headers = {
        'Content-Type': 'application/json',
        'Authorization': 'key=' + serverToken,
    }

    res1 = requests.get(f'{API_ENDPOINT}/get_token')
    deviceToken = res1.content.decode('utf-8')

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
    requests.post('https://fcm.googleapis.com/fcm/send', headers=headers, data=json.dumps(body))

def measure_load():
    hx = HX711(5, 6)
    hx.set_reading_format("MSB", "MSB")

    """HOW TO CALCULATE THE REFFERENCE UNIT
    To set the reference unit to 1. Put 1kg on your sensor or anything you have and know exactly how much it weights.
    In this case, 92 is 1 gram because, with 1 as a reference unit I got numbers near 0 without any weight
    and I got numbers around 184000 when I added 2kg. So, according to the rule of thirds:
    If 2000 grams is 184000 then 1000 grams is 184000 / 2000 = 92."""

    hx.set_reference_unit(113)

    hx.set_reference_unit(referenceUnit)

    hx.reset()

    hx.tare()

    print("Tare done! Add weight now...")

    while True:
        try:
            current_median = hx.read_median() #change to this value
            print(f"Wynik przyporzadkowania to: zwierze {find_nearest(current_median)} i wartosc {current_median}")

            if current_median > 70000:
                actionAfterDetection()
                break
            # if find_nearest(current_median) != "brak":
            #     image_name = get_image()
            #     actionAfterDetection(image_name)

            hx.power_down()
            hx.power_up()
            time.sleep(0.1)

        except (KeyboardInterrupt, SystemExit):
            # cleanup()
            cleanAndExit()

def cleanAndExit():
    print("Cleaning...")

    if not EMULATE_HX711:
        GPIO.cleanup()
        
    print("Bye!")
    sys.exit()

if __name__ == '__main__':
    measure_load()