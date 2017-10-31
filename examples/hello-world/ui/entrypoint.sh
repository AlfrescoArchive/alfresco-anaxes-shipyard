#!/bin/sh

sed -i s/HELLO_BACKEND_HOST/"$HELLO_BACKEND_HOST"/g /usr/share/nginx/html/app.config.json
sed -i s/HELLO_BACKEND_PORT/"$HELLO_BACKEND_PORT"/g /usr/share/nginx/html/app.config.json

nginx -g "daemon off;"
