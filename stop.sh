#!/bin/bash

PROJECT_NAME="kursach_vlad"
TOMCAT_DIR="apache-tomcat-10"
PID_FILE="tomcat10.pid"

echo "=========================================="
echo "Остановка проекта: $PROJECT_NAME"
echo "=========================================="

# Проверка наличия PID файла
if [ ! -f "$PID_FILE" ]; then
    echo "PID файл не найден. Попытка остановить Tomcat через shutdown.sh..."
    cd "$TOMCAT_DIR"
    ./bin/shutdown.sh
    cd ..
    sleep 2
    
    # Проверка, запущен ли еще Tomcat
    if pgrep -f "catalina" > /dev/null; then
        echo "Tomcat все еще запущен. Принудительная остановка..."
        pkill -f "catalina"
        sleep 1
    fi
else
    PID=$(cat "$PID_FILE")
    echo "Остановка Tomcat (PID: $PID)..."
    
    # Попытка корректной остановки
    cd "$TOMCAT_DIR"
    ./bin/shutdown.sh
    cd ..
    sleep 3
    
    # Проверка, запущен ли процесс
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "Процесс все еще запущен. Принудительная остановка..."
        kill -9 "$PID" 2>/dev/null
        sleep 1
    fi
    
    # Удаление PID файла
    rm -f "$PID_FILE"
fi

# Дополнительная проверка и очистка
if pgrep -f "catalina.*$TOMCAT_DIR" > /dev/null; then
    echo "Найдены оставшиеся процессы Tomcat. Остановка..."
    pkill -f "catalina.*$TOMCAT_DIR"
    sleep 1
fi

echo ""
echo "=========================================="
echo "Проект $PROJECT_NAME остановлен ✓"
echo "=========================================="
