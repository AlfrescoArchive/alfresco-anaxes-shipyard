# Welcome

Build:

    npm install

To run just the UI locally:

    npm start

To run UI with backend, change `HELLO_BACKEND_HOST` and `HELLO_BACKEND_HOST` in `src/assets/app.config.json` to the host and port where your service is running. Then run: `npm start`

Build Docker Image:
    
    docker build . -t [image-name]:latest

Run Docker:

    docker run -p 4200:80 -e "HELLO_BACKEND_HOST=[backend-host]" -e "HELLO_BACKEND_PORT=[backend-port]" [image-name]:latest