#!/bin/sh

sed -i s%HELLO_BACKEND_URL%"$HELLO_BACKEND_URL"%g /usr/share/nginx/html/app.config.json
sed -i '/index  index.html index.htm;/ a\
        try_files $uri $uri/ /index.html;' /etc/nginx/conf.d/default.conf
nginx -g "daemon off;"
