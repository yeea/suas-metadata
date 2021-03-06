/bin/bash 
# Download elasticsearch image
docker pull docker.elastic.co/elasticsearch/elasticsearch-oss:6.5.4

# Download kibana image:
docker pull docker.elastic.co/kibana/kibana-oss:6.5.4

# Run & Install ElasticSearch:
docker run --restart always -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch-oss:6.5.4

# Run & Install kibana:
docker run --restart always -d --net=host -e "ELASTICSEARCH_URL=http://localhost:9200" docker.elastic.co/kibana/kibana-oss:6.5.4
