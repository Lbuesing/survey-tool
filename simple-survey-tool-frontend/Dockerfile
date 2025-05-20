# Step 1: Build the application using a Node.js base image
FROM node:16 AS build

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy all the source files into the container
COPY . .
ARG VUE_APP_BACKEND_URL
ENV VUE_APP_BACKEND_URL=$VUE_APP_BACKEND_URL
RUN echo "Building with API: $VUE_APP_BACKEND_URL"
# Build the Vue.js app
RUN npm run build

# Step 2: Create a production image with Nginx to serve the built Vue app
FROM nginx:alpine

# Remove default nginx website
RUN rm -rf /usr/share/nginx/html/*

# Copy the built app from the previous step to the Nginx default directory
COPY --from=build /app/dist /usr/share/nginx/html

# Copy custom Nginx config to handle Vue SPA routing
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80 for the app
EXPOSE 80

# Start Nginx server to serve the app
CMD ["nginx", "-g", "daemon off;"]
