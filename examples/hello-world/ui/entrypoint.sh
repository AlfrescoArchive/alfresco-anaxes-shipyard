#!/bin/bash

sed -i s/TARGETHOST/"$SERVICE_HOST"/g proxy.conf.json
sed -i s/TARGETPORT/"$SERVICE_PORT"/g proxy.conf.json

npm start
