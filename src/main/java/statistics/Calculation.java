package statistics;

import entities.Manufacturer;
import entities.Product;
import entities.Review;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Calculation {

    // Конвейер с помощью Stream API на базе коллекторов из стандартной библиотеки
    public static Map<Manufacturer, Double> avgRatingWithPipeline(List<Product> products, List<Manufacturer> manufacturers, long delay) {
        return manufacturers.stream()
                .collect(Collectors.toMap(
                        manufacturer -> manufacturer,
                        manufacturer -> {
                            List<Review> reviews = products.stream()
                                    .filter(product -> product.getManufacturer().equals(manufacturer))
                                    .flatMap(product -> product.getReviewsWithDelay(delay).stream())
                                    .toList();

                            return reviews.isEmpty() ? 0.0 : reviews.stream()
                                    .mapToInt(Review::getRating)
                                    .average()
                                    .orElse(0.0);
                        }
                ));
    }

    public static Map<Manufacturer, Double> avgRatingWithPipelineParallel(List<Product> products, List<Manufacturer> manufacturers, long delay) {
        return manufacturers.parallelStream()
                .collect(Collectors.toMap(
                        manufacturer -> manufacturer,
                        manufacturer -> {
                            List<Review> reviews = products.stream()
                                    .filter(product -> product.getManufacturer().equals(manufacturer))
                                    .flatMap(product -> product.getReviewsWithDelay(delay).stream())
                                    .toList();

                            return reviews.isEmpty() ? 0.0 : reviews.stream()
                                    .mapToInt(Review::getRating)
                                    .average()
                                    .orElse(0.0);
                        }
                ));
    }


    // Собственный коллектор
    public static Map<Manufacturer, Double> avgRatingWithCollector(List<Product> products, List<Manufacturer> manufacturers, long delay) {
        return products.stream()
                .collect(new AverageRatingCollector(manufacturers, delay));
    }

    public static Map<Manufacturer, Double> avgRatingWithCollectorParallel(List<Product> products, List<Manufacturer> manufacturers, long delay) {
        return products.parallelStream()
                .collect(new AverageRatingCollector(manufacturers, delay));
    }

    public static Map<Manufacturer, Double> avgRatingWithCollectorParallelSpliterator(List<Product> products, List<Manufacturer> manufacturers, long delay) {
        ProductSpliterator productSpliterator = new ProductSpliterator(products);
        return StreamSupport.stream(productSpliterator, true)
                .collect(new AverageRatingCollector(manufacturers, delay));
    }


    public static void printResult(Map<Manufacturer, Double> result) {
        for (Manufacturer manufacturer : result.keySet()) {
            System.out.printf("For manufacturer \"%s\" average product rating is %f\n", manufacturer.name(), result.get(manufacturer));
        }
    }
}
