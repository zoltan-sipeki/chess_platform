call mvn clean install -f .\common\pom.xml
call mvn clean package -DskipTests && docker compose up --build -d