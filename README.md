## Добро пожаловать в Cache Storage API! (November 2023)

### Базовые структуры:

___

- HashMap - хранилище "ключ-значение". Представляет L1 cache.
    - Ключом выступает класс String - в силу своей производительности в качестве ключа хэш-таблицы.
    - В качестве значения можно хранить любой объект.
- PriorityQueue - приоритетная очередь кэш-элементов.
    - При переполнении кэша удаляется элемент из головы этой очереди.
    - Используется стратегиями, которые могут конфигурировать эту очередь своим компаратором.
- HashSet - множество ключей кэш-элементов, хранящихся в L2 cache.
    - При включении L2 cache наполняется ключами кэш-элементов из кэш-файла.
    - Через него проходят проверки, нужно ли добавлять кэш-элемент в L2 (может он там уже есть?)

### API

___

#### Создание CacheStorage.

Создание CacheStorage происходит при помощи CacheStorageBuilder.

        CacheStorageBuilder builder = new CacheStorageBuilder();
        CacheStorage storage = builder
                .setCapacity(16)
                .setCacheStrategy(DeleteStrategyType.LFU)
                .build();

Вы можете настроить создаваемый CacheStorage под свои нужды, установив ему вместимость (`setCapacity()`)

подав ему на вход уже готовое хранилище типа HashMap<String, Object> (`setKeyValueStorage()`).

Таким образом, вы получите кэш-хранилище, использующее L1 (HashMap).

#### Установка стратегии

Стратегия вытеснения элементов из кэша при нехватке места устанавливается методом `setCacheStrategy(yourStrategy)`

        CacheStorageBuilder builder = new CacheStorageBuilder();
        CacheStorage storage = builder
                .setCapacity(8)
                .setCacheStrategy(yourStrategy)
                .build();

Всего есть 3 стратегии:

- Least Frequency Used (LFU) - `DeleteStrategyType.LFU`
- Most Frequency Used (MFU) - `DeleteStrategyType.MFU`
- Least Recently Used (LRU) - `DeleteStrategyType.LRU`

#### Активация L2 cache.

Активация происходит посредством использования метода `enableLCache()`

        CacheStorageBuilder builder = new CacheStorageBuilder();
        CacheStorage storage = builder
                .setCapacity(6)
                .setCacheStrategy(DeleteStrategyType.MFU)
                .enableL2Cache()
                .build();

После этого произойдет активация класса `L2CacheResolver`, который создаст кэш-файл, а также

структуры HashSet, которая будет хранить ключи кэш-элементов, уже имеющихся в кэш-файле.