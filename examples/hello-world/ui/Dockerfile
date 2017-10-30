FROM node
COPY . /hello-ui-build
RUN chown -R root:root /hello-ui-build/* && chmod +x /hello-ui-build/entrypoint.sh && cd /hello-ui-build && npm install && npm run build

FROM nginx:1.13.6-alpine

COPY --from=0 /hello-ui-build/dist /usr/share/nginx/html
COPY --from=0 /hello-ui-build/entrypoint.sh /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]