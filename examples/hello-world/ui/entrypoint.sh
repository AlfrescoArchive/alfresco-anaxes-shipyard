#!/bin/sh

sed -i s%HELLO_BACKEND_URL%"$HELLO_BACKEND_URL"%g /usr/share/nginx/html/app.config.json

nginx -g "daemon off;"
