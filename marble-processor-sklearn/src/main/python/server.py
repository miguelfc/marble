# Rest Server
from flask import Flask, jsonify, abort, request
# Eureka client
from eureka.client import EurekaClient
# Background tasks
import threading
import atexit
import logging
import socket
import netifaces as ni
import sys
import os
import time

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn import svm
from sklearn.metrics import classification_report

import nltk
import numpy as np
nltk.download('movie_reviews')
from nltk.corpus import movie_reviews

pool_time = 5  # Seconds

# variables that are accessible from anywhere
commonDataStruct = {}
# lock to control access to variable
dataLock = threading.Lock()
# thread handler
yourThread = threading.Thread()

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Global variables
app_name = "processor-sklearn"

try:
    ni.ifaddresses('eth0')
    app_ip = ni.ifaddresses('eth0')[2][0]['addr']
except Exception:
    app_ip = "localhost"
app_host = socket.gethostname()
app_port = 8080
secure_app_port = 8443

eureka_url = "http://registry:1111/eureka/"

np.set_printoptions(precision=8)

# Read the data
classes = ['pos', 'neg']
train_data = []
train_labels = []
test_data = []
test_labels = []
for curr_class in classes:
    ids = movie_reviews.fileids(curr_class)
    for f in ids:
        train_data.append(movie_reviews.raw(f))
        train_labels.append(curr_class)

# Create feature vectors
vectorizer = TfidfVectorizer(min_df=5,
                             max_df=0.8,
                             sublinear_tf=True,
                             use_idf=True)
train_vectors = vectorizer.fit_transform(train_data)

# Perform classification with SVM, kernel=rbf
classifier_rbf = svm.SVC()
t0 = time.time()
classifier_rbf.fit(train_vectors, train_labels)
t1 = time.time()
time_rbf_train = t1 - t0
logger.info("Results for SVC(kernel=rbf)")
logger.info("Training time: %fs" % (time_rbf_train))

# Perform classification with SVM, kernel=linear
classifier_linear = svm.SVC(kernel='linear')
t0 = time.time()
classifier_linear.fit(train_vectors, train_labels)
t1 = time.time()
time_linear_train = t1 - t0
logger.info("Results for SVC(kernel=linear)")
logger.info("Training time: %fs" % (time_linear_train))

# Perform classification with SVM, kernel=linear
classifier_liblinear = svm.LinearSVC()
t0 = time.time()
classifier_liblinear.fit(train_vectors, train_labels)
t1 = time.time()
time_liblinear_train = t1 - t0
logger.info("Results for LinearSVC()")
logger.info("Training time: %fs" % (time_liblinear_train))

def create_app():
    app = Flask(__name__)

    def interrupt():
        global yourThread
        yourThread.cancel()

    def doStuff():
        global commonDataStruct
        global yourThread
        with dataLock:
            # TODO: Handle what happens when eureka goes down
            try:
                commonDataStruct['ec'].heartbeat()
            except Exception:
                logger.info("Registering to Eureka...")
                try:
                    commonDataStruct['ec'].register(initial_status="UP")
                    logger.info("Registered to Eureka.")
                    commonDataStruct['ec'].heartbeat()
                except Exception as e:
                    logger.warning(
                        "Caught exception while trying to register in Eureka: " + str(e) + ". Will retry again shortly.")
                    exc_type, exc_obj, exc_tb = sys.exc_info()
                    fname = os.path.split(
                        exc_tb.tb_frame.f_code.co_filename)[1]
                    print((exc_type, fname, exc_tb.tb_lineno))

        # Set the next thread to happen
        yourThread = threading.Timer(pool_time, doStuff, ())
        yourThread.start()

    def doStuffStart():
        # Do initialisation stuff here
        # no spaces or underscores, this needs to be url-friendly

        commonDataStruct['ec'] = EurekaClient(app_name,
                                              ip_address=app_ip,
                                              eureka_url=eureka_url,
                                              eureka_domain_name="",
                                              data_center="MyOwn",
                                              port=app_port,
                                              secure_port=None,
                                              use_dns=False,
                                              region="none",
                                              prefer_same_zone=False,
                                              context="",
                                              host_name=app_host,
                                              vip_address=app_name,
                                              secure_vip_address=app_name)

        global yourThread
        # Create your thread
        yourThread = threading.Timer(pool_time, doStuff, ())
        yourThread.start()

    # Initiate
    doStuffStart()
    # When you kill Flask (SIGTERM), clear the trigger for the next thread
    atexit.register(interrupt)
    return app


def processMessage(message, options):
    polarity = None

    if (options['type'] == "svc_rbf"):
        logger.debug("Processing message <" + message + "> using svc_rbf.")
        transformed = vectorizer.transform([message])
        ss = classifier_rbf.decision_function(transformed)[0]
        polarity = ss
    elif (options['type'] == "svc_linear"):
        logger.debug("Processing message <" + message + "> using svc_linear.")
        transformed = vectorizer.transform([message])
        ss = classifier_linear.decision_function(transformed)[0]
        polarity = ss
    elif (options['type'] == "svc_liblinear"):
        logger.debug("Processing message <" + message + "> using svc_liblinear.")
        transformed = vectorizer.transform([message])
        ss = classifier_rbf.decision_function(transformed)[0]
        polarity = ss

    if (polarity != None):
        response = {
            'valid': True,
            'polarity': polarity,
            'message':  message,
            'notes': "Processed message <" + message + "> using " + app_name + " processor."
        }
    else:
        response = {
            'valid': False,
            'polarity': None,
            'message':  message,
            'notes': "Invalid type. Processed message <" + message + "> using " + app_name + " processor."
        }
    return response

app = create_app()


@app.route('/api/process', methods=['POST'])
def process():
    if not request.json or not 'message' in request.json:
        abort(400)
    response = processMessage(
        request.json['message'], request.json.get('options', {}))
    return jsonify(response), 200

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=app_port)
