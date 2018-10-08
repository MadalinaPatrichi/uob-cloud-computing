#!/usr/bin/env python

import json
import subprocess
import os
import glob

# Define variables.
GH_OWNER = 'MadalinaPatrichi'
GH_REPO = 'uob-cloud-computing'
GH_API = "https://api.github.com"
GH_REPO_API = "{}/repos/{}/{}".format(GH_API, GH_OWNER, GH_REPO)
GH_TAGS = "{}/releases/latest".format(GH_REPO_API)
AUTH = "Authorization: token {}".format(os.environ['GH_API_TOKEN'])
WGET_ARGS = "--content-disposition --auth-no-challenge --no-cookie"
PATTERNS = [
    'build/libs/*.jar'
]

# Validate token.
if subprocess.call(['curl', '-o', '/dev/null', '-s', '-H', AUTH, GH_REPO_API]) != 0:
    raise Exception("Error: Invalid repo, token or network issue!")

# read relevant release
output = subprocess.check_output(['curl', '-H', AUTH, GH_TAGS])
data = json.loads(output)
print "Release Data:", data

# get id of the release
release_id = data['id']
print "Release ID:", release_id

for p in PATTERNS:
    items = glob.glob(p)
    print "Want to upload", items
    for item in items:
        print "Uploading", item
        exists = next((a for a in data['assets'] if a['name'] == os.path.basename(item)), None)
        if exists:
            asset_id = exists['id']
            print "Deleting existing asset with the same name"
            subprocess.check_call(['curl', '-H', AUTH, '-X', 'DELETE', '{}/releases/assets/{}'.format(GH_REPO_API, asset_id)])

        asset_url = "https://uploads.github.com/repos/{}/{}/releases/{}/assets?name={}".format(
            GH_OWNER, GH_REPO,
            release_id,
            os.path.basename(item)
        )

        print "Beginning upload of", item
        subprocess.check_call(['curl', '-H', AUTH, '--data-binary', '@' + item, '-H', 'Content-Type: application/octet-stream', asset_url])
