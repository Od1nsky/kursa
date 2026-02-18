#!/bin/bash

PROJECT_NAME="kursach_vlad"
DB_NAME="kursach_vlad"
TOMCAT_DIR="apache-tomcat-10"
WAR_FILE="target/kursach.war"
PID_FILE="tomcat10.pid"

echo "=========================================="
echo "Запуск проекта: $PROJECT_NAME"
echo "=========================================="

# Функция для запуска PostgreSQL
start_postgresql() {
    echo "Попытка запуска PostgreSQL..."
    
    # Проверяем наличие brew
    if command -v brew > /dev/null 2>&1; then
        # Пытаемся определить версию PostgreSQL
        PG_VERSION=$(psql --version 2>/dev/null | grep -oE '[0-9]+\.[0-9]+' | head -1)
        if [ -n "$PG_VERSION" ]; then
            MAJOR_VERSION=$(echo $PG_VERSION | cut -d. -f1)
            echo "Обнаружена версия PostgreSQL: $MAJOR_VERSION"
            
            # Пытаемся запустить через brew services
            if brew services start postgresql@$MAJOR_VERSION > /dev/null 2>&1; then
                echo "PostgreSQL запущен через brew services ✓"
                sleep 3
                return 0
            fi
        fi
        
        # Пробуем общий вариант
        if brew services start postgresql > /dev/null 2>&1; then
            echo "PostgreSQL запущен через brew services ✓"
            sleep 3
            return 0
        fi
    fi
    
    # Пытаемся найти data directory и запустить через pg_ctl
    if command -v pg_ctl > /dev/null 2>&1; then
        # Ищем data directory в стандартных местах
        for DATA_DIR in "$HOME/Library/Application Support/Postgres/var-$PG_VERSION" \
                        "/opt/homebrew/var/postgresql@$MAJOR_VERSION" \
                        "/usr/local/var/postgresql@$MAJOR_VERSION" \
                        "/opt/homebrew/var/postgres" \
                        "/usr/local/var/postgres"; do
            if [ -d "$DATA_DIR" ]; then
                echo "Найден data directory: $DATA_DIR"
                if pg_ctl -D "$DATA_DIR" start > /dev/null 2>&1; then
                    echo "PostgreSQL запущен через pg_ctl ✓"
                    sleep 3
                    return 0
                fi
            fi
        done
    fi
    
    echo "Не удалось автоматически запустить PostgreSQL"
    return 1
}

# Проверка PostgreSQL
echo "Проверка PostgreSQL..."
if ! pg_isready -h localhost -p 5433 > /dev/null 2>&1; then
    echo "PostgreSQL не запущен, пытаюсь запустить..."
    if ! start_postgresql; then
        echo "ОШИБКА: Не удалось запустить PostgreSQL автоматически!"
        echo "Пожалуйста, запустите PostgreSQL вручную:"
        echo "  brew services start postgresql@14"
        echo "или"
        echo "  pg_ctl -D <data_directory> start"
        exit 1
    fi
    
    # Дополнительная проверка после запуска
    sleep 2
    if ! pg_isready -h localhost -p 5433 > /dev/null 2>&1; then
        echo "ОШИБКА: PostgreSQL все еще не доступен после попытки запуска!"
        exit 1
    fi
fi
echo "PostgreSQL запущен ✓"

# Проверка существования базы данных
echo "Проверка базы данных $DB_NAME..."
POSTGRES_USER="${POSTGRES_USER:-postgres}"
POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-postgres123}"
export PGPASSWORD="$POSTGRES_PASSWORD"
DB_EXISTS=$(psql -h localhost -p 5433 -U $POSTGRES_USER -lqt 2>/dev/null | cut -d \| -f 1 | grep -w $DB_NAME | wc -l)
unset PGPASSWORD

if [ "$DB_EXISTS" -eq 0 ]; then
    echo "База данных $DB_NAME не найдена."
    echo "Запуск setup-db.sh для создания базы данных..."
    if [ -f "setup-db.sh" ]; then
        chmod +x setup-db.sh
        ./setup-db.sh
        if [ $? -ne 0 ]; then
            echo "ОШИБКА: Не удалось настроить базу данных!"
            exit 1
        fi
    else
        echo "ОШИБКА: Файл setup-db.sh не найден!"
        echo "Пожалуйста, запустите setup-db.sh вручную для создания базы данных."
        exit 1
    fi
else
    echo "База данных $DB_NAME существует ✓"
fi

# Проверка, не запущен ли уже Tomcat
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "Tomcat уже запущен (PID: $PID)"
        echo "Используйте ./stop.sh для остановки"
        exit 1
    else
        echo "Удаляю устаревший PID файл..."
        rm -f "$PID_FILE"
    fi
fi

# Сборка проекта
echo ""
echo "Сборка проекта..."
if ! mvn clean package -DskipTests; then
    echo "ОШИБКА: Не удалось собрать проект!"
    exit 1
fi
echo "Проект собран ✓"

# Проверка наличия WAR файла
if [ ! -f "$WAR_FILE" ]; then
    echo "ОШИБКА: WAR файл не найден: $WAR_FILE"
    exit 1
fi

# Копирование WAR в Tomcat
echo ""
echo "Копирование WAR в Tomcat..."
rm -rf "$TOMCAT_DIR/webapps/kursach" "$TOMCAT_DIR/webapps/kursach.war"
cp "$WAR_FILE" "$TOMCAT_DIR/webapps/"
echo "WAR файл скопирован ✓"

# Запуск Tomcat
echo ""
echo "Запуск Tomcat..."
cd "$TOMCAT_DIR"
./bin/startup.sh
cd ..

# Сохранение PID
sleep 2
if [ -f "$TOMCAT_DIR/logs/catalina.pid" ]; then
    cp "$TOMCAT_DIR/logs/catalina.pid" "$PID_FILE"
    PID=$(cat "$PID_FILE")
    echo "Tomcat запущен (PID: $PID) ✓"
else
    echo "Предупреждение: PID файл не найден, но Tomcat должен быть запущен"
fi

echo ""
echo "=========================================="
echo "Проект $PROJECT_NAME запущен!"
echo "=========================================="
echo "Приложение доступно по адресу:"
echo "  http://localhost:8082/kursach/"
echo ""
echo "Для остановки используйте: ./stop.sh"
echo "Логи Tomcat: $TOMCAT_DIR/logs/catalina.out"
echo ""
