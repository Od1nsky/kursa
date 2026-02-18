#!/bin/bash

echo "Настройка базы данных для kursach_vlad..."
echo "=========================================="

# Параметры подключения
DB_NAME="kursach_vlad"
DB_USER="project_role"
DB_PASSWORD="project_role"
DB_HOST="localhost"
DB_PORT="5432"
POSTGRES_USER="${POSTGRES_USER:-postgres}"
POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-postgres123}"

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

# Проверка, запущен ли PostgreSQL
if ! pg_isready -h $DB_HOST -p $DB_PORT > /dev/null 2>&1; then
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
    if ! pg_isready -h $DB_HOST -p $DB_PORT > /dev/null 2>&1; then
        echo "ОШИБКА: PostgreSQL все еще не доступен после попытки запуска!"
        exit 1
    fi
fi

echo "PostgreSQL запущен ✓"

# Проверка подключения к PostgreSQL и настройка пароля
# На macOS с Homebrew используется текущий пользователь системы, а не postgres
CURRENT_USER=$(whoami)
export PGPASSWORD="$POSTGRES_PASSWORD"

# Сначала пытаемся подключиться с указанным пользователем и паролем
if ! psql -h $DB_HOST -p $DB_PORT -U $POSTGRES_USER -d postgres -c '\q' 2>/dev/null; then
    echo "Попытка подключения с текущим пользователем системы ($CURRENT_USER)..."
    unset PGPASSWORD
    # Пытаемся подключиться с текущим пользователем системы
    if psql -h $DB_HOST -p $DB_PORT -U $CURRENT_USER -d postgres -c '\q' 2>/dev/null; then
        echo "Подключение с пользователем $CURRENT_USER успешно ✓"
        # Проверяем, существует ли пользователь postgres
        USER_EXISTS=$(psql -h $DB_HOST -p $DB_PORT -U $CURRENT_USER -d postgres -tAc "SELECT 1 FROM pg_roles WHERE rolname='$POSTGRES_USER';" 2>/dev/null)
        if [ "$USER_EXISTS" != "1" ]; then
            echo "Создание пользователя $POSTGRES_USER..."
            psql -h $DB_HOST -p $DB_PORT -U $CURRENT_USER -d postgres -c "CREATE USER $POSTGRES_USER WITH SUPERUSER PASSWORD '$POSTGRES_PASSWORD';" 2>/dev/null
            if [ $? -eq 0 ]; then
                echo "Пользователь $POSTGRES_USER создан ✓"
            fi
        else
            echo "Пользователь $POSTGRES_USER уже существует, обновление пароля..."
            psql -h $DB_HOST -p $DB_PORT -U $CURRENT_USER -d postgres -c "ALTER USER $POSTGRES_USER WITH PASSWORD '$POSTGRES_PASSWORD';" 2>/dev/null
            if [ $? -eq 0 ]; then
                echo "Пароль для $POSTGRES_USER обновлен ✓"
            fi
        fi
        # Теперь пробуем подключиться с пользователем postgres
        export PGPASSWORD="$POSTGRES_PASSWORD"
        if psql -h $DB_HOST -p $DB_PORT -U $POSTGRES_USER -d postgres -c '\q' 2>/dev/null; then
            echo "Подключение с пользователем $POSTGRES_USER успешно ✓"
        else
            echo "Предупреждение: Не удалось подключиться с пользователем $POSTGRES_USER, используем $CURRENT_USER"
            POSTGRES_USER="$CURRENT_USER"
            unset PGPASSWORD
        fi
    else
        echo "ОШИБКА: Не удается подключиться к PostgreSQL!"
        echo "Убедитесь, что PostgreSQL запущен и доступен."
        exit 1
    fi
else
    echo "Подключение с пользователем $POSTGRES_USER успешно ✓"
fi

echo "Подключение к PostgreSQL настроено ✓"

# Создание базы данных
echo "Создание базы данных $DB_NAME..."
export PGPASSWORD="$POSTGRES_PASSWORD"
psql -h $DB_HOST -p $DB_PORT -U $POSTGRES_USER <<EOF
-- Создание пользователя, если не существует
DO \$\$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '$DB_USER') THEN
        CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';
        RAISE NOTICE 'Пользователь $DB_USER создан';
    ELSE
        RAISE NOTICE 'Пользователь $DB_USER уже существует';
        -- Обновляем пароль, если пользователь существует
        ALTER USER $DB_USER WITH PASSWORD '$DB_PASSWORD';
    END IF;
END
\$\$;

-- Удаление базы данных, если она существует
DROP DATABASE IF EXISTS $DB_NAME;

-- Создание базы данных
CREATE DATABASE $DB_NAME OWNER $DB_USER;

-- Предоставление прав на базу данных
GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;
EOF

# Подключение к созданной базе данных для настройки схемы
psql -h $DB_HOST -p $DB_PORT -U $POSTGRES_USER -d $DB_NAME <<EOF
-- Предоставление прав на схему public
GRANT ALL ON SCHEMA public TO $DB_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $DB_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $DB_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO $DB_USER;

-- Убеждаемся, что пользователь может создавать объекты
ALTER SCHEMA public OWNER TO $DB_USER;
EOF

unset PGPASSWORD

if [ $? -eq 0 ]; then
    echo "База данных $DB_NAME успешно создана ✓"
    echo "Пользователь $DB_USER настроен ✓"
    echo "Права на схему public предоставлены ✓"
    echo ""
    echo "База данных готова к использованию!"
    echo "При первом запуске приложения Hibernate автоматически создаст таблицы."
else
    echo "ОШИБКА: Не удалось создать базу данных!"
    unset PGPASSWORD
    exit 1
fi
