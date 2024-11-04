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

        System.out.printf("Generating %d manufacturers... ", manufacturerCount);

        ManufacturerGenerator manufacturerGenerator = new ManufacturerGenerator();
        manufacturers = manufacturerGenerator.generateList(manufacturerCount);

        System.out.println("Done.");
        System.out.printf("Generating %d products with %d reviews... ", productCount, reviewCount);

        ProductGenerator productGenerator = new ProductGenerator(manufacturers, reviewCount);
        products = productGenerator.generateList(productCount);

        System.out.println("Done.");
    }

//    @Benchmark
//    public Map<Manufacturer, Double> calculateAvgRating() {
//        //List<Product> firstNProducts = products.subList(0, productCount);
//        Map<Manufacturer, Double> result = Calculation.avgRatingWithPipeline(products, manufacturers);
//        return result;
//        //Map<Manufacturer, Double> avgRatingWithPipeline = Calculation.avgRatingWithPipeline(products, manufacturers);
//        //Map<Manufacturer, Double> avgRatingWithCollector = Calculation.avgRatingWithCollector(products, manufacturers);
//    }

    @Benchmark
    @Group("СonsistentStreamNoDelay")
    public Map<Manufacturer, Double> calculateAvgRatingWithPipelineNoDelay() {
        return Calculation.avgRatingWithPipeline(products, manufacturers, 0);
    }

    @Benchmark
    @Group("СonsistentStreamNoDelay")
    public Map<Manufacturer, Double> calculateAvgRatingWithCollectorNoDelay() {
        return Calculation.avgRatingWithCollector(products, manufacturers, 0);
    }

    @Benchmark
    @Group("СonsistentStreamWithDelay")
    public Map<Manufacturer, Double> calculateAvgRatingWithPipelineWithDelay() {
        return Calculation.avgRatingWithPipeline(products, manufacturers, 1);
    }

    @Benchmark
    @Group("СonsistentStreamWithDelay")
    public Map<Manufacturer, Double> calculateAvgRatingWithCollectorWithDelay() {
        return Calculation.avgRatingWithCollector(products, manufacturers, 1);
    }

    @Benchmark
    @Group("ParallelStreamNoDelay")
    public Map<Manufacturer, Double> calculateAvgRatingWithPipelineNoDelayParallel() {
        return Calculation.avgRatingWithPipeline(products, manufacturers, 0);
    }

    @Benchmark
    @Group("ParallelStreamNoDelay")
    public Map<Manufacturer, Double> calculateAvgRatingWithCollectorNoDelayParallel() {
        return Calculation.avgRatingWithCollectorParallel(products, manufacturers, 0);
    }

    @Benchmark
    @Group("ParallelStreamNoDelay")
    public Map<Manufacturer, Double> calculateAvgRatingWithCollectorSpliteratorNoDelayParallel() {
        return Calculation.avgRatingWithCollectorParallelSpliterator(products, manufacturers, 0);
    }

    @Benchmark
    @Group("ParallelStreamWithDelay")
    public Map<Manufacturer, Double> calculateAvgRatingWithPipelineWithDelayParallel() {
        return Calculation.avgRatingWithPipelineParallel(products, manufacturers, 1);
    }

    @Benchmark
    @Group("ParallelStreamWithDelay")
    public Map<Manufacturer, Double> calculateAvgRatingWithCollectorWithDelayParallel() {
        return Calculation.avgRatingWithCollector(products, manufacturers, 1);
    }

    @Benchmark
    @Group("ParallelStreamWithDelay")
    public Map<Manufacturer, Double> calculateAvgRatingWithCollectorSpliteratorWithDelayParallel() {
        return Calculation.avgRatingWithCollectorParallelSpliterator(products, manufacturers, 1);
    }
}