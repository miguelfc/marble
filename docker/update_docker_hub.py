"""Utility script to update docker hub descriptions using the Rest API.

Usage:
  update_docker_hub.py update --username=<username> --password=<password> [--image=<image>]
  update_docker_hub.py (-h | --help)
  update_docker_hub.py --version

Options:
  -h --help                 Show this screen.
  --version                 Show version.
  --username=<username>     Docker Hub username
  --password=<password>     Docker Hub password
  --image=<image>           Image name to update 

"""
from docopt import docopt
import pycurl
import sys
import os
import glob
import certifi
import json
import io
from io import BytesIO
from urllib.parse import urlencode


short_desc_name = "README-short.txt"
full_desc_name = "README.md"
docker_subpath = "src/main/docker"

if __name__ == '__main__':
    arguments = docopt(__doc__, version='1.0')
    # print(arguments)

    update = arguments['update']

    if (update):
        username = arguments['--username']
        password = arguments['--password']
        image = arguments['--image']

        images_to_update = []

        if (image):
            images_to_update.append(image)
        else:
            for full_dir_path in glob.glob("../marble-*"):
                image_name = os.path.basename(full_dir_path)
                if (os.path.isdir(os.path.join(full_dir_path, docker_subpath))):
                    images_to_update.append(image_name)

        buffer = BytesIO()
        c = pycurl.Curl()
        post_data = {'username': username, 'password': password}
        postfields = urlencode(post_data)
        c.setopt(c.POSTFIELDS, postfields)
        c.setopt(pycurl.CAINFO, certifi.where())
        c.setopt(c.URL, 'https://hub.docker.com/v2/users/login/')
        c.setopt(c.WRITEDATA, buffer)
        print("Getting Token...")
        c.perform()
        status = c.getinfo(c.RESPONSE_CODE)
        c.close()

        if (status != 200):
            sys.exit("Wrong response code received: <" + str(status) + ">")
        token_dict = json.loads(buffer.getvalue())
        token = token_dict['token']
        #print(token)

        for image in images_to_update:
            docker_path = os.path.join("../", image, docker_subpath)
            short_desc = os.path.join(docker_path, short_desc_name)
            full_desc = os.path.join(docker_path, full_desc_name)
            
            if (os.path.isfile(short_desc) and os.path.isfile(full_desc)):
                with io.open(short_desc,'r',encoding='utf8') as f:
                    short_desc_content = f.read()
                with io.open(full_desc,'r',encoding='utf8') as f:
                    full_desc_content = f.read()
                
                buffer = BytesIO()
                c = pycurl.Curl()
                post_data = {}
                post_data['description'] = short_desc_content
                post_data['full_description'] = full_desc_content
                postfields = urlencode(post_data)

                c.setopt(c.POSTFIELDS, postfields)
                c.setopt(pycurl.CAINFO, certifi.where())
                c.setopt(pycurl.CUSTOMREQUEST, "PATCH")
                c.setopt(pycurl.HTTPHEADER, ['Authorization: JWT ' + token])
                c.setopt(
                    c.URL, 'https://hub.docker.com/v2/repositories/miguelfc/' + image + "/")
                c.setopt(c.WRITEDATA, buffer)
                print("Updating description for image <" + image + ">...")
                c.perform()
                status = c.getinfo(c.RESPONSE_CODE)
                c.close()
                if (status == 404):
                    #print (buffer.getvalue())
                    print("Image not found on Docker Hub: <" + str(status) + ">")
                elif (status != 200):
                    #print (buffer.getvalue())
                    sys.exit("Wrong response code received: <" + str(status) + ">")

            else:
                print ("WARNING: Image " + image +
                       " doesn't have the required description files. Skipping.")

        print ("Done.")
