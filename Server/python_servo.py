import RPi.GPIO as GPIO

def open_gate():
	#will this work?
	GPIO.setmode(GPIO.BOARD)

	#initial config
	opened_position = 12.5
	sleep_time = 1
	servomechanism_pin = 11

	#setup
	GPIO.setup(servomechanism_pin, GPIO.OUT)
	pwm=GPIO.PWM(servomechanism_pin, 50) #pin and frequency
	pwm.start(0)

	#open gate
	pwm.ChangeDutyCycle(opened_position)

	#cleanup
	pwm.stop()
	GPIO.cleanup()

def close_gate():
        #will this work?
        GPIO.setmode(GPIO.BOARD)

        #initial config
        closed_position = 10   #dutycycle angle/18 +2
        sleep_time = 1
        servomechanism_pin = 11

        #setup
        GPIO.setup(servomechanism_pin, GPIO.OUT)
        pwm=GPIO.PWM(servomechanism_pin, 50) #pin and frequency
        pwm.start(0)

        #open gate
        pwm.ChangeDutyCycle(closed_position)

        #cleanup
        pwm.stop()
        GPIO.cleanup()
