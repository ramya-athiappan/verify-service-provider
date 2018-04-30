FROM govukverify/java8

WORKDIR /app

ADD configuration/local/vsp.yml vsp.yml
ADD build/distributions/verify-service-provider-*.zip vsp.zip

RUN unzip vsp.zip

CMD verify-service-provider-*-local/bin/verify-service-provider server vsp.yml

