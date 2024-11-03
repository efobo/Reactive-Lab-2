package benchmark;

import entities.Manufacturer;
import entities.Product;
import generators.ManufacturerGenerator;
import generators.ProductGenerator;
import org.openjdk.jmh.annotations.*;
import statistics.Calculation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 0)
@Fork(1)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BenchmarkCalculation {
    @Param({"5000", "50000", "250000"})
    private int productCount;

    private List<Manufacturer> manufacturers;
    private List<Product> products;

    @Setup(Level.Trial)
    public void generate() {
        int manufacturerCount = 3;
        int reviewCount = 10;
        //int productCount = 250000;

        System.out.printf("Generating %d manufacturers... ", manufacturerCount);

        ManufacturerGenerator manufacturerGenerator = new ManufacturerGenerator();
        manufacturers = manufacturerGenerator.generateList(manufacturerCount);

        System.out.println("Done.");
        System.out.printf("Generating %d products with %d reviews... ", productCount, reviewCount);

        ProductGenerator productGenerator = new ProductGenerator(manufacturers, reviewCount);
        products = productGenerator.generateList(productCount);

        System.out.println("Done.");
    }

    @Benchmark
    public Map<Manufacturer, Double> calculateAvgRating() {
        //List<Product> firstNProducts = products.subList(0, productCount);
        Map<Manufacturer, Double> result = Calculation.avgRatingWithPipeline(products, manufacturers);
        return result;
        //Map<Manufacturer, Double> avgRatingWithPipeline = Calculation.avgRatingWithPipeline(products, manufacturers);
        //Map<Manufacturer, Double> avgRatingWithCollector = Calculation.avgRatingWithCollector(products, manufacturers);
    }
}