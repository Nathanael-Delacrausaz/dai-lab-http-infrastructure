FROM nginx:latest

# Remove default Nginx configuration
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy website files
COPY html /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
