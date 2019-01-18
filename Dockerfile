FROM anapsix/alpine-java

COPY build/distributions/rpa-speak.tar /

WORKDIR /

RUN tar -xf rpa-speak.tar

EXPOSE 5050

ENTRYPOINT ["/rpa-speak/bin/rpa-speak"]
