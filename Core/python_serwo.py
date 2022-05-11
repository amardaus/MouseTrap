import RPi.GPIO as GPIO
import time

#initial config
# closed_position = 7   #dutycycle angle/18 +2
# opened_position = 10
# sleep_time = 2
# 
# servomechanism_pin = 11   

# def test_angle():
#     GPIO.setmode(GPIO.BOARD)
#     #setup
#     GPIO.setup(servomechanism_pin, GPIO.OUT)
#     pwm=GPIO.PWM(servomechanism_pin, 50) #pin and frequency
#     pwm.start(0)
#     pwm.ChangeDutyCycle(7)
#     time.sleep(sleep_time)
#     pwm.stop()
#     GPIO.cleanup()

def open_gate():
    closed_position = 7   #dutycycle angle/18 +2
    opened_position = 10
    sleep_time = 2

    servomechanism_pin = 11  
    GPIO.setmode(GPIO.BOARD)
    #setup
    GPIO.setup(servomechanism_pin, GPIO.OUT)
    pwm=GPIO.PWM(servomechanism_pin, 50) #pin and frequency
    pwm.start(0)
    pwm.ChangeDutyCycle(opened_position)
    time.sleep(sleep_time)
    pwm.stop()
    GPIO.cleanup()

def close_gate():
    closed_position = 7   #dutycycle angle/18 +2
    opened_position = 10
    sleep_time = 2

    servomechanism_pin = 11  
    GPIO.setmode(GPIO.BOARD)
    #setup
    GPIO.setup(servomechanism_pin, GPIO.OUT)
    pwm=GPIO.PWM(servomechanism_pin, 50) #pin and frequency
    pwm.start(0)
    pwm.ChangeDutyCycle(closed_position)
    time.sleep(sleep_time)
    pwm.stop()
    GPIO.cleanup()

if __name__ == '__main__':
    open_gate()
