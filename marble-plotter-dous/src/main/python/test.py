from io import BytesIO
import gridfs
from pymongo import MongoClient

client = MongoClient('mongodb', 27017)

DATABASE_NAME = 'marble'
POSTS_COLLECTION = 'posts'
PROCESSED_POSTS_COLLECTION = 'processed_posts'

# Temp Variables
topicName = 'Apple Microsoft'

db = client[DATABASE_NAME]
fs = gridfs.GridFS(db)

posts_collection = db.get_collection(POSTS_COLLECTION)
processed_posts_collection = db.get_collection(PROCESSED_POSTS_COLLECTION)

posts = posts_collection.find({'topicName': topicName})

# for post in posts:
#    print(post['_id'])

import numpy as np
import matplotlib.pyplot as plt


N = 50
x = np.random.rand(N)
y = np.random.rand(N)
colors = np.random.rand(N)
area = np.pi * (15 * np.random.rand(N))**2  # 0 to 15 point radii

plt.scatter(x, y, s=area, c=colors, alpha=0.5)
#plt.show()
my_plot = plt.gcf()

imgdata = BytesIO()
my_plot.savefig(imgdata, format='png')
my_plot.savefig('imgdata.png', format='png')
imgdata.seek(0)
b = fs.put(imgdata, filename="foo.png", topicName=topicName)
print(b)

chart_1 = Chart(
    title='Sample Post',
    content='Some engaging content',
    author='Scott'
)
chart_1.save()       # This will perform an insert


class Chart(Document):
    title = StringField()
    published = BooleanField()
