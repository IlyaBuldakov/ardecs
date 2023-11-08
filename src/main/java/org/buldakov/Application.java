package org.buldakov;

import org.buldakov.model.cache.CacheStorage;
import org.buldakov.model.cache.CacheStorageBuilder;
import org.buldakov.model.cache.enums.DeleteStrategyType;

public class Application {

    public static void main(String[] args) {
        lruTest();
    }

    private static void lfuTest() {
        CacheStorageBuilder<Integer> builder = new CacheStorageBuilder<>();
        CacheStorage<Integer> storage = builder
                .setCapacity(2)
                .setCacheStrategy(DeleteStrategyType.LFU)
                .build();

        // Добавляем тестовые элементы
        storage.addData("Cache1", 1);
        storage.addData("Cache2", 1);

        // Два раза используем Cache1
        storage.getData("Cache1");
        storage.getData("Cache1");

        // Добавляем тестовый элемент
        storage.addData("Cache3", 1);

        // Один раз используем Cache3
        storage.getData("Cache3");

        // Добавляем тестовый элемент
        storage.addData("Cache4", 1);

        /* Согласно стратегии LFU, будут удалены элементы с наименьшим использованием на момент добавления
         * нового элемента в кэш. Cache1 будет существовать до конца, как элемент с наибольшим приоритетом.
         * Cache3 будет добавлен и один раз использован, но также будет беспощадно вытеснен из кэша из-за принудительного
         * добавления элемента Cache4, в процессе которого будут сравниваться Cache1 (2 исп. ) и Cache3 (1 исп.) */
    }

    private static void mfuTest() {
        CacheStorageBuilder<Integer> builder = new CacheStorageBuilder<>();
        CacheStorage<Integer> storage = builder
                .setCapacity(2)
                .setCacheStrategy(DeleteStrategyType.MFU)
                .build();

        // Добавляем тестовые элементы
        storage.addData("Cache1", 1);
        storage.addData("Cache2", 1);

        // Два раза используем Cache1
        storage.getData("Cache1");
        storage.getData("Cache1");

        // Добавляем тестовый элемент
        storage.addData("Cache3", 1);

        // Один раз используем Cache3
        storage.getData("Cache3");

        // Добавляем тестовый элемент
        storage.addData("Cache4", 1);

        /* Те же тестовые данные, но MFU конфигурация хранилища. На выходе получаем хранилище с элементами
        * Cache2 и Cache4, как наименее использованные (не использованные) элементы хранилища.
        * Эта стратегия - как LFU, только с "-" в компараторе, и, как следствие, обратным приоритетом. */
    }

    private static void lruTest() {
        CacheStorageBuilder<Integer> builder2 = new CacheStorageBuilder<>();
        CacheStorage<Integer> storage2 = builder2
                .setCapacity(2)
                .setCacheStrategy(DeleteStrategyType.LRU)
                .build();

        // Добавляем тестовые элементы
        storage2.addData("Cache1", 1);
        storage2.addData("Cache2", 1);

        // Два раза используем Cache1
        storage2.getData("Cache1");
        storage2.getData("Cache1");

        // Добавляем тестовый элемент
        storage2.addData("Cache3", 1);

        // Один раз используем Cache3
        storage2.getData("Cache3");

        // Добавляем тестовый элемент
        storage2.addData("Cache4", 1);

        /* В отличии от предыдущих стратегий, LRU стратегия будет вытеснять те элементы, которые использованы
        * раннее (более давно). Таким образом привычный тест оборачивается неожиданным результатом, поскольку
        * в процессе добавления Cache4 перед стратегией предстает дилемма - кого выгнать? Cache1 или Cache3?
        * Без лишних сожалений стратегия вытесняет Cache1, несмотря на высокий клиентский спрос, так как
        * использовали её давно :\ */
    }
}
