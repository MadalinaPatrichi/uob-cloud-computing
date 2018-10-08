#!/bin/bash

set -e
set -x
set -u

# Define variables.
GH_OWNER=MadalinaPatrichi
GH_REPO=uob-cloud-computing
GH_API="https://api.github.com"
GH_REPO_API="$GH_API/repos/$GH_OWNER/$GH_REPO"
GH_TAGS="$GH_REPO_API/releases/latest"
AUTH="Authorization: token $GH_API_TOKEN"
WGET_ARGS="--content-disposition --auth-no-challenge --no-cookie"
CURL_ARGS="-LJO#"
FILENAME=build/libs/uob-todo-app-0.1.0.jar

# Validate token.
curl -o /dev/null -sH "$AUTH" $GH_REPO_API || { echo "Error: Invalid repo, token or network issue!";  exit 1; }

# Read asset tags.
response=$(curl -sH "$AUTH" $GH_TAGS)

# Get ID of the asset based on given filename.
eval $(echo "$response" | grep -m 1 "id.:" | grep -w id | tr : = | tr -cd '[[:alnum:]]=')
[ "$id" ] || { echo "Error: Failed to get release id for tag: latest"; echo "$response" | awk 'length($0)<100' >&2; exit 1; }

# Upload asset
echo "Uploading asset... "

# Construct url
GH_ASSET="https://uploads.github.com/repos/$GH_OWNER/$GH_REPO/releases/$id/assets?name=$(basename $FILENAME)"

curl -H "$AUTH" --data-binary @"$FILENAME" -H "Content-Type: application/octet-stream" $GH_ASSET
