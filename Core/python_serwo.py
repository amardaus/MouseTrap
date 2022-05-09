import RPi.GPIO as GPIO


#will this work?
GPIO.setmode(GPIO.BOARD)

#initial config
closed_position = 10   #dutycycle angle/18 +2
opened_position = 12.5
sleep_time = 1

servomechanism_pin = 11

#setup
GPIO.setup(servomechanism_pin, GPIO.OUT)
pwm=GPIO.PWM(servomechanism_pin, 50) #pin and frequency
pwm.start(0)

def open_gate(pwm):
    pwm.ChangeDutyCycle(12.5)

def close_gate(pwm):
    pwm.ChangeDutyCycle(10)

def cleanup():
    pwm.stop()
    GPIO.cleanup()