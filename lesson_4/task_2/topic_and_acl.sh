#!/bin/bash

BROKER="kafka-1:9093"


function kafka_command {
  docker exec -i kafka-1 kafka-topics \
    --bootstrap-server $BROKER \
    --command-config /tmp/admin-client.properties \
    "$@"
}

function acl_command {
  docker exec -i kafka-1 kafka-acls \
    --bootstrap-server $BROKER \
    --command-config /tmp/admin-client.properties \
    "$@"
}


docker exec -i kafka-1 bash -c 'cat > /tmp/admin-client.properties <<EOF
bootstrap.servers=localhost:9093
security.protocol=SASL_PLAINTEXT
sasl.mechanism=PLAIN
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="admin" password="admin-secret";
EOF'

# Создание топиков
echo "Creating topics..."
kafka_command --create --topic topic_1 --partitions 3 --replication-factor 3
kafka_command --create --topic topic_2 --partitions 3 --replication-factor 3

# Настройка ACL
echo "Configuring ACLs..."
acl_command --add --allow-principal "User:*" --operation All --topic topic_1 --group "*"
acl_command --add --allow-principal "User:*" --operation Write --topic topic_2
acl_command --add --deny-principal "User:*" --operation Read --topic topic_2 --group "*"