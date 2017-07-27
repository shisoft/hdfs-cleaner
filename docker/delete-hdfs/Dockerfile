FROM factual/docker-cdh5-base:java8

ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8

ADD scripts /scripts
WORKDIR /scripts

CMD ["bash", "/scripts/entrypoint.sh"]