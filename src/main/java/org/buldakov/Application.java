package org.buldakov;

import org.buldakov.model.cache.CacheStorage;
import org.buldakov.model.cache.CacheStorageBuilder;
import org.buldakov.model.cache.enums.DeleteStrategyType;

import java.io.IOException;
import java.util.Locale;

public class Application {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DeleteStrategyType strategy = parseStrategy(args);
        cacheTest(strategy);
    }

    /**
     * Метод получения стратегии из входных параметров приложения.
     *
     * @param args Входные параметры.
     * @return Стратегия.
     */
    private static DeleteStrategyType parseStrategy(String[] args) {
        DeleteStrategyType result = DeleteStrategyType.LFU;
        if (args.length == 1) {
            try {
                result = DeleteStrategyType.valueOf(args[0].toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                System.err.println("Неверный параметр");
            }
        } else {
            System.err.println("Ожидается на вход 1 параметр - стратегия. По умолчанию использована LFU");
        }
        return result;
    }

    /**
     * Метод для демонстрации работы кэш-хранилища.
     */
    private static void cacheTest(DeleteStrategyType deleteStrategy) throws IOException, ClassNotFoundException {
        CacheStorageBuilder builder = new CacheStorageBuilder();
        CacheStorage storage = builder
                .setCapacity(2)
                .setCacheStrategy(deleteStrategy)
                .enableL2Cache()
                .build();

        System.out.printf("=== Стратегия %s ===%n", deleteStrategy);

        // Добавляем тестовые элементы
        storage.addData("Cache1", 1);
        storage.addData("Cache2", 1);

        storage.getData("Cache2");

        // Два раза используем Cache1
        storage.getData("Cache1");
        storage.getData("Cache1");

        // Добавляем тестовый элемент
        storage.addData("Cache3", 1);

        // Один раз используем Cache3
        storage.getData("Cache3");

        // Добавляем тестовый элемент
        storage.addData("Cache4", 1);

        System.out.printf("=== Стратегия %s ===", deleteStrategy);

        /* Согласно стратегии LFU, будут удалены элементы с наименьшим использованием на момент добавления
         * нового элемента в кэш. Cache1 будет существовать до конца, как элемент с наибольшим приоритетом.
         * Cache3 будет добавлен и один раз использован, но также будет беспощадно вытеснен из кэша из-за принудительного
         * добавления элемента Cache4, в процессе которого будут сравниваться Cache1 (2 исп. ) и Cache3 (1 исп.) */

        /*  MFU конфигурация хранилища. На выходе получаем хранилище с элементами
         * Cache2 и Cache4, как наименее использованные (не использованные) элементы хранилища.
         * Эта стратегия - как LFU, только с "-" в компараторе, и, как следствие, обратным приоритетом. */

        /* В отличии от предыдущих стратегий, LRU стратегия будет вытеснять те элементы, которые использованы
         * раннее (более давно). Таким образом привычный тест оборачивается неожиданным результатом, поскольку
         * в процессе добавления Cache4 перед стратегией предстает дилемма - кого выгнать? Cache1 или Cache3?
         * Без лишних сожалений стратегия вытесняет Cache1, несмотря на высокий клиентский спрос, так как
         * использовали её давно :\ */
    }
}
