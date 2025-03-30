Для старта всех контейнеров

` docker compose up -d `

Затем необходимо наполнить базу данных: 
```
docker exec -it postgres psql -h 127.0.0.1 -U postgres-user -d customers
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    product_name VARCHAR(100),
    quantity INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Добавление пользователей
INSERT INTO users (name, email) VALUES ('John Doe', 'john@example.com');
INSERT INTO users (name, email) VALUES ('Jane Smith', 'jane@example.com');
INSERT INTO users (name, email) VALUES ('Alice Johnson', 'alice@example.com');
INSERT INTO users (name, email) VALUES ('Bob Brown', 'bob@example.com');


-- Добавление заказов
INSERT INTO orders (user_id, product_name, quantity) VALUES (1, 'Product A', 2);
INSERT INTO orders (user_id, product_name, quantity) VALUES (1, 'Product B', 1);
INSERT INTO orders (user_id, product_name, quantity) VALUES (2, 'Product C', 5);
INSERT INTO orders (user_id, product_name, quantity) VALUES (3, 'Product D', 3);
INSERT INTO orders (user_id, product_name, quantity) VALUES (4, 'Product E', 4);

exit
```


Команда для конфигурации Kafka Connect (настройки будут взяты из файла connector.json в этой папке)

`curl -X PUT -H 'Content-Type: application/json' --data @connector.json http://localhost:8083/connectors/pg-connector/config`

После этого можно открыть kafka ui 
`http://localhost:8080/ui/clusters/kraft/all-topics?perPage=25`

в топиках customers.public.users и customers.public.orders будут сообщения реплецирующие данные которые вставлены выше

Для просмотра Prometeus 
`http://localhost:9090/classic/graph`

Для просмотра Grafany 
`http://localhost:3000/dashboard/import`

Нажимаем Upload Json file 
и выбираем ./lesson3/grafana/dashboards/connect.json
Указываем названия и создаем дашборд

