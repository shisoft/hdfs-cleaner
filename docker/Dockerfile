FROM factual/docker-cdh5-base:java8

RUN apt-get update && apt-get install -y ldap-auth-config git-core
RUN apt-get install -y ruby-full
RUN apt-get install -y openjdk-8-jdk-headless
RUN apt-get clean
RUN rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8

ENV LEIN_ROOT yes
RUN curl https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -o /usr/bin/lein && chmod +x /usr/bin/lein

ADD source /source
ADD scripts /scripts
WORKDIR /source

EXPOSE 4050

CMD ["bash", "/scripts/entrypoint.sh"]
