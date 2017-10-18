FROM node
COPY . /npmapp
RUN chown -R root:root /npmapp/* && chmod +x /npmapp/entrypoint.sh && cd /npmapp && npm install
EXPOSE 4200
WORKDIR /npmapp
ENTRYPOINT ["./entrypoint.sh"]