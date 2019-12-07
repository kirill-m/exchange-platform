FROM nginx

ENV JS_STATIC_DIR /usr/share/nginx/js
ENV BUNDLE_DIR $JS_STATIC_DIR/frontend

EXPOSE 8080

RUN mkdir $JS_STATIC_DIR
RUN mkdir $BUNDLE_DIR

COPY build/bundle/frontend.bundle.js $BUNDLE_DIR
COPY resources/nginx.conf /etc/nginx/nginx.conf

CMD ["nginx", "-g", "daemon off;"]

