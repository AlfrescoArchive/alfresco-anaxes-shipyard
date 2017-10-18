# Welcome

Build: mvn clean package

To run just the UI locally: npm start
To run UI with backend: 
    
    change in "proxy.conf.json" TARGETHOST and TARGETPORT to the ip and port where your service is running. Then run "npm start".

Build Docker Image:
    
    docker build . -t [image-name]:latest

Run Docker:

    docker run -p 4200:4200 -e "SERVICE_HOST=[backend-ip]" -e "SERVICE_PORT=[backend-port]" [image-name]:latest